package mrfast.skyblockfeatures.features.impl.overlays;

import java.util.HashMap;
import java.util.List;

import com.google.gson.JsonObject;

import com.mojang.realmsclient.gui.ChatFormatting;

import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.commands.AccessoriesCommand;
import mrfast.skyblockfeatures.commands.AccessoriesCommand.Inventory;
import mrfast.skyblockfeatures.core.structure.FloatPair;
import mrfast.skyblockfeatures.core.structure.GuiElement;
import mrfast.skyblockfeatures.utils.Utils;
import mrfast.skyblockfeatures.utils.graphics.ScreenRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import mrfast.skyblockfeatures.events.SecondPassedEvent;
import mrfast.skyblockfeatures.utils.APIUtil;
import mrfast.skyblockfeatures.utils.NumberUtil;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BaitCounterOverlay {
    public static Integer seconds = 0;
    public static HashMap<String,Integer> typesOfBait = new HashMap<>();
    @SubscribeEvent
    public void onSecond(SecondPassedEvent event) {
        if(Utils.GetMC().thePlayer==null || !skyblockfeatures.config.baitCounter) return;
        if(seconds==0) {
            reloadFishingBag();
        }
        seconds++;

        if(seconds==61) {
            seconds = 1;
            reloadFishingBag();
        }
    }

    public void reloadFishingBag() {
        typesOfBait.clear();
        new Thread(() -> {
                String key = skyblockfeatures.config.apiKey;
                if (key.equals("")) return;
                
                // Get UUID for Hypixel API requests
                String uuid = APIUtil.getUUID(Utils.GetMC().thePlayer.getName());;
                String latestProfile = APIUtil.getLatestProfileID(uuid, key);
                if (latestProfile == null) return;

                String profileURL = "https://api.hypixel.net/skyblock/profile?profile=" + latestProfile + "&key=" + key;
                System.out.println("Fetching profile...");
                JsonObject profileResponse = APIUtil.getResponse(profileURL);
                if(profileResponse.toString().equals("{}")) {
                    Utils.SendMessage(EnumChatFormatting.RED + "Hypixel API is having problems!");
                    return;
                }

                if(profileResponse.get("profile").getAsJsonObject().get("members").getAsJsonObject().get(uuid).getAsJsonObject().has("fishing_bag")) {
                    String inventoryBase64 = profileResponse.get("profile").getAsJsonObject().get("members").getAsJsonObject().get(uuid).getAsJsonObject().get("fishing_bag").getAsJsonObject().get("data").getAsString();
                    Inventory items = new Inventory(inventoryBase64);
                    List<ItemStack> a = AccessoriesCommand.decodeItem(items);
                    for(ItemStack item: a) {
                        if(item==null) continue;
                        String name = item.getDisplayName();
                        Integer count = item.stackSize;
                        if(typesOfBait.containsKey(name)) {
                            typesOfBait.put(name, typesOfBait.get(name)+count);
                        } else {
                            typesOfBait.put(name, count);
                        }
                    }
                }
        }).start();
    }

    public static Minecraft mc = Utils.GetMC();
    static {
        new baitCounter();
    }   

    public static class baitCounter extends GuiElement {
        public baitCounter() {
            super("baitCounter", new FloatPair(0.6125f, 0.675f));
            skyblockfeatures.GUIMANAGER.registerElement(this);
        }

        @Override
        public void render() {
            if(mc.thePlayer == null || !Utils.inSkyblock || Utils.GetMC().theWorld==null || !skyblockfeatures.config.baitCounter) return;
            Utils.drawTextWithStyle3(ChatFormatting.AQUA+"Bait: "+ChatFormatting.GRAY+"("+(61-seconds)+")", 0, 0);
            int index = 0;
            if(typesOfBait.size()==0) {
                Utils.drawTextWithStyle3(" "+ChatFormatting.RED+"Loading..", 0, index*(Utils.GetMC().fontRendererObj.FONT_HEIGHT+1)+10);
            }
            for(String baitName:typesOfBait.keySet()) {
                Utils.drawTextWithStyle3(" "+baitName+ChatFormatting.DARK_GRAY+" x"+NumberUtil.nf.format(typesOfBait.get(baitName)), 0, index*(Utils.GetMC().fontRendererObj.FONT_HEIGHT+1)+10);
                index++;
            }
        }

        @Override
        public void demoRender() {
            if(mc.thePlayer == null || !Utils.inSkyblock) return;
            Utils.drawTextWithStyle3(ChatFormatting.AQUA+"Bait:", 0, 0);
            Utils.drawTextWithStyle3(ChatFormatting.WHITE+" Corrupted Bait "+ChatFormatting.DARK_GRAY+"x218", 0, 1*(10)+10);
            Utils.drawTextWithStyle3(ChatFormatting.GREEN+" Blessed Bait "+ChatFormatting.DARK_GRAY+"x381", 0, 2*(10)+10);
            Utils.drawTextWithStyle3(ChatFormatting.GREEN+" Shark Bait "+ChatFormatting.DARK_GRAY+"x313", 0, 3*(10)+10);
            Utils.drawTextWithStyle3(ChatFormatting.WHITE+" Corrupted Bait "+ChatFormatting.DARK_GRAY+"x831", 0, 4*(10)+10);
        }

        @Override
        public boolean getToggled() {
            return skyblockfeatures.config.baitCounter;
        }

        @Override
        public int getHeight() {
            return ScreenRenderer.fontRenderer.FONT_HEIGHT*5;
        }

        @Override
        public int getWidth() {
            return ScreenRenderer.fontRenderer.getStringWidth(ChatFormatting.WHITE+" Corrupted Bait "+ChatFormatting.DARK_GRAY+"x128   .");
        }
    }
}
