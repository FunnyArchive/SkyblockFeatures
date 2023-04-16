package mrfast.skyblockfeatures.features.impl.dungeons.solvers;

import com.google.common.collect.Lists;
import com.mojang.realmsclient.gui.ChatFormatting;

import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.events.PacketEvent;
import mrfast.skyblockfeatures.utils.BoulderUtils;
import mrfast.skyblockfeatures.utils.RenderUtil;
import mrfast.skyblockfeatures.utils.Utils;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import java.awt.*;
import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 * Original code was taken from Skytils under GNU Affero General Public License v3.0 and modified by MrFast
 *
 * @author Skytils Team
 * @link https://github.com/Skytils/SkytilsMod/blob/1.x/LICENSE.md
 */
public class BoulderSolver {

    private static final Minecraft mc = Minecraft.getMinecraft();
    public static BlockPos boulderChest = null;
    public static EnumFacing boulderFacing = null;
    public static BoulderState[][] grid = new BoulderState[7][6];
    public static int roomVariant = -1;
    public static ArrayList<ArrayList<BoulderPush>> variantSteps = new ArrayList<>();
    public static ArrayList<ArrayList<BoulderState>> expectedBoulders = new ArrayList<>();
    private static int ticks = 0;
    private static Thread workerThread = null;

    public BoulderSolver() {

        expectedBoulders.add(Lists.newArrayList(BoulderState.EMPTY, BoulderState.FILLED, BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.EMPTY));
        variantSteps.add(Lists.newArrayList(new BoulderPush(2, 4, Direction.RIGHT), new BoulderPush(2, 3, Direction.FORWARD), new BoulderPush(3, 3, Direction.RIGHT), new BoulderPush(4, 3, Direction.RIGHT), new BoulderPush(4, 1, Direction.FORWARD), new BoulderPush(5, 1, Direction.RIGHT)));

        expectedBoulders.add(Lists.newArrayList(BoulderState.FILLED, BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.FILLED));
        variantSteps.add(Lists.newArrayList(new BoulderPush(3, 4, Direction.FORWARD), new BoulderPush(2, 4, Direction.LEFT), new BoulderPush(3, 3, Direction.RIGHT), new BoulderPush(3, 2, Direction.FORWARD), new BoulderPush(2, 2, Direction.LEFT), new BoulderPush(4, 2, Direction.RIGHT), new BoulderPush(2, 1, Direction.FORWARD), new BoulderPush(4, 1, Direction.FORWARD), new BoulderPush(3, 1, Direction.RIGHT)));

        expectedBoulders.add(Lists.newArrayList(BoulderState.FILLED, BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.FILLED, BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.FILLED));
        variantSteps.add(Lists.newArrayList(new BoulderPush(1, 1, Direction.RIGHT)));

        expectedBoulders.add(Lists.newArrayList(BoulderState.FILLED, BoulderState.FILLED, BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.FILLED, BoulderState.FILLED));
        variantSteps.add(Lists.newArrayList(new BoulderPush(4, 3, Direction.FORWARD), new BoulderPush(3, 3, Direction.LEFT), new BoulderPush(3, 1, Direction.FORWARD), new BoulderPush(2, 1, Direction.LEFT)));

        expectedBoulders.add(Lists.newArrayList(BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.FILLED));
        variantSteps.add(Lists.newArrayList(new BoulderPush(3, 4, Direction.FORWARD), new BoulderPush(3, 3, Direction.FORWARD), new BoulderPush(2, 1, Direction.FORWARD), new BoulderPush(1, 1, Direction.LEFT)));

        expectedBoulders.add(Lists.newArrayList(BoulderState.FILLED, BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.FILLED, BoulderState.EMPTY, BoulderState.FILLED, BoulderState.EMPTY));
        variantSteps.add(Lists.newArrayList(new BoulderPush(1, 4, Direction.FORWARD), new BoulderPush(1, 1, Direction.RIGHT)));

        expectedBoulders.add(Lists.newArrayList(BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.FILLED, BoulderState.FILLED, BoulderState.FILLED, BoulderState.FILLED, BoulderState.FILLED, BoulderState.FILLED, BoulderState.EMPTY, BoulderState.FILLED));
        variantSteps.add(Lists.newArrayList(new BoulderPush(6, 4, Direction.FORWARD), new BoulderPush(6, 3, Direction.FORWARD), new BoulderPush(4, 1, Direction.FORWARD), new BoulderPush(5, 1, Direction.RIGHT)));

        expectedBoulders.add(Lists.newArrayList(BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.EMPTY, BoulderState.FILLED));
        variantSteps.add(Lists.newArrayList(new BoulderPush(0, 1, Direction.FORWARD)));

    }

