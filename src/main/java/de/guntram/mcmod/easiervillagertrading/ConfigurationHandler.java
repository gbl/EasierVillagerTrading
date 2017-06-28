package de.guntram.mcmod.easiervillagertrading;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import java.io.File;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;

public class ConfigurationHandler {

    private static ConfigurationHandler instance;

    private Configuration config;
    private String configFileName;
    private boolean showLeft;
    private int leftPixelOffset;
    private boolean autoFocusSearch;

    public static ConfigurationHandler getInstance() {
        if (instance==null)
            instance=new ConfigurationHandler();
        return instance;
    }

    public void load(final File configFile) {
        if (config == null) {
            config = new Configuration(configFile);
            configFileName=configFile.getPath();
            loadConfig();
        }
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        // System.out.println("OnConfigChanged for "+event.getModID());
        if (event.getModID().equalsIgnoreCase(EasierVillagerTrading.MODID)) {
            loadConfig();
        }
    }
    
    private void loadConfig() {
        showLeft=config.getBoolean("Trades list left", Configuration.CATEGORY_CLIENT, 
                Loader.isModLoaded("jei"),
                "Show trades list to the left, for Just Enough Items compatibility");
        leftPixelOffset=config.getInt("Trades left pixel offset", Configuration.CATEGORY_CLIENT,
                0, 0, Integer.MAX_VALUE, 
                "How many pixels left of the GUI the trades list will be shown. Use 0 for auto detect. "+
                "Only used if Trades list left is true.");
        if (config.hasChanged())
            config.save();
    }
    
    public static Configuration getConfig() {
        return getInstance().config;
    }
    
    public static String getConfigFileName() {
        return getInstance().configFileName;
    }
    
    public static boolean showLeft() { return getInstance().showLeft; }
    public static int leftPixelOffset() { return getInstance().leftPixelOffset; }
    public static boolean autoFocusSearch() { return getInstance().autoFocusSearch; }
}
