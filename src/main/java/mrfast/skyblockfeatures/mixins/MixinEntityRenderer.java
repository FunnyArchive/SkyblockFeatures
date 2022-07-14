package mrfast.skyblockfeatures.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.utils.Utils;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {

    @Shadow private Minecraft mc;

    @ModifyVariable(method={"orientCamera"}, ordinal=3, at=@At(value="STORE", ordinal=0), require=1)
    public double changeCameraDistanceHook(double range) {
        if (skyblockfeatures.perspectiveToggled) {
            return 4;
        } else {
            return range;
        }
    }

    @ModifyVariable(method={"orientCamera"}, ordinal=7, at=@At(value="STORE", ordinal=0), require=1)
    public double orientCameraHook(double range) {
        if (skyblockfeatures.perspectiveToggled) {
            return 4;
        } else {
            return range;
        }
    }

    @Inject(method = "hurtCameraEffect", at = @At("HEAD"), cancellable = true)
    private void onHurtcam(float partialTicks, CallbackInfo ci) {
        if (Utils.inSkyblock && skyblockfeatures.config.noHurtcam) ci.cancel();
    }

    @Redirect(method = "updateLightmap", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getLastLightningBolt()I"))
    private int getLastLightningBolt(World world) {
        if (skyblockfeatures.config.hideLightning && Utils.inSkyblock) return 0;
        return world.getLastLightningBolt();
    }


    @Inject(method = "renderHand", at = @At("TAIL"))
    private void renderHand(float partialTicks, int xOffset, CallbackInfo ci) {
        if (skyblockfeatures.config.oldAnimations) {
            if (this.mc.thePlayer.getItemInUseCount() != 0 && this.mc.gameSettings.keyBindAttack.isKeyDown() && this.mc.gameSettings.keyBindUseItem.isKeyDown() && this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit.equals(MovingObjectPosition.MovingObjectType.BLOCK)) {
                this.swingItem(this.mc.thePlayer);
            }
        }
    }

    private void swingItem(EntityLivingBase entity) {
        ItemStack stack = entity.getHeldItem();
        if (stack != null && stack.getItem() != null && (!entity.isSwingInProgress || entity.swingProgressInt >= this.getArmSwingAnimationEnd(entity) / 2 || entity.swingProgressInt < 0)) {
            entity.swingProgressInt = -1;
            entity.isSwingInProgress = true;
        }
    }

    private int getArmSwingAnimationEnd(EntityLivingBase e) {
        return e.isPotionActive(Potion.digSpeed) ? (6 - (1 + e.getActivePotionEffect(Potion.digSpeed).getAmplifier())) : (e.isPotionActive(Potion.digSlowdown) ? (6 + (1 + e.getActivePotionEffect(Potion.digSlowdown).getAmplifier()) * 2) : 6);
    }
    

}
