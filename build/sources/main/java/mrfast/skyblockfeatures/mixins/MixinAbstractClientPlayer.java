package mrfast.skyblockfeatures.mixins;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;
import mrfast.skyblockfeatures.utils.CapeUtils;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(value={AbstractClientPlayer.class})
public abstract class MixinAbstractClientPlayer {
    @Shadow
    @Nullable
    protected abstract NetworkPlayerInfo getPlayerInfo();
    
    @Inject(method={"getLocationCape"}, at={@At(value="HEAD")}, cancellable=true)
    public void getLocationCape(CallbackInfoReturnable<ResourceLocation> callbackInfoReturnable) {
        NetworkPlayerInfo info = this.getPlayerInfo();
        assert info != null;
        String uuid = info.getGameProfile().getId().toString();

        if (CapeUtils.is_uuid_valid(info.getGameProfile().getId())) {
            callbackInfoReturnable.setReturnValue(new ResourceLocation("skyblockfeatures","capes/cape.png"));
        }
    }
}

