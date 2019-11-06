package de.guntram.mcmod.easiervillagertrading;

import de.guntram.mcmod.fabrictools.ConfigChangedEvent;
import de.guntram.mcmod.fabrictools.Configuration;
import de.guntram.mcmod.fabrictools.ModConfigurationHandler;
import java.io.File;

public class ConfigurationHandler implements ModConfigurationHandler {

    private static ConfigurationHandler instance;

    private String configFileName;
    private Configuration config;
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

    private void loadConfig() {
        showLeft=config.getBoolean("Trades list left", Configuration.CATEGORY_CLIENT, 
                false, "Show trades list to the left, for Just Enough Items compatibility");
        leftPixelOffset=config.getInt("Trades left pixel offset", Configuration.CATEGORY_CLIENT,
                0, 0, Integer.MAX_VALUE, 
                "How many pixels left of the GUI the trades list will be shown. Use 0 for auto detect. "+
                "Only used if Trades list left is true.");
        if (config.hasChanged())
            config.save();
    }
    
    public static String getConfigFileName() {
        return getInstance().configFileName;
    }
    
    public static boolean showLeft() { return getInstance().showLeft; }
    public static int leftPixelOffset() { return getInstance().leftPixelOffset; }
    public static boolean autoFocusSearch() { return getInstance().autoFocusSearch; }

    @Override
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equalsIgnoreCase(EasierVillagerTrading.MODID)) {
            loadConfig();
        }
    }

    @Override
    public Configuration getConfig() {
        return getInstance().config;
    }
}
