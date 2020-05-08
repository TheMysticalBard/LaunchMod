package themysticalbard.launch.common.enchantment.enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import themysticalbard.launch.common.Launch;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LaunchEnchantment extends CustomEnchantment
{
	private static int chargeStage;
	private static int chargeTimer;
	private static int completedStage;
	private static final Logger LOGGER = LogManager.getLogger(Launch.MOD_ID);

	public LaunchEnchantment() {
		super(Rarity.RARE, EnchantmentType.ARMOR_LEGS, EquipmentSlotType.LEGS);
		chargeStage = -1;
		chargeTimer = 0;
		completedStage = -1;
	}

	@Override
	public boolean isAllowedOnBooks() {
		return true;
	}

	@Override
	public boolean isTreasureEnchantment() {
		return true;
	}

	@Override
	public int getMaxLevel() {
		return 3;
	}

	@SubscribeEvent
	public void onPlayerJump(LivingEvent.LivingJumpEvent event) {
		if(event.getEntity() != null
				&& event.getEntity() instanceof PlayerEntity
				&& event.getEntity().isAlive()
				&& !event.getEntity().world.isRemote
				&& Launch.LAUNCH_ENCHANTMENT.get() != null
		) {
			final PlayerEntity player = (PlayerEntity) event.getEntity();
			final int launchLevel = EnchantmentHelper.getEnchantmentLevel(Launch.LAUNCH_ENCHANTMENT.get(), player.getItemStackFromSlot(EquipmentSlotType.LEGS));
			if(launchLevel > 0) {
				if(!player.isSneaking()) {
					Vec3d previousMotion = player.getMotion();
					player.setMotion(previousMotion.x, player.getMotion().y * 1.2d, previousMotion.z);
					if (player.isSprinting()) {
						player.jumpMovementFactor = 0.026F;
					}
					LOGGER.debug("Player had enchantment, velocity added!");
					player.velocityChanged = true;
				}
			}
			if(player.isSneaking()) {
				if(completedStage > 0 && completedStage <= launchLevel) {
					launchPlayer(player);
				}
				chargeStage = -1;
				chargeTimer = 0;
				completedStage = -1;
			}
		}
	}

	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if(event.player != null
				&& event.player.isAlive()
				&& !event.player.world.isRemote
				&& Launch.LAUNCH_ENCHANTMENT.get() != null
		) {
			int launchLevel = EnchantmentHelper.getEnchantmentLevel(Launch.LAUNCH_ENCHANTMENT.get(), event.player.getItemStackFromSlot(EquipmentSlotType.LEGS));
			if(launchLevel > 0 && event.player.isSneaking() && !event.player.isAirBorne) {
				LOGGER.debug("Charge Stage: " + chargeStage);
				LOGGER.debug("Completed Stage: " + completedStage);
				if(chargeStage == -1) {
					chargeStage = 0;
					chargeTimer = MathHelper.ceil(20 * 2);
				}
				else {
					chargeTimer--;
				}
				if(chargeTimer <= 0) {
					int oldStage = completedStage++;
					completedStage = Math.min(launchLevel, completedStage);
					if(chargeStage > 0 && oldStage != completedStage /*&& Launch.CHARGING_SOUND.get() != null*/) {
						SoundEvent playerCharging = SoundEvents.BLOCK_BEACON_ACTIVATE;
						event.player.world.playSound(null,
								event.player.posX,
								event.player.posY,
								event.player.posZ,
								playerCharging,
								SoundCategory.PLAYERS,
								1.0f,
								1.1f + 0.3f * completedStage);
					}
					if(chargeStage < launchLevel) {
						chargeStage++;
						chargeTimer = MathHelper.ceil(20 * 3);
					}
				}
				chargeTimer = Math.max(0, chargeTimer);
			}
			if(!event.player.isSneaking()) {
				chargeStage = -1;
				chargeTimer = 0;
				completedStage = -1;
			}
		}
	}

	private void launchPlayer(PlayerEntity player) {
		final double FIRST_MOD = 0.90d;
		final double SECOND_MOD = 1.35d;
		final double THIRD_MOD = 1.75d;

		Vec3d prevMotion = player.getMotion();
		double jumpMod = completedStage == 1 ? FIRST_MOD : completedStage == 2 ? SECOND_MOD : THIRD_MOD;
		player.setMotion(prevMotion.x, jumpMod, prevMotion.z);
		player.isAirBorne = true;
		player.velocityChanged = true;
	}
}