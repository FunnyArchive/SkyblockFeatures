package mrfast.skyblockfeatures.features.impl.dungeons.solvers;

import java.awt.Color;

import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.utils.RenderUtil;
import mrfast.skyblockfeatures.utils.Utils;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class LividFinder {
    public static Entity livid = null;
    @SubscribeEvent
    public void onWorldChange(WorldEvent.Load event) {
        livid = null;
    }

    @SubscribeEvent
    public void onRender3D(RenderWorldLastEvent event) {
        if(livid != null && skyblockfeatures.config.highlightCorrectLivid && Utils.inDungeons) {
            AxisAlignedBB lividPos = new AxisAlignedBB(livid.posX-0.5, livid.posY, livid.posZ-0.5, 
            livid.posX+0.5, livid.posY+2, livid.posZ+0.5);
            RenderUtil.drawOutlinedFilledBoundingBox(lividPos,Color.CYAN,event.partialTicks);
        }
    }
    
    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if(Utils.GetMC().theWorld == null || !skyblockfeatures.config.highlightCorrectLivid || !Utils.inDungeons) return;
        IBlockState state = Utils.GetMC().theWorld.getBlockState(new BlockPos(6, 109, 43));
        try {
            EnumDyeColor color = state.getValue(BlockStainedGlass.COLOR);
            String lividName = null;
            switch (color) {
                case WHITE: lividName="Vendetta"; break;
                case MAGENTA: lividName="Crossed"; break;
                case PINK: lividName="Crossed"; break;
                case RED: lividName="Hockey"; break;
                case SILVER: lividName="Doctor"; break;
                case GRAY: lividName="Doctor"; break;
                case GREEN: lividName="Frog"; break;
                case LIME: lividName="Smile"; break;
                case BLUE: lividName="Scream"; break;
                case PURPLE: lividName="Purple"; break;
                case YELLOW: lividName="Arcade"; break;
                default:
                    break;
            }
            for(Entity entity : Utils.GetMC().theWorld.loadedEntityList) {
                if (lividName != null && entity.getName().contains(lividName)) {
                    livid = entity;
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}
 