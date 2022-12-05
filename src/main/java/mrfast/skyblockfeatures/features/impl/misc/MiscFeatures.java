package mrfast.skyblockfeatures.features.impl.misc;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.lwjgl.opengl.GL11;

import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.events.CheckRenderEntityEvent;
import mrfast.skyblockfeatures.events.PacketEvent;
import mrfast.skyblockfeatures.utils.RenderUtil;
import mrfast.skyblockfeatures.utils.SBInfo;
import mrfast.skyblockfeatures.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MiscFeatures {
    @SubscribeEvent
    public void onCheckRender(CheckRenderEntityEvent event) {
        if (!Utils.inSkyblock) return;

        if (event.entity instanceof EntityItem) {
            EntityItem entity = (EntityItem) event.entity;
            if (skyblockfeatures.config.hideJerryRune) {
                ItemStack item = entity.getEntityItem();
                if(item.getItem() == Items.spawn_egg && Objects.equals(ItemMonsterPlacer.getEntityName(item), "Villager") && item.getDisplayName().equals("Spawn Villager") && entity.lifespan == 6000) {
                    event.setCanceled(true);
                }
            }
        }

        if (event.entity instanceof EntityLightningBolt) {
            if (skyblockfeatures.config.hideLightning) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onRenderOverlayPre(RenderGameOverlayEvent.Pre event) {
        if (!Utils.inSkyblock) return;
        if (event.type == RenderGameOverlayEvent.ElementType.AIR && skyblockfeatures.config.hideAirDisplay && !Utils.inDungeons) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onRenderBlockOverlay(RenderBlockOverlayEvent event) {
        if (Utils.inSkyblock && skyblockfeatures.config.noFire && event.overlayType == RenderBlockOverlayEvent.OverlayType.FIRE) {
            event.setCanceled(true);
        }
    }
    List<Vec3> particles = new ArrayList<Vec3>();
    @SubscribeEvent
    public void onRecievePacket(PacketEvent.ReceiveEvent event) {
        if(event.packet instanceof S2APacketParticles  && skyblockfeatures.config.highlightMushrooms) {
            S2APacketParticles packet = (S2APacketParticles) event.packet;
            EnumParticleTypes type = packet.getParticleType();
            Vec3 pos = new Vec3(Math.floor(packet.getXCoordinate()),Math.floor(packet.getYCoordinate()),Math.floor(packet.getZCoordinate()));
            boolean dupe = false;
            for(Vec3 part:particles) {
                if(part.distanceTo(pos) < 1 || part==pos) {
                    dupe = true;
                }
            }
            
            if(!dupe && type == EnumParticleTypes.SPELL_MOB && SBInfo.getInstance().location.contains("Glowing")) {
                particles.add(pos);
            }
        }
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        if(SBInfo.getInstance().location.contains("Glowing") && skyblockfeatures.config.highlightMushrooms) {
            try {
                for(Vec3 packet:particles) {
                    Color color = new Color(0x55FF55);
                    highlightBlock(color, Math.floor(packet.xCoord),Math.floor(packet.yCoord), Math.floor(packet.zCoord), event.partialTicks);

                    Block block = Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(packet)).getBlock();
                    if(block != null && block == Blocks.air) {
                        particles.remove(packet);
                    }
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

    public static void highlightBlock(Color c, double d, double d1, double d2, float ticks) {
        RenderUtil.drawOutlinedFilledBoundingBox(new AxisAlignedBB(d + 1.0D, d1 + 1, d2 + 1.0D, d, d1, d2),c,ticks);
    }

    public static void drawBoundingBox(AxisAlignedBB aabb, Color color, double partialTicks) {
        Entity render = Minecraft.getMinecraft().getRenderViewEntity();

        double realX = render.lastTickPosX + (render.posX - render.lastTickPosX) * partialTicks;
        double realY = render.lastTickPosY + (render.posY - render.lastTickPosY) * partialTicks;
        double realZ = render.lastTickPosZ + (render.posZ - render.lastTickPosZ) * partialTicks;

        GlStateManager.pushMatrix();
        GlStateManager.translate(-realX, -realY, -realZ);
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GL11.glLineWidth(2);

        RenderGlobal.drawOutlinedBoundingBox(aabb, color.getRed(), color.getRed(), color.getRed(), 1);

        GlStateManager.translate(realX, realY, realZ);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
     }
}
