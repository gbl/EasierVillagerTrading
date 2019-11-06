/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.guntram.mcmod.easiervillagertrading.mixins;

import de.guntram.mcmod.easiervillagertrading.AutoTrade;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.container.MerchantContainer;
import net.minecraft.container.SlotActionType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MerchantScreen.class)
public abstract class MerchantScreenMixin extends AbstractContainerScreen<MerchantContainer> {
    
    @Shadow private int field_19161;

    public MerchantScreenMixin(MerchantContainer merchantContainer_1, PlayerInventory playerInventory_1, Text text_1) {
        super(merchantContainer_1, playerInventory_1, text_1);
    }
    
    @Inject(method="syncRecipeIndex", at=@At("RETURN"))
    public void tradeOnSetRecipeIndex(CallbackInfo ci) {
        if (hasControlDown()) {
            return;
        }
        this.onMouseClick(null, 0, 0, SlotActionType.QUICK_MOVE);
        this.onMouseClick(null, 1, 0, SlotActionType.QUICK_MOVE);

        ((AutoTrade)this).trade(field_19161);
    }
}
