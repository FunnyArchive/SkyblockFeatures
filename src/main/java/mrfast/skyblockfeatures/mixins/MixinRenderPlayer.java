package mrfast.skyblockfeatures.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.utils.Utils;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

@Mixin(RenderPlayer.class)
public abstract class MixinRenderPlayer extends MixinRendererLivingEntity {
    @Inject(method = { "preRenderCallback(Lnet/minecraft/client/entity/AbstractClientPlayer;F)V" }, at = { @At("HEAD") })
    public void beforeRender(AbstractClientPlayer entitylivingbaseIn, float partialTickTime, CallbackInfo ci) {
        if (skyblockfeatures.config.DisguisePlayersAs == 7 && skyblockfeatures.config.playerDiguiser && !Utils.isNPC(entitylivingbaseIn) && Utils.inSkyblock) {
            GlStateManager.scale(0.5, 0.5, 0.5);
        }
        if (skyblockfeatures.config.DisguisePlayersAs == 8 && skyblockfeatures.config.playerDiguiser && !Utils.isNPC(entitylivingbaseIn) && Utils.inSkyblock) {
            GlStateManager.scale(1, 0.75, 1);
        }
    }

    @Shadow protected abstract ResourceLocation getEntityTexture(AbstractClientPlayer entity);

    @Inject(method = "getEntityTexture", at = @At("HEAD"), cancellable = true)
    private void onGetEntityTexture(AbstractClientPlayer entity, CallbackInfoReturnable<ResourceLocation> info) {
        if (skyblockfeatures.config.DisguisePlayersAs == 8 && skyblockfeatures.config.playerDiguiser && !Utils.isNPC(entity) && Utils.inSkyblock) {
            ResourceLocation customSkin = new ResourceLocation("skyblockfeatures", "monki.png");
            info.setReturnValue(customSkin);
            if(entity instanceof EntityOtherPlayerMP) {
                entity.setCurrentItemOrArmor(0, new ItemStack(Items.stick));
                entity.setCurrentItemOrArmor(4, null);
            }
        }
    }

}
