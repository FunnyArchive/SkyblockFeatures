package mrfast.skyblockfeatures.features.impl.overlays;

import java.util.List;

import com.mojang.realmsclient.gui.ChatFormatting;

import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.events.GuiContainerEvent;
import mrfast.skyblockfeatures.events.GuiContainerEvent.TitleDrawnEvent;
import mrfast.skyblockfeatures.features.impl.handlers.AuctionData;
import mrfast.skyblockfeatures.utils.ItemRarity;
import mrfast.skyblockfeatures.utils.ItemUtil;
import mrfast.skyblockfeatures.utils.NumberUtil;
import mrfast.skyblockfeatures.utils.Utils;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MinionOverlay {
    @SubscribeEvent
    public void onDrawContainerTitle(TitleDrawnEvent event) {
        if (event.gui !=null && event.gui instanceof GuiChest && skyblockfeatures.config.minionOverlay) {
            GuiChest gui = (GuiChest) event.gui;
            ContainerChest chest = (ContainerChest) gui.inventorySlots;
            IInventory inv = chest.getLowerChestInventory();
            Double totalValue = 0d;

            String chestName = inv.getDisplayName().getUnformattedText().trim();
            if(chestName.contains(" Minion ") && !chestName.contains("Recipe")) {
                int secondsPerAction = 0;
                ItemStack generating = null;
                for(int slotId = 0;slotId<inv.getSizeInventory();slotId++) {
                    if(inv.getStackInSlot(slotId)==null) continue;

                    ItemStack stack = inv.getStackInSlot(slotId);
                    if (slotId == 4) {
                        List<String> lore = ItemUtil.getItemLore(stack);
                        for(int i=0;i<lore.size();i++) {
                            String line = Utils.cleanColour(lore.get(i));
                            if(line.contains("Actions:")) {
                                secondsPerAction = Integer.parseInt(line.replaceAll("[^0-9]", ""));
                                if(line.contains(".")) {
                                    secondsPerAction/=10;
                                }
                            }
                        }
                    }
                    int i=slotId;
                    if((i == 21||i == 22||i == 23||i == 24||i == 25) || (i == 30||i == 31||i == 32||i == 33||i == 34) || (i == 39||i == 40||i == 41||i == 42||i == 43)) {
                        String identifier = AuctionData.getIdentifier(stack);
                        if(identifier!=null) {
                            Double sellPrice = AuctionData.bazaarPrices.get(identifier);
                            if(sellPrice!=null) totalValue += (sellPrice*stack.stackSize);
                        }
                        if(stack.getDisplayName().contains("Block") && !stack.getDisplayName().contains("Snow")) {
                            continue;
                        }
                        if(generating == null && ItemUtil.getRarity(stack) == ItemRarity.COMMON) {
                            generating = stack;
                        }
                    }
                }
                if(generating != null && ItemUtil.getRarity(generating) == ItemRarity.COMMON) {
                    String identifier = AuctionData.getIdentifier(generating);
                    if (identifier != null) {
                        Utils.drawGraySquareWithBorder(180, 0, 150, 7*Utils.GetMC().fontRendererObj.FONT_HEIGHT,3);
                        if(skyblockfeatures.config.apiKey.length()<1) {
                            Utils.GetMC().fontRendererObj.drawStringWithShadow(ChatFormatting.RED+"API Key Required! /api new", 190, 0, -1);
                            return;
                        }
                        Double sellPrice = AuctionData.bazaarPrices.get(identifier);
                        if(sellPrice != null) {
                            Double perHour = Math.floor((3600/secondsPerAction)*sellPrice);

                            String[] lines = {
                                ChatFormatting.LIGHT_PURPLE+chestName,
                                ChatFormatting.WHITE+"Time Between Actions: "+ChatFormatting.GREEN+secondsPerAction+"s",
                                ChatFormatting.WHITE+"Coins Per Hour: "+ChatFormatting.GOLD+NumberUtil.nf.format(perHour),
                                ChatFormatting.WHITE+"Total Value: "+ChatFormatting.GOLD+NumberUtil.format(totalValue.longValue())
                            };
                            int lineCount = 0;
                            for(String line:lines) {
                                Utils.GetMC().fontRendererObj.drawStringWithShadow(line, 190, lineCount*(Utils.GetMC().fontRendererObj.FONT_HEIGHT+1)+10, -1);
                                lineCount++;
                            }
                        } else {
                            String[] lines = {
                                ChatFormatting.RED+"Unable to get item price!",
                                ChatFormatting.RED+"Minion Generates: "+identifier
                            };
                            int lineCount = 0;
                            for(String line:lines) {
                                Utils.GetMC().fontRendererObj.drawStringWithShadow(line, 190, lineCount*(Utils.GetMC().fontRendererObj.FONT_HEIGHT+1)+10, -1);
                                lineCount++;
                            }
                        }
                    } else {
                        String[] lines = {
                            ChatFormatting.RED+"Unable to get item id!",
                            ChatFormatting.RED+"Minion Generates: "+identifier
                        };
                        int lineCount = 0;
                        for(String line:lines) {
                            Utils.GetMC().fontRendererObj.drawStringWithShadow(line, 190, lineCount*(Utils.GetMC().fontRendererObj.FONT_HEIGHT+1)+10, -1);
                            lineCount++;
                        }
                    }
                }
            }
        }
    }
}
