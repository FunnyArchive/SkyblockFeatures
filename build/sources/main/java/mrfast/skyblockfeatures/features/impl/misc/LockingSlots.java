package mrfast.skyblockfeatures.features.impl.misc;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.HashSet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.core.DataFetcher;
import mrfast.skyblockfeatures.events.InventorySlotClickEvent;
import mrfast.skyblockfeatures.events.ItemDropEvent;
import mrfast.skyblockfeatures.utils.RenderUtil;
import mrfast.skyblockfeatures.utils.Utils;

public class LockingSlots {
    private static final ResourceLocation LOCK_IMG = new ResourceLocation("skyblockfeatures:lock.png");

	Minecraft mc = Utils.GetMC();
	private void playLockedSlotInteractSound() {
		mc.thePlayer.playSound("note.bass", 1f, 0.5f);
	}
	
	public static HashSet<String> lockedSlots = new HashSet<>();
	@SubscribeEvent
	void onSlotClick(InventorySlotClickEvent event) {
		if (lockedSlots.contains(String.valueOf(event.getSlot().getSlotIndex()))
				&& event.getSlot().inventory.equals(mc.thePlayer.inventory)) {
			event.setCanceled(true);
			playLockedSlotInteractSound();
		}
	}

	public static void drawSlot(Slot slot) {
		if(lockedSlots.contains(String.valueOf(slot.getSlotIndex()))
				&& slot.inventory.equals(Utils.GetMC().thePlayer.inventory)) {
			GlStateManager.translate(0, 0, 400);
			Minecraft.getMinecraft().getTextureManager().bindTexture(LOCK_IMG);
			GlStateManager.color(1, 1, 1, 0.5f);
			GlStateManager.depthMask(false);
			RenderUtil.drawTexturedRect(slot.xDisplayPosition, slot.yDisplayPosition, 16, 16, 0, 1, 0, 1, GL11.GL_NEAREST);
			GlStateManager.depthMask(true);
			GlStateManager.enableBlend();
			GlStateManager.translate(0, 0, -400);
		}
	}

	@SubscribeEvent
	void onDrop(ItemDropEvent event) {
		System.out.println("slot drop event");
		System.out.println("is locked: " + lockedSlots.contains(String.valueOf(mc.thePlayer.inventory.currentItem)));
		if (lockedSlots.contains(String.valueOf(mc.thePlayer.inventory.currentItem))) {
			System.out.println("cancelled");
			event.setCanceled(true);
			playLockedSlotInteractSound();
		}
	}

	@SubscribeEvent
	void onKeyDown(GuiScreenEvent.KeyboardInputEvent.Pre event) {
		if (event.gui instanceof GuiContainer && Keyboard.isKeyDown(skyblockfeatures.slotLockKeybind.getKeyCode())) {
			event.setCanceled(true);
			Slot slot = ((GuiContainer) event.gui).getSlotUnderMouse();
			if(slot == null) return;
			if (!slot.inventory.equals(mc.thePlayer.inventory))
				return;

			if (lockedSlots.contains(String.valueOf(slot.getSlotIndex()))) {
				lockedSlots.remove(String.valueOf(slot.getSlotIndex()));
				mc.thePlayer.playSound("random.orb", 1f, 1f);
			} else {
				lockedSlots.add(String.valueOf(slot.getSlotIndex()));
				mc.thePlayer.playSound("random.orb", 1f, 0.1f);
			}
			writeSave();
		}
	}

	public LockingSlots() {
        saveFile = new File(skyblockfeatures.modDir, "slotLocking.json");
        reloadSave();
    }

	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static File saveFile;

	public static void reloadSave() {
        lockedSlots.clear();
        JsonArray dataArray;
        try (FileReader in = new FileReader(saveFile)) {
            dataArray = gson.fromJson(in, JsonArray.class);
            lockedSlots.addAll(Arrays.asList(DataFetcher.getStringArrayFromJsonArray(dataArray)));
        } catch (Exception e) {
            dataArray = new JsonArray();
            try (FileWriter writer = new FileWriter(saveFile)) {
                gson.toJson(dataArray, writer);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

	public static void writeSave() {
        try (FileWriter writer = new FileWriter(saveFile)) {
            JsonArray arr = new JsonArray();
            for (String itemId : lockedSlots) {
                arr.add(new JsonPrimitive(itemId));
            }
            gson.toJson(arr, writer);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
