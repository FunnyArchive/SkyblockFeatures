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

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.events.SendChatMessageEvent;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.Map;

public class CommandAliases {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Minecraft mc = Minecraft.getMinecraft();

    private static File aliasesFile;
    public static final HashMap<String, String> aliases = new HashMap<>();

    public CommandAliases() {
        aliasesFile = new File(skyblockfeatures.modDir, "commandaliases.json");
        reloadAliases();
    }

    public static void reloadAliases() {
        aliases.clear();
        JsonObject aliasesObject;
        try (FileReader in = new FileReader(aliasesFile)) {
            aliasesObject = gson.fromJson(in, JsonObject.class);
        } catch (Exception e) {
            aliasesObject = new JsonObject();
            try (FileWriter writer = new FileWriter(aliasesFile)) {
                gson.toJson(aliasesObject, writer);
            } catch (Exception ignored) {

            }
        }
        for (Map.Entry<String, JsonElement> alias : aliasesObject.entrySet()) {
            aliases.put(alias.getKey(), alias.getValue().getAsString());
        }
    }

    public static void saveAliases() {
        try (FileWriter writer = new FileWriter(aliasesFile)) {
            JsonObject obj = new JsonObject();
            for (Map.Entry<String, String> alias : aliases.entrySet()) {
                obj.addProperty(alias.getKey(), alias.getValue());
            }
            gson.toJson(obj, writer);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    @SubscribeEvent
    public void onSendChatMessage(SendChatMessageEvent event) {
        if (event.message.startsWith("/")) {
            ArrayList<String> args = Lists.newArrayList(event.message.substring(1).trim().split(" +"));
	        String command = args.remove(0);
	        if (aliases.containsKey(command)) {
                event.setCanceled(true);
                try {
                    String msg = "/" + aliases.get(command) + " " + String.join(" ", args);
                    if (event.addToChat) {
                        mc.ingameGUI.getChatGUI().addToSentMessages(msg);
                    }
                    if (ClientCommandHandler.instance.executeCommand(mc.thePlayer, msg) != 0) return;

                    skyblockfeatures.sendMessageQueue.add(msg);
                } catch(IllegalFormatException ignored) {
                    if (event.addToChat) mc.ingameGUI.getChatGUI().addToSentMessages(event.message);
                    mc.thePlayer.addChatMessage(new ChatComponentText("§cYou did not specify the correct amount of arguments for this alias!"));
                }
            }
        }
    }
}
