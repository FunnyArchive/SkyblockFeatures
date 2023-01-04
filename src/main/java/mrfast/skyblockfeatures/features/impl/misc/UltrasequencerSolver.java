package mrfast.skyblockfeatures.features.impl.misc;

import java.util.HashMap;
import java.util.List;

import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.events.GuiChestBackgroundDrawnEvent;
import mrfast.skyblockfeatures.utils.Utils;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class UltrasequencerSolver {
    static Slot[] clickInOrderSlots = new Slot[36];
    static int lastUltraSequencerClicked = 0;
    static HashMap<Slot,ItemStack> answers = new HashMap<>();

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        if(!skyblockfeatures.config.enchantingSolvers) return;

        if (Utils.GetMC().currentScreen instanceof GuiChest) {
            if (Utils.GetMC().thePlayer == null) return;
            ContainerChest chest = (ContainerChest) Utils.GetMC().thePlayer.openContainer;
            List<Slot> invSlots = ((GuiChest) Utils.GetMC().currentScreen).inventorySlots.inventorySlots;
            String chestName = chest.getLowerChestInventory().getDisplayName().getUnformattedText().trim();

            if (chestName.startsWith("Ultrasequencer (")) {
                if (invSlots.get(49).getStack() != null && invSlots.get(49).getStack().getDisplayName().equals("§aRemember the pattern!")) {
                    answers.clear();
                    for (int i = 9; i <= 44; i++) {
                        if (invSlots.get(i) == null || invSlots.get(i).getStack() == null) continue;
                        String itemName = StringUtils.stripControlCodes(invSlots.get(i).getStack().getDisplayName());
                        if (itemName.matches("\\d+")) {
                            int number = Integer.parseInt(itemName);
                            clickInOrderSlots[number - 1] = invSlots.get(i);
                            answers.put(invSlots.get(i), invSlots.get(i).getStack());
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onGuiRender(GuiChestBackgroundDrawnEvent event) {
        if(!skyblockfeatures.config.enchantingSolvers) return;

        if (event.displayName.startsWith("Ultrasequencer (")) {
            List<Slot> invSlots = event.slots;
            if (invSlots.size() > 48 && invSlots.get(49).getStack() != null) {
                if (invSlots.get(49).getStack().getDisplayName().startsWith("§7Timer: §a")) {
                    for (Slot slot : invSlots) {
                        if(answers.containsKey(slot)) {
                            event.chest.inventorySlots.putStackInSlot(slot.slotNumber, answers.get(slot));
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        clickInOrderSlots = new Slot[36];
    }
}
