/*
 * skyblockfeatures - Hypixel Skyblock Quality of Life Mod
 * Copyright (C) 2021 skyblockfeatures
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package mrfast.skyblockfeatures.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.BlockPos;
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

    private static void loadData() {
        String dataUrl = skyblockfeatures.config.dataURL;
        new Thread(() -> {
            JsonObject fetchurData = APIUtil.getJSONResponse(dataUrl + "solvers/fetchur.json");
            for (Map.Entry<String, JsonElement> solution : fetchurData.entrySet()) {
                MiningFeatures.fetchurItems.put(solution.getKey(), solution.getValue().getAsString());
            }
            JsonArray threeWeirdosSolutions = APIUtil.getArrayResponse(dataUrl + "solvers/threeweirdos.json");
            for (Map.Entry<String, JsonElement> sellPrice : APIUtil.getJSONResponse(dataUrl + "constants/sellprices.json").entrySet()) {
                ItemFeatures.sellPrices.put(sellPrice.getKey(), sellPrice.getValue().getAsDouble());
            }
        }).start();
    }

    private static void clearData() {
        ItemFeatures.sellPrices.clear();
        MiningFeatures.fetchurItems.clear();
    }

    public static void reloadData() {
        clearData();
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
