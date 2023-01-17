package mrfast.skyblockfeatures.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.Scanner;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.features.impl.handlers.AuctionData;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

/**
 * Modified from Danker's Skyblock Mod under GPL 3.0 license
 * https://github.com/bowser0000/SkyblockMod/blob/master/LICENSE
 * @author bowser0000
 */
public class APIUtil {

    public static CloseableHttpClient client = HttpClients.custom().setUserAgent("skyblockfeatures/" + skyblockfeatures.VERSION).addInterceptorFirst((HttpRequestInterceptor) (request, context) -> {
        if (!request.containsHeader("Pragma")) request.addHeader("Pragma", "no-cache");
        if (!request.containsHeader("Cache-Control")) request.addHeader("Cache-Control", "no-cache");
    }).build();

    public static JsonObject getJSONResponse(String urlString) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        try {
            HttpGet request = new HttpGet(new URL(urlString).toURI());
            request.setProtocolVersion(HttpVersion.HTTP_1_1);

            HttpResponse response = client.execute(request);

            HttpEntity entity = response.getEntity();

            if (response.getStatusLine().getStatusCode() == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(entity.getContent()));
                String input;
                StringBuilder r = new StringBuilder();

                while ((input = in.readLine()) != null) {
                    r.append(input);
                }
                in.close();

                Gson gson = new Gson();

                return gson.fromJson(r.toString(), JsonObject.class);
            } else {
                if (urlString.startsWith("https://api.hypixel.net/") || urlString.equals(AuctionData.dataURL)) {
                    InputStream errorStream = entity.getContent();
                    try (Scanner scanner = new Scanner(errorStream)) {
                        scanner.useDelimiter("\\Z");
                        String error = scanner.next();

                        if (error.startsWith("{")) {
                            Gson gson = new Gson();
                            return gson.fromJson(error, JsonObject.class);
                        }
                    }
                } else if (urlString.startsWith("https://api.mojang.com/users/profiles/minecraft/") && response.getStatusLine().getStatusCode() == 204) {
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Failed with reason: Player does not exist."));
                } else {
                    // player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Request failed. HTTP Error Code: " + response.getStatusLine().getStatusCode()));
                }
            }
        } catch (IOException | URISyntaxException ex) {
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "An error has occured."));
            // ex.printStackTrace();
        }

        return new JsonObject();
    }

    // Only used for UUID => Username
    public static JsonArray getArrayResponse(String urlString) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;

        try {
            HttpGet request = new HttpGet(new URL(urlString).toURI());

            request.setProtocolVersion(HttpVersion.HTTP_1_1);

            HttpResponse response = client.execute(request);

            HttpEntity entity = response.getEntity();

            if (response.getStatusLine().getStatusCode() == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(entity.getContent()));
                String input;
                StringBuilder r = new StringBuilder();

                while ((input = in.readLine()) != null) {
                    r.append(input);
                }
                in.close();

                Gson gson = new Gson();

                return gson.fromJson(r.toString(), JsonArray.class);
            } else {
                // player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Request failed. HTTP Error Code: " + response.getStatusLine().getStatusCode()));
            }
        } catch (IOException | URISyntaxException ex) {
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "An error has occured."));
            // ex.printStackTrace();
        }
        return new JsonArray();
    }

    public static JsonObject getResponse(String urlString) {
        return getJSONResponse(urlString);
		// EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		
		// try {
		// 	URL url = new URL(urlString);
		// 	HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		// 	conn.setRequestMethod("GET");
			
		// 	if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
		// 		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		// 		String input;
		// 		StringBuilder response = new StringBuilder();
				
		// 		while ((input = in.readLine()) != null) {
		// 			response.append(input);
		// 		}
		// 		in.close();
				
		// 		Gson gson = new Gson();

		// 		return gson.fromJson(response.toString(), JsonObject.class);
		// 	} else {
		// 		if (urlString.startsWith("https://api.hypixel.net/")) {
		// 			InputStream errorStream = conn.getErrorStream();
		// 			try (Scanner scanner = new Scanner(errorStream)) {
		// 				scanner.useDelimiter("\\Z");
		// 				String error = scanner.next();
						
		// 				Gson gson = new Gson();
		// 				return gson.fromJson(error, JsonObject.class);
		// 			}
		// 		} else if (urlString.startsWith("https://api.mojang.com/users/profiles/minecraft/") && conn.getResponseCode() == 204) {
		// 			player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Failed with reason: Player does not exist."));
		// 		} else {
		// 			// player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Request failed. HTTP Error Code: " + conn.getResponseCode()));
		// 		}
		// 	}
		// } catch (IOException ex) {
		// 	player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "An error has occured. See logs for more details."));
		// 	ex.printStackTrace();
		// }

		// return new JsonObject();
	}

    public static String getUUID(String username) {
        JsonObject uuidResponse = getJSONResponse("https://api.mojang.com/users/profiles/minecraft/" + username);
        return uuidResponse.get("id").getAsString();
    }
    private static Gson gson = new Gson();
    public static String getName(String uuid) {
        try {
            try (Scanner scanner = new Scanner(new URL("https://api.mojang.com/user/profiles/" + uuid + "/names").openStream(), "UTF-8").useDelimiter("\\A")) {
                String json = scanner.next();
                JsonArray array = gson.fromJson(json, JsonArray.class);
                return array.get(array.size() - 1).getAsJsonObject().get("name").getAsString();
            }
        } catch (Exception e) {
            return null;
        }
      }

    public static String getLatestProfileID(String uuid, String key) {
        // Get profiles
        System.out.println("Fetching profiles...");
        JsonObject profilesResponse = getJSONResponse("https://sky.shiiyu.moe/api/v2/profile/"+uuid);
        if (profilesResponse.has("error")) {
            String reason = profilesResponse.get("error").getAsString();
            Utils.SendMessage(EnumChatFormatting.RED + "Failed with reason: " + reason);
            return null;
        }
        if(!profilesResponse.has("profiles")) {
            Utils.SendMessage(EnumChatFormatting.RED + "This player doesn't appear to have played SkyBlock.");
            return null;
        }

        System.out.println("Looping through profiles...");
        String latestProfile = "";
        JsonObject profilesArray = profilesResponse.get("profiles").getAsJsonObject();
        for (Map.Entry<String,JsonElement> profile : profilesArray.entrySet()) {
            System.out.println("Fetching profile: "+profile.getKey());
            JsonObject profileJSON = profile.getValue().getAsJsonObject();
            boolean selectedProfile = false;
            if (profileJSON.has("current")) {
                selectedProfile = profileJSON.get("current").getAsBoolean();
            }

            if (selectedProfile) {
                latestProfile = profile.getKey();
            }
        }
        return latestProfile;
    }
}
