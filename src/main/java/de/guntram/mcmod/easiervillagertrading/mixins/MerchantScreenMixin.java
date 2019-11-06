/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.guntram.mcmod.easiervillagertrading.mixins;

import de.guntram.mcmod.easiervillagertrading.MerchantScreenExporter;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.container.MerchantContainer;
import net.minecraft.container.SlotActionType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.server.network.packet.SelectVillagerTradeC2SPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MerchantScreen.class)
public abstract class MerchantScreenMixin extends AbstractContainerScreen<MerchantContainer> implements MerchantScreenExporter {
    
    @Shadow private int field_19161;

    public MerchantScreenMixin(MerchantContainer merchantContainer_1, PlayerInventory playerInventory_1, Text text_1) {
        super(merchantContainer_1, playerInventory_1, text_1);
    }

    @Override
    public void setRecipeIndex(int i) {
        this.field_19161 = i;
        ((MerchantContainer)container).setRecipeIndex(this.field_19161);
        // We don't really want this switch, but the server does it as well,
        // so we need to do this here and cancel the results.
        ((MerchantContainer)container).switchTo(this.field_19161);
        this.minecraft.getNetworkHandler().sendPacket(new SelectVillagerTradeC2SPacket(this.field_19161));
        this.onMouseClick(null, 0, 0, SlotActionType.QUICK_MOVE);
        this.onMouseClick(null, 1, 0, SlotActionType.QUICK_MOVE);
    }
    
    @Override
    public void renderNoTrades(int a, int b, float f) {
        super.render(a, b, f);
    };
}
