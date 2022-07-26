package mrfast.skyblockfeatures.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import com.mojang.realmsclient.gui.ChatFormatting;
import gg.essential.api.utils.GuiUtil;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import mrfast.skyblockfeatures.utils.Utils;

public class TerminalCommand extends CommandBase {

	@Override
	public String getCommandName() {
		return "terminal";
	}

	@Override
	public String getCommandUsage(ICommandSender arg0) {
		return "/terminal";
	}
	
	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}
	public static int[] slots = {
		11, 12, 13, 14, 15,
		20, 21, 22, 23, 24,
		29, 30, 31, 32, 33
	};
	public static InventoryBasic Terminal = new InventoryBasic(ChatFormatting.GREEN+"✯ Practice Terminal", true, 45);
	public static List<Integer> clicked = new ArrayList<Integer>();
	public static long start = 0;
	@Override
	public void processCommand(ICommandSender arg0, String[] arg1) throws CommandException {
		clicked.clear();
		start = 0;
		for(int i = 0; i < 45; i++) {
			Terminal.setInventorySlotContents(i, new ItemStack(Blocks.stained_glass_pane, 1, 15).setStackDisplayName(ChatFormatting.RESET+""));
		}
		for(int i = 0; i < 45; i++) {
			Terminal.setInventorySlotContents(i, new ItemStack(Blocks.stained_glass_pane, 1, 15).setStackDisplayName(ChatFormatting.RESET+""));
		}

		int startingSlot = new Random().nextInt(slots.length);
		Terminal.setInventorySlotContents(slots[startingSlot], new ItemStack(Blocks.stained_glass_pane, 1, 5).setStackDisplayName(ChatFormatting.RESET+""));

		for(int slot : slots) {
			if(slots[startingSlot] == slot) continue;
			Terminal.setInventorySlotContents(slot, new ItemStack(Blocks.stained_glass_pane, 1, 14).setStackDisplayName(ChatFormatting.RESET+""));
		}

		GuiUtil.open(Objects.requireNonNull(new GuiChest(Utils.GetMC().thePlayer.inventory, Terminal)));
	}
}
