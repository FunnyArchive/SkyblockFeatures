package mrfast.skyblockfeatures.features.impl.misc;

import net.minecraft.network.play.server.S45PacketTitle;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.events.PacketEvent;
import mrfast.skyblockfeatures.utils.StringUtils;
import mrfast.skyblockfeatures.utils.Utils;

public class FarmingFeatures {
    // @SubscribeEvent
    // public void onAttemptBreak(DamageBlockEvent event) {
    //     if (!Utils.inSkyblock || mc.thePlayer == null || mc.theWorld == null) return;
    //     EntityPlayerSP p = mc.thePlayer;
    // }

    @SubscribeEvent
    public void onReceivePacket(PacketEvent.ReceiveEvent event) {
        if (!Utils.inSkyblock) return;
        if (event.packet instanceof S45PacketTitle) {
            S45PacketTitle packet = (S45PacketTitle) event.packet;
            if (packet.getMessage() != null) {
                String unformatted = StringUtils.stripControlCodes(packet.getMessage().getUnformattedText());
                if (skyblockfeatures.config.hideFarmingRNGTitles && unformatted.contains("DROP!")) {
                    event.setCanceled(true);
                }
            }
        }
    }

}
