package mrfast.skyblockfeatures.features.impl.misc;

import java.util.Random;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.events.PacketEvent;
import mrfast.skyblockfeatures.events.ReceivePacketEvent;
import mrfast.skyblockfeatures.utils.Utils;

public class FishingHelper {
    boolean reelingIn = false;
    @SubscribeEvent
    public void onRecievePacket(PacketEvent.ReceiveEvent event) {
        if (!Utils.inSkyblock || Utils.GetMC().theWorld == null || !skyblockfeatures.config.fishthing || reelingIn || !Utils.GetMC().inGameHasFocus) return;
        if(event.packet instanceof S2APacketParticles) {
            S2APacketParticles packet = (S2APacketParticles) event.packet;
            EnumParticleTypes type = packet.getParticleType();

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
                            double angleNeeded = calculateAngle(Utils.GetMC().thePlayer.posX,Utils.GetMC().thePlayer.posZ,hook.posX,hook.posZ);
                            float headRotation = -(Utils.GetMC().thePlayer.rotationYawHead%360);
                            double distance = Math.sqrt(Math.pow(Utils.GetMC().thePlayer.posX-hook.posX,2)+Math.pow(Utils.GetMC().thePlayer.posZ-hook.posZ,2));

                            // if(headRotation>angleNeeded-Math.pow(distance, 2) && headRotation<angleNeeded+Math.pow(distance, 2)) {
                                reelingIn = true;
                                Utils.SendMessage(ChatFormatting.GREEN+"Reel it in!");
                                Utils.GetMC().thePlayer.playSound("note.pling", 1, 2);
                                Utils.setTimeout(()-> {
                                    Utils.GetMC().playerController.sendUseItem(Utils.GetMC().thePlayer, Utils.GetMC().thePlayer.worldObj, Utils.GetMC().thePlayer.getHeldItem());
                                },150);
                            // }
                        }
                        Utils.setTimeout(()->{
                            reelingIn = false;
                        }, 500);
                    }
                    
                }
            }
        }
    }

    public static double calculateAngle(double x1, double y1, double x2, double y2) {
        double angle = Math.toDegrees(Math.atan2(x2 - x1, y2 - y1));
        // Keep angle between 0 and 360
        angle = angle + Math.ceil( -angle / 360 ) * 360;

        return angle;
    }
}
