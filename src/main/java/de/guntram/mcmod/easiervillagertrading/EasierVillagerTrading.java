package de.guntram.mcmod.easiervillagertrading;

import java.io.File;
import org.dimdev.riftloader.listener.InitializationListener;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

public class EasierVillagerTrading implements InitializationListener {

    public static final String MODID = "easiervillagertrading";
    public static final String VERSION = "1.2";
    
    @Override
    public void onInitialization() {
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.easiervillagertrading.json");        
        ConfigurationHandler confHandler = ConfigurationHandler.getInstance();
        confHandler.load(new File("easiervillagertrading.json"));         // TODO
    }
}
