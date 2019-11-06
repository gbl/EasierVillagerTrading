/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.guntram.mcmod.easiervillagertrading;

import com.mojang.blaze3d.platform.GlStateManager;
import de.guntram.mcmod.debug.NBTdump;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.container.MerchantContainer;
import net.minecraft.container.SlotActionType;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TraderOfferList;

/**
 *
 * @author gbl
 */
public class BetterGuiMerchant extends MerchantScreen {
    
    private int xBase=0;
    private int scrollCount=0;
    private final int lineHeight=18;
    private final int titleDistance=20;
    private final int firstBuyItemXpos=0;
    private final int secondBuyItemXpos=18;
    private final int okNokXpos=40;
    private final int sellItemXpos=60;
    private final int textXpos=85;
    private static final Identifier icons=new Identifier(EasierVillagerTrading.MODID, "textures/icons.png");

    private int frames;     //DEBUG
    
    public BetterGuiMerchant (MerchantContainer container, PlayerInventory inv, Text title) {
        super(container, inv, title);
        if (ConfigurationHandler.showLeft()) {
            xBase=-ConfigurationHandler.leftPixelOffset();
            if (xBase==0)
                xBase=-this.containerWidth;
        }
        else {
            xBase=this.containerWidth+5;
        }
        frames=0; //DEBUG
    }

    @Override
    protected void init() {
        super.init();
        if (ConfigurationHandler.suppressOriginalGUI()) {
            this.buttons.clear();
        }
    }
    
    @Override
    public void render(int a, int b, float f) {
        if (ConfigurationHandler.suppressOriginalGUI()) {
            this.renderBackground();
            ((MerchantScreenExporter)this).renderNoTrades(a, b, f);     // basically super.super.render()
            drawMouseoverTooltip(a, b);
        } else {
            super.render(a, b, f);
        }
    }

