package mrfast.skyblockfeatures.commands;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.google.common.eventbus.Subscribe;
import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import mrfast.skyblockfeatures.features.impl.ItemFeatures.HideGlass;
import mrfast.skyblockfeatures.utils.ScoreboardUtil;
import mrfast.skyblockfeatures.utils.TabListUtils;
import mrfast.skyblockfeatures.utils.Utils;

public class DebugCommand extends CommandBase {

	@Override
    public String getCommandName() {
        return "debug";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/debug";
    }

    @Override
    public List<String> getCommandAliases() {
        return Collections.singletonList("db");
    }

	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}
	@Override
	public void processCommand(ICommandSender arg0, String[] args) throws CommandException {
        String output = "----------===== Debug Command =====----------\n\n";

        GuiScreen.setClipboardString(output);
	}

    public static <T> T getRandomElement(T[] arr){
        return arr[ThreadLocalRandom.current().nextInt(arr.length)];
    }
}
