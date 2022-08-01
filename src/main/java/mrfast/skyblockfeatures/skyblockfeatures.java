package mrfast.skyblockfeatures;

import java.io.File;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import gg.essential.api.EssentialAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.ICommand;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import mrfast.skyblockfeatures.commands.AccessoriesCommand;
import mrfast.skyblockfeatures.commands.ArmorCommand;
import mrfast.skyblockfeatures.commands.BankCommand;
import mrfast.skyblockfeatures.commands.DungeonsCommand;
import mrfast.skyblockfeatures.commands.GetkeyCommand;
import mrfast.skyblockfeatures.commands.GoodbyeCommand;
import mrfast.skyblockfeatures.commands.HelloCommand;
import mrfast.skyblockfeatures.commands.InventoryCommand;
import mrfast.skyblockfeatures.commands.RepartyCommand;
import mrfast.skyblockfeatures.commands.ShrugCommand;
import mrfast.skyblockfeatures.commands.SkillsCommand;
import mrfast.skyblockfeatures.commands.SkyCommand;
import mrfast.skyblockfeatures.commands.TerminalCommand;
import mrfast.skyblockfeatures.commands.ViewModelCommand;
import mrfast.skyblockfeatures.commands.configCommand;
import mrfast.skyblockfeatures.commands.getNbtCommand;
import mrfast.skyblockfeatures.commands.sidebarCommand;
import mrfast.skyblockfeatures.core.Config;
import mrfast.skyblockfeatures.core.DataFetcher;
import mrfast.skyblockfeatures.core.GuiManager;
import mrfast.skyblockfeatures.events.ChestSlotClickedEvent;
import mrfast.skyblockfeatures.events.GuiChestBackgroundDrawnEvent;
import mrfast.skyblockfeatures.events.PacketEvent;
import mrfast.skyblockfeatures.events.SecondPassedEvent;
import mrfast.skyblockfeatures.features.impl.ItemFeatures.HideGlass;
import mrfast.skyblockfeatures.features.impl.bar.ActionBarListener;
import mrfast.skyblockfeatures.features.impl.bar.CryptDisplay;
import mrfast.skyblockfeatures.features.impl.bar.DefenceDisplay;
import mrfast.skyblockfeatures.features.impl.bar.EffectiveHealthDisplay;
import mrfast.skyblockfeatures.features.impl.bar.HealthBarFeature;
import mrfast.skyblockfeatures.features.impl.bar.HealthDisplay;
import mrfast.skyblockfeatures.features.impl.bar.ManaDisplay;
import mrfast.skyblockfeatures.features.impl.bar.SecretDisplay;
import mrfast.skyblockfeatures.features.impl.bar.SpeedDisplay;
// import mrfast.skyblockfeatures.features.impl.bar.SkillDisplay;
import mrfast.skyblockfeatures.features.impl.dungeons.BetterParties;
import mrfast.skyblockfeatures.features.impl.dungeons.ChestProfit;
import mrfast.skyblockfeatures.features.impl.dungeons.DungeonBlocks;
import mrfast.skyblockfeatures.features.impl.dungeons.DungeonsFeatures;
import mrfast.skyblockfeatures.features.impl.dungeons.Nametags;
import mrfast.skyblockfeatures.features.impl.events.JerryTimer;
import mrfast.skyblockfeatures.features.impl.events.MayorJerry;
import mrfast.skyblockfeatures.features.impl.glowingstuff.PartyGlow;
import mrfast.skyblockfeatures.features.impl.handlers.AuctionData;
import mrfast.skyblockfeatures.features.impl.handlers.KeyShortcuts;
import mrfast.skyblockfeatures.features.impl.hidestuff.HideStuff;
import mrfast.skyblockfeatures.features.impl.mining.CommisionsTracker;
import mrfast.skyblockfeatures.features.impl.mining.MetalDetectorHelp;
import mrfast.skyblockfeatures.features.impl.mining.MiningFeatures;
import mrfast.skyblockfeatures.features.impl.misc.AuctionFeatures;
import mrfast.skyblockfeatures.features.impl.misc.ConjuringCooldown;
import mrfast.skyblockfeatures.features.impl.misc.CropCounter;
import mrfast.skyblockfeatures.features.impl.misc.DamageSplash;
import mrfast.skyblockfeatures.features.impl.misc.FavoritePets;
import mrfast.skyblockfeatures.features.impl.misc.FishingHelper;
import mrfast.skyblockfeatures.features.impl.misc.ItemFeatures;
import mrfast.skyblockfeatures.features.impl.misc.LockingSlots;
import mrfast.skyblockfeatures.features.impl.misc.MiscFeatures;
import mrfast.skyblockfeatures.features.impl.misc.SpamHider;
import mrfast.skyblockfeatures.features.impl.overlays.CompactChat;
import mrfast.skyblockfeatures.features.impl.overlays.CrystalHollowsMap;
import mrfast.skyblockfeatures.features.impl.overlays.DamageOverlays;
import mrfast.skyblockfeatures.features.impl.overlays.DungeonMap;
import mrfast.skyblockfeatures.features.impl.overlays.FairySoulWaypoints;
import mrfast.skyblockfeatures.features.impl.overlays.GiftCompassWaypoints;
import mrfast.skyblockfeatures.features.impl.overlays.MinionOverlay;
import mrfast.skyblockfeatures.features.impl.overlays.ZealotSpawnLocations;
import mrfast.skyblockfeatures.features.impl.trackers.AutomatonTracker;
import mrfast.skyblockfeatures.listeners.ChatListener;
import mrfast.skyblockfeatures.mixins.AccessorCommandHandler;
import mrfast.skyblockfeatures.utils.CapeUtils;
// import mrfast.skyblockfeatures.utils.Friend;
// import mrfast.skyblockfeatures.utils.FriendManager;
import mrfast.skyblockfeatures.utils.SBInfo;
import mrfast.skyblockfeatures.utils.Utils;

