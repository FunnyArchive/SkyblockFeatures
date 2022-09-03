package mrfast.skyblockfeatures.listeners;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.commands.RepartyCommand;
import mrfast.skyblockfeatures.features.impl.glowingstuff.PartyGlow;
import mrfast.skyblockfeatures.utils.APIUtil;
import mrfast.skyblockfeatures.utils.ItemUtil;
import mrfast.skyblockfeatures.utils.StringUtils;
import mrfast.skyblockfeatures.utils.Utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonObject;
import com.mojang.realmsclient.gui.ChatFormatting;

public class ChatListener {

    public static Minecraft mc = Minecraft.getMinecraft();
    private static Thread rejoinThread;
    private static String lastPartyDisbander = "";

    String delimiter = EnumChatFormatting.GREEN.toString() + EnumChatFormatting.STRIKETHROUGH.toString() + "" + EnumChatFormatting.BOLD + "---------------------------";
    private static final Pattern invitePattern = Pattern.compile("(?:(?:\\[.+?] )?(?:\\w+) invited )(?:\\[.+?] )?(\\w+)");
    private static final Pattern playerPattern = Pattern.compile("(?:\\[.+?] )?(\\w+)");
    private static final Pattern party_start_pattern = Pattern.compile("^Party Members \\((\\d+)\\)$");
    private static final Pattern leader_pattern = Pattern.compile("^Party Leader: (?:\\[.+?] )?(\\w+) ●$");
    private static final Pattern members_pattern = Pattern.compile(" (?:\\[.+?] )?(\\w+) ●");
    public static boolean alreadySent = false;
    int barCount = 0;

    @SubscribeEvent
    public void onWorldChange(WorldEvent.Load event) {
        barCount = 0;
    }
    
