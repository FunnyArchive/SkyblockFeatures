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

package mrfast.skyblockfeatures.features.impl.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.commons.lang3.time.StopWatch;
import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.utils.APIUtil;
import mrfast.skyblockfeatures.utils.ItemUtil;
import mrfast.skyblockfeatures.utils.Utils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AuctionData {

    public static final String dataURL = "https://moulberry.codes/lowestbin.json";
    public static final HashMap<String, Double> lowestBINs = new HashMap<>();
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
                        id = enchant.toUpperCase(Locale.US) + ";" + enchants.getInteger(enchant);
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

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START || !Utils.inSkyblock) return;
        
        if (reloadTimer.getTime() >= 90000 || !reloadTimer.isStarted()) {
            System.out.println("A");
            if(reloadTimer.getTime() >= 90000) reloadTimer.reset();
            else reloadTimer.start();
            if (skyblockfeatures.config.showLowestBINPrice || skyblockfeatures.config.dungeonChestProfit) {
                System.out.println("Reloading Auction Prices");
                new Thread(() -> {
                    JsonObject data = APIUtil.getJSONResponse(dataURL);
                    for (Map.Entry<String, JsonElement> items : data.entrySet()) {
                        lowestBINs.put(items.getKey(), items.getValue().getAsDouble());
                    }
                }, "skyblockfeatures-FetchAuctionData").start();
            }
        }
    }

}
