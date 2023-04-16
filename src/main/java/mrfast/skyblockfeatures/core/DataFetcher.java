package mrfast.skyblockfeatures.core;

import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import mrfast.skyblockfeatures.features.impl.misc.ItemFeatures;
import mrfast.skyblockfeatures.utils.APIUtil;
import mrfast.skyblockfeatures.utils.Utils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class DataFetcher {

    private static long lastReload = 0;

    public DataFetcher() {
        loadData();
    }

    private static void loadData() {
        new Thread(() -> {
            for (Map.Entry<String, JsonElement> sellPrice : APIUtil.getJSONResponse("https://skytilsmod-data.pages.dev/constants/sellprices.json").entrySet()) {
                ItemFeatures.sellPrices.put(sellPrice.getKey(), sellPrice.getValue().getAsDouble());
            }
        }).start();
    }

    public static void reloadData() {
        loadData();
    }

    public static String[] getStringArrayFromJsonArray(JsonArray jsonArray) {
        int arraySize = jsonArray.size();
        String[] stringArray = new String[arraySize];

        for (int i = 0; i < arraySize; i++) {
            stringArray[i] = jsonArray.get(i).getAsString();
        }

        return stringArray;
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (!Utils.inSkyblock) return;
        if (System.currentTimeMillis() - lastReload > 60 * 60 * 1000) {
            lastReload = System.currentTimeMillis();
            reloadData();
        }
    }
}
