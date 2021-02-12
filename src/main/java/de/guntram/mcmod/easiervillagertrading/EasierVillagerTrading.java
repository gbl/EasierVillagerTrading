package de.guntram.mcmod.easiervillagertrading;

import de.guntram.mcmod.fabrictools.ConfigurationProvider;
import net.fabricmc.api.ClientModInitializer;

public class EasierVillagerTrading implements ClientModInitializer {

    public static final String MODID = "easiervillagertrading";
    public static final String MODNAME = "EasierVillagerTrading";
    public static final String VERSION = "1.4";
    
    @Override
    public void onInitializeClient() {
        ConfigurationHandler confHandler = ConfigurationHandler.getInstance();
        ConfigurationProvider.register(MODNAME, confHandler);
        confHandler.load(ConfigurationProvider.getSuggestedFile(MODID));        
    }
}
