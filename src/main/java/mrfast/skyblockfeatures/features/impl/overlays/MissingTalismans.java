package mrfast.skyblockfeatures.features.impl.overlays;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.realmsclient.gui.ChatFormatting;

import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.events.GuiContainerEvent;
import mrfast.skyblockfeatures.events.GuiContainerEvent.TitleDrawnEvent;
import mrfast.skyblockfeatures.features.impl.handlers.AuctionData;
import mrfast.skyblockfeatures.utils.APIUtil;
import mrfast.skyblockfeatures.utils.NumberUtil;
import mrfast.skyblockfeatures.utils.Utils;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class MissingTalismans {
    boolean inAccessoryBag = false;
    JsonArray MissingTalismans = null;
    @SubscribeEvent
    public void onCloseWindow(GuiContainerEvent.CloseWindowEvent event) {
        MissingTalismans = null;
    }
    boolean thread = false;
    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if(!skyblockfeatures.config.showMissingAccessories) return;
        if(Utils.GetMC().currentScreen != null) {
            if(Utils.GetMC().currentScreen instanceof GuiChest) {
                GuiChest gui = (GuiChest) Utils.GetMC().currentScreen;
                ContainerChest chest = (ContainerChest) gui.inventorySlots;
                IInventory inv = chest.getLowerChestInventory();
                String chestName = inv.getDisplayName().getUnformattedText().trim();
                inAccessoryBag = chestName.contains("Accessory Bag");
            }
        } else {
            inAccessoryBag = false;
        }
        if(inAccessoryBag) {
            if(MissingTalismans==null) {
                new Thread(() -> {
                    String username = Utils.GetMC().thePlayer.getName();
                    String uuid = APIUtil.getUUID(username);
                    // Find stats of latest profile
                    String latestProfile = APIUtil.getLatestProfileID(uuid, "");
                    if (latestProfile == null) {
                        Utils.SendMessage("no latest profile");
                        return;
                    };
                    
                    String profileURL = "https://sky.shiiyu.moe/api/v2/profile/"+username;
                    JsonObject profileResponse = APIUtil.getJSONResponse(profileURL);
                    profileResponse = profileResponse.get("profiles").getAsJsonObject();
                    MissingTalismans = profileResponse.get(latestProfile).getAsJsonObject().get("data").getAsJsonObject().get("missingAccessories").getAsJsonObject().get("missing").getAsJsonArray();
                }).start();
            }
        }
    }

    @SubscribeEvent
    public void onDrawContainerTitle(TitleDrawnEvent event) {
        if(!skyblockfeatures.config.showMissingAccessories) return;
        if (event.gui != null && event.gui instanceof GuiChest) {
            if(inAccessoryBag) {
                if(MissingTalismans==null) {
                    Utils.drawGraySquareWithBorder(180, 0, 200, 3*Utils.GetMC().fontRendererObj.FONT_HEIGHT,3);
                    Utils.GetMC().fontRendererObj.drawStringWithShadow(ChatFormatting.RED+"Loading..", 190, 10, -1);
                    return;
                }
                Utils.drawGraySquareWithBorder(180, 0, 200, MissingTalismans.size()*Utils.GetMC().fontRendererObj.FONT_HEIGHT,3);
                int index = 0;
                Utils.GetMC().fontRendererObj.drawStringWithShadow(ChatFormatting.WHITE+"Missing Talismans ("+MissingTalismans.size()+")", 190, index*(Utils.GetMC().fontRendererObj.FONT_HEIGHT+1)+10, -1);
                index++;
                for(JsonElement item:MissingTalismans) {
                    String id = item.getAsJsonObject().get("name").getAsString();
                    String name = item.getAsJsonObject().get("display_name").getAsString();
                    Double value = AuctionData.lowestBINs.get(id);
                    if(value!=null) {
                        String price = ChatFormatting.GOLD+" ("+(NumberUtil.nf.format(value))+")";
                        Utils.GetMC().fontRendererObj.drawStringWithShadow(name+price, 190, index*(Utils.GetMC().fontRendererObj.FONT_HEIGHT+1)+10, -1);
                        index++;
                    } else {
                        Utils.GetMC().fontRendererObj.drawStringWithShadow(name, 190, index*(Utils.GetMC().fontRendererObj.FONT_HEIGHT+1)+10, -1);
                        index++;
                    }
                }
            }
        }
    }
}
