package mrfast.skyblockfeatures.features.impl.events;

import java.awt.Color;

import com.mojang.realmsclient.gui.ChatFormatting;

import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.events.PacketEvent;
import mrfast.skyblockfeatures.utils.RenderUtil;
import mrfast.skyblockfeatures.utils.Utils;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MythologicalEvent {
    BlockPos burrow = null;
    boolean sendNotif = true;
    @SubscribeEvent
    public void onRecievePacket(PacketEvent.ReceiveEvent event) {
        if (!Utils.inSkyblock || Utils.GetMC().theWorld == null || !Utils.GetMC().inGameHasFocus || !skyblockfeatures.config.MythologicalHelper) return;
        if(event.packet instanceof S2APacketParticles) {
            S2APacketParticles packet = (S2APacketParticles) event.packet;
            EnumParticleTypes type = packet.getParticleType();
            if(type == EnumParticleTypes.FOOTSTEP) {
                if(!(burrow!=null && (burrow.getX()==Math.floor(packet.getXCoordinate()) || burrow.getZ()==Math.floor(packet.getZCoordinate())))) {
                    if(sendNotif) {
                        sendNotif = false;
                        Utils.SendMessage(ChatFormatting.GREEN+"Located new Griffin Burrow!");
                        Utils.playLoudSound("random.orb", 0.1);
                        Utils.setTimeout(()->{
                            sendNotif = true;
                        }, 15*1000);
                    }
                    
                    burrow = new BlockPos(Math.floor(packet.getXCoordinate()),Math.floor(packet.getYCoordinate()),Math.floor(packet.getZCoordinate()));
                }
            }
        }
    }
    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        if (!Utils.inSkyblock || event.type == 2 || !skyblockfeatures.config.MythologicalHelper) return;

        String unformatted = Utils.cleanColour(event.message.getUnformattedText());
        if(unformatted.contains("You dug out a Griffin Burrow")) {
            Utils.setTimeout(()->{
                burrow = null;
                sendNotif = true;
            }, 300);
        }
    }
    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        if(burrow!=null && skyblockfeatures.config.MythologicalHelper) {
            AxisAlignedBB aabb2 = new AxisAlignedBB(burrow, burrow.add(1, 1, 1));
            RenderUtil.drawOutlinedFilledBoundingBox(aabb2, Color.green, event.partialTicks);

            AxisAlignedBB aabb = new AxisAlignedBB(burrow.getX()+0.5, burrow.getY()+100, burrow.getZ()+0.5, burrow.getX()+0.5, burrow.getY(), burrow.getZ()+0.5);
            RenderUtil.drawOutlinedFilledBoundingBox(aabb, Color.green, event.partialTicks);
        }
    }
}
