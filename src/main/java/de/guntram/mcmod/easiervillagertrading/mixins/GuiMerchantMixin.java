package de.guntram.mcmod.easiervillagertrading.mixins;

import de.guntram.mcmod.easiervillagertrading.BetterGuiMerchant;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screens;
import net.minecraft.container.ContainerType;
import net.minecraft.container.MerchantContainer;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screens.class)
public abstract class GuiMerchantMixin {
    
    @Inject(method = "open", at = @At("HEAD"), cancellable = true)
    private static void displayVillagerTradeGui(ContainerType type, MinecraftClient client,
            int any, Text component, CallbackInfo ci) {

        if (type == ContainerType.MERCHANT) {
            MerchantContainer container = ContainerType.MERCHANT.create(any, client.player.inventory);
            BetterGuiMerchant screen = new BetterGuiMerchant(container, client.player.inventory, component);
            client.player.container = container;
            client.openScreen(screen);
            ci.cancel();
        }
    }
}
