package mrfast.skyblockfeatures.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.features.impl.mining.MiningFeatures;
import mrfast.skyblockfeatures.features.impl.misc.ItemFeatures;
import mrfast.skyblockfeatures.utils.APIUtil;
import mrfast.skyblockfeatures.utils.Utils;

import java.util.Map;

public class DataFetcher {

    private static long lastReload = 0;

    private static void clearData() {
        ItemFeatures.sellPrices.clear();
    }

    public static void reloadData() {
        clearData();
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
