/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.guntram.mcmod.easiervillagertrading;

import java.io.IOException;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;

/**
 *
 * @author gbl
 */
public class BetterGuiMerchant extends GuiMerchant {
    
    private final int addXSize=0;
    private final ItemStack tradeOK, tradeNOK;
    
    BetterGuiMerchant (InventoryPlayer inv, GuiMerchant template, World world) {
        super(inv, template.getMerchant(), world);
        this.xSize+=addXSize;
        tradeOK=new ItemStack(Item.getItemById(351), 1, 2);
        tradeNOK=new ItemStack(Item.getItemById(351), 1, 1);
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        MerchantRecipeList trades=getMerchant().getRecipes(null);
        if (trades==null)
            return;
        String s = trades.size()+" trades";
        this.fontRenderer.drawString(s, this.xSize-addXSize+5, 0, 0xff00ff);
        // First draw all items, then all tooltips. This is extra effort,
        // but we don't want any items in front of any tooltips.
        for (int i=0; i<trades.size(); i++) {
            MerchantRecipe trade=trades.get(i);
            ItemStack i1=trade.getItemToBuy();
            ItemStack i2=trade.hasSecondItemToBuy() ? trade.getSecondItemToBuy() : null;
            ItemStack o1=trade.getItemToSell();
            drawItem(i1, this.xSize-addXSize+5,    i*18+20);
            drawItem(i2, this.xSize-addXSize+5+18, i*18+20);
            drawItem(o1, this.xSize-addXSize+5+60, i*18+20);

            NBTTagList enchantments;
            
            if (o1.getItem() instanceof ItemEnchantedBook) {
                enchantments=((ItemEnchantedBook)(o1.getItem())).getEnchantments(o1);
            } else {
                enchantments=o1.getEnchantmentTagList();
            }
            if (enchantments != null)
            {
                StringBuilder enchants=new StringBuilder();
                for (int t = 0; t < enchantments.tagCount(); ++t)
                {
                    int j = enchantments.getCompoundTagAt(t).getShort("id");
                    int k = enchantments.getCompoundTagAt(t).getShort("lvl");

                    if (Enchantment.getEnchantmentByID(j) != null)
                    {
                        if (t>0)
                            enchants.append(", ");
                        enchants.append(Enchantment.getEnchantmentByID(j).getTranslatedName(k));
                    }
                }
                fontRenderer.drawString(enchants.toString(), this.xSize-addXSize+85, i*18+24, 0xffff00);
            }
            drawItem(trade.isRecipeDisabled() ? tradeNOK : tradeOK, xSize-addXSize+5+40, i*18+20);
        }
        for (int i=0; i<trades.size(); i++) {
            MerchantRecipe trade=trades.get(i);
            ItemStack i1=trade.getItemToBuy();
            ItemStack i2=trade.hasSecondItemToBuy() ? trade.getSecondItemToBuy() : null;
            ItemStack o1=trade.getItemToSell();
            drawTooltip(i1, this.xSize-addXSize+5,    i*18+20, mouseX, mouseY);
            drawTooltip(i2, this.xSize-addXSize+5+18, i*18+20, mouseX, mouseY);
            drawTooltip(o1, this.xSize-addXSize+5+54, i*18+20, mouseX, mouseY);
        }
    }
    
    private void drawItem(ItemStack stack, int x, int y) {
        if (stack==null)
            return;
        itemRender.renderItemAndEffectIntoGUI(stack, x, y);
        itemRender.renderItemOverlays(fontRenderer, stack, x, y);
    }
    
    private void drawTooltip(ItemStack stack, int x, int y, int mousex, int mousey) {
        if (stack==null)
            return;
        mousex-=guiLeft;
        mousey-=guiTop;
        if (mousex>=x && mousex<=x+16 && mousey>=y && mousey<=y+16)
            renderToolTip(stack, mousex, mousey);
    }

    @Override
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
        System.out.println("click at "+mouseX+"/"+mouseY);
        if ((mouseX - this.guiLeft) > this.xSize-addXSize  && (mouseX - this.guiLeft) < this.xSize-addXSize+80) {
            int tradeIndex=(mouseY-this.guiTop-20)/18;
            MerchantRecipeList trades=getMerchant().getRecipes(null);
            int numTrades=trades.size();
            if (tradeIndex>=0 && tradeIndex<numTrades) {
                System.out.println("tradeIndex="+tradeIndex+", numTrades="+numTrades);
                GuiButton myNextButton = this.buttonList.get(0);
                GuiButton myPrevButton = this.buttonList.get(1);
                for (int i=0; i<numTrades; i++)
                    this.actionPerformed(myPrevButton);
                for (int i=0; i<tradeIndex; i++)
                    this.actionPerformed(myNextButton);
                MerchantRecipe recipe=trades.get(tradeIndex);
                if (!recipe.isRecipeDisabled()
                &&  inputSlotsAreEmpty()
                &&  hasEnoughItemsInInventory(recipe)
                &&  canReceiveOutput(recipe.getItemToSell())) {
                    transact(recipe);
                }
                
            }
        } else {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }
    
    private boolean inputSlotsAreEmpty() {
        return
            inventorySlots.getSlot(0).getHasStack() == false
        &&  inventorySlots.getSlot(1).getHasStack() == false
        &&  inventorySlots.getSlot(2).getHasStack() == false;
               
    }

    private boolean hasEnoughItemsInInventory(MerchantRecipe recipe) {
        if (!hasEnoughItemsInInventory(recipe.getItemToBuy()))
            return false;
        if (recipe.hasSecondItemToBuy() && !hasEnoughItemsInInventory(recipe.getSecondItemToBuy()))
            return false;
        return true;
    }
    
