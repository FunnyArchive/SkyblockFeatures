package mrfast.skyblockfeatures.commands;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public class HelloCommand extends CommandBase {

	@Override
    public String getCommandName() {
        return "hello";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/hello [player]";
    }

    @Override
    public List<String> getCommandAliases() {
        return Collections.singletonList("hi");
    }

	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}

	String[] strs = {"Hallo", "ello", "Hello", "Bonjour", "Hola", "Hi"};

	@Override
	public void processCommand(ICommandSender arg0, String[] args) throws CommandException {
		String chatMsg = getRandomElement(strs);
        if (args.length > 0) {
            chatMsg = chatMsg +" "+ String.join(" ", args);
        }
        Minecraft.getMinecraft().thePlayer.sendChatMessage(chatMsg);
	}

    public static <T> T getRandomElement(T[] arr){
        return arr[ThreadLocalRandom.current().nextInt(arr.length)];
    }
}