    @Override
    protected void drawForeground(int mouseX, int mouseY)
    {
        if (++frames%300==0) {
//            System.out.println("drawForegroundLayer");
        }
        super.drawForeground(mouseX, mouseY);
        TraderOfferList trades=container.getRecipes();
        if (trades==null)
            return;
        int topAdjust=getTopAdjust(trades.size());
        String s = trades.size()+" trades";
        this.font.draw(s, xBase+okNokXpos, -topAdjust, 0xff00ff);
        if (frames%300==0) { // DEBUG
//            System.out.println("drawing "+s+" at "+xBase+"/"+(-topAdjust)); //DEBUG
        } //DEBUG
        // First draw all items, then all tooltips. This is extra effort,
        // but we don't want any items in front of any tooltips.

        GuiLighting.enableForItems();
        for (int i=0; i<trades.size()-scrollCount; i++) {
            TradeOffer trade = trades.get(i+scrollCount);
            ItemStack i1=trade.getAdjustedFirstBuyItem();
            ItemStack i2=trade.getSecondBuyItem();
            ItemStack o1=trade.getSellItem();
            if (frames%300==0) { //DEBUG
                  System.out.println("second item is "+i2.getTranslationKey());
//                System.out.println("drawing items at "+(xBase+firstBuyItemXpos)+ "/"+(i*lineHeight-topAdjust+titleDistance)); //DEBUG
            } //DEBUG
            drawItem(i1, xBase+firstBuyItemXpos,  i*lineHeight-topAdjust+titleDistance);
            drawItem(i2, xBase+secondBuyItemXpos, i*lineHeight-topAdjust+titleDistance);
            drawItem(o1, xBase+sellItemXpos,      i*lineHeight-topAdjust+titleDistance);

            ListTag enchantments;
            
            if (o1.getItem() instanceof EnchantedBookItem) {
                enchantments=EnchantedBookItem.getEnchantmentTag(o1);
            } else {
                enchantments=o1.getEnchantments();
            }
            if (enchantments != null)
            {
                StringBuilder enchants=new StringBuilder();
                for (int t = 0; t < enchantments.size(); ++t)
                {
                    CompoundTag singleTag = enchantments.getCompoundTag(t);
                    if (frames%300==0) {
                        NBTdump.dump(singleTag, 0);
                    }
                    String name = singleTag.getString("id");
                    int level = singleTag.getShort("lvl");
                    
                    Enchantment enchant = Registry.ENCHANTMENT.get(new Identifier(name));
                    if (enchant != null)
                    {
                        if (t>0)
                            enchants.append(", ");
                        enchants.append(enchant.getName(level).asFormattedString());
                    }
                }
                String shownEnchants=enchants.toString();
                if (xBase<0)
                    shownEnchants=font.trimToWidth(shownEnchants, -xBase-textXpos-5);
                
                if (frames%300==0) { //DEBUG
//                    System.out.println("Enchant"+shownEnchants+" at "+(xBase+firstBuyItemXpos)+ "/"+(i*lineHeight-topAdjust+titleDistance)); //DEBUG
                } //DEBUG
                font.draw(shownEnchants, xBase+textXpos, i*lineHeight-topAdjust+24, 0xffff00);
            }
        }
        GuiLighting.disable();

        GlStateManager.color4f(1f, 1f, 1f, 1f);               // needed so items don't get a text color overlay
        GlStateManager.enableBlend();
        this.minecraft.getTextureManager().bindTexture(icons);     // arrows; use standard item lighting for them so we need a separate loop
        int arrowX=xBase+okNokXpos;
        int[] tradeState=new int[trades.size()];
        for (int i=0; i<trades.size()-scrollCount; i++) {
            int y=i*lineHeight-topAdjust+titleDistance;
            TradeOffer trade = trades.get(i+scrollCount);
            if (!trade.isDisabled()
                &&  inputSlotsAreEmpty()
                &&  hasEnoughItemsInInventory(trade)
                &&  canReceiveOutput(trade.getSellItem())) {
                    this.blit(arrowX, y, 6*18, 2*18, 18, 18);   // green arrow right
                    tradeState[i]=0;
            } else if (!trade.isDisabled()) {
                this.blit(arrowX, y, 5*18, 3*18, 18, 18);       // empty arrow right
                tradeState[i]=1;
            } else {
                this.blit(arrowX, y, 12*18, 3*18, 18, 18);      // red X
                tradeState[i]=2;
            }
        }
        
        if (scrollCount>0) {
            this.blit(xBase+firstBuyItemXpos, -topAdjust-3, 9*18, 2*18, 18,18);
        }
        if ((trades.size()-1-scrollCount)*lineHeight + titleDistance*2 >= height) {
            if (frames%300==0) { //DEBUG
//                    System.out.println("size="+trades.size()+
//                            ", scrolled by "+scrollCount+
//                            ", including scoll="+(trades.size()+1-scrollCount)+
//                            ", needed = "+((trades.size()+1-scrollCount)*lineHeight + titleDistance*2)+
//                            ", compared to height "+height
//                            );
            } //DEBUG
            this.blit(xBase+secondBuyItemXpos, -topAdjust-3, 1*18, 2*18, 18,18);
        }

// tooltips after textures as font rendering resets the texture
        for (int i=0; i<trades.size(); i++) {
            int y=i*lineHeight-topAdjust+titleDistance;
            TradeOffer trade = trades.get(i);
            ItemStack i1=trade.getAdjustedFirstBuyItem();
            ItemStack i2=trade.getSecondBuyItem();
            ItemStack o1=trade.getSellItem();
            drawTooltip(i1, xBase+firstBuyItemXpos,    y, mouseX, mouseY);
            drawTooltip(i2, xBase+secondBuyItemXpos,   y, mouseX, mouseY);
            drawTooltip(o1, xBase+sellItemXpos,        y, mouseX, mouseY);
            switch (tradeState[i]) {
                case 0: this.drawTooltip(I18n.translate("msg.cantrade", (Object[]) null), arrowX, y, mouseX, mouseY); break;
                case 1: this.drawTooltip(I18n.translate("msg.notradeinv", (Object[]) null), arrowX, y, mouseX, mouseY); break;
                case 2: this.drawTooltip(I18n.translate("msg.tradelocked", (Object[]) null), arrowX, y, mouseX, mouseY);
            }
        }
    }
    
