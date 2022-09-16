package mrfast.skyblockfeatures.features.impl.misc;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.mojang.realmsclient.gui.ChatFormatting;

import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.events.PacketEvent;
import mrfast.skyblockfeatures.utils.RenderUtil;
import mrfast.skyblockfeatures.utils.SBInfo;
import mrfast.skyblockfeatures.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FishingHelper {
    boolean reelingIn = false;
    List<Vec3> particles = new ArrayList<Vec3>();
    List<Vec3> fishingParticles = new ArrayList<Vec3>();
    public static Entity fishingHook = null;
    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        if(geyser != null && skyblockfeatures.config.geyserBoundingBox) {
            Entity viewer = Minecraft.getMinecraft().getRenderViewEntity();
            double viewerX = viewer.lastTickPosX + (viewer.posX - viewer.lastTickPosX) * event.partialTicks;
            double viewerY = viewer.lastTickPosY + (viewer.posY - viewer.lastTickPosY) * event.partialTicks;
            double viewerZ = viewer.lastTickPosZ + (viewer.posZ - viewer.lastTickPosZ) * event.partialTicks;
            double x = geyser.getXCoordinate() - viewerX;
            double y = 118 - viewerY;
            double z = geyser.getZCoordinate() - viewerZ;
            GlStateManager.enableCull();
            Color color = 
                fishingHook!=null?
                    fishingHook.getDistance(geyser.getXCoordinate(), fishingHook.posY, geyser.getZCoordinate())<=1?
                        new Color(85,255,85): // Green
                    new Color(255,85,255): // Magenta
                new Color(255,85,255); // Magenta

            RenderUtil.drawFilledBoundingBox(new AxisAlignedBB(x-1, y-0.1, z-1, x+1, y-0.09, z+1),color,0.5f);
            GlStateManager.disableCull();
        }

        Vec3 prev = null;
        // if(skyblockfeatures.config.MythologicalHelper) {
            // try {
            //     for(Vec3 particle : fishingParticles) {
            //         if(prev == null) {
            //             prev = particle;
            //             continue;
            //         }
            //         if(prev.distanceTo(particle) < 1) {
            //             GlStateManager.disableCull();
            //             RenderUtil.draw3DLine(prev, particle, 1, new Color(255, 0, 0), event.partialTicks);
            //             GlStateManager.enableCull();
            //         }
            //         prev = particle;
            //     }
            // } catch (Exception e) {
            //     //TODO: handle exception
            // }
        // };

        // Diana helper
        if(skyblockfeatures.config.MythologicalHelper) {
            prev = null;
            try {
                double xDif = 0;
                double zDif = 0;
                double yDif = 0;
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
                    yDif = prev.yCoord-particle.yCoord;
                    if(index == particles.size()) {
                        for(int i=0;i<300;i++) {
                            GlStateManager.disableCull();
                            RenderUtil.draw3DLine(prev, new Vec3(particle.xCoord+xDif*-(i),particle.yCoord-yDif*i,particle.zCoord+zDif*-(i)), 5, new Color(255, 255, 255), event.partialTicks);
                            GlStateManager.enableCull();
                            prev = new Vec3(particle.xCoord+xDif*-(i),particle.yCoord-yDif*i,particle.zCoord+zDif*-(i));
                        }
                    }
                    prev = particle;
                }
            } catch (Exception e) {
                //TODO: handle exception
            }
        };
    }
    Vec3 oldParticle = null;
    S2APacketParticles geyser = null;
    
    @SubscribeEvent
    public void onRecievePacket(PacketEvent.ReceiveEvent event) {
        if (!Utils.inSkyblock || Utils.GetMC().theWorld == null || !skyblockfeatures.config.fishthing || reelingIn || !Utils.GetMC().inGameHasFocus) return;
        if(event.packet instanceof S2APacketParticles) {
            S2APacketParticles packet = (S2APacketParticles) event.packet;
            EnumParticleTypes type = packet.getParticleType();
            if(SBInfo.getInstance().location.contains("Volcano")) {
                Entity hook = null;
                for(Entity entity:Utils.GetMC().theWorld.loadedEntityList) {
                    if(entity instanceof EntityFishHook) {
                        if(!(((EntityFishHook) entity).angler instanceof EntityOtherPlayerMP)) hook = entity;
                    }
                }
                if(type == EnumParticleTypes.CLOUD && skyblockfeatures.config.geyserBoundingBox) {
                    geyser = packet;
                    fishingHook = hook;
                }
                if(hook != null && skyblockfeatures.config.hideGeyserParticles) {
                    if(type == EnumParticleTypes.SMOKE_NORMAL) {
                        if(packet.getYCoordinate() > hook.posY-0.2 && packet.getYCoordinate() < hook.posY+0.2) {
                            // Dont know if i want this in here yet
                            // if(hook.getDistance(packet.getXCoordinate(), packet.getYCoordinate(), packet.getZCoordinate())<0.15 && Utils.GetMC().thePlayer.canEntityBeSeen(hook)) {
                            //     Utils.SendMessage(ChatFormatting.GREEN+"Reel it in!");
                            //     Utils.GetMC().thePlayer.playSound("note.pling", 1, 2);
                            // }
                        } else {
                            event.setCanceled(true);
                        }
                    } else {
                        event.setCanceled(true);
                    }
                }
            }
            
            if(Utils.GetMC().thePlayer.getHeldItem() != null && Utils.GetMC().thePlayer.getHeldItem().getDisplayName().contains("Ancestral")) {
                if(type == EnumParticleTypes.DRIP_LAVA) {
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
                    Vec3 pos = new Vec3(packet.getXCoordinate(), packet.getYCoordinate(), packet.getZCoordinate());
                    if(hook.getDistance(packet.getXCoordinate(), packet.getYCoordinate(), packet.getZCoordinate())<6 && !fishingParticles.contains(pos)) {
                        fishingParticles.add(pos);
                    }
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
                } else {
                    fishingParticles.clear();
                }
            }
        }
    }
}
