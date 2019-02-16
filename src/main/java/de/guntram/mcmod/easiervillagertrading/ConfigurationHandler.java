package de.guntram.mcmod.easiervillagertrading;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import de.guntram.mcmod.rifttools.ConfigChangedEvent;
import de.guntram.mcmod.rifttools.Configuration;
import de.guntram.mcmod.rifttools.ModConfigurationHandler;
import java.io.File;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TextComponentString;
import org.dimdev.rift.listener.client.LocalCommandAdder;

public class ConfigurationHandler implements LocalCommandAdder, ModConfigurationHandler {

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
    public void registerLocalCommands(CommandDispatcher<CommandSource> cd) {
        cd.register(
            Commands.literal("easiervillagertrading")
                .then(
                    Commands.literal("left").executes(c->{
                        getInstance().showLeft=true;
                        Minecraft.getInstance().player.sendMessage(new TextComponentString("Gui will be shown left"));
                        return 1;
                    })
                )
                .then(
                    Commands.literal("right").executes(c->{
                        getInstance().showLeft=false;
                        Minecraft.getInstance().player.sendMessage(new TextComponentString("Gui will be shown right"));
                        return 1;
                    })
                )
                .then (
                    Commands.argument("pixels", IntegerArgumentType.integer()).executes (c->{
                        getInstance().leftPixelOffset=IntegerArgumentType.getInteger(c, "pixels");
                        getInstance().showLeft=true;
                        Minecraft.getInstance().player.sendMessage(new TextComponentString("Gui will be shown "+IntegerArgumentType.getInteger(c, "pixels")+" pixels left of trade window"));
                        return 1;
                    })
                )
        );
    }

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
