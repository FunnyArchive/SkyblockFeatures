package mrfast.skyblockfeatures.mixins;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.events.CheckRenderEntityEvent;
import mrfast.skyblockfeatures.features.impl.dungeons.DungeonBlocks;
import mrfast.skyblockfeatures.features.impl.dungeons.DungeonsFeatures;
import mrfast.skyblockfeatures.features.impl.dungeons.Nametags;
import mrfast.skyblockfeatures.utils.SpecialColour;
import mrfast.skyblockfeatures.utils.Utils;

@Mixin(Render.class)
public abstract class MixinRender<T extends Entity> {

    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private void shouldRender(T livingEntity, ICamera camera, double camX, double camY, double camZ, CallbackInfoReturnable<Boolean> cir) {
        if(!Utils.isNPC(livingEntity) && livingEntity.getDistanceToEntity(Utils.GetMC().thePlayer) > 49 && Utils.inSkyblock && skyblockfeatures.config.HideFarEntity && !Utils.inDungeons) {
            cir.setReturnValue(false);
        }
        
        try {
            if (MinecraftForge.EVENT_BUS.post(new CheckRenderEntityEvent<T>(livingEntity, camera, camX, camY, camZ))) cir.setReturnValue(false);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Inject(method = "renderEntityOnFire", at = @At("HEAD"), cancellable = true)
    private void removeEntityOnFire(Entity entity, double x, double y, double z, float partialTicks, CallbackInfo ci) {
        if (skyblockfeatures.config.hideEntityFire && Utils.inSkyblock) {
            ci.cancel();
        }
    }

    @Inject(method="bindEntityTexture", at=@At("HEAD"), cancellable = true)
    public void bindEntityTexture(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if(entity instanceof EntityBat && DungeonBlocks.isOverriding()) {
            if(DungeonBlocks.bindModifiedTexture(new ResourceLocation("textures/entity/bat.png"),
                    SpecialColour.specialToChromaRGB("0:100:12:255:0"))) {
                cir.setReturnValue(true);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST_MIPMAP_LINEAR);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
            }
        }
    }

}
