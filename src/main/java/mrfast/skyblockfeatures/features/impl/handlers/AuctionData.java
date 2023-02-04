package mrfast.skyblockfeatures.features.impl.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import gg.essential.api.gui.EssentialGUI;
import gg.essential.vigilance.VigilanceConfig;
import gg.essential.vigilance.gui.VigilancePalette;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.commons.lang3.time.StopWatch;
import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.core.AuctionUtil;
import mrfast.skyblockfeatures.utils.APIUtil;
import mrfast.skyblockfeatures.utils.ItemUtil;
import mrfast.skyblockfeatures.utils.Utils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AuctionData {

    public static final String dataURL = "https://moulberry.codes/lowestbin.json";
    public static final HashMap<String, Double> lowestBINs = new HashMap<>();
    public static final HashMap<String, Double> averageLowestBINs = new HashMap<>();
    public static final HashMap<String, Double> bazaarPrices = new HashMap<>();
    static JsonObject auctionPricesJson = null;
    public static final StopWatch reloadTimer = new StopWatch();

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static int getTier(String str) {
        int tier = 0;
        switch (str) {
            case "COMMON":tier = 0;break;
            case "UNCOMMON":tier = 1;break;
            case "RARE":tier = 2;break;
            case "EPIC":tier = 3;break;
            case "LEGENDARY":tier = 4;break;
            case "MYTHIC":tier = 5;break;
        }
        return tier;
    }
    public static String getIdentifier(ItemStack item) {
        NBTTagCompound extraAttr = ItemUtil.getExtraAttributes(item);
        String id = ItemUtil.getSkyBlockItemID(extraAttr);
        if (id == null) return null;
        switch (id) {
            case "PET":
                if (extraAttr.hasKey("petInfo")) {
                    JsonObject petInfo = gson.fromJson(extraAttr.getString("petInfo"), JsonObject.class);
                    if (petInfo.has("type") && petInfo.has("tier")) {
                        id = petInfo.get("type").getAsString() + ";" + getTier(petInfo.get("tier").getAsString());
                    }
                }
            break;
            case "ENCHANTED_BOOK":
                if (extraAttr.hasKey("enchantments")) {
                    NBTTagCompound enchants = extraAttr.getCompoundTag("enchantments");
                    if (!enchants.hasNoTags()) {
                        String enchant = enchants.getKeySet().iterator().next();
                        id = "ENCHANTMENT_"+enchant.toUpperCase(Locale.US) + "_" + enchants.getInteger(enchant);
                    }
                }
            break;
            case "POTION":
                if (extraAttr.hasKey("potion") && extraAttr.hasKey("potion_level")) {
                    id = "POTION";
                }
            break;
        }
        return id;
    }

    public static JsonObject getItemAuctionInfo(String internalname) {
		if (auctionPricesJson == null) return null;
		JsonElement e = auctionPricesJson.get(internalname);
		if (e == null) {
			return null;
		}
		return e.getAsJsonObject();
	}

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START || !Utils.inSkyblock) return;
        if (reloadTimer.getTime() >= 90000 || !reloadTimer.isStarted()) {
            if(reloadTimer.getTime() >= 90000) reloadTimer.reset();
            else reloadTimer.start();
            new Thread(() -> {
                JsonObject data = APIUtil.getJSONResponse(dataURL);
                for (Map.Entry<String, JsonElement> items : data.entrySet()) {
                    lowestBINs.put(items.getKey(), Math.floor(items.getValue().getAsDouble()));
                }
                AuctionUtil.getMyApiGZIPAsync("https://moulberry.codes/auction_averages_lbin/1day.json.gz", (jsonObject) -> {
                    for (Map.Entry<String, JsonElement> items : jsonObject.entrySet()) {
                        averageLowestBINs.put(items.getKey(), Math.floor(items.getValue().getAsDouble()));
                    }
                }, ()->{});
            }, "skyblockfeatures-FetchAuctionData").start();
            if (skyblockfeatures.config.auctionGuis || skyblockfeatures.config.autoAuctionFlip) {
                new Thread(() -> {
                    AuctionUtil.getMyApiGZIPAsync("https://moulberry.codes/auction_averages/3day.json.gz", (jsonObject) -> {
                        auctionPricesJson = jsonObject;
                    }, ()->{});
                }, "skyblockfeatures-FetchAuctionStuff").start();
            }
            if (bazaarPrices.size() == 0 && skyblockfeatures.config.apiKey.length()>1) {
                new Thread(() -> {
                    JsonObject data = APIUtil.getJSONResponse("https://api.hypixel.net/skyblock/bazaar?key="+skyblockfeatures.config.apiKey);
                    JsonObject products = data.get("products").getAsJsonObject();
                    for (Map.Entry<String, JsonElement> entry : products.entrySet()) {
                        if (entry.getValue().isJsonObject()) {
                            JsonObject product = entry.getValue().getAsJsonObject();
                            JsonObject quickStatus = product.get("quick_status").getAsJsonObject();
                            Double sellPrice = Math.floor(quickStatus.get("sellPrice").getAsDouble());
                            String id = quickStatus.get("productId").toString().split(":")[0];
                            bazaarPrices.put(id.replace("\"", ""), sellPrice);
                        }
                    }
                }, "skyblockfeatures-FetchBazaarData").start();
            }
        }
    }
}
