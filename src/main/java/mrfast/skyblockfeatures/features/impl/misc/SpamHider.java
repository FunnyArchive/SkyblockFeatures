package mrfast.skyblockfeatures.features.impl.misc;

import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.events.PacketEvent;
import mrfast.skyblockfeatures.utils.StringUtils;
import mrfast.skyblockfeatures.utils.Utils;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SpamHider {

    private static void cancelChatPacket(PacketEvent.ReceiveEvent ReceivePacketEvent, boolean addToSpam) {
        if (!(ReceivePacketEvent.packet instanceof S02PacketChat)) return;
        ReceivePacketEvent.setCanceled(true);
        S02PacketChat packet = ((S02PacketChat) ReceivePacketEvent.packet);
        MinecraftForge.EVENT_BUS.post(new ClientChatReceivedEvent(packet.getType(), packet.getChatComponent()));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public void onChatPacket(PacketEvent.ReceiveEvent event) {
        if (!(event.packet instanceof S02PacketChat)) return;
        S02PacketChat packet = (S02PacketChat) event.packet;
        if (packet.getType() == 2) return;
        String unformatted = StringUtils.stripControlCodes(packet.getChatComponent().getUnformattedText());

        if (!Utils.inSkyblock) return;
        
        try {
            if (unformatted.contains("[Auction]") || unformatted.contains("Bid of") || unformatted.contains("created a") || unformatted.contains("Auction started")) return;
            if (unformatted.toLowerCase().contains("cheap") || unformatted.toLowerCase().contains("selling") || unformatted.toLowerCase().contains("buying") || unformatted.toLowerCase().contains("visit") || unformatted.toLowerCase().contains("ah") || unformatted.toLowerCase().contains("auction")) {
                if (skyblockfeatures.config.hideAdvertisments) {
                    cancelChatPacket(event, false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
