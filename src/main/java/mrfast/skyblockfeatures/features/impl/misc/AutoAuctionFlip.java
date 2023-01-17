package mrfast.skyblockfeatures.features.impl.misc;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.codec.binary.Base64InputStream;
import org.lwjgl.input.Keyboard;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.realmsclient.gui.ChatFormatting;

import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.core.AuctionUtil;
import mrfast.skyblockfeatures.core.structure.FloatPair;
import mrfast.skyblockfeatures.core.structure.GuiElement;
import mrfast.skyblockfeatures.events.GuiContainerEvent;
import mrfast.skyblockfeatures.features.impl.ItemFeatures.HideGlass;
import mrfast.skyblockfeatures.features.impl.handlers.AuctionData;
import mrfast.skyblockfeatures.utils.APIUtil;
import mrfast.skyblockfeatures.utils.NumberUtil;
import mrfast.skyblockfeatures.utils.SBInfo;
import mrfast.skyblockfeatures.utils.Utils;
import mrfast.skyblockfeatures.utils.graphics.ScreenRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;


// COFL & TFM IS FOR NERDS!!
public class AutoAuctionFlip {
    static int seconds = 50;
    static int auctionsFilteredThrough = 0;
    static int auctionsPassedFilteredThrough = 0;

    Auction bestAuction = null;
    static boolean sent = false;
    static boolean clicking = false;
    static boolean clicking2 = false;
    List<Auction> auctionFlips = new ArrayList<>();
     
    public class Auction {
        String auctionId = "";
        JsonObject item_Data = null;
        Double profit = 0d;

        public Auction(String aucId,JsonObject itemData,Double profit,String itemName) {
            this.profit=profit;
            this.auctionId=aucId;
            this.item_Data=itemData;
        }
    }

    public class TotalAuction {
        String name = "";
        Double price = 0d;

        public TotalAuction(String name,Double price) {
            this.price=price;
            this.name=name;
        }
    }
    