@Mod(modid = skyblockfeatures.MODID, name = skyblockfeatures.MOD_NAME, version = skyblockfeatures.VERSION, acceptedMinecraftVersions = "[1.8.9]", clientSideOnly = true)
public class skyblockfeatures {
    public static final String MODID = "skyblockfeatures";
    public static final String MOD_NAME = "skyblockfeatures";
    public static final String VERSION = "1.0";
    public static final Minecraft mc = Minecraft.getMinecraft();

    public static Config config = new Config();
    public static File modDir = new File(new File(mc.mcDataDir, "config"), "skyblockfeatures");
    public static GuiManager GUIMANAGER;
    public static Logger LOGGER = LogManager.getLogger(MOD_NAME);
    public static int ticks = 0;

    public static ArrayDeque<String> sendMessageQueue = new ArrayDeque<>();
    public static boolean usingDungeonRooms = false;
    public static boolean usingLabymod = false;
    public static boolean usingNEU = false;

    public static File jarFile = null;
    private static long lastChatMessage = 0;
    // private static FriendManager m_FriendManager = new FriendManager();

    @Mod.Instance(MODID)
    public static skyblockfeatures INSTANCE;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        if (!modDir.exists()) modDir.mkdirs();
        GUIMANAGER = new GuiManager();
        jarFile = event.getSourceFile();
    }
    
    // boolean a = false;
    // String delimiter = EnumChatFormatting.AQUA.toString() + EnumChatFormatting.STRIKETHROUGH.toString() + "" + EnumChatFormatting.BOLD + "--------------------------------------";
    // @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    // public void onChat(PlayerTickEvent event) {
    //     if(!a && getVersion() != VERSION) {
    //         a = true;
            
    //         ClickEvent versionCheckChatClickEvent = new ClickEvent(ClickEvent.Action.OPEN_URL, 
    //         "https://drive.google.com/u/0/uc?id=1JTCpU6PoE1cBqQ2teOzv62HkGUpE_2Uz&export=download");
    //         ChatStyle clickableChatStyle = new ChatStyle().setChatClickEvent(versionCheckChatClickEvent);
    //         ChatComponentText versionWarningChatComponent = 
    //         new ChatComponentText(EnumChatFormatting.RED+"Your running an outdated version of Skyblock! Click Here to update.");
    //         versionWarningChatComponent.setChatStyle(clickableChatStyle);
    //         // mc.thePlayer.addChatMessage(versionWarningChatComponent);
    //         Utils.GetMC().thePlayer.addChatMessage(
    //                 new ChatComponentText(delimiter)
    //                 .appendText("\n")
    //                 .appendSibling(versionWarningChatComponent)
    //                 .appendText("\n")
    //                 .appendSibling(new ChatComponentText(delimiter))
    //         );
    //     }
    // }

    // public static String versionURL = ""; // for template

    // public static String getVersion() {
    //     String versionNumber = null;
    //     try {
    //         final URL url = new URL(versionURL);
    //         BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
    //         String inputText = bufferedReader.readLine();
    //         return inputText;
    //     }catch (Exception exception){
    //         FMLLog.getLogger().info("There was a error getting the version Number");
    //     }
    //     return versionNumber;
    // }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        try {
            // DiscordRPC.INSTANCE.start();
        } catch (NoClassDefFoundError e) {
            e.printStackTrace();
        }
        config.preload();

        // EssentialAPI.getCommandRegistry().registerCommand(new configCommand());
        EssentialAPI.getCommandRegistry().registerCommand(new ViewModelCommand());

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new ChatListener());
        MinecraftForge.EVENT_BUS.register(new DataFetcher());
        MinecraftForge.EVENT_BUS.register(GUIMANAGER);
        MinecraftForge.EVENT_BUS.register(SBInfo.getInstance());

        MinecraftForge.EVENT_BUS.register(new SpamHider());
        MinecraftForge.EVENT_BUS.register(new AuctionData());
        MinecraftForge.EVENT_BUS.register(new ZealotSpawnLocations());
        MinecraftForge.EVENT_BUS.register(new ChestProfit());
        MinecraftForge.EVENT_BUS.register(new DungeonMap());
        MinecraftForge.EVENT_BUS.register(new DamageSplash());
        MinecraftForge.EVENT_BUS.register(new DungeonsFeatures());
        MinecraftForge.EVENT_BUS.register(new ItemFeatures());
        MinecraftForge.EVENT_BUS.register(new KeyShortcuts());
        MinecraftForge.EVENT_BUS.register(new CrystalHollowsMap());
        MinecraftForge.EVENT_BUS.register(new MayorJerry());
        MinecraftForge.EVENT_BUS.register(new MiningFeatures());
        MinecraftForge.EVENT_BUS.register(new MiscFeatures());
        MinecraftForge.EVENT_BUS.register(new DungeonBlocks());
        MinecraftForge.EVENT_BUS.register(new DamageOverlays());
        MinecraftForge.EVENT_BUS.register(new Nametags());
        MinecraftForge.EVENT_BUS.register(new ConjuringCooldown());
        MinecraftForge.EVENT_BUS.register(new LockingSlots());
        MinecraftForge.EVENT_BUS.register(new PartyGlow());
        MinecraftForge.EVENT_BUS.register(new FavoritePets());
        MinecraftForge.EVENT_BUS.register(new SpeedDisplay());
        MinecraftForge.EVENT_BUS.register(new EffectiveHealthDisplay());
        MinecraftForge.EVENT_BUS.register(new ManaDisplay());
        MinecraftForge.EVENT_BUS.register(new HealthDisplay());
        MinecraftForge.EVENT_BUS.register(new SecretDisplay());
        MinecraftForge.EVENT_BUS.register(new CryptDisplay());
        MinecraftForge.EVENT_BUS.register(new DefenceDisplay());
        MinecraftForge.EVENT_BUS.register(new HideStuff());
        MinecraftForge.EVENT_BUS.register(new HealthBarFeature());
        MinecraftForge.EVENT_BUS.register(new ActionBarListener());
        MinecraftForge.EVENT_BUS.register(new CompactChat());
        MinecraftForge.EVENT_BUS.register(new BetterParties());
        MinecraftForge.EVENT_BUS.register(new CommisionsTracker());
        MinecraftForge.EVENT_BUS.register(new FairySoulWaypoints());
        MinecraftForge.EVENT_BUS.register(new JerryTimer());
        MinecraftForge.EVENT_BUS.register(new GiftCompassWaypoints());
        MinecraftForge.EVENT_BUS.register(new CropCounter());
        MinecraftForge.EVENT_BUS.register(new HideGlass());
        MinecraftForge.EVENT_BUS.register(new FishingHelper());
        MinecraftForge.EVENT_BUS.register(new AuctionFeatures());
        MinecraftForge.EVENT_BUS.register(new CapeUtils());
        MinecraftForge.EVENT_BUS.register(new MetalDetectorHelp());
        MinecraftForge.EVENT_BUS.register(new MinionOverlay());
        MinecraftForge.EVENT_BUS.register(new AutomatonTracker());
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        usingDungeonRooms = Loader.isModLoaded("dungeonrooms");
        usingLabymod = Loader.isModLoaded("labymod");
        usingNEU = Loader.isModLoaded("notenoughupdates");

        ClientCommandHandler cch = ClientCommandHandler.instance;

        if (!cch.getCommands().containsKey("getnbt")) cch.registerCommand(new getNbtCommand());

        if (!cch.getCommands().containsKey("sky")) cch.registerCommand(new SkyCommand());

        if (!cch.getCommands().containsKey("skyblockfeatures")) cch.registerCommand(new configCommand());

        if (!cch.getCommands().containsKey("accessories")) cch.registerCommand(new AccessoriesCommand());

        if (!cch.getCommands().containsKey("reparty")) cch.registerCommand(new RepartyCommand());

        if (!cch.getCommands().containsKey("goodbye")) cch.registerCommand(new GoodbyeCommand());

        if (!cch.getCommands().containsKey("hello")) cch.registerCommand(new HelloCommand());

        if (!cch.getCommands().containsKey("terminal")) cch.registerCommand(new TerminalCommand());

        if (!cch.getCommands().containsKey("shrug")) cch.registerCommand(new ShrugCommand());

        if (!cch.getCommands().containsKey("bank")) cch.registerCommand(new BankCommand());

        if (!cch.getCommands().containsKey("armor")) cch.registerCommand(new ArmorCommand());

        if (!cch.getCommands().containsKey("inventory")) cch.registerCommand(new InventoryCommand());

        if (!cch.getCommands().containsKey("key")) cch.registerCommand(new GetkeyCommand());

        if (!cch.getCommands().containsKey("dungeons")) cch.registerCommand(new DungeonsCommand());

        if (!cch.getCommands().containsKey("skills")) cch.registerCommand(new SkillsCommand());

        if (!cch.getCommands().containsKey("sidebar")) cch.registerCommand(new sidebarCommand());
    

        if (!cch.getCommands().containsKey("rp")) {
            ((AccessorCommandHandler) cch).getCommandSet().add(new RepartyCommand());
            ((AccessorCommandHandler) cch).getCommandMap().put("rp", new RepartyCommand());
        }
        if (skyblockfeatures.config.overrideReparty) {
            if (!cch.getCommands().containsKey("rp")) {
                ((AccessorCommandHandler) cch).getCommandSet().add(new RepartyCommand());
                ((AccessorCommandHandler) cch).getCommandMap().put("rp", new RepartyCommand());
            }
            for (Map.Entry<String, ICommand> entry : cch.getCommands().entrySet()) {
                if (Objects.equals(entry.getKey(), "reparty") || Objects.equals(entry.getKey(), "rp")) {
                    entry.setValue(new RepartyCommand());
                }
            }
        }
    }

    public static boolean auctionPricesLoaded = false;
    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;

        if (mc.thePlayer != null && sendMessageQueue.size() > 0 && System.currentTimeMillis() - lastChatMessage > 200) {
            String msg = sendMessageQueue.pollFirst();
            if (msg != null) {
                mc.thePlayer.sendChatMessage(msg);
            }
        }

        if (ticks % 20 == 0) {
            if (mc.thePlayer != null) {
                // try {
                    Utils.checkForSkyblock();
                    Utils.checkForDungeons();
                //     if(!auctionPricesLoaded)
                //     if(AuctionData.lowestBINs.get("GOBLIN_HELMET") != 0) {
                //         auctionPricesLoaded = true;
                //         Utils.GetMC().thePlayer.addChatMessage(new ChatComponentText(ChatFormatting.GREEN+"Auction Prices Loaded!"));
                //         Utils.GetMC().thePlayer.playSound("note.pling", 1, 2);
                //     }
                // } catch (Exception e) {
                //     //TODO: handle exception
                // }
            }
            MinecraftForge.EVENT_BUS.post(new SecondPassedEvent());
            ticks = 0;
        }

        ticks++;
        if(Minecraft.getMinecraft().theWorld == null) return;
        if(Minecraft.getMinecraft().thePlayer == null) return;
        DungeonBlocks.tick();
    }

    @SubscribeEvent
    public void onSendPacket(PacketEvent.SendEvent event) {
        if (event.packet instanceof C01PacketChatMessage) {
            lastChatMessage = System.currentTimeMillis();
        }
    }
    
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START && !Minecraft.getMinecraft().isSingleplayer() && Minecraft.getMinecraft().theWorld != null && Minecraft.getMinecraft().getNetHandler() != null && EssentialAPI.getMinecraftUtil().isHypixel()) {
            try {
                Scoreboard scoreboard = Minecraft.getMinecraft().thePlayer.getWorldScoreboard();
                ScoreObjective scoreObjective = scoreboard.getObjectiveInDisplaySlot(1);
                Collection<Score> collection = scoreboard.getSortedScores(scoreObjective);
                for (Score score1 : collection)
                {
                    ScorePlayerTeam scorePlayerTeam = scoreboard.getPlayersTeam(score1.getPlayerName());
                    String scoreText = EnumChatFormatting.getTextWithoutFormattingCodes(ScorePlayerTeam.formatPlayerName(scorePlayerTeam, score1.getPlayerName()));

                    if (scoreText.contains("⏣")) {
                        locationString = keepLettersAndNumbersOnly(scoreText.replace("⏣", ""));
                    }
                }
            } catch (NullPointerException  e) {
                //TODO: handle exception
            }
        }
    }
    
    public static String locationString = "Unknown";
    private static final Pattern LETTERS_NUMBERS = Pattern.compile("[^a-z A-Z:0-9/'()]");

    private String keepLettersAndNumbersOnly(String text)
    {
        return LETTERS_NUMBERS.matcher(EnumChatFormatting.getTextWithoutFormattingCodes(text)).replaceAll("");
    }

    @SubscribeEvent
    public void onGuiRender(GuiScreenEvent.BackgroundDrawnEvent event) {
        if (!Utils.inSkyblock) return;
        if (event.gui instanceof GuiChest) {
            GuiChest inventory = (GuiChest) event.gui;
            Container containerChest = inventory.inventorySlots;
            if (containerChest instanceof ContainerChest) {
                List<Slot> invSlots = inventory.inventorySlots.inventorySlots;
                String displayName = ((ContainerChest) containerChest).getLowerChestInventory().getDisplayName().getUnformattedText().trim();
                int chestSize = inventory.inventorySlots.inventorySlots.size();

                MinecraftForge.EVENT_BUS.post(new GuiChestBackgroundDrawnEvent(inventory, displayName, chestSize, invSlots));
            }
        }
    }

    @SubscribeEvent
    public void onGuiMouseInputPre(GuiScreenEvent.MouseInputEvent.Pre event) {
        // if (!Utils.inSkyblock) return;
        if (Mouse.getEventButton() != 0 && Mouse.getEventButton() != 1 && Mouse.getEventButton() != 2)
            return; // Left click, middle click or right click
        if (!Mouse.getEventButtonState()) return;

        if (event.gui instanceof GuiChest) {
            Container containerChest = ((GuiChest) event.gui).inventorySlots;
            if (containerChest instanceof ContainerChest) {
                // a lot of declarations here, if you get scarred, my bad
                GuiChest chest = (GuiChest) event.gui;
                IInventory inventory = ((ContainerChest) containerChest).getLowerChestInventory();
                Slot slot = chest.getSlotUnderMouse();
                if (slot == null) return;
                ItemStack item = slot.getStack();
                String inventoryName = inventory.getDisplayName().getUnformattedText();
                if (item == null) {
                    if (MinecraftForge.EVENT_BUS.post(new ChestSlotClickedEvent(chest, inventory, inventoryName, slot))) event.setCanceled(true);
                } else {
                    if (MinecraftForge.EVENT_BUS.post(new ChestSlotClickedEvent(chest, inventory, inventoryName, slot, item))) event.setCanceled(true);
                }
            }
        }
    }


    // @SubscribeEvent
    // public void onGuiAction(GuiScreenEvent.ActionPerformedEvent.Post event) {
    //     if (skyblockfeatures.config.configButtonOnPause && event.gui instanceof GuiIngameMenu && event.button.id == 6969420) {
    //         ModCore.getInstance().getGuiHandler().open(new OptionsGui());
    //     }
    // }

    // @SubscribeEvent
    // public void onGuiChange(GuiOpenEvent event) {
    //     GuiScreen old = mc.currentScreen;
    //     if (event.gui == null && skyblockfeatures.config.reopenOptionsMenu) {
    //         boolean isSettingsGui = old instanceof CommandAliasesGui || old instanceof LocationEditGui || old instanceof KeyShortcutsGui || (old instanceof SettingsGui);
    //         if (isSettingsGui) event.gui = new OptionsGui();
    //     }
    // }

    private KeyBinding toggleSprint;
    private static boolean toggled = true;

    public final static KeyBinding slotLockKeybind = new KeyBinding("Lock Slot", Keyboard.KEY_L, "skyblockfeatures 2.0");
    public final static KeyBinding ghostBlockKeybind = new KeyBinding("Ghost Block", Keyboard.KEY_G, "skyblockfeatures 2.0");
    public final static KeyBinding favoritePetKeybind = new KeyBinding("Toggle Favorite Pet", Keyboard.KEY_F, "skyblockfeatures 2.0");
    public final static KeyBinding reloadAH = new KeyBinding("Reload AH", Keyboard.KEY_R, "skyblockfeatures 2.0");

    
    @EventHandler
    public void inist(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        ClientRegistry.registerKeyBinding(slotLockKeybind);
        ClientRegistry.registerKeyBinding(ghostBlockKeybind);
        ClientRegistry.registerKeyBinding(favoritePetKeybind);
        ClientRegistry.registerKeyBinding(reloadAH);

        toggleSprint = new KeyBinding("Toggle Sprint", Keyboard.KEY_I, "skyblockfeatures 2.0");
        ClientRegistry.registerKeyBinding(toggleSprint);
    }

    @SubscribeEvent
    public void onTsick(TickEvent.ClientTickEvent e) {
        if (toggleSprint.isPressed()) {
            if (toggled) {
                mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Togglesprint disabled."));
            } else {
                mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Togglesprint enabled."));
            }

            toggled = !toggled;

        }

        if (toggled) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
        } else {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), false);
        }
    }
}