    private int getTopAdjust(int numTrades) {
        int topAdjust = ((numTrades * lineHeight + titleDistance) - this.containerWidth)/2;
        if (topAdjust < 0)
            topAdjust = 0;
        if (topAdjust > this.top - this.titleDistance/2)
            topAdjust = this.left - this.titleDistance/2;
        return topAdjust;
    }
    
    private void drawItem(ItemStack stack, int x, int y) {
        if (stack==null)
            return;
        itemRenderer.renderGuiItem(stack, x, y);
        itemRenderer.renderGuiItemOverlay(font, stack, x, y);
    }
    
    private void drawTooltip(ItemStack stack, int x, int y, int mousex, int mousey) {
        if (stack==null)
            return;
        mousex-=left;
        mousey-=top;
        if (mousex>=x && mousex<=x+16 && mousey>=y && mousey<=y+16)
            renderTooltip(stack, mousex, mousey);
    }

    private void drawTooltip(String s, int x, int y, int mousex, int mousey) {
        mousex-=left;
        mousey-=top;
        if (mousex>=x && mousex<=x+16 && mousey>=y && mousey<=y+16)
            renderComponentHoverEffect(new LiteralText(s), mousex, mousey);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, final int mouseButton) {
        // System.out.println("click at "+mouseX+"/"+mouseY);
        if (mouseButton==0
        &&  (mouseX - this.left) >= xBase
        &&  (mouseX - this.left) <= xBase+textXpos
        ) {
            TraderOfferList trades=container.getRecipes();
            if (trades==null)
                return false;
            int numTrades=trades.size();
            int topAdjust=getTopAdjust(numTrades);
            int yPixel=(int)mouseY+topAdjust-this.top-titleDistance;
            if (yPixel>=0) {
                int tradeIndex=(yPixel)/lineHeight+scrollCount;
                if (tradeIndex>=0 && tradeIndex<numTrades) {
                    ((MerchantScreenExporter)this).setRecipeIndex(tradeIndex);
                    TradeOffer recipe = trades.get(tradeIndex);
                    while (!recipe.isDisabled()
                    &&  inputSlotsAreEmpty()
                    &&  hasEnoughItemsInInventory(recipe)
                    &&  canReceiveOutput(recipe.getSellItem())) {
                        transact(recipe);
                        if (!hasShiftDown()) {
                            break;
                        }
                    }
                    return true;
                }
            } else {
                System.out.println("yPixel="+yPixel);
                if (mouseX - this.left < xBase+secondBuyItemXpos) {
                    mouseScrolled(1.0, 0, 0);
                }
                else if (mouseX - this.left < xBase+okNokXpos) {
                    mouseScrolled(-1.0, 0, 0);
                }
            }
        } else {
            return super.mouseClicked(mouseX, mouseY, mouseButton);
        }
        return false;
    }
    
    @Override
    public boolean mouseScrolled(double delta, double x, double y) {
        // System.out.println("scrolled by "+delta);
        TraderOfferList trades=container.getRecipes();
        if ((trades=container.getRecipes())!=null) {
            scrollCount-=(int)delta;
            while ((trades.size()-scrollCount)*lineHeight + titleDistance*2 < height) {
                scrollCount--;
            }
            if (scrollCount<0)
                scrollCount=0;
        }
        return true;
    }
    
    private boolean inputSlotsAreEmpty() {
        boolean result =
            container.getSlot(0).getStack().isEmpty()
        &&  container.getSlot(1).getStack().isEmpty()
        &&  container.getSlot(2).getStack().isEmpty();
        if (frames % 300 == 0) {
            System.out.println("stack 0: "+container.getSlot(0).getStack().getTranslationKey()+"/"+container.getSlot(0).getStack().getCount());
            System.out.println("stack 1: "+container.getSlot(1).getStack().getTranslationKey()+"/"+container.getSlot(0).getStack().getCount());
            System.out.println("stack 2: "+container.getSlot(2).getStack().getTranslationKey()+"/"+container.getSlot(0).getStack().getCount());
            System.out.println("result = "+result);
        }
        return result;
               
    }

