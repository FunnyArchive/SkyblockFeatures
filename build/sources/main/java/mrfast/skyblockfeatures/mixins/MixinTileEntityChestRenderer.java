// /*
//  * skyblockfeatures - Hypixel Skyblock Quality of Life Mod
//  * Copyright (C) 2021 skyblockfeatures
//  *
//  * This program is free software: you can redistribute it and/or modify
//  * it under the terms of the GNU Affero General Public License as published
//  * by the Free Software Foundation, either version 3 of the License, or
//  * (at your option) any later version.
//  *
//  * This program is distributed in the hope that it will be useful,
//  * but WITHOUT ANY WARRANTY; without even the implied warranty of
//  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  * GNU Affero General Public License for more details.
//  *
//  * You should have received a copy of the GNU Affero General Public License
//  * along with this program.  If not, see <https://www.gnu.org/licenses/>.
//  */

// package mrfast.skyblockfeatures.mixins;

// import java.awt.Color;

// import org.spongepowered.asm.mixin.Mixin;
// import org.spongepowered.asm.mixin.injection.At;
// import org.spongepowered.asm.mixin.injection.Inject;
// import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// import net.minecraft.client.renderer.GlStateManager;
// import net.minecraft.client.renderer.tileentity.TileEntityChestRenderer;
// import net.minecraft.tileentity.TileEntityChest;
// import mrfast.skyblockfeatures.features.impl.dungeons.solvers.ThreeWeirdosSolver;

// @Mixin(TileEntityChestRenderer.class)
// public class MixinTileEntityChestRenderer {

//     @Inject(method = "renderTileEntityAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelChest;renderAll()V", shift = At.Shift.BEFORE))
//     private void setChestColor(TileEntityChest te, double x, double y, double z, float partialTicks, int destroyStage, CallbackInfo ci) {
//         if (te.getPos().equals(ThreeWeirdosSolver.riddleChest)) {
//             Color colour = new Color(255, 0, 0, 198);
//             GlStateManager.color((float)colour.getRed()/255, (float)colour.getGreen()/255, (float)colour.getBlue()/255);
//             GlStateManager.disableTexture2D();
//         }
//     }

//     @Inject(method = "renderTileEntityAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelChest;renderAll()V", shift = At.Shift.AFTER))
//     private void setChestColorPost(TileEntityChest te, double x, double y, double z, float partialTicks, int destroyStage, CallbackInfo ci) {
//         GlStateManager.enableTexture2D();
//     }

// }
