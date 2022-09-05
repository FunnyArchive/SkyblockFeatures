package mrfast.skyblockfeatures.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import mrfast.skyblockfeatures.events.BossBarEvent;

@Mixin(BossStatus.class)
public class MixinBossStatus {

    @Inject(method = "setBossStatus", at = @At("HEAD"), cancellable = true)
    private static void onSetBossStatus(IBossDisplayData displayData, boolean hasColorModifierIn, CallbackInfo ci) {
        try {
            if (MinecraftForge.EVENT_BUS.post(new BossBarEvent.Set(displayData, hasColorModifierIn))) ci.cancel();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}