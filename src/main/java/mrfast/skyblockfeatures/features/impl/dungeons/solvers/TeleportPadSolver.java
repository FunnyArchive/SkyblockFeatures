package mrfast.skyblockfeatures.features.impl.dungeons.solvers;

import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.utils.RenderUtil;
import mrfast.skyblockfeatures.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEndPortalFrame;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

public class TeleportPadSolver {
    List<BlockPos> endportalFrames = new ArrayList<>();
    @SubscribeEvent
    public void onWorldChange(WorldEvent.Load event) {
        endportalFrames.clear();
    }

    @SubscribeEvent
    public void onTick(ClientTickEvent event) {
        Minecraft mc = Utils.GetMC();
        if(Utils.GetMC().theWorld == null || !Utils.inDungeons || !skyblockfeatures.config.teleportPadSolver) return;
        BlockPos playerPos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
        IBlockState blockState = Utils.GetMC().theWorld.getBlockState(playerPos);
        Block block = blockState.getBlock();
        if(block instanceof BlockEndPortalFrame && !endportalFrames.contains(playerPos)) {
            endportalFrames.add(playerPos);
        }
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        if(Utils.GetMC().theWorld == null || !Utils.inDungeons || !skyblockfeatures.config.teleportPadSolver) return;

        for(BlockPos frame:endportalFrames) {
            AxisAlignedBB aabb = new AxisAlignedBB(frame, frame.add(1, 1, 1));
            RenderUtil.drawOutlinedFilledBoundingBox(aabb, Color.green, event.partialTicks);
        }
    }
}
