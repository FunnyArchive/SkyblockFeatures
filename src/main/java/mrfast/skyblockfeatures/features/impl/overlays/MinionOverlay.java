package mrfast.skyblockfeatures.features.impl.overlays;

import java.util.List;

import com.mojang.realmsclient.gui.ChatFormatting;

import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.events.GuiContainerEvent;
import mrfast.skyblockfeatures.features.impl.handlers.AuctionData;
import mrfast.skyblockfeatures.utils.ItemRarity;
import mrfast.skyblockfeatures.utils.ItemUtil;
import mrfast.skyblockfeatures.utils.NumberUtil;
import mrfast.skyblockfeatures.utils.Utils;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MinionOverlay {
    @SubscribeEvent
    public void onDrawContainerTitle(GuiContainerEvent.TitleDrawnEvent.Post event) {
        if (event.gui instanceof GuiChest && skyblockfeatures.config.minionOverlay) {
            GuiChest gui = (GuiChest) event.gui;
            ContainerChest chest = (ContainerChest) gui.inventorySlots;
            IInventory inv = chest.getLowerChestInventory();
            String chestName = inv.getDisplayName().getUnformattedText().trim();
            if(chestName.contains(" Minion ")) {
                int secondsPerAction = 0;
                ItemStack generating = null;
                for(Slot slot:gui.inventorySlots.inventorySlots) {
                    if (slot.getHasStack() && slot.getSlotIndex() == 4) {
                        List<String> lore = ItemUtil.getItemLore(slot.getStack());
                        for(int i=0;i<lore.size();i++) {
                            String line = Utils.cleanColour(lore.get(i));
                            if(line.contains("Actions:")) {
                                secondsPerAction = Integer.parseInt(line.replaceAll("[^0-9]", ""));
                                if(secondsPerAction>100) {
                                    secondsPerAction/=10;
                                }
                            }
                        }
                    }
                    if(slot.getHasStack() && ItemUtil.getRarity(slot.getStack()) == ItemRarity.COMMON) {
                        if(slot.getSlotIndex() == 21) generating = slot.getStack();
                        if(slot.getSlotIndex() == 22) generating = slot.getStack();
                        if(slot.getSlotIndex() == 23) generating = slot.getStack();
                        if(slot.getSlotIndex() == 24) generating = slot.getStack();
                    }
                }
                if(generating != null && ItemUtil.getRarity(generating) == ItemRarity.COMMON) {
                    String identifier = AuctionData.getIdentifier(generating);
                    if (identifier != null) {
                        Utils.drawGraySquareWithBorder(180, 0, 150, 7*Utils.GetMC().fontRendererObj.FONT_HEIGHT,3);
                        if(skyblockfeatures.config.apiKey.length()>1) {
                            Utils.GetMC().fontRendererObj.drawString(ChatFormatting.RED+"API Key Required! /api new", 190, 0, -1);
                            return;
                        }
                        Float sellPrice = AuctionData.bazaarPrices.get(identifier);
                        if(sellPrice != null) {
                            Float perHour = (3600/secondsPerAction)*sellPrice;

                            String[] lines = {
                                ChatFormatting.LIGHT_PURPLE+chestName,
                                ChatFormatting.WHITE+"Time Between Actions: "+ChatFormatting.GREEN+secondsPerAction+"s",
                                ChatFormatting.WHITE+"Coins Per Hour: "+ChatFormatting.GOLD+NumberUtil.nf.format(perHour)
                            };
                            int lineCount = 0;
                            for(String line:lines) {
                                Utils.GetMC().fontRendererObj.drawString(line, 190, lineCount*(Utils.GetMC().fontRendererObj.FONT_HEIGHT+1)+10, -1);
                                lineCount++;
                            }
                        } else {
                            Utils.GetMC().fontRendererObj.drawString(ChatFormatting.RED+"Unable to get item price!", 190, 0, -1);
                        }
                    }
                }
            }
        }
    }
}
