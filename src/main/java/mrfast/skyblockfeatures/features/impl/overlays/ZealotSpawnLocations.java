package mrfast.skyblockfeatures.features.impl.overlays;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.vecmath.Color4f;

import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.utils.RenderUtil;
import mrfast.skyblockfeatures.utils.SBInfo;
import mrfast.skyblockfeatures.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.internal.EntitySpawnHandler;
import java.awt.Color;

public class ZealotSpawnLocations {
    List<BlockPos> zealotSpawns = new ArrayList<>(
        Arrays.asList(
        new BlockPos(-646,5,-274),
        new BlockPos(-633,5,-277),
        new BlockPos(-639,7,-305),
        new BlockPos(-631,5,-327),
        new BlockPos(-619,6,-313),
        new BlockPos(-665,10,-313),
        new BlockPos(-632,5,-260),
        new BlockPos(-630,7,-229),
        new BlockPos(-647,5,-221),
        new BlockPos(-684,5,-261),
        new BlockPos(-699,6,-263),
        new BlockPos(-683,5,-292),
        new BlockPos(-698,5,-319),
        new BlockPos(-714,5,-289),
        new BlockPos(-732,5,-295),
        new BlockPos(-731,6,-275)));

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        if(SBInfo.getInstance().location.contains("Dragon's Nest") && skyblockfeatures.config.showZealotSpawns) {
            for(BlockPos pos:zealotSpawns) {
                drawParticleESP(new Color(200,55,200), pos.getX() - Minecraft.getMinecraft().getRenderManager().viewerPosX,pos.getY() - Minecraft.getMinecraft().getRenderManager().viewerPosY, pos.getZ() - Minecraft.getMinecraft().getRenderManager().viewerPosZ, 5.0D);
            }
        }
    }

    public static void drawParticleESP(Color c, double d, double d1, double d2, double size) {
        GlStateManager.enableBlend();
        GlStateManager.disableLighting();
        // GlStateManager.disableDepth();
        GlStateManager.enableCull();
        // GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.disableTexture2D();
        drawBoundingBox(c, new AxisAlignedBB(d-size, d1+0.1, d2-size, d+size, d1-3, d2+size));
        GlStateManager.enableTexture2D();
        // GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.disableCull();
     }

     public static void drawBoundingBox(Color c, AxisAlignedBB aa) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        int color = c.getRGB();
        float a = (float)(color >> 24 & 255) / 255.0F;
        a = (float)((double)a * 0.15D);
        float r = (float)(color >> 16 & 255) / 255.0F;
        float g = (float)(color >> 8 & 255) / 255.0F;
        float b = (float)(color & 255) / 255.0F;
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).color(r, g, b, a).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).color(r, g, b, a).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).color(r, g, b, a).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).color(r, g, b, a).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).color(r, g, b, a).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).color(r, g, b, a).endVertex();
        tessellator.draw();
     }
}