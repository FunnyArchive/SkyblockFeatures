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

package mrfast.skyblockfeatures.features.impl.mining;

import java.awt.Color;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.core.DataFetcher;
import mrfast.skyblockfeatures.utils.RenderUtil;
import mrfast.skyblockfeatures.utils.ScoreboardUtil;
import mrfast.skyblockfeatures.utils.StringUtils;
import mrfast.skyblockfeatures.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MiningFeatures {

    public static LinkedHashMap<String, String> fetchurItems = new LinkedHashMap<>();

    private static final Minecraft mc = Minecraft.getMinecraft();
    private static BlockPos puzzlerSolution = null;


    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onChat(ClientChatReceivedEvent event) {
        if (!Utils.inSkyblock || event.type == 2) return;

        String unformatted = StringUtils.stripControlCodes(event.message.getUnformattedText());

        if (skyblockfeatures.config.puzzlerSolver && unformatted.startsWith("[NPC] Puzzler:")) {
            if (unformatted.contains("Nice")) {
                puzzlerSolution = null;
                return;
            }
            if (unformatted.contains("Wrong") || unformatted.contains("Come") || (!unformatted.contains("▶") && !unformatted.contains("▲") && !unformatted.contains("◀") && !unformatted.contains("▼"))) return;
            if (ScoreboardUtil.getSidebarLines().stream().anyMatch(line -> ScoreboardUtil.cleanSB(line).contains("Dwarven Mines"))) {
                puzzlerSolution = new BlockPos(181, 195, 135);
                String msg = unformatted.substring(15).trim();
                Matcher matcher = Pattern.compile("([▶▲◀▼]+)").matcher(unformatted);
                if (matcher.find()) {
                    String sequence = matcher.group(1).trim();
                    if (sequence.length() != msg.length()) {
                        System.out.println(String.format("%s - %s | %s - %s", sequence, msg, sequence.length(), unformatted.length()));
                    }
                    for (char c : sequence.toCharArray()) {
                        switch (String.valueOf(c)) {
                            case "▲":
                                puzzlerSolution = puzzlerSolution.south();
                                break;
                            case "▶":
                                puzzlerSolution = puzzlerSolution.west();
                                break;
                            case "◀":
                                puzzlerSolution = puzzlerSolution.east();
                                break;
                            case "▼":
                                puzzlerSolution = puzzlerSolution.north();
                                break;
                            default:
                                System.out.println("Invalid Puzzler character: " + c);
                        }
                    }
                    System.out.println("Puzzler Solution: " + puzzlerSolution);
                    mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Mine the block highlighted in " + EnumChatFormatting.RED + EnumChatFormatting.BOLD + "RED" + EnumChatFormatting.GREEN + "!"));
                }
            }
        }

        if (skyblockfeatures.config.fetchurSolver && unformatted.startsWith("[NPC] Fetchur:")) {
            if (fetchurItems.size() == 0) {
                mc.thePlayer.addChatMessage(new ChatComponentText("§cskyblockfeatures did not load any solutions."));
                DataFetcher.reloadData();
                return;
            }
            String solution = fetchurItems.keySet().stream().filter(unformatted::contains).findFirst().map(fetchurItems::get).orElse(null);
            new Thread(() -> {
                try {
                    Thread.sleep(2500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (solution != null) {
                    mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Fetchur needs: " + EnumChatFormatting.DARK_GREEN + EnumChatFormatting.BOLD + solution + EnumChatFormatting.GREEN + "!"));
                } else {
                    if (unformatted.contains("its") || unformatted.contains("theyre")) {
                        System.out.println("Missing Fetchur item: " + unformatted);
                        mc.thePlayer.addChatMessage(new ChatComponentText(String.format("§cskyblockfeatures couldn't determine the Fetchur item. There were %s solutions loaded.", fetchurItems.size())));
                    }
                }

            }).start();
        }
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (!Utils.inSkyblock) return;
        Entity viewer = Minecraft.getMinecraft().getRenderViewEntity();
        double viewerX = viewer.lastTickPosX + (viewer.posX - viewer.lastTickPosX) * event.partialTicks;
        double viewerY = viewer.lastTickPosY + (viewer.posY - viewer.lastTickPosY) * event.partialTicks;
        double viewerZ = viewer.lastTickPosZ + (viewer.posZ - viewer.lastTickPosZ) * event.partialTicks;
        if (skyblockfeatures.config.puzzlerSolver && puzzlerSolution != null) {
            double x = puzzlerSolution.getX() - viewerX;
            double y = puzzlerSolution.getY() - viewerY;
            double z = puzzlerSolution.getZ() - viewerZ;
            GlStateManager.enableCull();
            RenderUtil.drawFilledBoundingBox(new AxisAlignedBB(x, y, z, x + 1, y + 1.01, z + 1), new Color(255, 0, 0, 200), 1f);
            GlStateManager.disableCull();
        }
    }

    @SubscribeEvent
    public void onWorldChange(WorldEvent.Load event) {
        puzzlerSolution = null;
    }


}
