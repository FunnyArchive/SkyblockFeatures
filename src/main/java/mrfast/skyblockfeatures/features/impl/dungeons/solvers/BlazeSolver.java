package mrfast.skyblockfeatures.features.impl.dungeons.solvers;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.Color;
import java.util.List;

import mrfast.skyblockfeatures.utils.RenderUtil;
import mrfast.skyblockfeatures.utils.Utils;

/**
 * Original code was taken from Danker's Skyblock Mod under GPL 3.0 license and modified by the Skytils team
 * https://github.com/bowser0000/SkyblockMod/blob/master/LICENSE
 * @author bowser0000
 */
public class BlazeSolver {

    static Entity highestBlaze = null;
    static Entity lowestBlaze = null;
    static boolean higherToLower = false;
    static boolean foundChest = false;
    public static int LOWEST_BLAZE_COLOUR = 0x00FF00;
    public static int HIGHEST_BLAZE_COLOUR = 0x00FF00;

    @SubscribeEvent
    public void onWorldChange(WorldEvent.Load event) {
        higherToLower = false;
        foundChest = false;
        lowestBlaze = null;
        highestBlaze = null;
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;

        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.thePlayer;
        World world = mc.theWorld;

        if (Utils.inDungeons && world != null && player != null) {

            List<Entity> entities = world.getLoadedEntityList();
            int highestHealth = 0;
            highestBlaze = null;
            int lowestHealth = 99999999;
            lowestBlaze = null;

            for (Entity entity : entities) {
                if (entity.getName().contains("Blaze") && entity.getName().contains("/")) {
                    String blazeName = StringUtils.stripControlCodes(entity.getName());
                    try {
                        int health = Integer.parseInt(blazeName.substring(blazeName.indexOf("/") + 1, blazeName.length() - 1));
                        if (health > highestHealth) {
                            highestHealth = health;
                            highestBlaze = entity;
                        }
                        if (health < lowestHealth) {
                            lowestHealth = health;
                            lowestBlaze = entity;
                        }
                    } catch (NumberFormatException ex) {
                        ex.printStackTrace();
                    }
                }
            }

            if (!foundChest) {
                new Thread(() -> {
                    Iterable<BlockPos> blocks = BlockPos.getAllInBox(new BlockPos(player.posX - 27, 69, player.posZ - 27), new BlockPos(player.posX + 27, 70, player.posZ + 27));
                    for (BlockPos blockPos : blocks) {
                        Block block = world.getBlockState(blockPos).getBlock();
                        if (block == Blocks.chest && world.getBlockState(blockPos.add(0, 1, 0)).getBlock() == Blocks.iron_bars) {
                            Block blockbelow = world.getBlockState(blockPos.add(0, -1, 0)).getBlock();
                            if (blockbelow == Blocks.stone) {
                                higherToLower = false;
                                foundChest = true;
                            } else if (blockbelow == Blocks.air) {
                                higherToLower = true;
                                foundChest = true;
                            } else {
                                return;
                            }
                        }
                    }
                }).start();
            }
        }
    }

    @SubscribeEvent
    public void onWorldRender(RenderWorldLastEvent event) {
        if (Utils.inDungeons) {
            if (foundChest) {
                if (lowestBlaze != null && !higherToLower) {
                    AxisAlignedBB aabb = new AxisAlignedBB(lowestBlaze.posX - 0.5, lowestBlaze.posY - 2, lowestBlaze.posZ - 0.5, lowestBlaze.posX + 0.5, lowestBlaze.posY, lowestBlaze.posZ + 0.5);
                    RenderUtil.drawOutlinedFilledBoundingBox(aabb, new Color(LOWEST_BLAZE_COLOUR), event.partialTicks);
                }
                if (highestBlaze != null && higherToLower) {
                    AxisAlignedBB aabb = new AxisAlignedBB(highestBlaze.posX - 0.5, highestBlaze.posY - 2, highestBlaze.posZ - 0.5, highestBlaze.posX + 0.5, highestBlaze.posY, highestBlaze.posZ + 0.5);
                    RenderUtil.drawOutlinedFilledBoundingBox(aabb, new Color(LOWEST_BLAZE_COLOUR), event.partialTicks);
                }
            } else {
                if (lowestBlaze != null) {
                    AxisAlignedBB aabb = new AxisAlignedBB(lowestBlaze.posX - 0.5, lowestBlaze.posY - 2, lowestBlaze.posZ - 0.5, lowestBlaze.posX + 0.5, lowestBlaze.posY, lowestBlaze.posZ + 0.5);
                    RenderUtil.drawOutlinedFilledBoundingBox(aabb, new Color(LOWEST_BLAZE_COLOUR), event.partialTicks);
                }
                if (highestBlaze != null) {
                    AxisAlignedBB aabb = new AxisAlignedBB(highestBlaze.posX - 0.5, highestBlaze.posY - 2, highestBlaze.posZ - 0.5, highestBlaze.posX + 0.5, highestBlaze.posY, highestBlaze.posZ + 0.5);
                    RenderUtil.drawOutlinedFilledBoundingBox(aabb, new Color(HIGHEST_BLAZE_COLOUR), event.partialTicks);
                }
            }
        }
    }
}