package mrfast.skyblockfeatures.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.utils.Utils;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;

@Mixin(RenderPlayer.class)
public abstract class MixinRenderPlayer extends MixinRendererLivingEntity {
    @Inject(method = { "preRenderCallback(Lnet/minecraft/client/entity/AbstractClientPlayer;F)V" }, at = { @At("HEAD") })
    public void beforeRender(AbstractClientPlayer entitylivingbaseIn, float partialTickTime, CallbackInfo ci) {
        if (skyblockfeatures.config.DisguisePlayersAs == 7 && skyblockfeatures.config.playerDiguiser && !Utils.isNPC(entitylivingbaseIn) && Utils.inSkyblock) {
            GlStateManager.scale(0.5, 0.5, 0.5);
        }
    }
}
