package mrfast.skyblockfeatures.mixins;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import mrfast.skyblockfeatures.events.AddChatMessageEvent;
import mrfast.skyblockfeatures.events.ItemDropEvent;

@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP extends AbstractClientPlayer {
    @Shadow protected Minecraft mc;

    public MixinEntityPlayerSP(World worldIn, GameProfile playerProfile) {
        super(worldIn, playerProfile);
    }

    @Inject(method = "addChatMessage", at = @At("HEAD"), cancellable = true)
    private void onAddChatMessage(IChatComponent message, CallbackInfo ci) {
        try {
            if (MinecraftForge.EVENT_BUS.post(new AddChatMessageEvent(message))) ci.cancel();
        } catch (Throwable e) {
            mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText("§cskyblockfeatures caught and logged an exception at AddChatMessageEvent. Please report this on the Discord server."));
            e.printStackTrace();
        }
    }

    @Inject(method = "dropOneItem", at = @At("HEAD"), cancellable = true)
	void onDropItem(boolean dropAll, CallbackInfoReturnable<EntityItem> cir) {
		ItemDropEvent dropEvent = new ItemDropEvent();
		MinecraftForge.EVENT_BUS.post(dropEvent);

		if(dropEvent.isCanceled()) {
			cir.setReturnValue(null);
		}
	}
}
