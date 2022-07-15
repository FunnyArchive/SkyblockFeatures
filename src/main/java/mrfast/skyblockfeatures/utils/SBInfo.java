package mrfast.skyblockfeatures.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonObject;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * Taken from NotEnoughUpdates under Creative Commons Attribution-NonCommercial 3.0
 * https://github.com/Moulberry/NotEnoughUpdates/blob/master/LICENSE
 *
 * @author Moulberry
 */
public class SBInfo {

    private static final SBInfo INSTANCE = new SBInfo();

    private static final Pattern timePattern = Pattern.compile(".+(am|pm)");
    public String location = "";
    public String date = "";
    public String currentProfile = null;
    public String time = "";
    public String objective = "";
    public String mode = "";
    public Date currentTimeDate = null;
    public String lastOpenContainerName = null;
    private static final String profilePrefix = "\u00a7r\u00a7e\u00a7lProfile: \u00a7r\u00a7a";
    public JsonObject locraw = null;

    public static SBInfo getInstance() {
        return INSTANCE;
    }

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        if (!Utils.inSkyblock) return;

        if (event.gui instanceof GuiChest) {
            GuiChest chest = (GuiChest) event.gui;
            ContainerChest container = (ContainerChest) chest.inventorySlots;
            String containerName = container.getLowerChestInventory().getDisplayName().getUnformattedText();

            lastOpenContainerName = containerName;
        }
    }

    @SubscribeEvent
    public void onWorldChange(WorldEvent.Load event) {
        // lastLocRaw = -1;
        locraw = null;
        mode = null;
        System.currentTimeMillis();
        lastOpenContainerName = null;
    }

    public String getLocation() {
        if (mode == null) {
            return null;
        }
        return mode;
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START || Minecraft.getMinecraft().thePlayer == null || Minecraft.getMinecraft().theWorld == null || !Utils.inSkyblock)
            return;

        try {
            Scoreboard scoreboard = Minecraft.getMinecraft().thePlayer.getWorldScoreboard();

            ScoreObjective sidebarObjective = scoreboard.getObjectiveInDisplaySlot(1); //§707/14/20

            List<Score> scores = new ArrayList<>(scoreboard.getSortedScores(sidebarObjective));

            List<String> lines = new ArrayList<>();
            for (int i = scores.size() - 1; i >= 0; i--) {
                Score score = scores.get(i);
                ScorePlayerTeam scoreplayerteam1 = scoreboard.getPlayersTeam(score.getPlayerName());
                String line = ScorePlayerTeam.formatPlayerName(scoreplayerteam1, score.getPlayerName());
                line = StringUtils.stripControlCodes(line);
                lines.add(line);
            }
            for(NetworkPlayerInfo info : Minecraft.getMinecraft().thePlayer.sendQueue.getPlayerInfoMap()) {
                String name = Minecraft.getMinecraft().ingameGUI.getTabList().getPlayerName(info);
                if(name.startsWith(profilePrefix)) {
                    currentProfile = Utils.cleanColour(name.substring(profilePrefix.length()));
                }
            }
            

            if (lines.size() >= 5) {
                date = StringUtils.stripControlCodes(lines.get(2)).trim();
                //§74:40am
                Matcher matcher = timePattern.matcher(lines.get(3));
                if (matcher.find()) {
                    time = StringUtils.stripControlCodes(matcher.group()).trim();
                    try {
                        String timeSpace = time.replace("am", " am").replace("pm", " pm");
                        SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm a");
                        currentTimeDate = parseFormat.parse(timeSpace);
                    } catch (ParseException e) {
                    }
                }
                location = StringUtils.stripControlCodes(lines.get(4)).replaceAll("[^A-Za-z0-9() ]", "").trim();
            }
            objective = null;

            boolean objTextLast = false;
            for (String line : lines) {
                if (objTextLast) {
                    objective = line;
                }

                objTextLast = line.equals("Objective");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}