package mrfast.skyblockfeatures.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.esotericsoftware.asm.Opcodes;

import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.utils.Utils;
import mrfast.skyblockfeatures.utils.graphics.ScreenRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Shadow
    private EntityPlayerSP thePlayer;
    public FontRenderer mcFontRendererObj;

    @Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/IReloadableResourceManager;registerReloadListener(Lnet/minecraft/client/resources/IResourceManagerReloadListener;)V", shift = At.Shift.AFTER))
    private void initializeSmartFontRenderer(CallbackInfo ci) {
        ScreenRenderer.refresh();
    }
    @Redirect(method = "startGame", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;fontRendererObj:Lnet/minecraft/client/gui/FontRenderer;", opcode = Opcodes.PUTFIELD))
    public void startFontRenderer(Minecraft instance, FontRenderer value) {
        if(skyblockfeatures.config.customFont) {
            Utils.GetMC().fontRendererObj = new FontRenderer(Utils.GetMC().gameSettings, new ResourceLocation("skyblockfeatures:font/ascii.png"), Utils.GetMC().renderEngine, false);
        } else {
            Utils.GetMC().fontRendererObj = new FontRenderer(Utils.GetMC().gameSettings, new ResourceLocation("textures/font/ascii.png"), Utils.GetMC().renderEngine, false);
        }
    }
}
