package de.guntram.mcmod.easiercrafting.mixins;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClientPlayNetworkHandler.class)
public class GuiActionConfirmDebug {
/*
    @Inject(method="onConfirmScreenAction", at=@At("RETURN"))
    private void dumpActionConfirmInfo(ConfirmScreenActionS2CPacket packet, CallbackInfo ci) {
        System.out.println("confirm: id="+packet.getSyncId()+", action="+packet.getActionId()+", accepted="+packet.wasAccepted());
    }
    @Inject(method="onInventory", at=@At("RETURN"))
    private void dumpInventoryInfo(InventoryS2CPacket packet, CallbackInfo ci) {
        System.out.println("inventory: guiid="+packet.getSyncId()+", slotcount="+packet.getContents().size());
    }
*/
}
