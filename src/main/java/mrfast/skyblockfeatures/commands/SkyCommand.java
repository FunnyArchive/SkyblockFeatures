package mrfast.skyblockfeatures.commands;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import mrfast.skyblockfeatures.utils.Utils;

public class SkyCommand extends CommandBase {

	@Override
    public String getCommandName() {
        return "sky";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/sky [username]";
    }

	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}
	
	@Override
	public void processCommand(ICommandSender arg0, String[] arg1) throws CommandException {
        String username = "";
        if (arg1.length == 0) {
            username = Utils.GetMC().thePlayer.getName();
        } else {
            username = arg1[0];
        }
        String delimiter = EnumChatFormatting.AQUA.toString() + EnumChatFormatting.STRIKETHROUGH.toString() + "" + EnumChatFormatting.BOLD + "--------------------------------------";
        
        ClickEvent versionCheckChatClickEvent = new ClickEvent(ClickEvent.Action.OPEN_URL, 
        "https://sky.lea.moe/stats/"+username);

        ChatStyle clickableChatStyle = new ChatStyle().setChatClickEvent(versionCheckChatClickEvent);

        ChatComponentText versionWarningChatComponent = 

        new ChatComponentText(ChatFormatting.LIGHT_PURPLE+"Click Here for "+username+"'s Skyleamoe stats");
        versionWarningChatComponent.setChatStyle(clickableChatStyle);

        // mc.thePlayer.addChatMessage(versionWarningChatComponent);
        Utils.GetMC().thePlayer.addChatMessage(
                new ChatComponentText(delimiter)
                .appendText("\n")
                .appendSibling(versionWarningChatComponent)
                .appendText("\n")
                .appendSibling(new ChatComponentText(delimiter))
        );
	}
}