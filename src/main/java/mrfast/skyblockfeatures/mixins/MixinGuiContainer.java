package mrfast.skyblockfeatures.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import mrfast.skyblockfeatures.events.GuiContainerEvent;
import mrfast.skyblockfeatures.events.InventorySlotClickEvent;
import mrfast.skyblockfeatures.events.InventorySlotDrawEvent;
import mrfast.skyblockfeatures.features.impl.misc.LockingSlots;

@Mixin(GuiContainer.class)
public abstract class MixinGuiContainer extends GuiScreen {

    @Shadow public Container inventorySlots;

    @Inject(method="drawSlot", at=@At("RETURN"))
    public void drawSlotRet(Slot slotIn, CallbackInfo ci) {
        LockingSlots.drawSlot(slotIn);
    }

    private final GuiContainer that = (GuiContainer) (Object) this;

    @Inject(method = "keyTyped", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;closeScreen()V", shift = At.Shift.BEFORE))
    private void closeWindowPressed(CallbackInfo ci) {
        try {
            MinecraftForge.EVENT_BUS.post(new GuiContainerEvent.CloseWindowEvent(that, inventorySlots));
        } catch (Throwable e) {
            Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new ChatComponentText("§cskyblockfeatures caught and logged an exception at GuiContainerEvent.CloseWindowEvent. Please report this on the Discord server."));
            e.printStackTrace();
        }
    }

    @Inject(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;color(FFFF)V", ordinal = 1))
    private void backgroundDrawn(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        try {
            MinecraftForge.EVENT_BUS.post(new GuiContainerEvent.BackgroundDrawnEvent(that, inventorySlots, mouseX, mouseY, partialTicks));
        } catch (Throwable e) {
            Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new ChatComponentText("§cskyblockfeatures caught and logged an exception at GuiContainerEvent.BackgroundDrawnEvent. Please report this on the Discord server."));
            e.printStackTrace();
        }
    }

    @Inject(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/inventory/GuiContainer;drawGuiContainerForegroundLayer(II)V", ordinal = 0, shift = At.Shift.BEFORE))
    private void titleDrawn(int mouseX, int mouseY, float partialTicks,CallbackInfo ci) {
        try {
            MinecraftForge.EVENT_BUS.post(new GuiContainerEvent.TitleDrawnEvent.Pre(that, inventorySlots, mouseX, mouseY, partialTicks));
        } catch (Throwable e) {
            Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new ChatComponentText("§cskyblockfeatures caught and logged an exception at GuiContainerEvent.TitleDrawnEvent. Please report this on the Discord server."));
            e.printStackTrace();
        }
    }

    @Inject(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/inventory/GuiContainer;drawGuiContainerForegroundLayer(II)V", ordinal = 0, shift = At.Shift.AFTER))
    private void titlePostDrawn(int mouseX, int mouseY, float partialTicks,CallbackInfo ci) {
        try {
            MinecraftForge.EVENT_BUS.post(new GuiContainerEvent.TitleDrawnEvent.Post(that, inventorySlots, mouseX, mouseY, partialTicks));
        } catch (Throwable e) {
            Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new ChatComponentText("§cskyblockfeatures caught and logged an exception at GuiContainerEvent.TitleDrawnEvent. Please report this on the Discord server."));
            e.printStackTrace();
        }
    }

    @Inject(method = "drawSlot", at = @At("HEAD"), cancellable = true)
    private void onDrawSlot(Slot slot, CallbackInfo ci) {
        try {
            if (MinecraftForge.EVENT_BUS.post(new GuiContainerEvent.DrawSlotEvent.Pre(that, inventorySlots, slot))) ci.cancel();
        } catch (Throwable e) {
            Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new ChatComponentText("§cskyblockfeatures caught and logged an exception at GuiContainerEvent.DrawSlotEvent.Pre. Please report this on the Discord server."));
            e.printStackTrace();
        }
    }

    @Inject(method = "drawSlot", at = @At("RETURN"), cancellable = true)
    private void onDrawSlotPost(Slot slot, CallbackInfo ci) {
        try {
            MinecraftForge.EVENT_BUS.post(new GuiContainerEvent.DrawSlotEvent.Post(that, inventorySlots, slot));
        } catch (Throwable e) {
            Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new ChatComponentText("§cskyblockfeatures caught and logged an exception at GuiContainerEvent.DrawSlotEvent.Post. Please report this on the Discord server."));
            e.printStackTrace();
        }
    }

    
    
    @Inject(method = "handleMouseClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/PlayerControllerMP;windowClick(IIIILnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/item/ItemStack;"), cancellable = true)
    private void onMouseClick(Slot slot, int slotId, int clickedButton, int clickType, CallbackInfo ci) {
        if(null != slot) {
			InventorySlotClickEvent event = new InventorySlotClickEvent(slot);
			MinecraftForge.EVENT_BUS.post(event);
			if(event.isCanceled()) {
				ci.cancel();
			}
		}
        try {
            if (MinecraftForge.EVENT_BUS.post(new GuiContainerEvent.SlotClickEvent(that, inventorySlots, slot, slotId, clickedButton, clickType))) ci.cancel();
        } catch (Throwable e) {
            Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new ChatComponentText("§cskyblockfeatures caught and logged an exception at GuiContainerEvent.SlotClickEvent. Please report this on the Discord server."));
            e.printStackTrace();
        }
    }


    @Inject(
			method = "drawSlot",
			at = @At("HEAD")
	)
	void onSlotDraw(Slot slotIn, CallbackInfo ci) {
		MinecraftForge.EVENT_BUS.post(new InventorySlotDrawEvent.Pre(slotIn));
	}

	@Inject(
			method = "drawSlot",
			at = @At("TAIL")
	)
	void onPostSlotDraw(Slot slotIn, CallbackInfo ci) {
		MinecraftForge.EVENT_BUS.post(new InventorySlotDrawEvent.Post(slotIn));
	}
}