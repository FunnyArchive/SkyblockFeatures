package mrfast.skyblockfeatures.mixins;

import net.minecraft.client.renderer.InventoryEffectRenderer;
import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.utils.Utils;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;


@Mixin({InventoryEffectRenderer.class})
public class MixinInventoryEffectRenderer {

    @ModifyVariable(method="updateActivePotionEffects", at=@At(value="STORE"))
    public boolean hasVisibleEffect_updateActivePotionEffects(boolean hasVisibleEffect) {
        if(skyblockfeatures.config.hidepotion && Utils.inSkyblock) {
            return false;
        } else {
            return hasVisibleEffect;
        }
    }

}