    private boolean hasEnoughItemsInInventory(ItemStack stack) {
        int remaining=stack.getCount();
        for (int i=inventorySlots.inventorySlots.size()-36; i<inventorySlots.inventorySlots.size(); i++) {
            ItemStack invstack=inventorySlots.getSlot(i).getStack();
            if (invstack==null)
                continue;
            if (stack.getItem().equals(invstack.getItem())) {
                System.out.println("taking "+invstack.getCount()+" items from slot # "+i);
                remaining-=invstack.getCount();
            }
            if (remaining<=0)
                return true;
        }
        return false;
    }

    private boolean canReceiveOutput(ItemStack stack) {
        int remaining=stack.getCount();
        for (int i=inventorySlots.inventorySlots.size()-36; i<inventorySlots.inventorySlots.size(); i++) {
            ItemStack invstack=inventorySlots.getSlot(i).getStack();
            if (invstack==null || invstack.isEmpty()) {
                System.out.println("can put result into empty slot "+i);
                return true;
            }
            if (stack.getItem().equals(invstack.getItem())) {
                System.out.println("Can merge "+(invstack.getMaxStackSize()-invstack.getCount())+" items with slot "+i);
                remaining-=(invstack.getMaxStackSize()-invstack.getCount());
            }
            if (remaining<=0)
                return true;
        }
        return false;
    }
    
    private void transact(MerchantRecipe recipe) {
        System.out.println("fill input slots called");
        int putback0=-1, putback1=-1;
        putback0=fillSlot(0, recipe.getItemToBuy());
        if (recipe.hasSecondItemToBuy()) {
            putback1=fillSlot(1, recipe.getSecondItemToBuy());
        }
        getslot(2, recipe.getItemToSell(), putback0, putback1);
        System.out.println("putting back to slot "+putback0+" from 0, and to "+putback1+"from 1");
        if (putback0!=-1) {
            mc.playerController.windowClick(mc.player.openContainer.windowId, 0, 0, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(mc.player.openContainer.windowId, putback0, 0, ClickType.PICKUP, mc.player);
        }
        if (putback1!=-1) {
            mc.playerController.windowClick(mc.player.openContainer.windowId, 1, 0, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(mc.player.openContainer.windowId, putback1, 0, ClickType.PICKUP, mc.player);
        }
    }

    /**
     * 
     * @param slot - the number of the (trading) slot that should receive items
     * @param stack - what the trading slot should receive
     * @return the number of the inventory slot into which these items should be put back
     * after the transaction. May be -1 if nothing needs to be put back.
     */
    private int fillSlot(int slot, ItemStack stack) {
        int remaining=stack.getCount();
        for (int i=inventorySlots.inventorySlots.size()-36; i<inventorySlots.inventorySlots.size(); i++) {
            ItemStack invstack=inventorySlots.getSlot(i).getStack();
            if (invstack==null)
                continue;
            boolean needPutBack=false;
            if (stack.getItem().equals(invstack.getItem())) {
                if (stack.getCount()+invstack.getCount() > stack.getMaxStackSize())
                    needPutBack=true;
                remaining-=invstack.getCount();
                System.out.println("taking "+invstack.getCount()+" items from slot # "+i+", remaining is now "+remaining);
                mc.playerController.windowClick(mc.player.openContainer.windowId, i, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(mc.player.openContainer.windowId, slot, 0, ClickType.PICKUP, mc.player);
            }
            if (needPutBack) {
                mc.playerController.windowClick(mc.player.openContainer.windowId, i, 0, ClickType.PICKUP, mc.player);
            }
            if (remaining<=0)
                return remaining<0 ? i : -1;
        }
        // We should not be able to arrive here, since hasEnoughItemsInInventory should have been
        // called before fillSlot. But if we do, something went wrong; in this case better do a bit less.
        return -1;
    }
    
    private void getslot(int slot, ItemStack stack, int... forbidden) {
        int remaining=stack.getCount();
        mc.playerController.windowClick(mc.player.openContainer.windowId, slot, 0, ClickType.PICKUP, mc.player);
        for (int i=inventorySlots.inventorySlots.size()-36; i<inventorySlots.inventorySlots.size(); i++) {
            ItemStack invstack=inventorySlots.getSlot(i).getStack();
            if (invstack==null || invstack.isEmpty()) {
                continue;
            }
            if (stack.getItem().equals(invstack.getItem())) {
                System.out.println("Can merge "+(invstack.getMaxStackSize()-invstack.getCount())+" items with slot "+i);
                remaining-=(invstack.getMaxStackSize()-invstack.getCount());
                mc.playerController.windowClick(mc.player.openContainer.windowId, i, 0, ClickType.PICKUP, mc.player);
            }
            if (remaining<=0)
                return;
        }
        
        // When looking for an empty slot, don't take one that we want to put some input back to.
        for (int i=inventorySlots.inventorySlots.size()-36; i<inventorySlots.inventorySlots.size(); i++) {
            boolean isForbidden=false;
            for (int f:forbidden) {
                if (i==f)
                    isForbidden=true;
            }
            if (isForbidden)
                continue;
            ItemStack invstack=inventorySlots.getSlot(i).getStack();
            if (invstack==null || invstack.isEmpty()) {
                mc.playerController.windowClick(mc.player.openContainer.windowId, i, 0, ClickType.PICKUP, mc.player);
                System.out.println("putting result into empty slot "+i);
                return;
            }
        }
    }
}
