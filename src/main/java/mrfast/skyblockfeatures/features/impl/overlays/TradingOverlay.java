package mrfast.skyblockfeatures.features.impl.overlays;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.mojang.realmsclient.gui.ChatFormatting;

import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.events.GuiContainerEvent;
import mrfast.skyblockfeatures.features.impl.handlers.AuctionData;
import mrfast.skyblockfeatures.utils.ItemUtil;
import mrfast.skyblockfeatures.utils.NumberUtil;
import mrfast.skyblockfeatures.utils.Utils;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TradingOverlay {
    List<Integer> topSelfSlotIds = new ArrayList<>(Arrays.asList(0,1,2,3));
    List<Integer> topOtherSlotIds = new ArrayList<>(Arrays.asList(5,6,7,8));

    @SubscribeEvent
    public void onTitleDrawn(GuiContainerEvent.TitleDrawnEvent event) {
        if (event.gui instanceof GuiChest && skyblockfeatures.config.tradeOverlay) {
            GuiChest gui = (GuiChest) event.gui;
            ContainerChest chest = (ContainerChest) gui.inventorySlots;
            IInventory inv = chest.getLowerChestInventory();
            String chestName = inv.getDisplayName().getUnformattedText().trim();
            if (chestName.contains("You                  ")) {
                List<Integer> selfSlots = new ArrayList<>();
                List<Integer> otherSlots = new ArrayList<>();
                for(int slot:topSelfSlotIds) {
                    selfSlots.add(slot);
                    selfSlots.add(slot+9);
                    selfSlots.add(slot+18);
                    selfSlots.add(slot+27);
                }
                for(int slot:topOtherSlotIds) {
                    otherSlots.add(slot);
                    otherSlots.add(slot+9);
                    otherSlots.add(slot+18);
                    otherSlots.add(slot+27);
                }
                HashMap<String,Double> selfItemsAndValues = new HashMap<>();
                HashMap<String,Double> otherItemsAndValues = new HashMap<>();

                double totalOther = 0;
                double totalSelf = 0;

                for(int slotId = 0;slotId<inv.getSizeInventory();slotId++) {
                    if(inv.getStackInSlot(slotId)==null) continue;
                    double value = 0;
                    String id = AuctionData.getIdentifier(inv.getStackInSlot(slotId));
                    boolean coins = inv.getStackInSlot(slotId).getDisplayName().contains("coins");
                    if(inv.getStackInSlot(slotId)!=null) {
                        if(id==null && !coins) continue;
                    } else {
                        if(id==null) continue;
                    }
                    if(coins) {
                        String line = Utils.cleanColour(inv.getStackInSlot(slotId).getDisplayName());
                        line = line.replace("k", "000").replace("M", "000000").replace("B", "000000000");
                        double coinValue = Double.parseDouble(line.replaceAll("[^0-9]", ""));
                        if(line.contains(".")) coinValue/=10;
                        if(selfSlots.contains(slotId)) {
                            value = coinValue;
                        }
                        if(otherSlots.contains(slotId)) {
                            value = coinValue;
                        }
                    }
                    else if(AuctionData.bazaarPrices.containsKey(id)) {
                        value = AuctionData.bazaarPrices.get(id);
                    }
                    else if(AuctionData.lowestBINs.containsKey(id)) {
                        value = ItemUtil.getEstimatedItemValue(inv.getStackInSlot(slotId))*inv.getStackInSlot(slotId).stackSize;
                    }
                    if(selfSlots.contains(slotId)) {
                        totalSelf+=value*(coins?1:inv.getStackInSlot(slotId).stackSize);
                        selfItemsAndValues.put(inv.getStackInSlot(slotId).getDisplayName(), value);
                    }
                    if(otherSlots.contains(slotId)) {
                        totalOther+=value*(coins?1:inv.getStackInSlot(slotId).stackSize);
                        otherItemsAndValues.put(inv.getStackInSlot(slotId).getDisplayName(), value);
                    }
                }

                drawOtherPersonValue(totalOther,otherItemsAndValues);
                drawSelfPersonValue(totalSelf,selfItemsAndValues);
            }
        }
    }

    public void drawOtherPersonValue(Double total,HashMap<String,Double> items) {
        Utils.drawGraySquareWithBorder(180, 0, 150, (int) ((items.size()+3)*2*Utils.GetMC().fontRendererObj.FONT_HEIGHT),3);
                
        List<String> lines = new ArrayList<>(Arrays.asList(ChatFormatting.WHITE+"Total Value: "+ChatFormatting.GOLD+NumberUtil.format(total.longValue()),""));
        for(String itemName:items.keySet()) {
            String name = itemName;
            if(itemName.length()>21) name = itemName.substring(0, 20)+"..";
            lines.add(name+" "+ChatFormatting.DARK_GRAY+ChatFormatting.ITALIC+"("+NumberUtil.format(items.get(itemName).longValue())+")");
        }
        int lineCount = 0;
        for(String line:lines) {
            Utils.GetMC().fontRendererObj.drawStringWithShadow(line, 190, lineCount*(Utils.GetMC().fontRendererObj.FONT_HEIGHT+1)+10, -1);
            lineCount++;
        }
    }

    public void drawSelfPersonValue(Double total,HashMap<String,Double> items) {
        Utils.drawGraySquareWithBorder(-155, 0, 150, (int) ((items.size()+3)*2*Utils.GetMC().fontRendererObj.FONT_HEIGHT),3);
                
        List<String> lines = new ArrayList<>(Arrays.asList(ChatFormatting.WHITE+"Total Value: "+ChatFormatting.GOLD+NumberUtil.format(total.longValue()),""));
        for(String itemName:items.keySet()) {
            String name = itemName;
            if(itemName.length()>21) name = itemName.substring(0, 20)+"..";
            lines.add(name+" "+ChatFormatting.DARK_GRAY+ChatFormatting.ITALIC+"("+NumberUtil.format(items.get(itemName).longValue())+")");
        }
        int lineCount = 0;
        for(String line:lines) {
            Utils.GetMC().fontRendererObj.drawStringWithShadow(line, -145, lineCount*(Utils.GetMC().fontRendererObj.FONT_HEIGHT+1)+10, -1);
            lineCount++;
        }
    }
}
