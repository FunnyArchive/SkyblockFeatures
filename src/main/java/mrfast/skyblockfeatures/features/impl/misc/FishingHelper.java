package mrfast.skyblockfeatures.features.impl.misc;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.core.GuiManager;
import mrfast.skyblockfeatures.events.PacketEvent;
import mrfast.skyblockfeatures.events.ReceivePacketEvent;
import mrfast.skyblockfeatures.utils.RenderUtil;
import mrfast.skyblockfeatures.utils.Utils;

public class FishingHelper {
    boolean reelingIn = false;
    List<Vec3> particles = new ArrayList<Vec3>();
    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        Vec3 prev = null;
        try {
            double xDif = 0;
            double zDif = 0;
            double index = 0;
            for(Vec3 particle : particles) {
                index++;
                if(prev == null) {
                    prev = particle;
                    continue;
                }
                GlStateManager.disableCull();
                RenderUtil.draw3DLine(prev, particle, 5, new Color(255, 85, 85), event.partialTicks);
                GlStateManager.enableCull();
                xDif = prev.xCoord-particle.xCoord;
                zDif = prev.zCoord-particle.zCoord;
                if(index == particles.size()) {
                    GlStateManager.disableCull();
                    RenderUtil.draw3DLine(particle, new Vec3(particle.xCoord+xDif*-300,particle.yCoord,particle.zCoord+zDif*-300), 5, new Color(255, 255, 255), event.partialTicks);
                    GlStateManager.enableCull();
                }
                prev = particle;
            }
        } catch (Exception e) {
            //TODO: handle exception
        }
    }
    Vec3 oldParticle = null;
    @SubscribeEvent
    public void onRecievePacket(PacketEvent.ReceiveEvent event) {
        if (!Utils.inSkyblock || Utils.GetMC().theWorld == null || !skyblockfeatures.config.fishthing || reelingIn || !Utils.GetMC().inGameHasFocus) return;
        if(event.packet instanceof S2APacketParticles) {
            S2APacketParticles packet = (S2APacketParticles) event.packet;
            EnumParticleTypes type = packet.getParticleType();
            
            if(type == EnumParticleTypes.DRIP_LAVA && Utils.GetMC().thePlayer.getHeldItem() != null) {
                if(Utils.GetMC().thePlayer.getHeldItem().getDisplayName().contains("Ancestral")) {
                    double dist = Utils.GetMC().thePlayer.getDistance(packet.getXCoordinate(), packet.getYCoordinate(), packet.getZCoordinate());
                    if(dist>3 && dist<5) {
                        particles.clear();
                    }
                    
                    if(dist>5) {
                        if(!particles.contains(new Vec3(packet.getXCoordinate(), packet.getYCoordinate(), packet.getZCoordinate()))) {
                            particles.add(new Vec3(packet.getXCoordinate(), packet.getYCoordinate(), packet.getZCoordinate()));
                        }
                    }
                }
            }

            if (type == EnumParticleTypes.WATER_WAKE) {
                Entity hook = null;
                for(Entity entity:Utils.GetMC().theWorld.loadedEntityList) {
                    if(entity instanceof EntityFishHook) {
                        if(!(((EntityFishHook) entity).angler instanceof EntityOtherPlayerMP)) hook = entity;
                    }
                }
                if(hook != null) {
                    if(hook.getDistance(packet.getXCoordinate(), packet.getYCoordinate(), packet.getZCoordinate())<0.15 && Utils.GetMC().thePlayer.canEntityBeSeen(hook)) {
                        if(Utils.GetMC().thePlayer.getHeldItem().getItem() instanceof ItemFishingRod) {
                            reelingIn = true;
                            Utils.SendMessage(ChatFormatting.GREEN+"Reel it in!");
                            Utils.GetMC().thePlayer.playSound("note.pling", 1, 2);
                        }
                        Utils.setTimeout(()->{
                            reelingIn = false;
                        }, 500);
                    }
                }
            }
        }
    }
}
