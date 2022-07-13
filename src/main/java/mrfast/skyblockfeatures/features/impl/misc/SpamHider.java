/*
 * skyblockfeatures - Hypixel Skyblock Quality of Life Mod
 * Copyright (C) 2021 skyblockfeatures
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