    @SubscribeEvent(receiveCanceled = true, priority = EventPriority.HIGHEST)
    public void onChat(ClientChatReceivedEvent event) {
        if (!Utils.isOnHypixel()) return;
        String delimiter1 = EnumChatFormatting.RED.toString() + EnumChatFormatting.STRIKETHROUGH.toString() + "" + EnumChatFormatting.BOLD + "---------------------------";
        String unformatted = StringUtils.stripControlCodes(event.message.getUnformattedText());
        if (unformatted.startsWith("Your new API key is ")) {
            String apiKey = event.message.getSiblings().get(0).getChatStyle().getChatClickEvent().getValue();
            skyblockfeatures.config.apiKey = apiKey;
            skyblockfeatures.config.markDirty();
            skyblockfeatures.config.writeData();
            mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "skyblockfeatures updated your set Hypixel API key to " + EnumChatFormatting.DARK_GREEN + apiKey));
        }

        if (unformatted.startsWith("Dungeon Finder")) {
            String[] args = unformatted.split(" ");
            if(args[3] != null) {
                new Thread(() -> {
                    // Check key
                    String key = skyblockfeatures.config.apiKey;
                    if (key.equals("")) {
                        mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "API key not set. Use /setkey."));
                    }
                    
                    // Get UUID for Hypixel API requests
                    String username;
                    String uuid;
                    username = args[3];
                    uuid = APIUtil.getUUID(username);
                    
                    
                    // Find stats of latest profile
                    String latestProfile = APIUtil.getLatestProfileID(uuid, key);
                    if (latestProfile == null) return;
        
                    String profileURL = "https://api.hypixel.net/skyblock/profile?profile=" + latestProfile + "&key=" + key;
                    JsonObject profileResponse = APIUtil.getResponse(profileURL);
                    if (!profileResponse.get("success").getAsBoolean()) {
                        String reason = profileResponse.get("cause").getAsString();
                        mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Failed with reason: " + reason));
                        return;
                    }
        
                    String playerURL = "https://api.hypixel.net/player?uuid=" + uuid + "&key=" + key;
                    System.out.println("Fetching player data...");
                    JsonObject playerResponse = APIUtil.getResponse(playerURL);
                    if(!playerResponse.get("success").getAsBoolean()){
                        String reason = profileResponse.get("cause").getAsString();
                        mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Failed with reason: " + reason));
                    }
                    int secrets = playerResponse.get("player").getAsJsonObject().get("achievements").getAsJsonObject().get("skyblock_treasure_hunter").getAsInt();
        
                    JsonObject dungeonsObject = profileResponse.get("profile").getAsJsonObject().get("members").getAsJsonObject().get(uuid).getAsJsonObject().get("dungeons").getAsJsonObject();
                    JsonObject catacombsObject = dungeonsObject.get("dungeon_types").getAsJsonObject().get("catacombs").getAsJsonObject();
                    double catacombs = Utils.xpToDungeonsLevel(catacombsObject.get("experience").getAsDouble());
                    
                    String armourBase64 = profileResponse.get("profile").getAsJsonObject().get("members").getAsJsonObject().get(uuid).getAsJsonObject().get("inv_armor").getAsJsonObject().get("data").getAsString();
                    InputStream armourStream = new ByteArrayInputStream(Base64.getDecoder().decode(armourBase64));
                    
                    try {
                        String inventoryBase64 = profileResponse.get("profile").getAsJsonObject().get("members").getAsJsonObject().get(uuid).getAsJsonObject().get("inv_contents").getAsJsonObject().get("data").getAsString();
                        InputStream inventoryStream = new ByteArrayInputStream(Base64.getDecoder().decode(inventoryBase64));
                    
                        NBTTagCompound armour = CompressedStreamTools.readCompressed(armourStream);
                        NBTTagList armourList = armour.getTagList("i", 10);
        
                        String weapon = EnumChatFormatting.RED + "None";
                        String weaponLore = EnumChatFormatting.RED + "None";
                        
                        if(!profileResponse.get("profile").getAsJsonObject().get("members").getAsJsonObject().get(uuid).getAsJsonObject().has("inv_contents")) {
                            weapon = ChatFormatting.RED+"This player has there API disabled!";
                            weaponLore = ChatFormatting.RED+"This player has there API disabled!";
                        } else {
                            NBTTagCompound inventory = CompressedStreamTools.readCompressed(inventoryStream);
                            NBTTagList inventoryList = inventory.getTagList("i", 10);

                            for (int i = 0; i < inventoryList.tagCount(); i++) {
                                NBTTagCompound item = inventoryList.getCompoundTagAt(i);
                                if (item.hasNoTags()) continue;
                                NBTTagCompound display = item.getCompoundTag("tag").getCompoundTag("display");
                                String itemName = item.getCompoundTag("tag").getCompoundTag("display").getString("Name");
                                String itemLore = "";
                                if (display.hasKey("Lore", ItemUtil.NBT_LIST)) {
                                    NBTTagList lore = display.getTagList("Lore", ItemUtil.NBT_STRING);
                    
                                    List<String> loreAsList = new ArrayList<>();
                                    for (int lineNumber = 0; lineNumber < lore.tagCount(); lineNumber++) {
                                        loreAsList.add(lore.getStringTagAt(lineNumber));
                                    }
                    
                                    itemLore = itemName+"\n"+String.join("\n",Collections.unmodifiableList(loreAsList));
                                }
                                // NBT is served boots -> helmet
                                switch (i) {
                                    case 0:
                                        weapon = itemName;
                                        weaponLore = itemLore;
                                        break;
                                    default:
                                        System.err.println("An error has occurred.");
                                        break;
                                }
                            }
                            inventoryStream.close();
                        }

                        String helmet = EnumChatFormatting.RED + "None";
                        String chest = EnumChatFormatting.RED + "None";
                        String legs = EnumChatFormatting.RED + "None";
                        String boots = EnumChatFormatting.RED + "None";
                        String helmetLore = EnumChatFormatting.RED + "None";
                        String chestLore = EnumChatFormatting.RED + "None";
                        String legsLore = EnumChatFormatting.RED + "None";
                        String bootsLore = EnumChatFormatting.RED + "None";
                        // Loop through armour
                        for (int i = 0; i < armourList.tagCount(); i++) {
                            NBTTagCompound armourPiece = armourList.getCompoundTagAt(i);
                            if (armourPiece.hasNoTags()) continue;
                            NBTTagCompound display = armourPiece.getCompoundTag("tag").getCompoundTag("display");
                            String armourPieceName = armourPiece.getCompoundTag("tag").getCompoundTag("display").getString("Name");
                            String armourPieceLore = "";
                            if (display.hasKey("Lore", ItemUtil.NBT_LIST)) {
                                NBTTagList lore = display.getTagList("Lore", ItemUtil.NBT_STRING);
                
                                List<String> loreAsList = new ArrayList<>();
                                for (int lineNumber = 0; lineNumber < lore.tagCount(); lineNumber++) {
                                    loreAsList.add(lore.getStringTagAt(lineNumber));
                                }
                
                                armourPieceLore = armourPieceName+"\n"+String.join("\n",Collections.unmodifiableList(loreAsList));
                            }
                            // NBT is served boots -> helmet
                            switch (i) {
                                case 0:
                                    boots = armourPieceName;
                                    bootsLore = armourPieceLore;
                                    break;
                                case 1:
                                    legs = armourPieceName;
                                    legsLore = armourPieceLore;
                                    break;
                                case 2:
                                    chest = armourPieceName;
                                    chestLore = armourPieceLore;
                                    break;
                                case 3:
                                    helmet = armourPieceName;
                                    helmetLore = armourPieceLore;
                                    break;
                                default:
                                    System.err.println("An error has occurred.");
                                    break;
                            }
                        }
                        armourStream.close();
        
        
                        ChatComponentText nameComponent = new ChatComponentText(EnumChatFormatting.AQUA+" Data For: " +EnumChatFormatting.YELLOW+ username + "\n ");
                        ChatComponentText kickComponent = new ChatComponentText("\n"+EnumChatFormatting.GREEN+"Click here to remove "+EnumChatFormatting.LIGHT_PURPLE+username+EnumChatFormatting.GREEN+" from the party");
                        ChatComponentText weaponComponent = new ChatComponentText(EnumChatFormatting.DARK_AQUA + weapon + "\n ");
                        ChatComponentText helmetComponent = new ChatComponentText(" "+EnumChatFormatting.DARK_AQUA + helmet + "\n ");
                        ChatComponentText chestComponent = new ChatComponentText(EnumChatFormatting.DARK_AQUA + chest + "\n ");
                        ChatComponentText legComponent = new ChatComponentText(EnumChatFormatting.DARK_AQUA + legs + "\n ");
                        ChatComponentText bootComponent = new ChatComponentText(EnumChatFormatting.DARK_AQUA + boots + "\n ");
        
                        weaponComponent.setChatStyle(weaponComponent.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(weaponLore))));
                        helmetComponent.setChatStyle(helmetComponent.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(helmetLore))));
                        chestComponent.setChatStyle(chestComponent.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(chestLore))));
                        legComponent.setChatStyle(legComponent.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(legsLore))));
                        kickComponent.setChatStyle(kickComponent.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/p kick "+username)));
                        bootComponent.setChatStyle(bootComponent.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(bootsLore))));
                        
                        StringBuilder completionsHoverString = new StringBuilder();
                        int highestFloor = catacombsObject.get("highest_tier_completed").getAsInt();
                        JsonObject completionObj = catacombsObject.get("tier_completions").getAsJsonObject();
                        int totalRuns = 0;
                        for (int i = 0; i <= highestFloor; i++) {
                            completionsHoverString
                                    .append(EnumChatFormatting.GOLD)
                                    .append(i == 0 ? "Entrance: " : "Floor " + i + ": ")
                                    .append(EnumChatFormatting.RESET)
                                    .append(completionObj.get(String.valueOf(i)).getAsInt())
                                    .append(i < highestFloor ? "\n": "");
        
                            totalRuns = totalRuns + completionObj.get(String.valueOf(i)).getAsInt();
                        }
                        completionsHoverString.append("\n"+EnumChatFormatting.GOLD+"Total: "+ChatFormatting.RESET+totalRuns);
                        ChatComponentText completions = new ChatComponentText(EnumChatFormatting.AQUA + " Floor Completions: "+ChatFormatting.GRAY+"(Hover)");
        
                        completions.setChatStyle(completions.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(completionsHoverString.toString()))));
        
                        mc.thePlayer.addChatMessage(
                            new ChatComponentText(delimiter1)
                            .appendText("\n")
                            .appendSibling(nameComponent)
                            .appendText(ChatFormatting.GREEN+"☠ Cata Level: "+ChatFormatting.YELLOW+catacombs+"\n")
                            .appendText(ChatFormatting.GREEN+" Total Secrets Found: "+ChatFormatting.YELLOW+secrets+"\n\n")
                            .appendSibling(helmetComponent)
                            .appendSibling(chestComponent)
                            .appendSibling(legComponent)
                            .appendSibling(bootComponent)
                            .appendSibling(weaponComponent)
                            .appendText("\n")
                            .appendSibling(completions)
                            .appendText("\n")
                            .appendSibling(new ChatComponentText(delimiter1))
                            .appendSibling(kickComponent));
                    } catch (IOException ex) {
                        System.out.println(ex);
                        Utils.SendMessage(ChatFormatting.RED+"Error! This player may not have there API on.");
                    }
                }).start();
            }
        }

        if (PartyGlow.gettingParty) {
            if(unformatted.isEmpty()||unformatted.contains("You are not currently in a party.")) {
                event.setCanceled(true);
            }
            else if (unformatted.contains("-----")) {
                event.setCanceled(true);
                switch(PartyGlow.Delimiter) {
                    case 0:
                        System.out.println("Get Party Delimiter Cancelled");
                        PartyGlow.Delimiter++;
                        return;
                    case 1:
                        System.out.println("Done querying party");
                        PartyGlow.gettingParty = false;
                        PartyGlow.Delimiter = 0;
                        return;
                }
            } else if (unformatted.startsWith("Party M") || unformatted.startsWith("Party Leader")){
                EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

                Matcher members = members_pattern.matcher(unformatted);

                while (members.find()) {
                    String partyMember = members.group(1);
                    if (!partyMember.equals(player.getName())) {
                        PartyGlow.party.add(partyMember);
                        System.out.println(partyMember);
                    }
                }

                event.setCanceled(true);
                return;
            }
        }

        if (skyblockfeatures.config.autoReparty) {
            if (unformatted.contains("has disbanded the party!") && !(unformatted.contains(mc.thePlayer.getName()))) {
                Matcher matcher = playerPattern.matcher(unformatted);
                if (matcher.find()) {
                    lastPartyDisbander = matcher.group(1);
                    System.out.println("Party disbanded by " + lastPartyDisbander);
                    rejoinThread = new Thread(() -> {
                        if (skyblockfeatures.config.autoRepartyTimeout == 0) return;
                        try {
                            System.out.println("waiting for timeout");
                            Thread.sleep(skyblockfeatures.config.autoRepartyTimeout * 1000);
                            lastPartyDisbander = "";
                            System.out.println("cleared last party disbander");
                        } catch (Exception e) {

                        }
                    });
                    rejoinThread.start();
                    return;
                }
            }
            if (unformatted.contains("You have 60 seconds to accept") && lastPartyDisbander.length() > 0 && event.message.getSiblings().size() > 0) {
                ChatStyle acceptMessage = event.message.getSiblings().get(6).getChatStyle();
                if (acceptMessage.getChatHoverEvent().getValue().getUnformattedText().contains(lastPartyDisbander)) {
                    skyblockfeatures.sendMessageQueue.add("/p accept " + lastPartyDisbander);
                    rejoinThread.interrupt();
                    lastPartyDisbander = "";
                    return;
                }
            }
        }


        // Reparty command
        // Getting party
        if (RepartyCommand.gettingParty) {
            if (unformatted.contains("-----")) {
                switch(RepartyCommand.Delimiter) {
                    case 0:
                        System.out.println("Get Party Delimiter Cancelled");
                        RepartyCommand.Delimiter++;
                        event.setCanceled(true);
                        return;
                    case 1:
                        System.out.println("Done querying party");
                        RepartyCommand.gettingParty = false;
                        RepartyCommand.Delimiter = 0;
                        event.setCanceled(true);
                        return;
                }
            } else if (unformatted.startsWith("Party M") || unformatted.startsWith("Party Leader")){
                EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

                Matcher party_start = party_start_pattern.matcher(unformatted);
                Matcher leader = leader_pattern.matcher(unformatted);
                Matcher members = members_pattern.matcher(unformatted);

                if (party_start.matches() && Integer.parseInt(party_start.group(1)) == 1) {
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "You cannot reparty yourself."));
                    RepartyCommand.partyThread.interrupt();
                } else if (leader.matches() && !(leader.group(1).equals(player.getName()))) {
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "You are not party leader."));
                    RepartyCommand.partyThread.interrupt();
                } else {
                    while (members.find()) {
                        String partyMember = members.group(1);
                        if (!partyMember.equals(player.getName())) {
                            RepartyCommand.party.add(partyMember);
                            System.out.println(partyMember);
                        }
                    }
                }
                event.setCanceled(true);
                return;
            }
        }
        // Disbanding party
        if (RepartyCommand.disbanding) {
            if (unformatted.contains("-----")) {
                switch (RepartyCommand.Delimiter) {
                    case 0:
                        System.out.println("Disband Delimiter Cancelled");
                        RepartyCommand.Delimiter++;
                        event.setCanceled(true);
                        return;
                    case 1:
                        System.out.println("Done disbanding");
                        RepartyCommand.disbanding = false;
                        RepartyCommand.Delimiter = 0;
                        event.setCanceled(true);
                        return;
                }
            } else if (unformatted.endsWith("has disbanded the party!")) {
                event.setCanceled(true);
                return;
            }
        }
        // Inviting
        if (RepartyCommand.inviting) {
            if (unformatted.contains("-----")) {
                switch (RepartyCommand.Delimiter) {
                    case 1:
                        event.setCanceled(true);
                        RepartyCommand.Delimiter = 0;
                        System.out.println("Player Invited!");
                        RepartyCommand.inviting = false;
                        return;
                    case 0:
                        RepartyCommand.Delimiter++;
                        event.setCanceled(true);
                        return;
                }
            } else if (unformatted.endsWith(" to the party! They have 60 seconds to accept.")) {
                Matcher invitee = invitePattern.matcher(unformatted);
                if (invitee.find()) {
                    System.out.println("" + invitee.group(1) + ": " + RepartyCommand.repartyFailList.remove(invitee.group(1)));
                }
                event.setCanceled(true);
                return;
            } else if (unformatted.contains("Couldn't find a player") || unformatted.contains("You cannot invite that player")) {
                event.setCanceled(true);
                return;
            }
        }
        // Fail Inviting
        if (RepartyCommand.failInviting) {
            if (unformatted.contains("-----")) {
                switch (RepartyCommand.Delimiter) {
                    case 1:
                        event.setCanceled(true);
                        RepartyCommand.Delimiter = 0;
                        System.out.println("Player Invited!");
                        RepartyCommand.inviting = false;
                        return;
                    case 0:
                        RepartyCommand.Delimiter++;
                        event.setCanceled(true);
                        return;
                }
            } else if (unformatted.endsWith(" to the party! They have 60 seconds to accept.")) {
                Matcher invitee = invitePattern.matcher(unformatted);
                if (invitee.find()) {
                    System.out.println("" + invitee.group(1) + ": " + RepartyCommand.repartyFailList.remove(invitee.group(1)));
                }
                event.setCanceled(true);
                return;
            } else if (unformatted.contains("Couldn't find a player") || unformatted.contains("You cannot invite that player")) {
                event.setCanceled(true);
                return;
            }
        }

        if (skyblockfeatures.config.firstLaunch && unformatted.equals("Welcome to Hypixel SkyBlock!")) {
            mc.thePlayer.addChatMessage(new ChatComponentText("§bThank you for downloading skyblockfeatures! Do /sf to start!"));

            skyblockfeatures.config.firstLaunch = false;
            skyblockfeatures.config.markDirty();
            skyblockfeatures.config.writeData();
        }
    }

    public int peoplejoined;
}
