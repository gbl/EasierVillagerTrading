/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.guntram.mcmod.easiervillagertrading;

import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;

/**
 *
 * @author gbl
 */
public class BetterGuiMerchant extends GuiMerchant {
    
    private int xBase=0;
    private final int lineHeight=18;
    private final int titleDistance=20;
    private final int firstBuyItemXpos=0;
    private final int secondBuyItemXpos=18;
    private final int okNokXpos=40;
    private final int sellItemXpos=60;
    private final int textXpos=85;
    private static final ResourceLocation icons=new ResourceLocation(EasierVillagerTrading.MODID, "textures/icons.png");
    
    BetterGuiMerchant (InventoryPlayer inv, GuiMerchant template, World world) {
        super(inv, template.getMerchant(), world);
        if (ConfigurationHandler.showLeft()) {
            xBase=-ConfigurationHandler.leftPixelOffset();
            if (xBase==0)
                xBase=-this.getXSize();
        }
        else
            xBase=this.getXSize()+5;
        System.out.println("icons="+icons);
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
        int topAdjust=getTopAdjust(trades.size());
        String s = trades.size()+" trades";
        this.fontRenderer.drawString(s, xBase, -topAdjust, 0xff00ff);
        // First draw all items, then all tooltips. This is extra effort,
        // but we don't want any items in front of any tooltips.

        RenderHelper.enableGUIStandardItemLighting();
        for (int i=0; i<trades.size(); i++) {
            MerchantRecipe trade=trades.get(i);
            ItemStack i1=trade.getItemToBuy();
            ItemStack i2=trade.hasSecondItemToBuy() ? trade.getSecondItemToBuy() : null;
            ItemStack o1=trade.getItemToSell();
            drawItem(i1, xBase+firstBuyItemXpos,  i*lineHeight-topAdjust+titleDistance);
            drawItem(i2, xBase+secondBuyItemXpos, i*lineHeight-topAdjust+titleDistance);
            drawItem(o1, xBase+sellItemXpos,      i*lineHeight-topAdjust+titleDistance);

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

                    Enchantment enchant = Enchantment.getEnchantmentByID(j);
                    if (enchant != null)
                    {
                        if (t>0)
                            enchants.append(", ");
                        enchants.append(enchant.getTranslatedName(k));
                    }
                }
                String shownEnchants=enchants.toString();
                if (xBase<0)
                    shownEnchants=fontRenderer.trimStringToWidth(shownEnchants, -xBase-textXpos-5);
                fontRenderer.drawString(shownEnchants, xBase+textXpos, i*lineHeight-topAdjust+24, 0xffff00);
            }
        }
        RenderHelper.disableStandardItemLighting();

        GlStateManager.color(1f, 1f, 1f, 1f);               // needed so items don't get a text color overlay
        GlStateManager.enableBlend();
        this.mc.getTextureManager().bindTexture(icons);     // arrows; use standard item lighting for them so we need a separate loop
        for (int i=0; i<trades.size(); i++) {
            MerchantRecipe trade=trades.get(i);        
            if (!trade.isRecipeDisabled()
                &&  inputSlotsAreEmpty()
                &&  hasEnoughItemsInInventory(trade)
                &&  canReceiveOutput(trade.getItemToSell())) {
                    this.drawTexturedModalRect(xBase+okNokXpos, i*lineHeight-topAdjust+titleDistance, 6*18, 2*18, 18, 18);   // green arrow right
            } else if (!trade.isRecipeDisabled()) {
                this.drawTexturedModalRect(xBase+okNokXpos, i*lineHeight-topAdjust+titleDistance, 5*18, 3*18, 18, 18);       // empty arrow right
            } else {
                this.drawTexturedModalRect(xBase+okNokXpos, i*lineHeight-topAdjust+titleDistance, 12*18, 3*18, 18, 18);      // red X
            }
        }

