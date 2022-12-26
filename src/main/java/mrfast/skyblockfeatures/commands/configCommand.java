package mrfast.skyblockfeatures.commands;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import com.google.common.collect.Lists;

import gg.essential.api.utils.GuiUtil;
import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.gui.LocationEditGui;
import mrfast.skyblockfeatures.utils.APIUtil;
import mrfast.skyblockfeatures.utils.Utils;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

public class configCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "skyblockfeatures";
    }

    @Override
    public List<String> getCommandAliases() {
        return Lists.newArrayList("sf","sbf");
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/" + getCommandName();
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return (args.length >= 1) ? getListOfStringsMatchingLastWord(args, Utils.getListOfPlayerUsernames()) : null;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        EntityPlayerSP player = (EntityPlayerSP) sender;
        if (args.length == 0) {
            GuiUtil.open(Objects.requireNonNull(skyblockfeatures.config.gui()));
            player.addChatMessage(new ChatComponentText("§9➜ Skyblock Features Commands and Info" + "\n" +
                    " §2§l ❣ §7§oThe current mod version is §f§o" + skyblockfeatures.VERSION + "§7§o." + "\n" +
                    "§9§l➜ Setup:" + "\n" +
                    " §3/skyblockfeatures config §l➡ §bOpens the configuration GUI." + "\n" +
                    " §3/skyblockfeatures setkey §l➡ §bSets your Hypixel API key." + "\n" +
                    " §3/skyblockfeatures help §l➡ §bShows this help menu." + "\n" +
                    " §3/skyblockfeatures edit §l➡ §bOpens the location editing GUI." + "\n" +
                    "§9§l➜ Miscellaneous:" + "\n" +
                    " §3/terminal §l➡ §bDisplays a gui with a f7 terminal for practice." + "\n" +
                    " §3/vm §l➡ §bDisplays a gui with item position offsets." + "\n" +
                    " §3/shrug §l➡ §bSends a chat message with '¯\\_(ツ)_/¯'" + "\n" +
                    " §3/reparty §l➡ §bDisbands and re-invites everyone in your party." + "\n" +
                    " §3/inventory §l➡ §bOpens a gui displaying the specified players inventory & armor." + "\n"+
                    " §3/accessories §l➡ §bOpens a gui displaying the specified players accessory bag." + "\n"+
                    " §3/bank §l➡ §bDisplays in chat the specified players bank and purse balance." + "\n"+
                    " §3/armor §l➡ §bDisplays in chat the specified players armor." + "\n"+
                    " §3/skills §l➡ §bDisplays in chat the specified players skills." + "\n"+
                    " §3/jerry §l➡ §bJERRRY MODEEE." + "\n"+
                    " §3/sky §l➡ §bGives the link to the specified players Skycrypt profile."));
            return;
        }
        String subcommand = args[0].toLowerCase(Locale.ENGLISH);
        switch (subcommand) {
            case "setkey":
                if (args.length == 1) {
                    player.addChatMessage(new ChatComponentText("§c§l[ERROR] §8» §cPlease provide your Hypixel API key!"));
                    return;
                }
                new Thread(() -> {
                    String apiKey = args[1];
                    if (APIUtil.getJSONResponse("https://api.hypixel.net/key?key=" + apiKey).get("success").getAsBoolean()) {
                        skyblockfeatures.config.apiKey = apiKey;
                        skyblockfeatures.config.markDirty();
                        player.addChatMessage(new ChatComponentText("§a§l[SUCCESS] §8» §aYour Hypixel API key has been set to §f" + apiKey + "§a."));
                        skyblockfeatures.config.writeData();
                    } else {
                        player.addChatMessage(new ChatComponentText("§c§l[ERROR] §8» §cThe Hypixel API key you provided was §finvalid§c."));
                    }
                }).start();
                break;
            case "config":
                GuiUtil.open(Objects.requireNonNull(skyblockfeatures.config.gui()));
                break;
            case "help":
                player.addChatMessage(new ChatComponentText("§9➜ Skyblock Features Commands and Info" + "\n" +
                " §2§l ❣ §7§oThe current mod version is §f§o" + skyblockfeatures.VERSION + "§7§o." + "\n" +
                "§9§l➜ Setup:" + "\n" +
                " §3/sbf §l➡ §bOpens the configuration GUI." + "\n" +
                " §3/sbf setkey §l➡ §bSets your Hypixel API key." + "\n" +
                " §3/sbf help §l➡ §bShows this help menu." + "\n" +
                " §3/sbf edit §l➡ §bOpens the location editing GUI." + "\n" +
                "§9§l➜ Miscellaneous:" + "\n" +
                " §3/terminal §l➡ §bDisplays a gui with a f7 terminal for practice." + "\n" +
                " §3/vm §l➡ §bDisplays a gui with item position offsets." + "\n" +
                " §3/shrug §l➡ §bSends a chat message with '¯\\_(ツ)_/¯'" + "\n" +
                " §3/reparty §l➡ §bDisbands and re-invites everyone in your party." + "\n" +
                " §3/inventory §l➡ §bOpens a gui displaying the specified players inventory & armor." + "\n"+
                " §3/accessories §l➡ §bOpens a gui displaying the specified players accessory bag." + "\n"+
                " §3/bank §l➡ §bDisplays in chat the specified players bank and purse balance." + "\n"+
                " §3/armor §l➡ §bDisplays in chat the specified players armor." + "\n"+
                " §3/skills §l➡ §bDisplays in chat the specified players skills." + "\n"+
                " §3/jerry §l➡ §bJERRRY MODEEE." + "\n"+
                " §3/sky §l➡ §bGives the link to the specified players Skycrypt profile."));
            case "editlocation":
            case "editlocations":
            case "location":
            case "locations":
            case "loc":
            case "edit":
            case "gui":
                GuiUtil.open(Objects.requireNonNull(new LocationEditGui()));
                break;
            default:
                player.addChatMessage(new ChatComponentText("§bSBF ➜ §cThis command doesn't exist!\n  §cUse §b/sbf help§c for a full list of commands"));
        }
    }
}