    @SubscribeEvent
	public void onClick(GuiContainerEvent.SlotClickEvent event) {
        if (Utils.GetMC().currentScreen instanceof GuiChest) {
            GuiChest gui = (GuiChest) Utils.GetMC().currentScreen;
            ContainerChest chest = (ContainerChest) gui.inventorySlots;
            IInventory inv = chest.getLowerChestInventory();
            String chestName = inv.getDisplayName().getUnformattedText().trim();
            try {
                if(!chestName.contains("Ultrasequencer")) {
                    if(HideGlass.isEmptyGlassPane(event.container.getSlot(event.slotId).getStack())) event.setCanceled(true);
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
            if(Utils.inDungeons || !skyblockfeatures.config.autoAuctionFlipEasyBuy) return;

            try {
                if(event.container.getSlot(event.slotId).getStack().getDisplayName().contains("Cancel") || event.container.getSlot(event.slotId).getStack().getDisplayName().contains("Go Back") || event.container.getSlot(event.slotId).getStack().getDisplayName().contains("Close")) {
                    return;
                }
            } catch (Exception e) {
                // TODO: handle exception
            }

            if(chestName.contains("BIN Auction View") && !clicking) {
                clicking = true;
                Utils.GetMC().playerController.windowClick(Utils.GetMC().thePlayer.openContainer.windowId, 31, 0, 0, Utils.GetMC().thePlayer);
                Utils.setTimeout(()->{
                    AutoAuctionFlip.clicking = false;
                },500);
            }
            if(chestName.contains("Confirm Purchase") && !clicking2) {
                clicking2 = true;
                Utils.GetMC().playerController.windowClick(Utils.GetMC().thePlayer.openContainer.windowId, 11, 0, 0, Utils.GetMC().thePlayer);
                Utils.setTimeout(()->{
                    AutoAuctionFlip.clicking2 = false;
                },500);
            }
        }
    }

    int lastSecond = -1;
    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if(Utils.inDungeons || !skyblockfeatures.config.autoAuctionFlip) return;
        if (Keyboard.isKeyDown(skyblockfeatures.openBestFlipKeybind.getKeyCode()) && Utils.GetMC().currentScreen==null) {
            if(auctionFlips.size()>0 && !sent) {
                bestAuction = auctionFlips.get(0);
                Utils.GetMC().thePlayer.sendChatMessage("/viewauction "+bestAuction.auctionId);
                sent = true;
                auctionFlips.remove(auctionFlips.get(0));
                Utils.setTimeout(()-> {
                    AutoAuctionFlip.sent = false;
                }, 1000);
            } else {
                Utils.SendMessage(ChatFormatting.RED+"Best flip not found! Keep holding to open next.");
            }
        }

        Date date = new Date();
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        seconds = calendar.get(Calendar.SECOND)+1;
        if(lastSecond == seconds) return;
        lastSecond = seconds;
        if(!skyblockfeatures.config.autoAuctionFlip || Utils.inDungeons) return;
        if(seconds == 10) {
            Utils.SendMessage(ChatFormatting.GRAY+"Filtered out "+NumberUtil.nf.format((auctionsFilteredThrough-auctionsPassedFilteredThrough))+" auctions in the past 60s");
        }
        if(seconds == 30) {
            auctionFlips.clear();
        }
        if(seconds == 47) {
            Utils.SendMessage(ChatFormatting.GRAY+"Scanning for auctions in 10s");
            auctionsFilteredThrough = 0;
            auctionsPassedFilteredThrough = 0;
        }
        if(seconds == 57 && Utils.GetMC().theWorld!=null && Utils.inSkyblock) {
            Utils.SendMessage(ChatFormatting.GRAY+"Scanning for auctions..");
            float pages = 1;
            JsonObject data2 = APIUtil.getJSONResponse("https://api.hypixel.net/skyblock/auctions?key="+skyblockfeatures.config.apiKey);
            pages = data2.get("totalPages").getAsFloat();
            for(int i=0;i<pages-1;i++) {
                int a = i;
                Utils.setTimeout(()->{
                    int index = a;
                    JsonObject data = APIUtil.getJSONResponse("https://api.hypixel.net/skyblock/auctions?key="+skyblockfeatures.config.apiKey+"&page="+index);
                    JsonArray products = data.get("auctions").getAsJsonArray();
                    doAuctionFlipStuff(products);
                }, i*75);
            }
            Utils.setTimeout(()->{
                try {
                    bestAuction = auctionFlips.get(0);
                    if(skyblockfeatures.config.autoAuctionFlipOpen) {
                        if(bestAuction != null) {
                            Utils.GetMC().thePlayer.sendChatMessage("/viewauction "+bestAuction.auctionId);
                            auctionFlips.remove(auctionFlips.get(0));
                        }
                    }
                } catch (Exception e) {
                    Utils.SendMessage(ChatFormatting.RED+"No flips that match your filter found!");
                }
            }, (int) (75*pages));
        }
    }
    
    public static double getEnchantWorth(String enchant, int i) {
        String id = "ENCHANTMENT_"+enchant.toUpperCase()+"_"+i;
        if(enchant.contains("scavenger")) return 0;
        if(enchant.contains("protection")) return 0;
        if(enchant.contains("growth") && i==6) return 0;

        if(AuctionData.bazaarPrices.get(id)!=null) return AuctionData.bazaarPrices.get(id)/3;
        else return 0;
    }

    public static double starValue(String name) {
        int countOfStars = name.replaceAll("[^✪]", "").length();
        double EssenceCostPer = AuctionData.bazaarPrices.get("ESSENCE_WITHER");
        if(name.contains("Dragon")) {
            EssenceCostPer = AuctionData.bazaarPrices.get("ESSENCE_DRAGON");
        }
        double total = countOfStars>0?25:0;
        for(int i=0;i<countOfStars;i++) {
            total*=1.4;
        }
        return total*EssenceCostPer;
    }
    HashMap<String,Double> totalAuctions = new HashMap<>();

    public void doAuctionFlipStuff(JsonArray products) {
        for (JsonElement entry : products) {
            if (entry.isJsonObject()) {
                JsonObject itemData = entry.getAsJsonObject();
                if(itemData.get("bin").getAsBoolean()) {
                    try {
                        String item_bytes = itemData.get("item_bytes").getAsString();
                        Base64InputStream is = new Base64InputStream(new ByteArrayInputStream(item_bytes.getBytes(StandardCharsets.UTF_8)));
                        NBTTagCompound nbt = CompressedStreamTools.readCompressed(is);
                        Double binPrice = itemData.get("starting_bid").getAsDouble();
                        String id = AuctionUtil.getInternalnameFromNBT(nbt.getTagList("i", 10).getCompoundTagAt(0).getCompoundTag("tag"));
                        String name = nbt.getTagList("i", 10).getCompoundTagAt(0).getCompoundTag("tag").getCompoundTag("display").getString("Name");
                        NBTTagCompound thing = nbt.getTagList("i", 10).getCompoundTagAt(0).getCompoundTag("tag").getCompoundTag("ExtraAttributes").getCompoundTag("enchantments");
                        Double lowestBinPrice = AuctionData.lowestBINs.get(id);
                        Double avgBinPrice = AuctionData.averageLowestBINs.get(id);
                        Float margin = Float.parseFloat(skyblockfeatures.config.autoAuctionFlipMargin);
                        Float minVolume = Float.parseFloat(skyblockfeatures.config.autoAuctionFlipMinVolume);
                        Float minPercent = Float.parseFloat(skyblockfeatures.config.autoAuctionFlipMinPercent);
                        JsonObject auctionData = AuctionData.getItemAuctionInfo(id);
                        if(lowestBinPrice==null||avgBinPrice==null) continue;
                        int volume = 5;
                        if(auctionData!=null) volume = auctionData.get("sales").getAsInt();
                        
                        String auctionId = itemData.get("uuid").toString().replaceAll("\"","");
                        Double enchantValue = 0d;
                        Double starValue = starValue(name);
                        for(String enchant:thing.getKeySet()) {enchantValue+=getEnchantWorth(enchant,thing.getInteger(enchant));}
                        Double priceToUse = lowestBinPrice;
                        // String cleanName = Utils.cleanColour(name);
                        // if(!totalAuctions.containsKey(cleanName)) {
                        //     totalAuctions.put(cleanName, binPrice);
                        // } else {
                        //     if(totalAuctions.get(cleanName)>binPrice) {
                        //         totalAuctions.remove(cleanName);
                        //         totalAuctions.put(cleanName, binPrice);
                        //     }
                        // }
                        // priceToUse = totalAuctions.get(cleanName020);

                        if(priceToUse>avgBinPrice*1.3) {
                            priceToUse=avgBinPrice;
                        }

                        Double valueOfTheItem = priceToUse+(skyblockfeatures.config.autoFlipAddEnchAndStar? enchantValue+starValue:0);
                        Double profit = valueOfTheItem-binPrice;

                        double percentage = Math.floor(((valueOfTheItem/binPrice)-1)*100);
                        String[] lore = itemData.get("item_lore").getAsString().split("Â");
                        // Utils.SendMessage("Checking Item: "+name);
                        auctionsFilteredThrough++;
                        // Filters
                        if(skyblockfeatures.config.autoAuctionFilterOutPets && id.contains("PET")) {
                            // System.out.println(name+" Removed Because Pet Filter"); 
                            continue;
                        }
                        if(skyblockfeatures.config.autoAuctionFilterOutSkins && id.contains("SKIN")) {
                            // System.out.println(name+" Removed Because Skin Filter"); 
                            continue;
                        }
                        if(skyblockfeatures.config.autoAuctionFilterOutRunes && itemData.get("item_name").getAsString().contains("Rune")) {
                            // System.out.println(name+" Removed Because Rune Filter"); 
                            continue;
                        }
                        if(skyblockfeatures.config.autoAuctionFilterOutDyes && itemData.get("item_name").getAsString().contains("Dye")) {
                            // System.out.println(name+" Removed Because Dye Filter"); 
                            continue;
                        }
                        if(skyblockfeatures.config.autoAuctionFilterOutFurniture && lore.toString().contains("Furniture")) {
                            // System.out.println(name+" Removed Because Furniture Filter"); 
                            continue;
                        }
                        if(volume<minVolume) {
                            // System.out.println(name+" Removed Because MinVol Filter"); 
                            continue;
                        }
                        if(percentage<minPercent) {
                            // System.out.println(name+" Removed Because MinPerc Filter Perc:"+percentage+" "+NumberUtil.nf.format(binPrice)+" "+NumberUtil.nf.format(valueOfTheItem)); 
                            continue;
                        }
                        if(profit<margin) {
                            // System.out.println(name+" Removed Because less than profit margin :"+profit); 
                            continue;
                        }
                        boolean inBlacklist = false;
                        if(skyblockfeatures.config.autoAuctionBlacklist.length()>1) {
                            try {
                                for(String blacklistedName:skyblockfeatures.config.autoAuctionBlacklist.split(";")) {
                                    if(Utils.cleanColour(name).toLowerCase().contains(blacklistedName)) inBlacklist = true;
                                }
                            } catch (Exception e) {
                                // TODO: handle exception
                            }
                        }
                        if(inBlacklist) continue;
                        if(skyblockfeatures.config.autoAuctionFlipSetPurse && SBInfo.getInstance().coins<binPrice) {
                            System.out.println(name+" Removed Because Purse Filter");
                            continue;
                        }
                        auctionsPassedFilteredThrough++;
                        
                        if(Utils.GetMC().currentScreen == null && auctionData!=null) {
                            Auction auction = new Auction(auctionId, itemData, profit,name);
                            auctionFlips.add(auction);

                            String iprofit = NumberUtil.format(profit.longValue());
                            String oPrice = NumberUtil.format(binPrice.longValue());
                            String itemValue = NumberUtil.format(valueOfTheItem.longValue());
                            String ePrice = NumberUtil.format(enchantValue.longValue());
                            String sPrice = NumberUtil.format(starValue.longValue());

                            IChatComponent message = new ChatComponentText("\n"+ChatFormatting.LIGHT_PURPLE+"[SBF] "+name+" "+ChatFormatting.GREEN+oPrice+" -> "+itemValue+" (+"+iprofit+" "+ChatFormatting.DARK_RED+percentage+"%"+ChatFormatting.GREEN+") "+
                            ChatFormatting.GRAY+"Vol: "+ChatFormatting.AQUA+(auctionData.get("sales").getAsInt())+" sales/day"+
                            (enchantValue>0?(ChatFormatting.GRAY+" Ench: "+ChatFormatting.AQUA+ePrice):"")+
                            (starValue>0?(ChatFormatting.GRAY+" Stars: "+ChatFormatting.AQUA+sPrice):""));
                            message.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/viewauction "+auctionId));
                            message.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(ChatFormatting.GREEN+"/viewauction "+auctionId)));

                            Utils.GetMC().thePlayer.addChatComponentMessage(message);
                        }
                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                        // Utils.SendMessage("ERROR ON AUCTION FILTERING");
                        // TODO: handle exception
                    }
                }
            }
        }
        auctionFlips.sort((a,b)->{
            return (int) (a.profit-b.profit);
        });
        List<Auction> reversed = new ArrayList<>();
        for(int i=0;i<auctionFlips.size();i++) {
            reversed.add(auctionFlips.get(auctionFlips.size()-i-1));
        }
        auctionFlips = reversed;
        // Utils.SendMessage("Auctions reveresed");
    }

    private static final Minecraft mc = Minecraft.getMinecraft();

    static {
        new AutoAuctionGui();
    }

    static String display = "Auction API update in 60s";
    
    public static class AutoAuctionGui extends GuiElement {
        public AutoAuctionGui() {
            super("Auto Auction Flip Counter", new FloatPair(0.25677082f, 0.4435921f));
            skyblockfeatures.GUIMANAGER.registerElement(this);
        }

        @Override
        public void render() {
            if(mc.thePlayer == null || !Utils.inSkyblock) return;
            if (this.getToggled() && Minecraft.getMinecraft().thePlayer != null && mc.theWorld != null) {
                Utils.drawTextWithStyle("Auction API update in "+(Math.max(57-seconds,0))+"s", 0, 0, 0x00FF00);
            }
        }
        @Override
        public void demoRender() {
            if(mc.thePlayer == null || !Utils.inSkyblock) return;
            Utils.drawTextWithStyle("Auction API update in 49s", 0, 0, 0x00FF00);
        }

        @Override   
        public boolean getToggled() {
            return Utils.inSkyblock && skyblockfeatures.config.autoAuctionFlipCounter;
        }

        @Override
        public int getHeight() {
            return ScreenRenderer.fontRenderer.FONT_HEIGHT;
        }

        @Override
        public int getWidth() {
            return ScreenRenderer.fontRenderer.getStringWidth(display);
        }
    }
}
