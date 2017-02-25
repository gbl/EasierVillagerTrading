package de.guntram.mcmod.easiervillagertrading;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.client.event.GuiOpenEvent;

public class OpenTradeEventHandler {
    
    static private OpenTradeEventHandler instance;
    private Minecraft mc;
    
    public static OpenTradeEventHandler getInstance() {
        if (instance==null) {
            instance=new OpenTradeEventHandler();
            instance.mc=Minecraft.getMinecraft();
        }
        return instance;
    }

    @SubscribeEvent
    public void guiOpenEvent(GuiOpenEvent event) {
        if (event.getGui() instanceof GuiMerchant) {
            event.setGui(new BetterGuiMerchant(mc.player.inventory, (GuiMerchant)event.getGui(), mc.world));
        }
    }
}
