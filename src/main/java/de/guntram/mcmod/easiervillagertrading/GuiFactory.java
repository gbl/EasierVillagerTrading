package de.guntram.mcmod.easiervillagertrading;

import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;

public class GuiFactory implements IModGuiFactory {
    @Override
    public void initialize(final Minecraft minecraftInstance) {
    }
    
    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass() {
        return de.guntram.mcmod.easiervillagertrading.GuiConfig.class;
    }
    
    @Override
    public Set<IModGuiFactory.RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }
    
    @Override
    public IModGuiFactory.RuntimeOptionGuiHandler getHandlerFor(final IModGuiFactory.RuntimeOptionCategoryElement element) {
        return null;
    }
}
