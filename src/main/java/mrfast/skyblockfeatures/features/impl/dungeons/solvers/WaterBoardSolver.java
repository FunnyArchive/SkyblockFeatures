package mrfast.skyblockfeatures.features.impl.dungeons.solvers;

import com.google.common.collect.ImmutableSet;

import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.utils.RenderUtil;
import mrfast.skyblockfeatures.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.block.BlockLever;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;


/**
 * Original code was taken from Skytils under GNU Affero General Public License v3.0 and modified by MrFast
 *
 * @author Skytils Team
 * @link https://github.com/Skytils/SkytilsMod/blob/1.x/LICENSE.md
 */
public class WaterBoardSolver {
    private final static Minecraft mc = Minecraft.getMinecraft();
    private static final HashMap<WoolColor, ImmutableSet<LeverBlock>> solutions = new HashMap<>();
    private static BlockPos chestPos = null;
    private static EnumFacing roomFacing = null;
    private static boolean prevInWaterRoom = false;
    private static boolean inWaterRoom = false;
    private static int variant = -1;
    private static WoolColor frontMostColor = null;
    private static Thread workerThread = null;
    private static int ticks = 0;

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START || !Utils.inDungeons || mc.theWorld == null || mc.thePlayer == null || !skyblockfeatures.config.waterboard) return;

        EntityPlayerSP player = mc.thePlayer;
        World world = mc.theWorld;

        if (ticks % 4 == 0) {
            if(chestPos!=null) {
                BlockPos outerMostWool = null;
                for (BlockPos blockPos : Utils.getBlocksWithinRangeAtSameY(chestPos, 8, 56)) {
                    boolean middleBlock = (blockPos.getX()==chestPos.getX())||(blockPos.getZ()==chestPos.getZ());
                    if (world.getBlockState(blockPos).getBlock() == Blocks.wool && middleBlock) {
                        if(outerMostWool==null) {
                            outerMostWool = blockPos;
                        } else {
                            double bestDist = chestPos.distanceSq(outerMostWool.getX(), outerMostWool.getY(), outerMostWool.getZ());
                            double thisDist = chestPos.distanceSq(blockPos.getX(), blockPos.getY(), blockPos.getZ());

                            if(thisDist>bestDist) {
                                outerMostWool = blockPos;
                            }
                        }
                    }
                }
                if(outerMostWool!=null) {
                    for (WoolColor color : WoolColor.values()) {
                        if(color.dyeColor==world.getBlockState(outerMostWool).getValue(BlockColored.COLOR)) {
                            frontMostColor = color;
                        }
                    }
                }
            }
            if (variant == -1 && (workerThread == null || !workerThread.isAlive() || workerThread.isInterrupted())) {
                workerThread = new Thread(() -> {

                    prevInWaterRoom = inWaterRoom;
                    inWaterRoom = false;

                    boolean foundPiston = false;

                    for (BlockPos potentialPiston : Utils.getBlocksWithinRangeAtSameY(player.getPosition(), 13, 54)) {
                        if (world.getBlockState(potentialPiston).getBlock() == Blocks.sticky_piston) {
                            foundPiston = true;
                            break;
                        }
                    }

                    if (foundPiston) {
                        if (chestPos == null) {
                            for (BlockPos potentialChestPos : Utils.getBlocksWithinRangeAtSameY(player.getPosition(), 25, 56)) {
                                if (world.getBlockState(potentialChestPos).getBlock() == Blocks.chest) {
                                    if (world.getBlockState(potentialChestPos.down()).getBlock() == Blocks.stone && world.getBlockState(potentialChestPos.up(2)).getBlock() == Blocks.stained_glass) {
                                        for (EnumFacing direction : EnumFacing.HORIZONTALS) {
                                            if (world.getBlockState(potentialChestPos.offset(direction.getOpposite(), 3).down(2)).getBlock() == Blocks.sticky_piston && world.getBlockState(potentialChestPos.offset(direction, 2)).getBlock() == Blocks.stone) {
                                                chestPos = potentialChestPos;
                                                System.out.println("Water board chest is at " + chestPos);
                                                roomFacing = direction;
                                                System.out.println("Water board room is facing " + direction);
                                                break;
                                            }
                                        }
                                        break;
                                    }
                                }
                            }
                        }

                        if (chestPos == null) return;
                        
                        for (BlockPos blockPos : Utils.getBlocksWithinRangeAtSameY(player.getPosition(), 25, 82)) {
                            if (world.getBlockState(blockPos).getBlock() == Blocks.piston_head) {
                                inWaterRoom = true;

                                if (!prevInWaterRoom) {
                                    
                                    boolean foundGold = false;
                                    boolean foundClay = false;
                                    boolean foundEmerald = false;
                                    boolean foundQuartz = false;
                                    boolean foundDiamond = false;

                                    int x = blockPos.getX();
                                    int z = blockPos.getZ();

                                    // Detect first blocks near water stream
                                    for (BlockPos puzzleBlockPos : BlockPos.getAllInBox(new BlockPos(x + 1, 78, z + 1), new BlockPos(x - 1, 77, z - 1))) {
                                        Block block = world.getBlockState(puzzleBlockPos).getBlock();
                                        if (block == Blocks.gold_block) {
                                            foundGold = true;
                                        } else if (block == Blocks.hardened_clay) {
                                            foundClay = true;
                                        } else if (block == Blocks.emerald_block) {
                                            foundEmerald = true;
                                        } else if (block == Blocks.quartz_block) {
                                            foundQuartz = true;
                                        } else if (block == Blocks.diamond_block) {
                                            foundDiamond = true;
                                        }
                                    }

                                    if (foundGold && foundClay) {
                                        variant = 0;
                                    } else if (foundEmerald && foundQuartz) {
                                        variant = 1;
                                    } else if (foundQuartz && foundDiamond) {
                                        variant = 2;
                                    } else if (foundGold && foundQuartz) {
                                        variant = 3;
                                    }

                                    switch (variant) {
                                        case 0:
                                            solutions.put(WoolColor.PURPLE, ImmutableSet.of(LeverBlock.QUARTZ, LeverBlock.GOLD, LeverBlock.DIAMOND, LeverBlock.CLAY));
                                            solutions.put(WoolColor.ORANGE, ImmutableSet.of(LeverBlock.GOLD, LeverBlock.COAL, LeverBlock.EMERALD));
                                            solutions.put(WoolColor.BLUE, ImmutableSet.of(LeverBlock.QUARTZ, LeverBlock.GOLD, LeverBlock.EMERALD, LeverBlock.CLAY));
                                            solutions.put(WoolColor.GREEN, ImmutableSet.of(LeverBlock.EMERALD));
                                            solutions.put(WoolColor.RED, ImmutableSet.of());
                                            break;
                                        case 1:
                                            solutions.put(WoolColor.PURPLE, ImmutableSet.of(LeverBlock.COAL));
                                            solutions.put(WoolColor.ORANGE, ImmutableSet.of(LeverBlock.QUARTZ, LeverBlock.GOLD, LeverBlock.EMERALD, LeverBlock.CLAY));
                                            solutions.put(WoolColor.BLUE, ImmutableSet.of(LeverBlock.QUARTZ, LeverBlock.DIAMOND, LeverBlock.EMERALD));
                                            solutions.put(WoolColor.GREEN, ImmutableSet.of(LeverBlock.QUARTZ, LeverBlock.EMERALD));
                                            solutions.put(WoolColor.RED, ImmutableSet.of(LeverBlock.QUARTZ, LeverBlock.COAL, LeverBlock.EMERALD));
                                            break;
                                        case 2:
                                            solutions.put(WoolColor.PURPLE, ImmutableSet.of(LeverBlock.QUARTZ, LeverBlock.GOLD, LeverBlock.DIAMOND));
                                            solutions.put(WoolColor.ORANGE, ImmutableSet.of(LeverBlock.EMERALD));
                                            solutions.put(WoolColor.BLUE, ImmutableSet.of(LeverBlock.QUARTZ, LeverBlock.DIAMOND));
                                            solutions.put(WoolColor.GREEN, ImmutableSet.of());
                                            solutions.put(WoolColor.RED, ImmutableSet.of(LeverBlock.GOLD, LeverBlock.EMERALD));
                                            break;
                                        case 3:
                                            solutions.put(WoolColor.PURPLE, ImmutableSet.of(LeverBlock.QUARTZ, LeverBlock.GOLD, LeverBlock.EMERALD, LeverBlock.CLAY));
                                            solutions.put(WoolColor.ORANGE, ImmutableSet.of(LeverBlock.GOLD, LeverBlock.COAL));
                                            solutions.put(WoolColor.BLUE, ImmutableSet.of(LeverBlock.QUARTZ, LeverBlock.GOLD, LeverBlock.COAL, LeverBlock.EMERALD, LeverBlock.CLAY));
                                            solutions.put(WoolColor.GREEN, ImmutableSet.of(LeverBlock.GOLD, LeverBlock.EMERALD));
                                            solutions.put(WoolColor.RED, ImmutableSet.of(LeverBlock.GOLD, LeverBlock.DIAMOND, LeverBlock.EMERALD, LeverBlock.CLAY));
                                            break;
                                        default:
                                            break;
                                    }
                                    break;
                                }
                            }
                        }
                    } else {
                        variant = -1;
                        solutions.clear();
                    }
                });
                workerThread.start();
            }
            ticks = 0;
        }
        ticks++;
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (chestPos == null || roomFacing == null || variant == -1 || !skyblockfeatures.config.waterboard) return;

        HashMap<LeverBlock, Boolean> leverStates = new HashMap<>();

        for (LeverBlock lever : LeverBlock.values()) {
            leverStates.put(lever, getLeverToggleState(lever.getLeverPos()));
        }

        if(frontMostColor == null) return;
        WoolColor color = frontMostColor;
        // Get color of wool
        Color renderColor = new Color(color.dyeColor.getMapColor().colorValue).brighter();

        if (color.isExtended()) {
            // Get levers that solve for that color
            ImmutableSet<LeverBlock> solution = solutions.get(color);
            if (solution == null) return;
            // Loop through levers that make the right solution
            for (Map.Entry<LeverBlock, Boolean> entry : leverStates.entrySet()) {
                LeverBlock lever = entry.getKey();
                boolean switched = entry.getValue();

                if ((switched && !solution.contains(lever)) || (!switched && solution.contains(lever))) {
                    BlockPos pos = lever.getLeverPos();
                    AxisAlignedBB aabb = new AxisAlignedBB(pos, pos.add(1, 1, 1));
                    RenderUtil.drawOutlinedFilledBoundingBox(aabb, renderColor, event.partialTicks);
                }
            }
            // Toggle water lever
            if (leverStates.entrySet().stream().allMatch(entry -> (entry.getValue() && solution.contains(entry.getKey()) || (!entry.getValue() && !solution.contains(entry.getKey()))))) {
                BlockPos pos = new BlockPos(new Vec3(chestPos.offset(roomFacing.getOpposite(), 17).up(5)));
                AxisAlignedBB aabb = new AxisAlignedBB(pos, pos.add(1, -1, 1));
                RenderUtil.drawOutlinedFilledBoundingBox(aabb, renderColor, event.partialTicks);
            }
        }
    }

    @SubscribeEvent
    public void onWorldChange(WorldEvent.Load event) {
        variant = -1;
        solutions.clear();
        chestPos = null;
        roomFacing = null;
        prevInWaterRoom = false;
        inWaterRoom = false;
    }

    private boolean getLeverToggleState(BlockPos pos) {
        IBlockState block = mc.theWorld.getBlockState(pos);

        if (block.getBlock() != Blocks.lever) return false;
        return block.getValue(BlockLever.POWERED);
    }

    public enum WoolColor {
        PURPLE(EnumDyeColor.PURPLE),
        ORANGE(EnumDyeColor.ORANGE),
        BLUE(EnumDyeColor.BLUE),
        GREEN(EnumDyeColor.LIME),
        RED(EnumDyeColor.RED);

        public EnumDyeColor dyeColor;

        WoolColor(EnumDyeColor dyeColor) {
            this.dyeColor = dyeColor;
        }

        public boolean isExtended() {
            if (chestPos == null || roomFacing == null) return false;
            return mc.theWorld.getBlockState(chestPos.offset(roomFacing.getOpposite(), 3 + this.ordinal())).getBlock() == Blocks.wool;
        }
    }


    public enum LeverBlock {
        QUARTZ(Blocks.quartz_block),
        GOLD(Blocks.gold_block),
        COAL(Blocks.coal_block),
        DIAMOND(Blocks.diamond_block),
        EMERALD(Blocks.emerald_block),
        CLAY(Blocks.hardened_clay);

        public Block block;

        LeverBlock(Block block) {
            this.block = block;
        }

        public BlockPos getLeverPos() {
            if (chestPos == null || roomFacing == null) return null;

            int shiftBy = (ordinal() % 3) * 5;
            EnumFacing leverSide = ordinal() < 3 ? roomFacing.rotateY() : roomFacing.rotateYCCW();
            return chestPos.up(5).offset(leverSide.getOpposite(), 6).offset(roomFacing.getOpposite(), 2 + shiftBy).offset(leverSide);
        }
    }


}
