package mrfast.skyblockfeatures.features.impl.misc;

import java.awt.Color;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.input.Keyboard;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.realmsclient.gui.ChatFormatting;

import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.events.GuiContainerEvent;
import mrfast.skyblockfeatures.events.RenderOverlayEvent;
import mrfast.skyblockfeatures.events.SecondPassedEvent;
import mrfast.skyblockfeatures.events.GuiContainerEvent.SlotClickEvent;
import mrfast.skyblockfeatures.events.GuiContainerEvent.TitleDrawnEvent;
import mrfast.skyblockfeatures.features.impl.handlers.AuctionData;
import mrfast.skyblockfeatures.utils.ItemUtil;
import mrfast.skyblockfeatures.utils.NumberUtil;
import mrfast.skyblockfeatures.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiScreenEvent;
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
        if(pricePaidMap.size()==0) {
            readConfig();
        }
    }

    boolean canRefresh = true;

    @SubscribeEvent
    public void onSlotClick(SlotClickEvent event) {
        // get price from when buying a BIN
        if (event.gui !=null && event.gui instanceof GuiChest && skyblockfeatures.config.showPricePaid) {
            GuiChest gui = (GuiChest) event.gui;
            ContainerChest chest = (ContainerChest) gui.inventorySlots;
            IInventory inv = chest.getLowerChestInventory();
            String chestName = inv.getDisplayName().getUnformattedText().trim();
            Boolean buySlot = skyblockfeatures.config.autoAuctionFlipEasyBuy?true:event.slotId==11;

            if(chestName.contains("Confirm Purchase") && buySlot) {
                int pricePaid = 0;
                for(String line:ItemUtil.getItemLore(inv.getStackInSlot(11))) {
                    if(!line.contains("Cost")) continue;
                    String numberOnly = Utils.cleanColour(line).replaceAll("[^0-9]", "");
                    if(numberOnly.length()>0) {
                        try {
                            pricePaid = Integer.parseInt(numberOnly);
                        } catch (Exception e) {
                            // TODO: handle exception
                        }
                    }
                }

                String uuid = ItemUtil.getItemUUID(inv.getStackInSlot(13));
                if(uuid!=null && pricePaid!=0) {
                    pricePaidMap.put(uuid, pricePaid);
                    saveConfig();
                }
            }
        }
    }

    @SubscribeEvent
    public void onGuiClose(GuiContainerEvent.CloseWindowEvent event) {
        canRefresh = true;
    }
    @SubscribeEvent
    public void onKeyInput(GuiScreenEvent.KeyboardInputEvent keyboardInputEvent) {
        GuiScreen screen = Minecraft.getMinecraft().currentScreen;
        if (screen instanceof GuiChest && Keyboard.isKeyDown(skyblockfeatures.reloadAH.getKeyCode()) && canRefresh){
            canRefresh = false;
            ContainerChest ch = (ContainerChest) ((GuiChest)screen).inventorySlots;
            if (!ch.getLowerChestInventory().getName().contains("Auctions")) return;
            int selectedSlot = 0;
            for(int i=0;i<=45;i+=9) {
                ItemStack item = ch.getSlot(i).getStack();
                List<String> lore = ItemUtil.getItemLore(item);
                for(String line:lore) {
                    if(line.contains("Currently")) selectedSlot = i;
                }
            }
            int slot = selectedSlot;
            Utils.setTimeout(()->{
                if(slot<45) {
                    Utils.GetMC().playerController.windowClick(Utils.GetMC().thePlayer.openContainer.windowId, slot+9, 0, 3, Utils.GetMC().thePlayer);
                    Utils.setTimeout(()->{
                        Utils.GetMC().playerController.windowClick(Utils.GetMC().thePlayer.openContainer.windowId,slot, 0, 3, Utils.GetMC().thePlayer);
                    }, 200);
                    Utils.setTimeout(()->{
                        canRefresh = true;
                    }, 500);
                } else {
                    Utils.GetMC().playerController.windowClick(Utils.GetMC().thePlayer.openContainer.windowId, slot-9, 0, 3, Utils.GetMC().thePlayer);
                    Utils.setTimeout(()->{
                        Utils.GetMC().playerController.windowClick(Utils.GetMC().thePlayer.openContainer.windowId,slot, 0, 3, Utils.GetMC().thePlayer);
                    }, 200);
                    Utils.setTimeout(()->{
                        canRefresh = true;
                    }, 500);
                }
            }, 100);
        }
    }
    ItemStack hoverItemStack = null;
    
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
        currentlySellingStack = null;
    }
    ItemStack currentlySellingStack = null;
    @SubscribeEvent
    public void onSignDraw(RenderOverlayEvent event) {
        if(currentlySellingStack!=null) {
            String auctionIdentifier = AuctionData.getIdentifier(currentlySellingStack);
            if (auctionIdentifier != null) {
                Double lowestBin = AuctionData.lowestBINs.get(auctionIdentifier)*currentlySellingStack.stackSize;
                Double avgBin = AuctionData.averageLowestBINs.get(auctionIdentifier)*currentlySellingStack.stackSize;
                int yHeight = (Utils.GetMC().currentScreen.height/8);
                Utils.drawGraySquareWithBorder((Utils.GetMC().currentScreen.width/2)+60, yHeight, 6*("Suggested Listing Price: "+lowestBin.toString()).length(), 5*Utils.GetMC().fontRendererObj.FONT_HEIGHT,3);
            
                String avgBinString = avgBin != null?ChatFormatting.GOLD+NumberUtil.nf.format(avgBin):ChatFormatting.RED+"Unknown";
                String lowestBinString = lowestBin != null?ChatFormatting.GOLD+NumberUtil.nf.format(lowestBin):ChatFormatting.RED+"Unknown";
                String[] lines = {
                    ChatFormatting.WHITE+"Lowest BIN: "+lowestBinString,
                    ChatFormatting.WHITE+"Average BIN: "+avgBinString,
                    ChatFormatting.WHITE+"Suggested Listing Price: "+ChatFormatting.GOLD+NumberUtil.nf.format(((lowestBin+avgBin)/2)-0.03),
                };
                int lineCount = 0;
                for(String line:lines) {
                    Utils.GetMC().fontRendererObj.drawStringWithShadow(line, (Utils.GetMC().currentScreen.width/2)+70, lineCount*(Utils.GetMC().fontRendererObj.FONT_HEIGHT+1)+10+yHeight, -1);
                    lineCount++;
                }
            }
        }
    }

    public class Auction {
        public Double profit;
        public ItemStack stack;
        public String identifer;
        public Slot slot;
        
        public Auction(Double p,ItemStack s,String i,Slot sl) {
            profit = p;
            stack = s;
            identifer = i;
            slot = sl;
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
                                String b = Utils.cleanColour(line);
                                String a = b.replaceAll("[^0-9]", "");
                                price = Float.parseFloat(a);
                            }
                        }
                        String identifier = AuctionData.getIdentifier(stack);
                        if (identifier != null && price != 0 && AuctionData.lowestBINs.containsKey(identifier)) {
                            Double avgBinValue = AuctionData.lowestBINs.get(identifier);
                            if(avgBinValue != null) {
                                Double profit = (avgBinValue*stack.stackSize) - price;
                                if (price < (avgBinValue)) {
                                    if(profit > 100000) {
                                        // Draw Green Square
                                        Gui.drawRect(x, y, x + 16, y + 16, new Color(85, 255, 85).getRGB());
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
                                String b = Utils.cleanColour(line);
                                String a = b.replaceAll("[^0-9]", "");
                                price = Float.parseFloat(a);
                            }
                        }
                        String identifier = AuctionData.getIdentifier(stack);
                        if (identifier != null && price != 0 && AuctionData.lowestBINs.containsKey(identifier)) {
                            Double BinValue = AuctionData.lowestBINs.get(identifier)*stack.stackSize;
                            if(BinValue != null) {
                                Double profit = (BinValue - price);
                                Boolean dupe = false;
                                Auction auction = new Auction(profit, stack, identifier,event.slot);

                                for(Auction auc:selfItems) {
                                    if(auc.stack == auction.stack || auc.identifer == auction.identifer || auc.profit == auction.profit) {
                                        dupe = true;
                                    }
                                }

                                if(!dupe || (skyblockfeatures.usingNEU && (selfItems.size()<itemCount || selfItems.size() == 0))) {
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
                                String b = Utils.cleanColour(line);
                                String a = b.replaceAll("[^0-9]", "");
                                price = Float.parseFloat(a);
                            }
                            if(line.contains("now:")) {
                                String b = Utils.cleanColour(line);
                                String a = b.replaceAll("[^0-9]", "");
                                price = Float.parseFloat(a);
                            }
                            if(line.contains("Sold for:")) {
                                String b = Utils.cleanColour(line);
                                String a = b.replaceAll("[^0-9]", "");
                                price = Float.parseFloat(a);
                            }
                        }
                        String identifier = AuctionData.getIdentifier(stack);
                        if (identifier != null && price != 0) {
                            Double profit = (double) price;
                            Boolean dupe = false;
                            Auction auction = new Auction(profit, stack, identifier,event.slot);

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
    static HashMap<String,Integer> pricePaidMap = new HashMap<>();
    static File pricePaidFile = null;
    @SubscribeEvent
    public void onDrawContainerTitle(TitleDrawnEvent event) {
        if (event.gui !=null && event.gui instanceof GuiChest && skyblockfeatures.config.auctionGuis) {
            GuiChest gui = (GuiChest) event.gui;
            ContainerChest chest = (ContainerChest) gui.inventorySlots;
            IInventory inv = chest.getLowerChestInventory();
            String chestName = inv.getDisplayName().getUnformattedText().trim();
            Double profit = (double) 0;
            for (Auction auction:selfItems) profit += auction.profit;

            // get price from when buying a auction
            if(chestName.contains("Auction View") && !chestName.contains("BIN")) {
                boolean alreadyGotIt = false;
                try {
                    if(inv.getStackInSlot(29).getDisplayName().contains("Collect Auction") && skyblockfeatures.config.showPricePaid) {
                        boolean canCollectItem = false;
                        int pricePaid = 0;
                        for(String line:ItemUtil.getItemLore(inv.getStackInSlot(29))) {
                            if(Utils.cleanColour(line).contains("collect the item")) {
                                canCollectItem=true;
                            } else {
                                String numberOnly = Utils.cleanColour(line).replaceAll("[^0-9]", "");
                                if(numberOnly.length()>0) {
                                    try {
                                        pricePaid = Integer.parseInt(numberOnly);
                                    } catch (Exception e) {
                                        // TODO: handle exception
                                    }
                                }
                            }
                        }
    
                        if(canCollectItem && pricePaid!=0) {
                            String uuid = ItemUtil.getItemUUID(inv.getStackInSlot(13));
                            if(uuid!=null) {
                                pricePaidMap.put(uuid, pricePaid);
                                saveConfig();
                            }
                        }
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                }
                
                for(Slot slot:gui.inventorySlots.inventorySlots) {
                    if (slot.getHasStack() && !alreadyGotIt  && slot.getSlotIndex() == 13) {
                        ItemStack stack = slot.getStack();
                        alreadyGotIt = true;
                        String auctionIdentifier = AuctionData.getIdentifier(stack);
                        if (auctionIdentifier != null) {
                            if(!AuctionData.lowestBINs.containsKey(auctionIdentifier)) {
                                continue;
                            }
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
                                Utils.drawGraySquareWithBorder(180, 0, 150, (int) (8.5*Utils.GetMC().fontRendererObj.FONT_HEIGHT),3);
                                String resellString = resellProfit>0? ChatFormatting.GREEN+"":ChatFormatting.RED+"";
                                Boolean Manipulated = false;
                                if(lowestBin != null && avgBin!=null) {
                                    if(lowestBin > avgBin+150000) {
                                        Manipulated = true;
                                    } else if(cost > avgBin+150000) {
                                        Manipulated = true;
                                    } else if(cost > lowestBin+150000) {
                                        Manipulated = true;
                                    }
                                }
                                if(avgBin == null && lowestBin!=null) {
                                    if(cost > lowestBin+150000) {
                                        Manipulated = true;
                                    }
                                }
                                cost = (int) Math.floor(cost);
                                String avgBinString = avgBin != null?ChatFormatting.GOLD+NumberUtil.nf.format(avgBin):ChatFormatting.RED+"Unknown";
                                String lowestBinString = lowestBin != null?ChatFormatting.GOLD+NumberUtil.nf.format(lowestBin):ChatFormatting.RED+"Unknown";
                                double putupTax = Math.floor(lowestBin*0.01);
                                double collectAuctionTax = cost>=1000000?Math.floor(lowestBin*0.01):0;
                                double totalTax = Math.floor(putupTax+collectAuctionTax);

                                String resellTax = collectAuctionTax>0?(NumberUtil.nf.format(totalTax))+ChatFormatting.GRAY+" (2%)":NumberUtil.nf.format(putupTax)+ChatFormatting.GRAY+" (1%)";
                                String[] lines = {
                                    ChatFormatting.WHITE+"Item Price: "+ChatFormatting.GOLD+NumberUtil.nf.format(cost),
                                    ChatFormatting.WHITE+"Lowest BIN: "+lowestBinString,
                                    ChatFormatting.WHITE+"Average BIN: "+avgBinString,
                                    ChatFormatting.WHITE+"Taxes: "+ChatFormatting.GOLD+resellTax,
                                    "",
                                    ChatFormatting.WHITE+"Resell Profit: "+resellString+(NumberUtil.nf.format(Math.floor(resellProfit*0.99)))
                                };
                                int lineCount = 0;
                                for(String line:lines) {
                                    Utils.GetMC().fontRendererObj.drawStringWithShadow(line, 190, lineCount*(Utils.GetMC().fontRendererObj.FONT_HEIGHT+1)+10, -1);
                                    lineCount++;
                                }
                                if(Manipulated) {
                                    Utils.drawGraySquareWithBorder(180, 9*Utils.GetMC().fontRendererObj.FONT_HEIGHT, 170, 3*Utils.GetMC().fontRendererObj.FONT_HEIGHT,3);
                                    Utils.GetMC().fontRendererObj.drawStringWithShadow(ChatFormatting.RED+""+ChatFormatting.BOLD+"Warning! This items price", 190, 9*Utils.GetMC().fontRendererObj.FONT_HEIGHT+5, -1);
                                    Utils.GetMC().fontRendererObj.drawStringWithShadow(ChatFormatting.RED+""+ChatFormatting.BOLD+"is higher than usual!", 190, 10*Utils.GetMC().fontRendererObj.FONT_HEIGHT+5, -1);
                                }
                                if(stack.getDisplayName().contains("Minion Skin")) {
                                    Utils.drawGraySquareWithBorder(180, 9*Utils.GetMC().fontRendererObj.FONT_HEIGHT, 170, 3*Utils.GetMC().fontRendererObj.FONT_HEIGHT,3);
                                    Utils.GetMC().fontRendererObj.drawStringWithShadow(ChatFormatting.RED+""+ChatFormatting.BOLD+"Warning! Minion skins are", 190, 9*Utils.GetMC().fontRendererObj.FONT_HEIGHT+5, -1);
                                    Utils.GetMC().fontRendererObj.drawStringWithShadow(ChatFormatting.RED+""+ChatFormatting.BOLD+"often manipulated!!", 190, 10*Utils.GetMC().fontRendererObj.FONT_HEIGHT+5, -1);
                                }
                            }
                        }
                    }
                }
            }

            if(chestName.contains("Create")) {
                boolean alreadyGotIt = false;
                for(Slot slot:gui.inventorySlots.inventorySlots) {
                    if (slot.getHasStack() && slot.getSlotIndex() == 13 && !alreadyGotIt) {
                        alreadyGotIt = true;
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
                                Utils.GetMC().fontRendererObj.drawStringWithShadow(line, 190, lineCount*(Utils.GetMC().fontRendererObj.FONT_HEIGHT+1)+10, -1);
                                lineCount++;
                            }
                            currentlySellingStack = stack;
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
                    Utils.GetMC().fontRendererObj.drawStringWithShadow(line, 190, lineCount*(Utils.GetMC().fontRendererObj.FONT_HEIGHT+1)+10, -1);
                    lineCount++;
                }
            }
            
            if(chestName.contains("Your Bids")) {
                int ended = 0;
                int winning = 0;
                int losing = 0;
                List<String> winningAuctions = new ArrayList<String>();
                for (Auction auction : selfItems) {
                    ItemStack stack = auction.stack;
                    for(String line : ItemUtil.getItemLore(stack)) {
                        line = Utils.cleanColour(line);
                        if(line.contains("Ended")) {
                            ended++;
                        }
                        if(line.contains(Utils.GetMC().thePlayer.getName()) && !winningAuctions.contains(auction.identifer)) {
                            winning++;
                            winningAuctions.add(auction.identifer);
                        }
                        else if(line.contains("Bidder")) {
                            int x = auction.slot.xDisplayPosition;
                            int y = auction.slot.yDisplayPosition;
                            if(skyblockfeatures.config.highlightlosingAuction) Gui.drawRect(x, y, x + 16, y + 16, new Color(255, 35, 35).getRGB());
                            losing++;
                            // endedAuctions.add(stack);
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
                    ChatFormatting.WHITE+"Total Profit: "+ChatFormatting.GOLD+NumberUtil.nf.format(profit)
                };
                int lineCount = 0;
                for(String line:lines) {
                    Utils.GetMC().fontRendererObj.drawStringWithShadow(line, 190, lineCount*(Utils.GetMC().fontRendererObj.FONT_HEIGHT+1)+10, -1);
                    lineCount++;
                }
            }
        }
    }
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public AuctionFeatures() {
        pricePaidFile = new File(skyblockfeatures.modDir, "pricePaid.json");
        readConfig();
    }    

    public static void readConfig() {
        if(Utils.GetMC().thePlayer==null) return;
        JsonObject file;
        try (FileReader in = new FileReader(pricePaidFile)) {
            file = gson.fromJson(in, JsonObject.class);
            for (Map.Entry<String, JsonElement> e : file.entrySet()) {
                pricePaidMap.put(e.getKey(), e.getValue().getAsInt());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveConfig() {
        try (FileWriter writer = new FileWriter(pricePaidFile)) {
            gson.toJson(pricePaidMap, writer);
        } catch (Exception ignored) {

        }
    }
}
