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
