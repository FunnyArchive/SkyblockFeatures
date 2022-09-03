package mrfast.skyblockfeatures.features.impl.misc;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.mojang.realmsclient.gui.ChatFormatting;

import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.events.GuiContainerEvent;
import mrfast.skyblockfeatures.events.SecondPassedEvent;
import mrfast.skyblockfeatures.features.impl.handlers.AuctionData;
import mrfast.skyblockfeatures.utils.ItemUtil;
import mrfast.skyblockfeatures.utils.NumberUtil;
import mrfast.skyblockfeatures.utils.StringUtils;
import mrfast.skyblockfeatures.utils.Utils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class AuctionFeatures {
    public static HashMap<ItemStack, Double> items = new HashMap<ItemStack, Double>();
    public static List<Auction> selfItems = new ArrayList<>();
    public static int sec = 0;
    public static double itemCount = 0;

    @SubscribeEvent
    public void onSeconds(SecondPassedEvent event) {
        selfItems.clear();
    }

    @SubscribeEvent
    public void onTick(ClientTickEvent event) {
        if (!Utils.inSkyblock || !skyblockfeatures.usingNEU || Utils.GetMC().thePlayer == null || Utils.GetMC().thePlayer.openContainer == null) return;
        itemCount = 0;
        for(ItemStack stack:Utils.GetMC().thePlayer.openContainer.inventoryItemStacks) {
            if(ItemUtil.getRarity(stack) != null) {
                itemCount++;
            }
        }
    }

    @SubscribeEvent
    public void onCloseWindow(GuiContainerEvent.CloseWindowEvent event) {
        if (!Utils.inSkyblock) return;
        items.clear();
        sec=0;
    }

    public class Auction {
        public Double profit;
        public ItemStack stack;
        public String identifer;
        
        public Auction(Double p,ItemStack s,String i) {
            profit = p;
            stack = s;
            identifer = i;
        }
    }

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
                            Double avgBinValue = AuctionData.lowestBINs.get(identifier);
                            if(avgBinValue != null) {
                                Double profit = avgBinValue - price;
                                if (price < (avgBinValue)) {
                                    if(profit > 100000) {
                                        Gui.drawRect(x, y, x + 16, y + 16, new Color(85, 255, 85, 255).getRGB());
                                    }
                                }
                                items.put(stack, profit);
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
                                Double profit = (BinValue - price)*stack.stackSize;
                                Boolean dupe = false;
                                Auction auction = new Auction(profit, stack, identifier);

                                for(Auction auc:selfItems) {
                                    if(auc.stack == auction.stack || auc.identifer == auction.identifer || auc.profit == auction.profit) {
                                        dupe = true;
                                    }
                                }

                                if(!dupe && (selfItems.size()<itemCount || selfItems.size() == 0)) {
                                    selfItems.add(auction);
                                }
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
                            // selfItems.put(stack, (double) price);
                            Double profit = (double) price;
                            Boolean dupe = false;
                            Auction auction = new Auction(profit, stack, identifier);

                            for(Auction auc:selfItems) {
                                if(auc.stack == auction.stack || auc.identifer == auction.identifer || auc.profit == auction.profit) {
                                    dupe = true;
                                }
                            }
                            if(!skyblockfeatures.usingNEU || (skyblockfeatures.usingNEU && !dupe && (selfItems.size()<itemCount || selfItems.size() == 0))) {
                                selfItems.add(auction);
                            }
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
            for (Auction auction:selfItems) profit += auction.profit;

            if(chestName.contains("Auction View") && !chestName.contains("BIN")) {
                for(Slot slot:gui.inventorySlots.inventorySlots) {
                    if (slot.getHasStack() && slot.getSlotIndex() == 13) {
                        ItemStack stack = slot.getStack();
                        String auctionIdentifier = AuctionData.getIdentifier(stack);
                        if (auctionIdentifier != null) {
                            Double lowestBin = AuctionData.lowestBINs.get(auctionIdentifier)*stack.stackSize;
                            Double avgBin = AuctionData.averageLowestBINs.get(auctionIdentifier)*stack.stackSize;
                            Integer cost = 0;
                            Double resellProfit = 0.0;
                            for(String line : ItemUtil.getItemLore(stack)) {
                                if(line.contains("bid:")) {
                                    cost = Integer.parseInt(Utils.cleanColour(line).replaceAll("[^0-9]", ""));
                                    if(lowestBin!=null) resellProfit = lowestBin-cost;
                                    else if(avgBin!=null) resellProfit = avgBin-cost;
                                }
                            }
                            if(resellProfit != 0) {
                                Utils.drawGraySquareWithBorder(180, 0, 150, 7*Utils.GetMC().fontRendererObj.FONT_HEIGHT,3);
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
                                if(lowestBin == null && avgBin != null) {
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
                            }
                        }
                    }
                }
            }

            if(chestName.contains("Create")) {
                for(Slot slot:gui.inventorySlots.inventorySlots) {
                    if (slot.getHasStack() && slot.getSlotIndex() == 13) {
                        ItemStack stack = slot.getStack();
                        String auctionIdentifier = AuctionData.getIdentifier(stack);
                        if (auctionIdentifier != null) {
                            Double lowestBin = AuctionData.lowestBINs.get(auctionIdentifier)*stack.stackSize;
                            Double avgBin = AuctionData.averageLowestBINs.get(auctionIdentifier)*stack.stackSize;
                            Utils.drawGraySquareWithBorder(180, 0, 6*("Suggested Listing Price: "+lowestBin.toString()).length(), 5*Utils.GetMC().fontRendererObj.FONT_HEIGHT,3);
                        
                            String avgBinString = avgBin != null?ChatFormatting.GOLD+NumberUtil.nf.format(avgBin):ChatFormatting.RED+"Unknown";
                            String lowestBinString = lowestBin != null?ChatFormatting.GOLD+NumberUtil.nf.format(lowestBin):ChatFormatting.RED+"Unknown";
                            String[] lines = {
                                ChatFormatting.WHITE+"Lowest BIN: "+lowestBinString,
                                ChatFormatting.WHITE+"Average BIN: "+avgBinString,
                                ChatFormatting.WHITE+"Suggested Listing Price: "+ChatFormatting.GOLD+NumberUtil.nf.format(((lowestBin+avgBin)/2)-0.03),
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
            
            if(chestName.contains("Manage Auctions")) {
                int unclaimed = 0;
                int expired = 0;
                int coins = 0;
                int toCollect = 0;
                List<ItemStack> endedAuctions = new ArrayList<ItemStack>();

                for (Auction auction : selfItems) {
                    ItemStack stack = auction.stack;
                    for(String line : ItemUtil.getItemLore(stack)) {
                        line = Utils.cleanColour(line);
                        if(line.contains("Ended")) {
                            unclaimed++;
                        }
                        if(line.contains("Sold for: ") || line.contains("Status: Sold")) {
                            unclaimed++;
                            try {
                                toCollect+=Integer.parseInt(line.replaceAll("[^0-9]", ""));
                            } catch (Exception e) {
                                //TODO: handle exception
                            }
                            endedAuctions.add(stack);
                        }
                        if(line.contains("Expired")) {
                            expired++;
                            endedAuctions.add(stack);
                        }
                        if((line.contains("Buy it now:") || line.contains("Top bid:"))) {
                            try {
                                coins+=Integer.parseInt(line.replaceAll("[^0-9]", ""));
                            } catch (Exception e) {
                                //TODO: handle exception
                            }
                        }
                    }
                }

                Utils.drawGraySquareWithBorder(180, 0, 150, 8*Utils.GetMC().fontRendererObj.FONT_HEIGHT,3);
 
                String[] lines = {
                    ChatFormatting.GREEN+""+(unclaimed/2)+ChatFormatting.WHITE+" Unclaimed",
                    ChatFormatting.RED+""+expired+ChatFormatting.WHITE+" Expired",
                    "",
                    ChatFormatting.WHITE+"Coins to collect: "+ChatFormatting.GOLD+NumberUtil.nf.format(toCollect),
                    ChatFormatting.WHITE+"Total Ask Value: "+ChatFormatting.GOLD+NumberUtil.nf.format(coins)
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
                List<String> winningAuctions = new ArrayList<String>();
                for (Auction auction : selfItems) {
                    ItemStack stack = auction.stack;
                    for(String line : ItemUtil.getItemLore(stack)) {
                        line = Utils.cleanColour(line);
                        if(line.contains("Ended")) {
                            ended++;
                        }
                        if(line.contains("Bidder") && line.contains(Utils.GetMC().thePlayer.getName()) && !winningAuctions.contains(auction.identifer)) {
                            winning++;
                            winningAuctions.add(auction.identifer);
                            try {
                                coinsSpent+=Integer.parseInt(Utils.cleanColour(line).replaceAll("[^0-9]", ""));
                            } catch (Exception e) {
                                //TODO: handle exception
                            }
                        }
                        if(line.contains("Bidder") && !line.contains(Utils.GetMC().thePlayer.getName())) {
                            losing++;
                            endedAuctions.add(stack);
                            profit -= auction.profit;
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
