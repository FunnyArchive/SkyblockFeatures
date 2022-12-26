package mrfast.skyblockfeatures.mixins;

import net.minecraft.client.renderer.VboRenderList;
import net.minecraft.util.EnumWorldBlockLayer;
import mrfast.skyblockfeatures.features.impl.dungeons.DungeonBlocks;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/**
 * Taken from NotEnoughUpdates under Creative Commons Attribution-NonCommercial 3.0
 * https://github.com/Moulberry/NotEnoughUpdates/blob/master/LICENSE
 * @author Moulberry
 */
@Mixin({VboRenderList.class})
public abstract class MixinVboRenderList {

    @Inject(method="renderChunkLayer", at=@At("HEAD"))
    public void renderChunkLayer(EnumWorldBlockLayer layer, CallbackInfo ci) {
        if(DungeonBlocks.textureExists()) {
            DungeonBlocks.bindTextureIfExists();

            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST_MIPMAP_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        }
    }
}