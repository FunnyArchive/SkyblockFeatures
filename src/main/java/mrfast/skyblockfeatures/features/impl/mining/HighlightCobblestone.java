package mrfast.skyblockfeatures.features.impl.mining;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mojang.realmsclient.gui.ChatFormatting;

import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.core.DataFetcher;
import mrfast.skyblockfeatures.events.BlockChangeEvent;
import mrfast.skyblockfeatures.events.PacketEvent;
import mrfast.skyblockfeatures.features.impl.misc.MiscFeatures;
import mrfast.skyblockfeatures.utils.RenderUtil;
import mrfast.skyblockfeatures.utils.SBInfo;
import mrfast.skyblockfeatures.utils.ScoreboardUtil;
import mrfast.skyblockfeatures.utils.StringUtils;
import mrfast.skyblockfeatures.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.BlockStone;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HighlightCobblestone {
    List<BlockPos> cobblestonePostitions = new ArrayList<BlockPos>();
    
    @SubscribeEvent
    public void onBlockPlace(BlockEvent.PlaceEvent event) {
        if(!skyblockfeatures.config.highlightCobblestone) return;
        if(event.placedBlock.getBlock() == Blocks.cobblestone && !cobblestonePostitions.contains(event.pos)) {
            cobblestonePostitions.add(event.pos);
        }
    }

    @SubscribeEvent
    public void onWorldChange(WorldEvent.Load event) {
        try {
            if(!skyblockfeatures.config.highlightCobblestone) return;
            cobblestonePostitions.clear();
        } catch(Exception e) {

        }
    }

    @SubscribeEvent
    public void RenderWorldLastEvent(RenderWorldLastEvent event) {
        if(!skyblockfeatures.config.highlightCobblestone) return;

        try {
            GlStateManager.disableDepth();
            for(BlockPos pos:cobblestonePostitions) {
                if(Utils.GetMC().theWorld.getBlockState(pos).getBlock() != Blocks.cobblestone) {
                    cobblestonePostitions.remove(pos);
                }
                AxisAlignedBB box = new AxisAlignedBB(pos, pos.add(1, 1, 1));
                RenderUtil.drawOutlinedFilledBoundingBox(box, Color.cyan, event.partialTicks);
            }
            GlStateManager.enableDepth();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}
