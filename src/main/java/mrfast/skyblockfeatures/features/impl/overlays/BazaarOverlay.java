package mrfast.skyblockfeatures.features.impl.overlays;

import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.events.GuiContainerEvent;
import mrfast.skyblockfeatures.events.GuiContainerEvent.DrawSlotEvent;
import mrfast.skyblockfeatures.features.impl.handlers.AuctionData;
import mrfast.skyblockfeatures.features.impl.misc.ItemFeatures;
import mrfast.skyblockfeatures.utils.ItemUtil;
import mrfast.skyblockfeatures.utils.NumberUtil;
import mrfast.skyblockfeatures.utils.Utils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import java.awt.Color;
import java.text.NumberFormat;
import java.util.HashMap;

import com.mojang.realmsclient.gui.ChatFormatting;

public class BazaarOverlay {
    @SubscribeEvent
    public void onDrawSlots(GuiContainerEvent.DrawSlotEvent.Pre event) {
        if (event.gui instanceof GuiChest && skyblockfeatures.config.bazaarFlip) {
            GuiChest gui = (GuiChest) event.gui;
            ContainerChest chest = (ContainerChest) gui.inventorySlots;
            IInventory inv = chest.getLowerChestInventory();
            String chestName = inv.getDisplayName().getUnformattedText().trim();
            if (chestName.contains("➜") && event.slot.getHasStack()) {
                ItemStack stack = event.slot.getStack();
                String EstimatedItemID = Utils.cleanColour(stack.getDisplayName()).toUpperCase().replaceAll(" ", "_");
                Double buyPrice = 0d;
                Double npcPrice = ItemFeatures.sellPrices.get(EstimatedItemID);
                for(String line:ItemUtil.getItemLore(stack)) {
                    line = Utils.cleanColour(line);
                    if(line.contains("Buy")) {
                        String line2 = line.replaceAll("[^0-9]", "");
                        if(line2.isEmpty()) continue;
                        buyPrice = Double.parseDouble(line2);
                        if(line.contains(".")) buyPrice/=10;
                        buyPrice*=.975;
                        break;
                    }
                }
                if(npcPrice==null || buyPrice == 0) return;
                if(buyPrice<npcPrice) {
                    int x = event.slot.xDisplayPosition;
                    int y = event.slot.yDisplayPosition;
                    Gui.drawRect(x, y, x + 16, y + 16, new Color(85, 255, 85).getRGB());
                }
            }
        }
    }
    @SubscribeEvent
    public void onTooltip(ItemTooltipEvent event) {
        if (Utils.GetMC().currentScreen instanceof GuiChest && skyblockfeatures.config.bazaarFlip) {
            GuiChest gui = (GuiChest) Utils.GetMC().currentScreen;
            ContainerChest chest = (ContainerChest) gui.inventorySlots;
            IInventory inv = chest.getLowerChestInventory();
            String chestName = inv.getDisplayName().getUnformattedText().trim();
            
            if (chestName.contains("➜") && event.itemStack!=null) {
                ItemStack stack = event.itemStack;
                String EstimatedItemID = Utils.cleanColour(stack.getDisplayName()).toUpperCase().replaceAll(" ", "_");
                Double buyPrice = 0d;
                Double npcPrice = ItemFeatures.sellPrices.get(EstimatedItemID);
                for(String line:ItemUtil.getItemLore(stack)) {
                    line = Utils.cleanColour(line);
                    if(line.contains("Buy")) {
                        String line2 = line.replaceAll("[^0-9]", "");
                        if(line2.isEmpty()) continue;
                        buyPrice = Double.parseDouble(line2);
                        if(line.contains(".")) buyPrice/=10;
                        buyPrice*=.975;
                        break;
                    }
                }
                if(npcPrice==null || buyPrice == 0) return;
                if(buyPrice<npcPrice) {
                    String profit = NumberUtil.format((long) ((npcPrice-buyPrice)*64));
                    event.toolTip.add(ChatFormatting.GOLD+"Profit Per Stack: "+ChatFormatting.DARK_GREEN+profit);
                }
            }
        }
    }
}
