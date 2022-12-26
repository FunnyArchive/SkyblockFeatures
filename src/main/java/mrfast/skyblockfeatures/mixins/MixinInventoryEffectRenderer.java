package mrfast.skyblockfeatures.mixins;

import net.minecraft.client.renderer.InventoryEffectRenderer;
import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.utils.Utils;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Taken from NotEnoughUpdates under Creative Commons Attribution-NonCommercial 3.0
 * https://github.com/Moulberry/NotEnoughUpdates/blob/master/LICENSE
 *
 * @author Moulberry
 */
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
