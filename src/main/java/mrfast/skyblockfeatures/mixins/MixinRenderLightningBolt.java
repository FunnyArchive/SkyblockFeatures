package mrfast.skyblockfeatures.mixins;

import net.minecraft.client.renderer.entity.RenderLightningBolt;
import net.minecraft.entity.effect.EntityLightningBolt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.utils.Utils;

@Mixin(RenderLightningBolt.class)
public class MixinRenderLightningBolt {
    @Inject(method = "doRender", at = @At("HEAD"), cancellable = true)
    private void onRenderLightning(EntityLightningBolt entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
        if (skyblockfeatures.config.hideLightning && Utils.inSkyblock) {
            ci.cancel();
        }
    }
}
