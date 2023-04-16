package mrfast.skyblockfeatures.features.impl.dungeons.solvers;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.core.DataFetcher;
import mrfast.skyblockfeatures.utils.APIUtil;
import mrfast.skyblockfeatures.utils.RenderUtil;
import mrfast.skyblockfeatures.utils.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Original code was taken from Skytils under GNU Affero General Public License v3.0 and modified by MrFast
 *
 * @author Skytils Team
 * @link https://github.com/Skytils/SkytilsMod/blob/1.x/LICENSE.md
 */
public class TriviaSolver {
    public static HashMap<String, String[]> triviaSolutions = new HashMap<>();
    public static String[] triviaAnswers = null;
    public static String triviaAnswer = null;
    public static boolean startedThread = false;
    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onChat(ClientChatReceivedEvent event) {
        String unformatted = StringUtils.stripControlCodes(event.message.getUnformattedText());
        if (Utils.inDungeons && skyblockfeatures.config.quizSolver) {
            if (unformatted.startsWith("[STATUE] Oruo the Omniscient: ") && unformatted.contains("answered Question #") && unformatted.endsWith("correctly!")) triviaAnswer = null;

            if (!startedThread && unformatted.equals("[STATUE] Oruo the Omniscient: I am Oruo the Omniscient. I have lived many lives. I have learned all there is to know.") && triviaSolutions.size() == 0) {
                startedThread = true;
                new Thread(()->{
                    JsonObject obj = APIUtil.getJSONResponse("https://raw.githubusercontent.com/Skytils/SkytilsMod-Data/main/solvers/oruotrivia.json");
                    for(Entry<String, JsonElement> a :obj.entrySet()) {
                        String question = a.getKey();
                        triviaSolutions.put(question,DataFetcher.getStringArrayFromJsonArray(a.getValue().getAsJsonArray()));
                    }
                }).start();
            }
            if (unformatted.contains("What SkyBlock year is it?")) {
                double currentTime = System.currentTimeMillis() / 1000d;

                double diff = Math.floor(currentTime - 1560276000);

                int year = (int) (diff / 446400 + 1);
                triviaAnswers = new String[]{"Year " + year};
            } else {
                for (String question : triviaSolutions.keySet()) {
                    if (unformatted.contains(question)) {
                        triviaAnswers = triviaSolutions.get(question);
                        break;
                    }
                }
            }
            // Set wrong answers to red and remove click events
            if (triviaAnswers != null && (unformatted.contains("ⓐ") || unformatted.contains("ⓑ") || unformatted.contains("ⓒ"))) {
                String answer = null;
                boolean isSolution = false;
                for (String solution : triviaAnswers) {
                    if (unformatted.contains(solution)) {
                        isSolution = true;
                        answer = solution;
                        break;
                    }
                }
                if (!isSolution) {
                    char letter = unformatted.charAt(5);
                    String option = unformatted.substring(6);
                    event.message = new ChatComponentText("     " + EnumChatFormatting.GOLD + letter + EnumChatFormatting.RED + option);
                    return;
                } else {
                    triviaAnswer = answer;
                }
            }
        }
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        if (triviaAnswer != null && skyblockfeatures.config.quizSolver) {
            for(Entity entity:Utils.GetMC().theWorld.loadedEntityList) {
                if (entity instanceof EntityArmorStand && entity.hasCustomName()) {
                    String name = entity.getCustomNameTag();
                    if (name.contains("ⓐ") || name.contains("ⓑ") || name.contains("ⓒ")) {
                        if (name.contains(triviaAnswer)) {
                            AxisAlignedBB aabb = new AxisAlignedBB(entity.posX-1, entity.posY, entity.posZ-1, entity.posX+1, entity.posY+2.5, entity.posZ+1);
                            RenderUtil.drawOutlinedFilledBoundingBox(aabb, Color.green, event.partialTicks);
                        }
                    }
                }
            }
        }
    }
    @SubscribeEvent
    public void onWorldChange(WorldEvent.Load event) {
        triviaAnswer = null;
    }

}