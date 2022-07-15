package mrfast.skyblockfeatures.features.impl.misc;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.events.GuiContainerEvent;
import mrfast.skyblockfeatures.features.impl.handlers.AuctionData;
import mrfast.skyblockfeatures.features.impl.overlays.AuctionPriceOverlay;
import mrfast.skyblockfeatures.mixins.AccessorGuiEditSign;
import mrfast.skyblockfeatures.utils.ItemUtil;
import mrfast.skyblockfeatures.utils.NumberUtil;
import mrfast.skyblockfeatures.utils.SBInfo;
import mrfast.skyblockfeatures.utils.StringUtils;
import mrfast.skyblockfeatures.utils.Utils;

public class AuctionFeatures {
    public static HashMap<ItemStack, Double> items = new HashMap<ItemStack, Double>();
    public static HashMap<ItemStack, Double> selfItems = new HashMap<ItemStack, Double>();

    @SubscribeEvent
    public void onCloseWindow(GuiContainerEvent.CloseWindowEvent event) {
        if (!Utils.inSkyblock) return;
        selfItems.clear();
    }

    // @SubscribeEvent
    // public void onGuiOpen(GuiOpenEvent event) {
    //     if (!Utils.inSkyblock || !skyblockfeatures.config.auctionGuis) return;

    //     if (event.gui instanceof GuiEditSign && Utils.equalsOneOf(SBInfo.getInstance().lastOpenContainerName, "Create Auction", "Create BIN Auction")) {
    //         TileEntitySign sign = ((AccessorGuiEditSign) event.gui).getTileSign();
    //         if (sign != null && sign.getPos().getY() == 0 && sign.signText[1].getUnformattedText().equals("^^^^^^^^^^^^^^^") && sign.signText[2].getUnformattedText().equals("Your auction") && sign.signText[3].getUnformattedText().equals("starting bid")) {
    //             Utils.drawGraySquareWithBorder(180, 0, 150, 3*Utils.GetMC().fontRendererObj.FONT_HEIGHT,3);
    //             String identifier = AuctionData.getIdentifier(AuctionPriceOverlay.lastAuctionedStack);
    //             if (identifier != null) {
    //                 Double BinValue = AuctionData.lowestBINs.get(identifier);
                    
    //                 String[] lines = {
    //                     ChatFormatting.WHITE+"Lowest BIN: "+ChatFormatting.GOLD+BinValue,
    //                     ChatFormatting.WHITE+"Suggested Price: "+ChatFormatting.GOLD+(BinValue-(BinValue/100)*6),
    //                 };
    //                 int lineCount = 0;
    //                 for(String line:lines) {
    //                     Utils.GetMC().fontRendererObj.drawString(line, 190, lineCount*(Utils.GetMC().fontRendererObj.FONT_HEIGHT+1)+10, -1);
    //                     lineCount++;
    //                 }
    //             }
    //         }
    //     }
    // }

