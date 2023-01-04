package mrfast.skyblockfeatures.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.utils.APIUtil;
import mrfast.skyblockfeatures.utils.Utils;

import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.google.gson.JsonObject;

public class NetworthCommand extends CommandBase {

	@Override
	public String getCommandName() {
		return "networth";
	}
	
	@Override
	public List<String> getCommandAliases() {
        return Collections.singletonList("nw");
    }

	@Override
	public String getCommandUsage(ICommandSender arg0) {
		return "/" + getCommandName() + " [name]";
	}

	public static String usage(ICommandSender arg0) {
		return new NetworthCommand().getCommandUsage(arg0);
	}

	@Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return (args.length >= 1) ? getListOfStringsMatchingLastWord(args, Utils.getListOfPlayerUsernames()) : null;
    }

	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}
	
	@Override
	public void processCommand(ICommandSender arg0, String[] arg1) throws CommandException {
		new Thread(() -> {
			EntityPlayer player = (EntityPlayer) arg0;
			
			// Check key
			String key = skyblockfeatures.config.apiKey;
			if (key.equals("")) {
				player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "API key not set."));
			}
			
			// Get UUID for Hypixel API requests
			String username;
			String uuid;
			if (arg1.length == 0) {
				username = player.getName();
				uuid = player.getUniqueID().toString().replaceAll("[\\-]", "");
			} else {
				username = arg1[0];
				uuid = APIUtil.getUUID(username);
			}
			player.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Checking networth of " + EnumChatFormatting.DARK_GREEN + username));
			
			// Find stats of latest profile
			String latestProfile = APIUtil.getLatestProfileID(uuid, key);
			if (latestProfile == null) return;
			
			String profileURL = "https://sky.shiiyu.moe/api/v2/profile/"+username;
			System.out.println("Fetching profile... "+profileURL);
			JsonObject profileResponse = APIUtil.getJSONResponse(profileURL);
			if (profileResponse.has("error")) {
				String reason = profileResponse.get("error").getAsString();
				player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Failed with reason: " + reason));
				return;
			}

			System.out.println(profileResponse);
			profileResponse = profileResponse.get("profiles").getAsJsonObject();
			
			System.out.println("Player Data ");
			JsonObject networthJson = profileResponse.get(latestProfile).getAsJsonObject().get("data").getAsJsonObject().get("networth").getAsJsonObject();
			JsonObject types = networthJson.get("types").getAsJsonObject();
			System.out.println("Got networth player data");
			NumberFormat nf = NumberFormat.getIntegerInstance(Locale.US);
			
			player.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA.toString()+EnumChatFormatting.STRIKETHROUGH.toString() + "" + EnumChatFormatting.BOLD + "-------------------\n" +
														EnumChatFormatting.AQUA + " " + username + "'s Networth:\n" +
														EnumChatFormatting.GREEN + " Purse: " + EnumChatFormatting.GOLD + nf.format(networthJson.get("purse").getAsDouble()) + "\n" +
														EnumChatFormatting.GREEN + " Bank: " + EnumChatFormatting.GOLD + nf.format(networthJson.get("bank").getAsDouble()) + "\n" +
														EnumChatFormatting.GREEN + " Sacks: " + EnumChatFormatting.GOLD + nf.format(types.get("sacks").getAsJsonObject().get("total").getAsDouble()) + "\n" +
														EnumChatFormatting.GREEN + " Armor: " + EnumChatFormatting.GOLD + nf.format(types.get("armor").getAsJsonObject().get("total").getAsDouble()) + "\n" +
														EnumChatFormatting.GREEN + " Equipment: " + EnumChatFormatting.GOLD + nf.format(types.get("equipment").getAsJsonObject().get("total").getAsDouble()) + "\n" +
														EnumChatFormatting.GREEN + " Wardrobe: " + EnumChatFormatting.GOLD + nf.format(types.get("wardrobe").getAsJsonObject().get("total").getAsDouble()) + "\n" +
														EnumChatFormatting.GREEN + " Inventory: " + EnumChatFormatting.GOLD + nf.format(types.get("inventory").getAsJsonObject().get("total").getAsDouble()) + "\n" +
														EnumChatFormatting.GREEN + " Enderchest: " + EnumChatFormatting.GOLD + nf.format(types.get("enderchest").getAsJsonObject().get("total").getAsDouble()) + "\n" +
														EnumChatFormatting.GREEN + " Accessories: " + EnumChatFormatting.GOLD + nf.format(types.get("accessories").getAsJsonObject().get("total").getAsDouble()) + "\n" +
														EnumChatFormatting.GREEN + " Vault: " + EnumChatFormatting.GOLD + nf.format(types.get("personal_vault").getAsJsonObject().get("total").getAsDouble()) + "\n" +
														EnumChatFormatting.GREEN + " Storage: " + EnumChatFormatting.GOLD + nf.format(types.get("storage").getAsJsonObject().get("total").getAsDouble()) + "\n" +
														EnumChatFormatting.GREEN + " Pets: " + EnumChatFormatting.GOLD + nf.format(types.get("pets").getAsJsonObject().get("total").getAsDouble()) + "\n" +
														EnumChatFormatting.GREEN + " Total Networth: " + EnumChatFormatting.GOLD + nf.format(networthJson.get("networth").getAsDouble()) + "\n" +
														EnumChatFormatting.AQUA.toString()+EnumChatFormatting.STRIKETHROUGH.toString() + " " + EnumChatFormatting.BOLD + "-------------------"));
		}).start();
	}

}
