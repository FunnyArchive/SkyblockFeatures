/*
 * skyblockfeatures - Hypixel Skyblock Quality of Life Mod
 * Copyright (C) 2021 skyblockfeatures
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package mrfast.skyblockfeatures.commands;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Nullable;

import gg.essential.api.commands.Command;
import gg.essential.api.commands.DefaultHandler;
import gg.essential.api.utils.GuiUtil;
import mrfast.skyblockfeatures.gui.OptionsGui;

public class configCommand extends Command {

    public configCommand() {
        super("skyblockfeatures");
        //TODO Auto-generated constructor stub
    }

    @DefaultHandler
    public void handle() {
        // Utils.GetMC().displayGuiScreen(new testcommand());
        GuiUtil.open(Objects.requireNonNull(new OptionsGui()));
    }

    @Nullable
    @Override
    public Set<Alias> getCommandAliases() {
        return Collections.singleton(new Alias("sf"));
    }

    // @Override
    // public String getCommandName() {
    //     return "skyblockfeatures";
    // }

    // @Override
    // public List<String> getCommandAliases() {
    //     return Lists.newArrayList("st");
    // }

    // @Override
    // public String getCommandUsage(ICommandSender sender) {
    //     return "/" + getCommandName();
    // }

    // @Override
    // public int getRequiredPermissionLevel() {
    //     return 0;
    // }

    // @Override
    // public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
    //     return null;
    // }

    // @Override
    // public void processCommand(ICommandSender sender, String[] args) throws CommandException {
    //     EntityPlayerSP player = (EntityPlayerSP) sender;
    //     if (args.length == 0) {
    //         ModCore.getInstance().getGuiHandler().open(new OptionsGui());
    //         return;
    //     }
    //     String subcommand = args[0].toLowerCase(Locale.ENGLISH);
    //     switch (subcommand) {
    //         case "setkey":
    //             if (args.length == 1) {
    //                 player.addChatMessage(new ChatComponentText("§c§l[ERROR] §8» §cPlease provide your Hypixel API key!"));
    //                 return;
    //             }
    //             new Thread(() -> {
    //                 String apiKey = args[1];
    //                 if (APIUtil.getJSONResponse("https://api.hypixel.net/key?key=" + apiKey).get("success").getAsBoolean()) {
    //                     skyblockfeatures.config.apiKey = apiKey;
    //                     skyblockfeatures.config.markDirty();
    //                     player.addChatMessage(new ChatComponentText("§a§l[SUCCESS] §8» §aYour Hypixel API key has been set to §f" + apiKey + "§a."));
    //                     skyblockfeatures.config.writeData();
    //                 } else {
    //                     player.addChatMessage(new ChatComponentText("§c§l[ERROR] §8» §cThe Hypixel API key you provided was §finvalid§c."));
    //                 }
    //             }).start();
    //             break;
    //         case "config":
    //             ModCore.getInstance().getGuiHandler().open(skyblockfeatures.config.gui());
    //             break;
    //         case "fetchur":
    //             player.addChatMessage(new ChatComponentText("§e§l[FETCHUR] §8» §eToday's Fetchur item is: §f" + MiningFeatures.fetchurItems.values().toArray()[(ZonedDateTime.now(ZoneId.of("America/New_York")).getDayOfMonth() - 1) % MiningFeatures.fetchurItems.size()]));
    //             break;
    //         case "griffin":
    //             if (args.length == 1) {
    //                 player.addChatMessage(new ChatComponentText("/skyblockfeatures griffin <refresh>"));
    //             } else {
    //                 String action = args[1].toLowerCase(Locale.ENGLISH);
    //                 switch (action) {
    //                     case "refresh":
    //                         GriffinBurrows.particleBurrows.removeIf(pb -> !pb.dug);
    //                         GriffinBurrows.burrows.clear();
    //                         GriffinBurrows.burrowRefreshTimer.reset();
    //                         GriffinBurrows.shouldRefreshBurrows = true;
    //                         break;
    //                     default:
    //                         player.addChatMessage(new ChatComponentText("/skyblockfeatures griffin <refresh>"));
    //                 }
    //             }
    //             break;
    //         case "reload":
    //             if (args.length == 1) {
    //                 player.addChatMessage(new ChatComponentText("/skyblockfeatures reload <aliases/data>"));
    //             } else {
    //                 String action = args[1].toLowerCase(Locale.ENGLISH);
    //                 switch (action) {
    //                     case "aliases":
    //                         CommandAliases.reloadAliases();
    //                         player.addChatMessage(new ChatComponentText("§b§l[RELOAD] §8» §bskyblockfeatures command aliases have been §freloaded§b successfully."));
    //                         break;
    //                     case "data":
    //                         DataFetcher.reloadData();
    //                         player.addChatMessage(new ChatComponentText("§b§l[RELOAD] §8» §bskyblockfeatures repository data has been §freloaded§b successfully."));
    //                         break;
    //                     case "mayor":
    //                         MayorInfo.fetchMayorData();
    //                         player.addChatMessage(new ChatComponentText("§b§l[RELOAD] §8» §bskyblockfeatures mayor data has been §freloaded§b successfully."));
    //                         break;
    //                     default:
    //                         player.addChatMessage(new ChatComponentText("/skyblockfeatures reload <aliases/data>"));
    //                 }
    //             }
    //         case "help":
    //             if (args.length == 1) {
    //                 player.addChatMessage(new ChatComponentText("§9➜ skyblockfeatures Commands and Info" + "\n" +
    //                         " §2§l ❣ §7§oCommands marked with a §a§o✯ §7§orequire an §f§oAPI key§7§o to work correctly." + "\n" +
    //                         " §2§l ❣ §7§oThe current mod version is §f§o" + skyblockfeatures.VERSION + "§7§o." + "\n" +
    //                         "§9§l➜ Setup:" + "\n" +
    //                         " §3/skyblockfeatures §l➡ §bOpens the main mod GUI. §7(Alias: §f/st§7)" + "\n" +
    //                         " §3/skyblockfeatures config §l➡ §bOpens the configuration GUI." + "\n" +
    //                         " §3/skyblockfeatures setkey §l➡ §bSets your Hypixel API key." + "\n" +
    //                         " §3/skyblockfeatures help §l➡ §bShows this help menu." + "\n" +
    //                         " §3/skyblockfeatures reload <aliases/data> §l➡ §bForces a refresh of command aliases or solutions from the data repository." + "\n" +
    //                         " §3/skyblockfeatures editlocations §l➡ §bOpens the location editing GUI." + "\n" +
    //                         " §3/skyblockfeatures aliases §l➡ §bOpens the command alias editing GUI." + "\n" +
    //                         "§9§l➜ Events:" + "\n" +
    //                         " §3/skyblockfeatures griffin refresh §l➡ §bForcefully refreshes Griffin Burrow waypoints. §a§o✯" + "\n" +
    //                         " §3/skyblockfeatures fetchur §l➡ §bShows the item that Fetchur wants." + "\n" +
    //                         "§9§l➜ Color and Glint" + "\n" +
    //                 		" §3/armorcolor <set/clear/clearall> §l➡ §bChanges the color of an armor piece to the hexcode or decimal color. §7(Alias: §f/armourcolour§7)" + "\n" +
    //                 		" §3/glintcustomize override <on/off/clear/clearall> §l➡ §bEnables or disables the enchantment glint on an item." + "\n" +
    //                 		" §3/glintcustomize color <set/clear/clearall> §l➡ §bChange the enchantment glint color for an item." + "\n" +
    //                         "§9§l➜ Miscellaneous:" + "\n" +
    //                         " §3/reparty §l➡ §bDisbands and re-invites everyone in your party. §7(Alias: §f/rp§7)" + "\n" +
    //                         " §3/blockability <clearall> §l➡ §bDisables the ability for the item in your hand."));
    //                 return;
    //             }
    //             break;
    //         case "aliases":
    //         case "alias":
    //         case "editaliases":
    //         case "commandaliases":
    //             ModCore.getInstance().getGuiHandler().open(new CommandAliasesGui());
    //             break;
    //         case "editlocation":
    //         case "editlocations":
    //         case "location":
    //         case "locations":
    //         case "loc":
    //         case "gui":
    //             ModCore.getInstance().getGuiHandler().open(new LocationEditGui());
    //             break;
    //         case "keyshortcuts":
    //         case "shortcuts":
    //             ModCore.getInstance().getGuiHandler().open(new KeyShortcutsGui());
    //             break;
    //         case "armorcolor":
    //         case "armorcolour":
    //         case "armourcolor":
    //         case "armourcolour":
    //             acc.processCommand(sender, Arrays.copyOfRange(args, 1, args.length));
    //             break;
    //         default:
    //             player.addChatMessage(new ChatComponentText("§bskyblockfeatures ➜ §cThis command doesn't exist!\n  §cUse §b/skyblockfeatures help§c for a full list of commands"));
    //     }
    // }
}