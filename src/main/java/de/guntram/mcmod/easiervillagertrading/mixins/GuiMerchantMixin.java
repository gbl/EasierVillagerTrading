package de.guntram.mcmod.easiervillagertrading.mixins;

import de.guntram.mcmod.easiervillagertrading.BetterGuiMerchant;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.IMerchant;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayerSP.class)
public abstract class GuiMerchantMixin extends AbstractClientPlayer {
    
    public GuiMerchantMixin() { super(null, null); }
    
    @Shadow protected Minecraft mc;

    @Inject(method = "displayVillagerTradeGui", at = @At("HEAD"), cancellable = true)
    public void displayVillagerTradeGui(IMerchant villager, CallbackInfo ci) {
        this.mc.displayGuiScreen(new BetterGuiMerchant(this.inventory, villager, this.world));
        ci.cancel();
    }
}