    public static void update() {
        EntityPlayerSP player = mc.thePlayer;
        World world = mc.theWorld;
        if (skyblockfeatures.config.BoulderSolver && Utils.inDungeons && world != null && player != null && roomVariant != -2 && (workerThread == null || !workerThread.isAlive() || workerThread.isInterrupted())) {
            workerThread = new Thread(() -> {
                boolean foundBirch = false;
                boolean foundBarrier = false;
                for (BlockPos potentialBarrier : Utils.getBlocksWithinRangeAtSameY(player.getPosition(), 13, 68)) {
                    if (foundBarrier && foundBirch) break;
                    if (!foundBarrier) {
                        if (world.getBlockState(potentialBarrier).getBlock() == Blocks.barrier) {
                            foundBarrier = true;
                        }
                    }
                    if (!foundBirch) {
                        BlockPos potentialBirch = potentialBarrier.down(2);
                        if (world.getBlockState(potentialBirch).getBlock() == Blocks.planks && Blocks.planks.getDamageValue(world, potentialBirch) == 2) {
                            foundBirch = true;
                        }
                    }
                }
                if (!foundBirch || !foundBarrier) return;
                if (boulderChest == null || boulderFacing == null) {
                    for (BlockPos potentialChestPos : Utils.getBlocksWithinRangeAtSameY(player.getPosition(), 25, 66)) {
                        if (boulderChest != null && boulderFacing != null) break;
                        if (world.getBlockState(potentialChestPos).getBlock() == Blocks.chest) {
                            if (world.getBlockState(potentialChestPos.down()).getBlock() == Blocks.stonebrick && world.getBlockState(potentialChestPos.up(3)).getBlock() == Blocks.barrier) {
                                boulderChest = potentialChestPos;
                                System.out.println("Boulder chest is at " + boulderChest);
                                for (EnumFacing direction : EnumFacing.HORIZONTALS) {
                                    if (world.getBlockState(potentialChestPos.offset(direction)).getBlock() == Blocks.stained_hardened_clay) {
                                        boulderFacing = direction;
                                        System.out.println("Boulder room is facing " + direction);
                                        break;
                                    }
                                }
                                break;
                            }
                        }
                    }
                } else {
                    EnumFacing downRow = boulderFacing.getOpposite();
                    EnumFacing rightColumn = boulderFacing.rotateY();
                    BlockPos farLeftPos = boulderChest.offset(downRow, 5).offset(rightColumn.getOpposite(), 9);
                    for (int row = 0; row < 6; row++) {
                        for (int column = 0; column < 7; column++) {
                            BlockPos current = farLeftPos.offset(rightColumn, 3 * column).offset(downRow, 3 * row);
                            IBlockState state = world.getBlockState(current);
                            grid[column][row] = state.getBlock() == Blocks.air ? BoulderState.EMPTY : BoulderState.FILLED;
                        }
                    }
                    if (roomVariant == -1) {
                        roomVariant = -2;
                        for (int i = 0; i < expectedBoulders.size(); i++) {
                            ArrayList<BoulderState> expected = expectedBoulders.get(i);
                            boolean isRight = true;
                            for (int j = 0; j < expected.size(); j++) {
                                int column = j % 7;
                                int row = (int) Math.floor(j / 7f);
                                BoulderState state = expected.get(j);
                                if (grid[column][row] != state && state != BoulderState.PLACEHOLDER) {
                                    isRight = false;
                                    break;
                                }
                            }
                            if (isRight) {
                                roomVariant = i;
                                break;
                            }
                        }
                        if (roomVariant == -2) {
                            mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Couldnt't solve boulder puzzle"));
                        }
                    }
                }
            });
            workerThread.start();
        }
    }

