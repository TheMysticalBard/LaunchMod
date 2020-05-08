package themysticalbard.launch.common;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import themysticalbard.launch.common.enchantment.enchantment.LaunchEnchantment;

@Mod.EventBusSubscriber(modid = Launch.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
@Mod(Launch.MOD_ID)
public class Launch
{
	public static final String MOD_ID = "launch";
	public static IEventBus MOD_EVENT_BUS;
	private static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	private static final DeferredRegister<Enchantment> ENCHANTMENTS = new DeferredRegister<>(ForgeRegistries.ENCHANTMENTS, MOD_ID);
//	private static final DeferredRegister<SoundEvent> SOUNDS = new DeferredRegister<>(ForgeRegistries.SOUND_EVENTS, MOD_ID);
	public static final RegistryObject<Enchantment> LAUNCH_ENCHANTMENT = ENCHANTMENTS.register("launch", LaunchEnchantment::new);
//	public static final RegistryObject<SoundEvent> CHARGING_SOUND = SOUNDS.register("player_charging", LaunchEnchantment.ChargingSound::new);
	public Launch() {
		MOD_EVENT_BUS = FMLJavaModLoadingContext.get().getModEventBus();
		ENCHANTMENTS.register(MOD_EVENT_BUS);
//		SOUNDS.register(MOD_EVENT_BUS);
		MinecraftForge.EVENT_BUS.register(new LaunchEnchantment());
//		MinecraftForge.EVENT_BUS.register(new LaunchEnchantment.ChargingSound());
	}
}