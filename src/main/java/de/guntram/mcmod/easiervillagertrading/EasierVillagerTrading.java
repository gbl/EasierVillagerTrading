package de.guntram.mcmod.easiervillagertrading;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = EasierVillagerTrading.MODID, version = EasierVillagerTrading.VERSION)
public class EasierVillagerTrading {

    public static final String MODID = "easiervillagertrading";
    public static final String VERSION = "0.1";
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(OpenTradeEventHandler.getInstance());
    }
}