    private boolean hasEnoughItemsInInventory(TradeOffer recipe) {
        if (!hasEnoughItemsInInventory(recipe.getAdjustedFirstBuyItem()))
            return false;
        if (!hasEnoughItemsInInventory(recipe.getSecondBuyItem()))
            return false;
        return true;
    }
    
    private boolean hasEnoughItemsInInventory(ItemStack stack) {
        int remaining=stack.getCount();
        for (int i=container.slotList.size()-36; i<container.slotList.size(); i++) {
            ItemStack invstack=container.getSlot(i).getStack();
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
        for (int i=container.slotList.size()-36; i<container.slotList.size(); i++) {
            ItemStack invstack=container.getSlot(i).getStack();
            if (invstack==null || invstack.isEmpty()) {
                //System.out.println("can put result into empty slot "+i);
                return true;
            }
            if (areItemStacksMergable(stack, invstack)
            &&  stack.getMaxCount() >= stack.getCount() + invstack.getCount()) {
                //System.out.println("Can merge "+(invstack.getMaxStackSize()-invstack.getCount())+" items with slot "+i);
                remaining-=(invstack.getMaxCount()-invstack.getCount());
            }
            if (remaining<=0)
                return true;
        }
        return false;
    }
    
    private void transact(TradeOffer recipe) {
        //System.out.println("fill input slots called");
        int putback0, putback1=-1;
        putback0=fillSlot(0, recipe.getAdjustedFirstBuyItem());
        putback1=fillSlot(1, recipe.getSecondBuyItem());

        getslot(2, recipe.getSellItem(), putback0, putback1);
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
        for (int i=container.slotList.size()-36; i<container.slotList.size(); i++) {
            ItemStack invstack=container.getSlot(i).getStack();
            if (invstack==null)
                continue;
            boolean needPutBack=false;
            if (areItemStacksMergable(stack, invstack)) {
                if (stack.getCount()+invstack.getCount() > stack.getMaxCount())
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
        &&  (!a.isDamageable() || a.getDamage()==b.getDamage())
        &&   ItemStack.areTagsEqual(a, b))
            return true;
        return false;
    }
    
    private void getslot(int slot, ItemStack stack, int... forbidden) {
        int remaining=stack.getCount();
        slotClick(slot);
        for (int i=container.slotList.size()-36; i<container.slotList.size(); i++) {
            ItemStack invstack=container.getSlot(i).getStack();
            if (invstack==null || invstack.isEmpty()) {
                continue;
            }
            if (areItemStacksMergable(stack, invstack)
                && invstack.getCount() < invstack.getMaxCount()
            ) {
                // System.out.println("Can merge "+(invstack.getMaxStackSize()-invstack.getCount())+" items with slot "+i);
                remaining-=(invstack.getMaxCount()-invstack.getCount());
                slotClick(i);
            }
            if (remaining<=0)
                return;
        }
        
        // When looking for an empty slot, don't take one that we want to put some input back to.
        for (int i=container.slotList.size()-36; i<container.slotList.size(); i++) {
            boolean isForbidden=false;
            for (int f:forbidden) {
                if (i==f)
                    isForbidden=true;
            }
            if (isForbidden)
                continue;
            ItemStack invstack=container.getSlot(i).getStack();
            if (invstack==null || invstack.isEmpty()) {
                slotClick(i);
                // System.out.println("putting result into empty slot "+i);
                return;
            }
        }
    }
    
    private void slotClick(int slot) {
        // System.out.println("Clicking slot "+slot);
        this.onMouseClick(null, slot, 0, SlotActionType.PICKUP);
    }
}
