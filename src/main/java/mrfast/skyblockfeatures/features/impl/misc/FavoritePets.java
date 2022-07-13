package mrfast.skyblockfeatures.features.impl.misc;

import java.awt.Color;
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

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemSkull;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.core.DataFetcher;
import mrfast.skyblockfeatures.events.GuiContainerEvent;

public class FavoritePets {

    public static Minecraft mc = Minecraft.getMinecraft();

    public static HashSet<String> favoritePets = new HashSet<>();
    @SubscribeEvent
	void onKeyDown(GuiScreenEvent.KeyboardInputEvent.Pre event) {
		if (event.gui instanceof GuiChest && Keyboard.isKeyDown(skyblockfeatures.favoritePetKeybind.getKeyCode())) {
            GuiChest gui = (GuiChest) event.gui;
            ContainerChest chest = (ContainerChest) gui.inventorySlots;
            IInventory inv = chest.getLowerChestInventory();
            String chestName = inv.getDisplayName().getUnformattedText().trim();
            if(!chestName.contains("Pets")) return;
			event.setCanceled(true);
			Slot slot = ((GuiContainer) event.gui).getSlotUnderMouse();
			if(slot == null || !slot.getHasStack()) return;
			if (!(slot.getStack().getItem() instanceof ItemSkull) || !slot.getStack().hasDisplayName()) return;

			if (favoritePets.contains(slot.getStack().getDisplayName())) {
                favoritePets.remove(slot.getStack().getDisplayName());
                mc.thePlayer.playSound("random.orb", 1f, 1f);
			} else {
                favoritePets.add(slot.getStack().getDisplayName());
                mc.thePlayer.playSound("random.orb", 1f, 0.1f);
			}
			writeSave();
		}
	}

    @SubscribeEvent
    public void onDrawSlot(GuiContainerEvent.DrawSlotEvent.Pre event) {
        if (event.gui instanceof GuiChest) {
            GuiChest gui = (GuiChest) event.gui;
            ContainerChest chest = (ContainerChest) gui.inventorySlots;
            IInventory inv = chest.getLowerChestInventory();
            String chestName = inv.getDisplayName().getUnformattedText().trim();
            if (!chestName.contains("Pets") || !event.slot.getHasStack() || !(event.slot.getStack().getItem() instanceof ItemSkull) || !event.slot.getStack().hasDisplayName()) return;
            if(favoritePets.contains(event.slot.getStack().getDisplayName())) {
                int x = event.slot.xDisplayPosition;
                int y = event.slot.yDisplayPosition;
                Gui.drawRect(x, y, x + 16, y + 16, new Color(255, 225, 30, 255).getRGB());
            }
        }
    }

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static File saveFile;

    public static void reloadSave() {
        favoritePets.clear();
        JsonArray dataArray;
        try (FileReader in = new FileReader(saveFile)) {
            dataArray = gson.fromJson(in, JsonArray.class);
            favoritePets.addAll(Arrays.asList(DataFetcher.getStringArrayFromJsonArray(dataArray)));
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
            for (String itemId : favoritePets) {
                arr.add(new JsonPrimitive(itemId));
            }
            gson.toJson(arr, writer);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public FavoritePets() {
        saveFile = new File(skyblockfeatures.modDir, "favoritepets.json");
        reloadSave();
    }

}
