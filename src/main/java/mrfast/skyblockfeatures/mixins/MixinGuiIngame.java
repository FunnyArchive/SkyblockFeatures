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

package mrfast.skyblockfeatures.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import mrfast.skyblockfeatures.events.SetActionBarEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.MinecraftForge;

@Mixin(GuiIngame.class)
public class MixinGuiIngame {

    @Inject(method = "setRecordPlaying(Ljava/lang/String;Z)V", at = @At("HEAD"), cancellable = true)
    private void onSetActionBar(String message, boolean isPlaying, CallbackInfo ci) {
        try {
            if (MinecraftForge.EVENT_BUS.post(new SetActionBarEvent(message, isPlaying))) ci.cancel();
        } catch (Throwable e) {
            Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new ChatComponentText("§cskyblockfeatures caught and logged an exception at SetActionBarEvent. Please report this on the Discord server."));
            e.printStackTrace();
        }
    }
}
