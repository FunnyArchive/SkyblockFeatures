package mrfast.skyblockfeatures.features.impl.dungeons.solvers;

import java.awt.Color;

import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.utils.RenderUtil;
import mrfast.skyblockfeatures.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
/**
 * Modified from SkyblockMod under GNU Lesser General Public License v3.0
 * https://github.com/bowser0000/SkyblockMod/blob/master/COPYING
 * @author Bowser0000
 */
public class ThreeWeirdosSolver {

    static String[] riddleSolutions = {"The reward is not in my chest!", "At least one of them is lying, and the reward is not in",
            "My chest doesn't have the reward. We are all telling the truth", "My chest has the reward and I'm telling the truth",
            "The reward isn't in any of our chests", "Both of them are telling the truth."};
    static BlockPos riddleChest = null;
    static Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onWorldChange(WorldEvent.Load event) {
        try {
            riddleChest = null;
        } catch(Exception e) {

        }
    }

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        String message = Utils.cleanColour(event.message.getUnformattedText());

        if (!Utils.inDungeons) return;
        if (message.contains("[NPC]") && skyblockfeatures.config.ThreeWeirdosSolver) {
            for (String solution : riddleSolutions) {
                if (message.contains(solution)) {
                    String npcName = message.split(" ")[1].replace(":","");
                    Utils.SendMessage(EnumChatFormatting.GOLD+""+ EnumChatFormatting.BOLD+"The rewards in " + StringUtils.stripControlCodes(npcName)+"'s Chest");
                    if (riddleChest == null) {
                        for (Entity entity : mc.theWorld.loadedEntityList) {
                            if (entity == null || !entity.hasCustomName()) continue;
                            if (entity.getCustomNameTag().contains(npcName)) {
                                BlockPos npcLocation = new BlockPos(entity.posX, 69, entity.posZ);
                                if (mc.theWorld.getBlockState(npcLocation.north()).getBlock() == Blocks.chest) {
                                    riddleChest = npcLocation.north();
                                } else if (mc.theWorld.getBlockState(npcLocation.east()).getBlock() == Blocks.chest) {
                                    riddleChest = npcLocation.east();
                                } else if (mc.theWorld.getBlockState(npcLocation.south()).getBlock() == Blocks.chest) {
                                    riddleChest = npcLocation.south();
                                } else if (mc.theWorld.getBlockState(npcLocation.west()).getBlock() == Blocks.chest) {
                                    riddleChest = npcLocation.west();
                                }
                                break;
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    @SubscribeEvent
    public void onWorldRender(RenderWorldLastEvent event) {
        if(riddleChest != null && skyblockfeatures.config.ThreeWeirdosSolver) {
            RenderUtil.drawOutlinedFilledBoundingBox(new AxisAlignedBB(riddleChest.getX(),riddleChest.getY(),riddleChest.getZ(),riddleChest.getX()+1,riddleChest.getY()+1,riddleChest.getZ()+1), Color.cyan,event.partialTicks);
        }
    }

}