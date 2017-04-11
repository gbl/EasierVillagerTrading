package de.guntram.mcmod.easiervillagertrading;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(
        modid = EasierVillagerTrading.MODID,
        version = EasierVillagerTrading.VERSION,
	clientSideOnly = true, 
	guiFactory = "de.guntram.mcmod.easiervillagertrading.GuiFactory",
	acceptedMinecraftVersions = "[1.11.2]",
        dependencies = "after:jei"
)

public class EasierVillagerTrading {

    public static final String MODID = "easiervillagertrading";
    public static final String VERSION = "1.1.0";
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(OpenTradeEventHandler.getInstance());
    }
    
    @EventHandler
    public void preInit(final FMLPreInitializationEvent event) {
        ConfigurationHandler confHandler = ConfigurationHandler.getInstance();
        confHandler.load(event.getSuggestedConfigurationFile());
        MinecraftForge.EVENT_BUS.register(confHandler);
    }
}
