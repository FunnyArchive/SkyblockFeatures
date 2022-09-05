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
            e.printStackTrace();
        }
    }
}