// tooltips        
        for (int i=0; i<trades.size(); i++) {
            MerchantRecipe trade=trades.get(i);
            ItemStack i1=trade.getItemToBuy();
            ItemStack i2=trade.hasSecondItemToBuy() ? trade.getSecondItemToBuy() : null;
            ItemStack o1=trade.getItemToSell();
            drawTooltip(i1, xBase+firstBuyItemXpos,    i*lineHeight-topAdjust+titleDistance, mouseX, mouseY);
            drawTooltip(i2, xBase+secondBuyItemXpos,   i*lineHeight-topAdjust+titleDistance, mouseX, mouseY);
            drawTooltip(o1, xBase+sellItemXpos,        i*lineHeight-topAdjust+titleDistance, mouseX, mouseY);
        }
    }
    
    private int getTopAdjust(int numTrades) {
        int topAdjust = ((numTrades * lineHeight + titleDistance) - this.ySize)/2;
        if (topAdjust < 0)
            topAdjust = 0;
        return topAdjust;
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
        // System.out.println("click at "+mouseX+"/"+mouseY);
        if (mouseButton==0
        &&  (mouseX - this.guiLeft) >= xBase
        &&  (mouseX - this.guiLeft) <= xBase+textXpos
        ) {
            MerchantRecipeList trades=getMerchant().getRecipes(null);
            if (trades==null)
                return;
            int numTrades=trades.size();
            int topAdjust=getTopAdjust(numTrades);
            int tradeIndex=(mouseY+topAdjust-this.guiTop-titleDistance)/lineHeight;
            if (tradeIndex>=0 && tradeIndex<numTrades) {
                // System.out.println("tradeIndex="+tradeIndex+", numTrades="+numTrades);
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
            if (areItemStacksMergable(stack, invstack)) {
                //System.out.println("taking "+invstack.getCount()+" items from slot # "+i);
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
                //System.out.println("can put result into empty slot "+i);
                return true;
            }
            if (areItemStacksMergable(stack, invstack)
            &&  stack.getMaxStackSize() >= stack.getCount() + invstack.getCount()) {
                //System.out.println("Can merge "+(invstack.getMaxStackSize()-invstack.getCount())+" items with slot "+i);
                remaining-=(invstack.getMaxStackSize()-invstack.getCount());
            }
            if (remaining<=0)
                return true;
        }
        return false;
    }
    
    private void transact(MerchantRecipe recipe) {
        //System.out.println("fill input slots called");
        int putback0, putback1=-1;
        putback0=fillSlot(0, recipe.getItemToBuy());
        if (recipe.hasSecondItemToBuy()) {
            putback1=fillSlot(1, recipe.getSecondItemToBuy());
        }
        getslot(2, recipe.getItemToSell(), putback0, putback1);
        //System.out.println("putting back to slot "+putback0+" from 0, and to "+putback1+"from 1");
        if (putback0!=-1) {
            slotClick(0);
            slotClick(putback0);
        }
        if (putback1!=-1) {
            slotClick(1);
            slotClick(putback1);
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
            if (areItemStacksMergable(stack, invstack)) {
                if (stack.getCount()+invstack.getCount() > stack.getMaxStackSize())
                    needPutBack=true;
                remaining-=invstack.getCount();
                // System.out.println("taking "+invstack.getCount()+" items from slot # "+i+", remaining is now "+remaining);
                slotClick(i);
                slotClick(slot);
            }
            if (needPutBack) {
                slotClick(i);
            }
            if (remaining<=0)
                return remaining<0 ? i : -1;
        }
        // We should not be able to arrive here, since hasEnoughItemsInInventory should have been
        // called before fillSlot. But if we do, something went wrong; in this case better do a bit less.
        return -1;
    }
    
    private boolean areItemStacksMergable(ItemStack a, ItemStack b) {
        if (a==null || b==null)
            return false;
        if (a.getItem() == b.getItem()
        &&  (!a.getHasSubtypes() || a.getItemDamage()==b.getItemDamage())
        &&   ItemStack.areItemStackTagsEqual(a, b))
            return true;
        return false;
    }
    
    private void getslot(int slot, ItemStack stack, int... forbidden) {
        int remaining=stack.getCount();
        slotClick(slot);
        for (int i=inventorySlots.inventorySlots.size()-36; i<inventorySlots.inventorySlots.size(); i++) {
            ItemStack invstack=inventorySlots.getSlot(i).getStack();
            if (invstack==null || invstack.isEmpty()) {
                continue;
            }
            if (areItemStacksMergable(stack, invstack)
                && invstack.getCount() < invstack.getMaxStackSize()
            ) {
                // System.out.println("Can merge "+(invstack.getMaxStackSize()-invstack.getCount())+" items with slot "+i);
                remaining-=(invstack.getMaxStackSize()-invstack.getCount());
                slotClick(i);
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
                slotClick(i);
                // System.out.println("putting result into empty slot "+i);
                return;
            }
        }
    }
    
    private void slotClick(int slot) {
        // System.out.println("Clicking slot "+slot);
        mc.playerController.windowClick(mc.player.openContainer.windowId, slot, 0, ClickType.PICKUP, mc.player);
    }
}
