package mrfast.skyblockfeatures.commands;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import com.google.gson.JsonObject;
import com.mojang.realmsclient.gui.ChatFormatting;

import gg.essential.api.utils.GuiUtil;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.features.impl.handlers.AuctionData;
import mrfast.skyblockfeatures.utils.APIUtil;
import mrfast.skyblockfeatures.utils.ItemUtil;
import mrfast.skyblockfeatures.utils.Utils;

public class AccessoriesCommand extends CommandBase {

	@Override
	public String getCommandName() {
		return "accessories";
	}

	@Override
	public String getCommandUsage(ICommandSender arg0) {
		return "/accessories [name]";
	}

	@Override
    public List<String> getCommandAliases() {
        return Collections.singletonList("acc");
    }

	@Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return (args.length >= 1) ? getListOfStringsMatchingLastWord(args, Utils.getListOfPlayerUsernames()) : null;
    }
	
	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}
	HashMap<String, List<String>> itemLores = new HashMap<String, List<String>>();
	@Override
	public void processCommand(ICommandSender arg0, String[] arg1) throws CommandException {
		String title = ChatFormatting.GREEN+"✯ "+arg1[0]+"'s Accessory Bag";
		if(title.length() > 30) title = title.substring(0, 30);
		InventoryBasic TargetInventory = new InventoryBasic(title, true, 54);

		new Thread(() -> {
			EntityPlayer player = (EntityPlayer) arg0;
			
			// Check key
			String key = skyblockfeatures.config.apiKey;
			if (key.equals("")) {
				player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "API key not set. Use /setkey."));
			}
			
			// Get UUID for Hypixel API requests
			String username;
			String uuid;
			username = arg1[0] != null?arg1[0]:Utils.GetMC().thePlayer.getName();
			player.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Checking Accessory Bag of " + EnumChatFormatting.DARK_GREEN + username));
			uuid = APIUtil.getUUID(username);
			
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

			String playerURL = "https://api.hypixel.net/player?uuid=" + uuid + "&key=" + key;
			System.out.println("Fetching player data...");
			JsonObject playerResponse = APIUtil.getResponse(playerURL);
			if(!playerResponse.get("success").getAsBoolean()){
				String reason = profileResponse.get("cause").getAsString();
				player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Failed with reason: " + reason));
			}
			
			for(int i = 0; i < 54; i++) {
				TargetInventory.setInventorySlotContents(i, new ItemStack(Blocks.stained_glass_pane, 1, 15).setStackDisplayName(ChatFormatting.RESET+""));
			}

			if(profileResponse.get("profile").getAsJsonObject().get("members").getAsJsonObject().get(uuid).getAsJsonObject().has("talisman_bag")) {
				String inventoryBase64 = profileResponse.get("profile").getAsJsonObject().get("members").getAsJsonObject().get(uuid).getAsJsonObject().get("talisman_bag").getAsJsonObject().get("data").getAsString();
				Inventory items = new Inventory(inventoryBase64);
				List<ItemStack> a = decodeItem(items);
				for(ItemStack item: a) if(item == null) a.remove(item);
				int index = 0;
				for(ItemStack item: a) {
					if(index<53) {
						TargetInventory.setInventorySlotContents(index, item);
						index++;
					}
				}
			}

			GuiUtil.open(Objects.requireNonNull(new GuiChest(Utils.GetMC().thePlayer.inventory, TargetInventory)));
		}).start();
	}

	public static class Inventory
    {
        private final String data;

        public Inventory(String data)
        {
            this.data = data;
        }

        public String getData()
        {
            return this.data.replace("\\u003d", "=");
        }
    }

	public static List<ItemStack> decodeItem(Inventory inventory)
    {
        if (inventory != null)
        {
            List<ItemStack> itemStack = new ArrayList<>();
            byte[] decode = Base64.getDecoder().decode(inventory.getData());

            try
            {
                NBTTagCompound compound = CompressedStreamTools.readCompressed(new ByteArrayInputStream(decode));
                NBTTagList list = compound.getTagList("i", 10);

                for (int i = 0; i < list.tagCount(); ++i)
                {
                    itemStack.add(ItemStack.loadItemStackFromNBT(list.getCompoundTagAt(i)));
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            Collections.rotate(itemStack, -9);

            return itemStack;
        }
        else
        {
            List<ItemStack> itemStack = new ArrayList<>();
            ItemStack barrier = new ItemStack(Blocks.barrier);
            barrier.setStackDisplayName(EnumChatFormatting.RESET + "" + EnumChatFormatting.RED + "Item is not available!");

            for (int i = 0; i < 36; ++i)
            {
                itemStack.add(barrier);
            }
            return itemStack;
        }
    }
}