    public static void reset() {
        boulderChest = null;
        boulderFacing = null;
        grid = new BoulderState[7][6];
        roomVariant = -1;
        workerThread = null;
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START || !skyblockfeatures.config.BoulderSolver) return;
        ticks++;
        if (ticks % 20 == 0) {
            ticks = 0;
            update();
        }
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (boulderChest == null || !skyblockfeatures.config.BoulderSolver) return;
        if (roomVariant >= 0) {
            ArrayList<BoulderPush> steps = variantSteps.get(roomVariant);
            for (BoulderPush step : steps) {
                if (grid[step.x][step.y] != BoulderState.EMPTY) {
                    EnumFacing downRow = boulderFacing.getOpposite();
                    EnumFacing rightColumn = boulderFacing.rotateY();
                    BlockPos farLeftPos = boulderChest.offset(downRow, 5).offset(rightColumn.getOpposite(), 9);

                    BlockPos boulderPos = farLeftPos.offset(rightColumn, 3 * step.x).offset(downRow, 3 * step.y);

                    EnumFacing actualDirection = null;

                    switch (step.direction) {
                        case FORWARD:
                            actualDirection = boulderFacing;
                            break;
                        case BACKWARD:
                            actualDirection = boulderFacing.getOpposite();
                            break;
                        case LEFT:
                            actualDirection = boulderFacing.rotateYCCW();
                            break;
                        case RIGHT:
                            actualDirection = boulderFacing.rotateY();
                            break;
                    }

                    BlockPos buttonPos = boulderPos.offset(actualDirection.getOpposite(), 2).down();
                    AxisAlignedBB aabb = getAABB(buttonPos);
                    if(aabb!=null) {
                        GlStateManager.disableCull();
                        RenderUtil.drawOutlinedFilledBoundingBox(aabb, new Color(0, 255, 255), event.partialTicks);
                        GlStateManager.enableCull();
                    }
                    break;
                }
            }
        }
    }

    public AxisAlignedBB getAABB(BlockPos buttonPos) {
        Block block = Utils.GetMC().theWorld.getBlockState(buttonPos).getBlock();

        if (block instanceof BlockButton) {
            EnumFacing facingDirection = Utils.GetMC().theWorld.getBlockState(buttonPos).getValue(BlockButton.FACING);
            // Set the bounds of the button based on the facing direction
            AxisAlignedBB buttonBounds = null;
            Utils.SendMessage(facingDirection.toString()+" button direction");
            switch(facingDirection) {
                case NORTH:
                    buttonBounds = new AxisAlignedBB(0.0, 0.0, 0.9375, 1.0, 1.0, 1.0);
                    break;
                case SOUTH:
                    buttonBounds = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 0.0625);
                    break;
                case WEST:
                    buttonBounds = new AxisAlignedBB(0.9375, 0.0, 0.0, 1.0, 1.0, 1.0);
                    break;
                case EAST:
                    buttonBounds = new AxisAlignedBB(0.0, 0.0, 0.0, 0.0625, 1.0, 1.0);
                    break;
            }

            // Offset the bounds based on the button's position
            buttonBounds = buttonBounds.offset(buttonPos.getX(), buttonPos.getY(), buttonPos.getZ());
            return buttonBounds;
        }
        return null;
    }

    @SubscribeEvent
    public void onSendPacket(PacketEvent.SendEvent event) {
        if (!Utils.inDungeons || !skyblockfeatures.config.BoulderSolver) return;
        if (event.packet instanceof C08PacketPlayerBlockPlacement) {
            C08PacketPlayerBlockPlacement packet = (C08PacketPlayerBlockPlacement) event.packet;
            if (packet.getPosition() != null && packet.getPosition().equals(boulderChest)) {
                roomVariant = -2;
            }
        }
    }

    @SubscribeEvent
    public void onWorldChange(WorldEvent.Load event) {
        reset();
    }

    public enum Direction {
        FORWARD,
        BACKWARD,
        LEFT,
        RIGHT
    }

    public enum BoulderState {
        EMPTY,
        FILLED,
        PLACEHOLDER
    }

    public static class BoulderPush {
        int x, y;
        Direction direction;

        public BoulderPush(int x, int y, Direction direction) {
            this.x = x;
            this.y = y;
            this.direction = direction;
        }
    }

}