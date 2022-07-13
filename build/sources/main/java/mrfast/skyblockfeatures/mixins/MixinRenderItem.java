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

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import mrfast.skyblockfeatures.events.GuiRenderItemEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;

@Mixin(RenderItem.class)
public abstract class MixinRenderItem {

    @Shadow @Final private static ResourceLocation RES_ITEM_GLINT;

    @Shadow @Final private TextureManager textureManager;

    @Shadow protected abstract void renderModel(IBakedModel model, int color);

    @Inject(method = "renderItemOverlayIntoGUI", at = @At("RETURN"))
    private void renderItemOverlayPost(FontRenderer fr, ItemStack stack, int xPosition, int yPosition, String text, CallbackInfo ci) {
        try {
            MinecraftForge.EVENT_BUS.post(new GuiRenderItemEvent.RenderOverlayEvent.Post(fr, stack, xPosition, yPosition, text));
        } catch (Throwable e) {
            Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new ChatComponentText("§cskyblockfeatures caught and logged an exception at GuiRenderItemEvent.RenderOverlayEvent.Pre. Please report this on the Discord server."));
            e.printStackTrace();
        }
    }
}
