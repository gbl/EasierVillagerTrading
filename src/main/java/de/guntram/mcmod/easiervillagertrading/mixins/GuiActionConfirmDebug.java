package de.guntram.mcmod.easiervillagertrading.mixins;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.ConfirmGuiActionS2CPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class GuiActionConfirmDebug {
    @Inject(method="onGuiActionConfirm", at=@At("RETURN"))
    private void dumpActionConfirmInfo(ConfirmGuiActionS2CPacket packet, CallbackInfo ci) {
        System.out.println("confirm: id="+packet.getId()+", action="+packet.getActionId()+", accepted="+packet.wasAccepted());
    }
    @Inject(method="onInventory", at=@At("RETURN"))
    private void dumpInventoryInfo(InventoryS2CPacket packet, CallbackInfo ci) {
        System.out.println("inventory: guiid="+packet.getSyncId()+", slotcount="+packet.getContents().size());
    }
}
