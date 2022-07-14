package mrfast.skyblockfeatures.mixins;

import net.minecraft.crash.CrashReport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(CrashReport.class)
public abstract class MixinCrashReport {

    public boolean isskyblockfeaturesCrash = false;

    @Shadow public abstract String getCauseStackTraceOrString();

    @ModifyArg(method = "getCompleteReport", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/common/asm/transformers/BlamingTransformer;onCrash(Ljava/lang/StringBuilder;)V", remap = false))
    private StringBuilder blameskyblockfeatures(StringBuilder stringbuilder) {
        if (getCauseStackTraceOrString().contains("mrfast.skyblockfeatures")) {
            isskyblockfeaturesCrash = true;
            stringbuilder.append("skyblockfeatures may have caused this crash.\nJoin the Discord for support at discord.gg/skyblockfeatures\n");
        }
        return stringbuilder;
    }

    @ModifyArg(method = "getCompleteReport", at = @At(value = "INVOKE", target = "Ljava/lang/StringBuilder;append(Ljava/lang/String;)Ljava/lang/StringBuilder;", ordinal = 2, remap = false))
    private String replaceWittyComment(String comment) {
        if (isskyblockfeaturesCrash) {
            comment = "Did Sychic do that?";
        }
        return comment;
    }

}
