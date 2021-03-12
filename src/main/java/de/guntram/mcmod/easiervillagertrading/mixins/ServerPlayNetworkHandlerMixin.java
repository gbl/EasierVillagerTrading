package de.guntram.mcmod.easiervillagertrading.mixins;

import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
/*    
    We probably won't need this anymore with the new way of confirming
    screen results in 21w10a.
    
    @Shadow public ServerPlayerEntity player;

    @Inject(method="onClickSlot", at=@At("RETURN"))
    private void clickWindowSendsCraftResult(ClickSlotC2SPacket packet, CallbackInfo ci) {
        if (this.player.currentScreenHandler instanceof MerchantScreenHandler
        &&  packet.getSlot() == 0 && packet.getClickData() == 99 && packet.getActionType() == SlotActionType.SWAP) {
            this.player.refreshScreenHandler(this.player.currentScreenHandler);
        }
    }
*/
}
