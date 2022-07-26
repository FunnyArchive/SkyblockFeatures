package mrfast.skyblockfeatures.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
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

public class BankCommand extends CommandBase {

	@Override
	public String getCommandName() {
		return "bank";
	}
	
	@Override
	public List<String> getCommandAliases() {
        return Collections.singletonList("purse");
    }

	@Override
	public String getCommandUsage(ICommandSender arg0) {
		return "/" + getCommandName() + " [name]";
	}

	public static String usage(ICommandSender arg0) {
		return new BankCommand().getCommandUsage(arg0);
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}
	
	@Override
	public void processCommand(ICommandSender arg0, String[] arg1) throws CommandException {
		if(arg1.length == 0) {
			Utils.SendMessage("§cMissing Arguments! Usage "+getCommandUsage(arg0)+" "+Utils.inSkyblock);
		}
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
				player.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Checking coins of " + EnumChatFormatting.DARK_GREEN + username));
			} else {
				username = arg1[0];
				player.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Checking coins of " + EnumChatFormatting.DARK_GREEN + username));
				uuid = APIUtil.getUUID(username);
			}
			
			// Find stats of latest profile
			String latestProfile = APIUtil.getLatestProfileID(uuid, key);
			if (latestProfile == null) return;
			
			String profileURL = "https://api.hypixel.net/skyblock/profile?profile=" + latestProfile + "&key=" + key;
			System.out.println("Fetching profile...");
			JsonObject profileResponse = APIUtil.getResponse(profileURL);
			if (!profileResponse.get("success").getAsBoolean()) {
				String reason = profileResponse.get("cause").getAsString();
				player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Failed with reason: " + reason));
				return;
			}
			
			System.out.println("Fetching bank + purse coins...");
			double purseCoins = profileResponse.get("profile").getAsJsonObject().get("members").getAsJsonObject().get(uuid).getAsJsonObject().get("coin_purse").getAsDouble();
			purseCoins = Math.floor(purseCoins * 100.0) / 100.0;
			NumberFormat nf = NumberFormat.getIntegerInstance(Locale.US);
			
			// Check for bank api
			if (profileResponse.get("profile").getAsJsonObject().has("banking")) {
				double bankCoins = profileResponse.get("profile").getAsJsonObject().get("banking").getAsJsonObject().get("balance").getAsDouble();
				bankCoins = Math.floor(bankCoins * 100.0) / 100.0;
				
				player.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA.toString()+EnumChatFormatting.STRIKETHROUGH.toString() + "" + EnumChatFormatting.BOLD + "-------------------\n" +
															EnumChatFormatting.AQUA + " " + username + "'s Coins:\n" +
															EnumChatFormatting.GREEN + " Bank: " + EnumChatFormatting.GOLD + nf.format(bankCoins) + "\n" +
															EnumChatFormatting.GREEN + " Purse: " + EnumChatFormatting.GOLD + nf.format(purseCoins) + "\n" +
															EnumChatFormatting.GREEN + " Total: " + EnumChatFormatting.GOLD + nf.format(bankCoins + purseCoins) + "\n" +
															EnumChatFormatting.AQUA.toString()+EnumChatFormatting.STRIKETHROUGH.toString() + " " + EnumChatFormatting.BOLD + "-------------------"));
			} else {
				player.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA.toString()+EnumChatFormatting.STRIKETHROUGH.toString() + "" + EnumChatFormatting.BOLD + "-------------------\n" +
															EnumChatFormatting.AQUA + " " + username + "'s Coins:\n" +
															EnumChatFormatting.GREEN + " Bank: " + EnumChatFormatting.RED + "Bank API disabled.\n" +
															EnumChatFormatting.GREEN + " Purse: " + EnumChatFormatting.GOLD + nf.format(purseCoins) + "\n" +
															EnumChatFormatting.AQUA.toString()+EnumChatFormatting.STRIKETHROUGH.toString() + " " + EnumChatFormatting.BOLD + "-------------------"));
			}
		}).start();
	}

}
