package mrfast.skyblockfeatures.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.ItemStack;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.realmsclient.gui.ChatFormatting;

import org.apache.commons.io.IOUtils;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import mrfast.skyblockfeatures.core.AuctionUtil;
import mrfast.skyblockfeatures.utils.APIUtil;
import mrfast.skyblockfeatures.utils.Utils;


public class AHCommand extends CommandBase {

    public static int apiPage = 0;

	@Override
    public String getCommandName() {
        return "skyblockfeaturesah";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/skyblockfeaturesah";
    }

	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}

    boolean first = false;
	
	@Override
	public void processCommand(ICommandSender arg0, String[] args) throws CommandException {
        try {
            Utils.SendMessage(ChatFormatting.GREEN+"Loading Auctions this may take a moment..");
            Thread.sleep(250);
            reloadAuctions();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}

    private static Gson gson = new Gson();
    private static HashSet<String> auctionIds = new HashSet<>();
    public static List<String> sortedAuctionIds = new ArrayList<>();
    private static TreeMap<String, Auction> auctionMap = new TreeMap<>();

    public static JsonObject getApiGZIPSync(String urlS) throws IOException {
        URL url = new URL(urlS);
        URLConnection connection = url.openConnection();
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);

        String response = IOUtils.toString(new GZIPInputStream(connection.getInputStream()), StandardCharsets.UTF_8);

        JsonObject json = gson.fromJson(response, JsonObject.class);
        return json;
    }

    public static void reloadAuctions() {
        try {
            String profileURL = "https://api.hypixel.net/skyblock/auctions";
			JsonObject jsonObject = APIUtil.getResponse(profileURL);
            if(jsonObject.get("success").getAsBoolean()) {
                JsonArray new_auctions = jsonObject.get("auctions").getAsJsonArray();
                for(JsonElement auctionElement : new_auctions) {
                    JsonObject auction = auctionElement.getAsJsonObject();
                    String item_bytes = auction.get("item_bytes").getAsString();
                    NBTTagCompound item_tag = CompressedStreamTools.readCompressed(new ByteArrayInputStream(Base64.getDecoder().decode(item_bytes)));
                    Utils.SendMessage(item_tag+" Auction!");
                    JsonObject item = AuctionUtil.getJsonFromNBT(item_tag);
                    String id = auction.get("auctioneer").getAsString();
                    long end = auction.get("end").getAsLong();
                    int starting_bid = auction.get("starting_bid").getAsInt();
                    int highest_bid_amount = auction.get("highest_bid_amount").getAsInt();
                    int bid_count = auction.get("bids").getAsJsonArray().size();
                    ItemStack stack = AuctionUtil.jsonToStack(item, false);
                    
                    if(auction.has("bin")) {
                        Auction auction1 = new Auction(id, stack, end, starting_bid, highest_bid_amount, bid_count);
                        if(auction1 != null) {
                            auctionMap.put(id, auction1);
                            for(Map.Entry<String, Auction> auc: auctionMap.entrySet()) {
                                if(!auctionIds.contains(auc.getKey())) {
                                    auctionIds.add(auc.getKey());
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            
        }
    }

    public static class Auction {
        public ItemStack stack;
        public long end;
        public int starting_bid;
        public int highest_bid_amount;
        public int bid_count;
        public String item_tag_str;

        public Auction(String item_tag_str, ItemStack stack, long end, int starting_bid, int highest_bid_amount, int bid_count) {
            this.item_tag_str = item_tag_str;
            this.stack = stack;
            this.end = end;
            this.starting_bid = starting_bid;
            this.highest_bid_amount = highest_bid_amount;
            this.bid_count = bid_count;
        }
    }

    public static void sortItems() throws ConcurrentModificationException {
        try {
            List<String> sortedAuctionIdsNew = new ArrayList<>();
            
            sortedAuctionIdsNew.addAll(auctionIds);
            sortedAuctionIdsNew.sort((o1, o2) -> {
                Auction auc1 = getAuctionItems().get(o1);
                Auction auc2 = getAuctionItems().get(o2);
                
                if (auc1 == null) return 1;
                if (auc2 == null) return -1;

                long end1 = auc1.end;
                long end2 = auc2.end;

                int diff = (int) (end1 - end2);
                if (diff != 0) {
                    return diff;
                }
                return o1.compareTo(o2);
            });
            sortedAuctionIds = sortedAuctionIdsNew;
        } catch (Exception e) {
            sortItems();
        }
    }

    public static TreeMap<String, Auction> getAuctionItems() {
        return auctionMap;
    }
}
