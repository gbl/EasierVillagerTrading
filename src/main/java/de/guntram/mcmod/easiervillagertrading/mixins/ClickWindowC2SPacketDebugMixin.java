package de.guntram.mcmod.easiervillagertrading.mixins;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClickSlotC2SPacket.class)
public class ClickWindowC2SPacketDebugMixin {
    
    static private final Logger LOGGER = LogManager.getLogger();
    
    @Inject(method="<init>(IIIILnet/minecraft/screen/slot/SlotActionType;Lnet/minecraft/item/ItemStack;Lit/unimi/dsi/fastutil/ints/Int2ObjectMap;)V", at=@At("RETURN"))
    private void dumpC2SNewInfo(int syncid, int revision, int slot, int button, SlotActionType actionType, ItemStack stack,
            Int2ObjectMap<ItemStack> int2ObjectMap, CallbackInfo ci) {
        LOGGER.debug(() -> "new ClickWindow C2S: syncid="+syncid+", slot="+slot+", button="+button+
                ", action="+actionType.toString()+", item="+stack.getCount()+" of "+stack.getName().getString()+
                "");
    }
}
