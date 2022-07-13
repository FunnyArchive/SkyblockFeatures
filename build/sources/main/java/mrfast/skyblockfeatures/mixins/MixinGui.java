package mrfast.skyblockfeatures.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiChest;
import mrfast.skyblockfeatures.utils.Utils;

@Mixin(Gui.class)
public abstract class MixinGui {
    @Inject(method = "drawGradientRect", at = @At(value = "HEAD"), cancellable = true)
    private void connect(int a, int b, int c, int d, int e, int f, CallbackInfo ci) {
        if(Utils.GetMC().currentScreen instanceof GuiChest)
        ci.cancel();
    }
}
