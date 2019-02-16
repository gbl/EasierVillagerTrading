package de.guntram.mcmod.easiervillagertrading;

import de.guntram.mcmod.rifttools.ConfigurationProvider;
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
        Mixins.addConfiguration("mixins.riftpatch-de-guntram.json");
        Mixins.addConfiguration("mixins.rifttools-de-guntram.json");
        ConfigurationHandler confHandler = ConfigurationHandler.getInstance();
        ConfigurationProvider.register("EasierVillagerTrading", confHandler);
        confHandler.load(ConfigurationProvider.getSuggestedFile(MODID));
    }
}
