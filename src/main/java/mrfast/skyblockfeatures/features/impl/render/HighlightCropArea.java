package mrfast.skyblockfeatures.features.impl.render;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.events.BlockChangeEvent;
import mrfast.skyblockfeatures.events.SecondPassedEvent;
import mrfast.skyblockfeatures.utils.RenderUtil;
import mrfast.skyblockfeatures.utils.SBInfo;
import mrfast.skyblockfeatures.utils.ScoreboardUtil;
import mrfast.skyblockfeatures.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HighlightCropArea {
    List<AxisAlignedBB> cropAreas = new ArrayList<>();
    List<BlockPos> blocksToDestroy = new ArrayList<>();
    boolean update = true;

    @SubscribeEvent
    public void secondPassed(SecondPassedEvent event) {
        if(SBInfo.getInstance()==null || !skyblockfeatures.config.GardenBlocksToRemove || Utils.GetMC().theWorld==null) return;
        try {if(!SBInfo.getInstance().mode.equals("garden")) return;} catch (Exception e) {}
    
        for(String line:ScoreboardUtil.getSidebarLines()) {
            if(line.contains("Cleanup") && blocksToDestroy.size()==0) {
                update = true;
            }
        }
    }

    @SubscribeEvent
    public void worldChange(WorldEvent.Load event) {
        if(!skyblockfeatures.config.GardenBlocksToRemove) return;
        update = false;
        cropAreas.clear();
        blocksToDestroy.clear();
    }
    
    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        if(SBInfo.getInstance()==null || !skyblockfeatures.config.GardenBlocksToRemove || Utils.GetMC().theWorld==null) return;
        try {if(!SBInfo.getInstance().mode.equals("garden")) return;} catch (Exception e) {}
        
        if(cropAreas.size()==0) {
            for(int x=-192;x<192;x+=96) {
                for(int z=-192;z<192;z+=96) {
                    AxisAlignedBB box = new AxisAlignedBB(x-48,65,z-48,x+48,116,z+48);
                    cropAreas.add(box);
                }
            }
        }
        if(update) {
            update = false;
            blocksToDestroy.clear();
            for(AxisAlignedBB cropArea:cropAreas) {
                if(cropArea.isVecInside(Utils.GetMC().thePlayer.getPositionVector())) {
                    for(double x=cropArea.minX;x<cropArea.maxX;x++) {
                        for(double z=cropArea.minZ;z<cropArea.maxZ;z++) {
                            for(double y=71;y<cropArea.maxY;y++) {
                                BlockPos pos = new BlockPos(x, y, z);
                                Block block = Utils.GetMC().theWorld.getBlockState(pos).getBlock();
                                if(block!=Blocks.air && block!=Blocks.grass && block!=Blocks.dirt) {
                                    if(!blocksToDestroy.contains(pos)) {
                                        blocksToDestroy.add(pos);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        for(BlockPos pos:blocksToDestroy) {
            RenderUtil.drawBoundingBox(new AxisAlignedBB(pos, pos.add(1, 1, 1)), Color.red, event.partialTicks);
        }
        
    }

    @SubscribeEvent
    public void onBlockChange(BlockChangeEvent event) {
        if(SBInfo.getInstance()==null || !skyblockfeatures.config.GardenBlocksToRemove || Utils.GetMC().theWorld==null) return;
        try {if(!SBInfo.getInstance().mode.equals("garden")) return;} catch (Exception e) {}

        if(blocksToDestroy.contains(event.pos)) {
            blocksToDestroy.remove(blocksToDestroy.indexOf(event.pos));
        }
    }
}
