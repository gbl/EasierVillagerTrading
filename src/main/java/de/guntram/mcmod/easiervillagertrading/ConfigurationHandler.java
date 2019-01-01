package de.guntram.mcmod.easiervillagertrading;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import java.io.File;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TextComponentString;
import org.dimdev.rift.listener.client.LocalCommandAdder;

public class ConfigurationHandler implements LocalCommandAdder {

    private static ConfigurationHandler instance;

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
    }

    private void loadConfig() {
        showLeft=false;
        leftPixelOffset=0;
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
}