    @SubscribeEvent
    public void onDrawSlots(GuiContainerEvent.DrawSlotEvent.Pre event) {
        if (event.gui instanceof GuiChest ) {
            if(event.slot.slotNumber == 0) selfItems.clear();
            GuiChest gui = (GuiChest) event.gui;
            ContainerChest chest = (ContainerChest) gui.inventorySlots;
            IInventory inv = chest.getLowerChestInventory();
            String chestName = inv.getDisplayName().getUnformattedText().trim();
            if (chestName.contains("Auctions") || chestName.contains("Bids")) {
                if (skyblockfeatures.config.highlightAuctionProfit) {
                    if (event.slot.getHasStack()) {
                        ItemStack stack = event.slot.getStack();
                        int x = event.slot.xDisplayPosition;
                        int y = event.slot.yDisplayPosition;
                        float price = 0;
                        for(String line : ItemUtil.getItemLore(stack)) {
                            if(line.contains("bid:")) {
                                String b = StringUtils.stripControlCodes(line);
                                String a = b.replaceAll("[^0-9]", "");
                                price = Float.parseFloat(a);
                            }
                        }
                        String identifier = AuctionData.getIdentifier(stack);
                        if (identifier != null && price != 0) {
                            Double avgBinValue = AuctionData.averageLowestBINs.get(identifier);
                            if(avgBinValue != null) {
                                Double profit = avgBinValue - price;
                                if (price < (avgBinValue)) {
                                    if(profit > 100000) {
                                        Gui.drawRect(x, y, x + 16, y + 16, new Color(85, 255, 85, 255).getRGB());
                                    }
                                }
                                items.put(stack, profit*stack.stackSize);
                            }
                        }
                    }
                }
            }
            if(skyblockfeatures.config.auctionGuis) {
                if(chestName.contains("Your Bids")) {
                    if (event.slot.getHasStack()) {
                        ItemStack stack = event.slot.getStack();
                        float price = 0;
                        for(String line : ItemUtil.getItemLore(stack)) {
                            if(line.contains("bid:")) {
                                String b = StringUtils.stripControlCodes(line);
                                String a = b.replaceAll("[^0-9]", "");
                                price = Float.parseFloat(a);
                            }
                        }
                        String identifier = AuctionData.getIdentifier(stack);
                        if (identifier != null && price != 0) {
                            Double BinValue = AuctionData.lowestBINs.get(identifier);
                            if(BinValue != null) {
                                Double profit = BinValue - price;
                                selfItems.put(stack, profit*stack.stackSize);
                            }
                        }
                    }
                }

                if(chestName.contains("Manage Auctions")) {
                    if (event.slot.getHasStack()) {
                        ItemStack stack = event.slot.getStack();
                        float price = 0;
                        for(String line : ItemUtil.getItemLore(stack)) {
                            if(line.contains("bid:")) {
                                String b = StringUtils.stripControlCodes(line);
                                String a = b.replaceAll("[^0-9]", "");
                                price = Float.parseFloat(a);
                            }
                            if(line.contains("now:")) {
                                String b = StringUtils.stripControlCodes(line);
                                String a = b.replaceAll("[^0-9]", "");
                                price = Float.parseFloat(a);
                            }
                            if(line.contains("Sold for:")) {
                                String b = StringUtils.stripControlCodes(line);
                                String a = b.replaceAll("[^0-9]", "");
                                price = Float.parseFloat(a);
                            }
                        }
                        String identifier = AuctionData.getIdentifier(stack);
                        if (identifier != null && price != 0) {
                            selfItems.put(stack, (double) price);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onDrawContainerTitle(GuiContainerEvent.TitleDrawnEvent.Post event) {
        if (event.gui instanceof GuiChest && skyblockfeatures.config.auctionGuis) {
            GuiChest gui = (GuiChest) event.gui;
            ContainerChest chest = (ContainerChest) gui.inventorySlots;
            IInventory inv = chest.getLowerChestInventory();
            String chestName = inv.getDisplayName().getUnformattedText().trim();
            Double profit = (double) 0;
            for (Double f : selfItems.values()) profit += f;

            if(chestName.contains("Auction View") && !chestName.contains("BIN")) {
                for(Slot slot:gui.inventorySlots.inventorySlots) {
                    if (slot.getHasStack() && slot.getSlotIndex() == 13) {
                        ItemStack stack = slot.getStack();
                        Utils.drawGraySquareWithBorder(180, 0, 150, 7*Utils.GetMC().fontRendererObj.FONT_HEIGHT,3);
                        String auctionIdentifier = AuctionData.getIdentifier(stack);
                        Double lowestBin = 0.0;
                        Double avgBin = 0.0;
                        if (auctionIdentifier != null) {
                            lowestBin = AuctionData.lowestBINs.get(auctionIdentifier);
                            avgBin = AuctionData.lowestBINs.get(auctionIdentifier);
                            Integer cost = 0;
                            Double resellProfit = 0.0;
                            for(String line : ItemUtil.getItemLore(stack)) {
                                if(line.contains("bid:")) {
                                    cost = Integer.parseInt(Utils.cleanColour(line).replaceAll("[^0-9]", ""));
                                    if(avgBin!=null) resellProfit = avgBin-cost;
                                    else if(lowestBin!=null) resellProfit = lowestBin-cost;
                                }
                            }
                            if(resellProfit != 0) {
                                String resellString = resellProfit>0? ChatFormatting.GREEN+"":ChatFormatting.RED+"";
                                Boolean Manipulated = false;
                                if(lowestBin != null && avgBin!=null) {
                                    if(lowestBin > avgBin+150000 || avgBin > lowestBin+150000) {
                                        Manipulated = true;
                                    } else if(cost > avgBin+150000) {
                                        Manipulated = true;
                                    } else if(cost > lowestBin+150000) {
                                        Manipulated = true;
                                    }
                                }
                                if(lowestBin == null && avgBin!=null) {
                                    if(cost > avgBin+150000) {
                                        Manipulated = true;
                                    }
                                }
                                if(avgBin == null && lowestBin!=null) {
                                    if(cost > lowestBin+150000) {
                                        Manipulated = true;
                                    }
                                }
                            
                                String avgBinString = avgBin != null?ChatFormatting.GOLD+NumberUtil.nf.format(avgBin):ChatFormatting.RED+"Unknown";
                                String lowestBinString = lowestBin != null?ChatFormatting.GOLD+NumberUtil.nf.format(lowestBin):ChatFormatting.RED+"Unknown";
                                String[] lines = {
                                    ChatFormatting.WHITE+"Item Price: "+ChatFormatting.GOLD+NumberUtil.nf.format(cost),
                                    ChatFormatting.WHITE+"Lowest BIN: "+lowestBinString,
                                    ChatFormatting.WHITE+"Average BIN: "+avgBinString,
                                    "",
                                    ChatFormatting.WHITE+"Resell Profit: "+resellString+(NumberUtil.nf.format(resellProfit))
                                };
                                int lineCount = 0;
                                for(String line:lines) {
                                    Utils.GetMC().fontRendererObj.drawString(line, 190, lineCount*(Utils.GetMC().fontRendererObj.FONT_HEIGHT+1)+10, -1);
                                    lineCount++;
                                }
                                if(Manipulated) {
                                    Utils.drawGraySquareWithBorder(180, 8*Utils.GetMC().fontRendererObj.FONT_HEIGHT, 170, 3*Utils.GetMC().fontRendererObj.FONT_HEIGHT,3);
                                    Utils.GetMC().fontRendererObj.drawString(ChatFormatting.RED+""+ChatFormatting.BOLD+"Warning! This items price", 190, 8*Utils.GetMC().fontRendererObj.FONT_HEIGHT+5, -1);
                                    Utils.GetMC().fontRendererObj.drawString(ChatFormatting.RED+""+ChatFormatting.BOLD+"is higher than usual!", 190, 9*Utils.GetMC().fontRendererObj.FONT_HEIGHT+5, -1);
                                }
                            } else {
                                Utils.GetMC().fontRendererObj.drawString(ChatFormatting.RED+"Error! Unable to get price", 190, 10, -1);
                            }
                        } else {
                            Utils.GetMC().fontRendererObj.drawString(ChatFormatting.RED+"Error! Unable to get item ID", 190, 10, -1);
                        }
                    }
                }
            }
            
            if(chestName.contains("Manage Auctions")) {
                int unclaimed = 0;
                int expired = 0;
                int coins = 0;
                List<ItemStack> endedAuctions = new ArrayList<ItemStack>();
                for (ItemStack stack : selfItems.keySet()) {
                    for(String line : ItemUtil.getItemLore(stack)) {
                        if(line.contains("Ended")) {
                            unclaimed++;
                        }
                        if(line.contains("Sold")) {
                            unclaimed++;
                            endedAuctions.add(stack);
                        }
                        if(line.contains("Expired")) {
                            expired++;
                            endedAuctions.add(stack);
                        }
                        if((line.contains("Buy it now:") || line.contains("Top bid:")) && endedAuctions.contains(stack)) {
                            try {
                                coins+=Integer.parseInt(Utils.cleanColour(line).replaceAll("[^0-9]", ""));
                            } catch (Exception e) {
                                //TODO: handle exception
                            }
                        }
                    }
                }

                Utils.drawGraySquareWithBorder(180, 0, 150, 8*Utils.GetMC().fontRendererObj.FONT_HEIGHT,3);

                String[] lines = {
                    ChatFormatting.GREEN+""+unclaimed+ChatFormatting.WHITE+" Unclaimed",
                    ChatFormatting.RED+""+expired+ChatFormatting.WHITE+" Expired",
                    "",
                    ChatFormatting.WHITE+"Coins to collect: "+ChatFormatting.GOLD+NumberUtil.nf.format(coins),
                    ChatFormatting.WHITE+"Total Value: "+ChatFormatting.GOLD+NumberUtil.nf.format(profit)
                };
                int lineCount = 0;
                for(String line:lines) {
                    Utils.GetMC().fontRendererObj.drawString(line, 190, lineCount*(Utils.GetMC().fontRendererObj.FONT_HEIGHT+1)+10, -1);
                    lineCount++;
                }
            }
            if(chestName.contains("Your Bids")) {
                int ended = 0;
                int winning = 0;
                int losing = 0;
                int coins = 0;
                int coinsSpent = 0;
                List<ItemStack> endedAuctions = new ArrayList<ItemStack>();
                for (ItemStack stack : selfItems.keySet()) {
                    for(String line : ItemUtil.getItemLore(stack)) {
                        if(line.contains("Ended")) {
                            ended++;
                        }
                        if(line.contains("Bidder") && line.contains(Utils.GetMC().thePlayer.getName())) {
                            winning++;
                            try {
                                coinsSpent+=Integer.parseInt(Utils.cleanColour(line).replaceAll("[^0-9]", ""));
                            } catch (Exception e) {
                                //TODO: handle exception
                            }
                        }
                        if(line.contains("Bidder") && !line.contains(Utils.GetMC().thePlayer.getName())) {
                            losing++;
                            endedAuctions.add(stack);
                            profit -= selfItems.get(stack);
                        }
                    }
                }

                Utils.drawGraySquareWithBorder(180, 0, 150, 8*Utils.GetMC().fontRendererObj.FONT_HEIGHT,3);

                String[] lines = {
                    ChatFormatting.GREEN+""+winning+ChatFormatting.WHITE+" Winning Auctions",
                    ChatFormatting.RED+""+losing+ChatFormatting.WHITE+" Losing Auctions",
                    "",
                    ChatFormatting.WHITE+"Ended Auctions: "+ChatFormatting.GOLD+NumberUtil.nf.format(ended),
                    ChatFormatting.WHITE+"Resell Profit: "+ChatFormatting.GOLD+NumberUtil.nf.format(profit)
                };
                int lineCount = 0;
                for(String line:lines) {
                    Utils.GetMC().fontRendererObj.drawString(line, 190, lineCount*(Utils.GetMC().fontRendererObj.FONT_HEIGHT+1)+10, -1);
                    lineCount++;
                }
            }
        }
    }
}
