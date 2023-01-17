package mrfast.skyblockfeatures.features.impl.dungeons;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lwjgl.input.Mouse;

import com.mojang.realmsclient.gui.ChatFormatting;

import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.core.structure.FloatPair;
import mrfast.skyblockfeatures.core.structure.GuiElement;
import mrfast.skyblockfeatures.events.GuiContainerEvent;
import mrfast.skyblockfeatures.utils.RenderUtil;
import mrfast.skyblockfeatures.utils.ScoreboardUtil;
import mrfast.skyblockfeatures.utils.StringUtils;
import mrfast.skyblockfeatures.utils.Utils;
import mrfast.skyblockfeatures.utils.graphics.ScreenRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DungeonsFeatures {

    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final Pattern playerPattern = Pattern.compile("(?:\\[.+?] )?(\\w+)");
    public static String dungeonFloor = null;
    public static boolean hasBossSpawned = false;
    public static boolean foundLivid = false;
    public static Entity livid = null;

    @SubscribeEvent
    public void onWorldChanges2(WorldEvent.Load event) {
        try {
            foundLivid = false;
            livid = null;
        } catch (Exception e) {

        }
    }

    @SubscribeEvent
    public void onRender3D(RenderWorldLastEvent event) {
        for(Entity entity:mc.theWorld.loadedEntityList) {
            if(entity instanceof EntityBat && skyblockfeatures.config.highlightBats && Utils.inDungeons && !entity.isInvisible()) {
                RenderUtil.drawOutlinedFilledBoundingBox(entity.getEntityBoundingBox(),Color.GREEN,event.partialTicks);
            }
        }
    }
    
    @SubscribeEvent
    public void onWorldChanges(WorldEvent.Load event) {
        try {
            count = 0;
            dungeonFloor = null;
            hasBossSpawned = false;
            bloodguy = null;
            blessings.clear();
            livid = null;
            foundLivid = false;
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    String delimiter = EnumChatFormatting.AQUA.toString() + EnumChatFormatting.STRIKETHROUGH.toString() + "" + EnumChatFormatting.BOLD + "--------------------------------------";
    int count = 0;
    public static EntityPlayer bloodguy;
    static Map<String,Integer> blessings = new HashMap<String,Integer>();
    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onChatMesaage(ClientChatReceivedEvent event) {
        if (!Utils.inDungeons || event.type == 2) return;
        String text = event.message.getUnformattedText();
        for (Map.Entry<EntityPlayer,String> entry : Nametags.players.entrySet()) {
            if(text.contains(entry.getKey().getName()) && text.contains("has obtained Blood Key!")) {
                bloodguy = entry.getKey();
            }
        }

        if(text.contains("Granted you ") && text.contains("and")) {
            int stat1 = 0;
            try {
                stat1 = Integer.parseInt(text.split(" ")[2]);
            } catch (Exception e) {
                // TODO: handle exception
            }
            String stat1Type = text.split(" ")[3];
            if(blessings.get(stat1Type) == null) blessings.put(stat1Type, stat1);
            else {
                blessings.replace(stat1Type, blessings.get(stat1Type), blessings.get(stat1Type)+stat1);
            }
            int stat2 = 0;
            try {
                stat2 = Integer.parseInt(text.split(" ")[6]);
            } catch (Exception e) {
                // TODO: handle exception
            }
            String stat2Type = text.split(" ")[7];
            if(blessings.get(stat2Type) == null) blessings.put(stat2Type, stat2);
            else {
                blessings.replace(stat2Type, blessings.get(stat2Type), blessings.get(stat2Type)+stat2);
            }
        }

        if(!skyblockfeatures.config.quickStart) return;
        
        if (text.contains("§6> §e§lEXTRA STATS §6<")) {
            count=1;
        }
        if (text.equals("▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬")) {
            if(count == 1) {
                ChatComponentText message = new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE+"[SBF] "+EnumChatFormatting.GOLD + "Dungeon finished! ");
                ChatComponentText warpout = new ChatComponentText(EnumChatFormatting.GREEN+""+EnumChatFormatting.BOLD + " [WARP-OUT]  ");
                ChatComponentText frag = new ChatComponentText(EnumChatFormatting.GREEN+""+EnumChatFormatting.BOLD + "[REPARTY]");
    
                frag.setChatStyle(frag.getChatStyle()
                .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/rp"))
                .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.GREEN+"Reparty Group"))));
                
    
                warpout.setChatStyle(warpout.getChatStyle()
                .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/warp dungeon_hub"))
                .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.GREEN+"Warp out of the dungeon"))));
    
                Utils.GetMC().thePlayer.addChatMessage(new ChatComponentText(ChatFormatting.GREEN+"▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"));
    
                Utils.GetMC().thePlayer.addChatMessage(
                    new ChatComponentText(delimiter)
                    .appendText("\n")
                    .appendSibling(message)
                    .appendSibling(warpout)
                    .appendSibling(frag)
                    .appendText("\n")
                    .appendSibling(new ChatComponentText(delimiter))
                );
    
                count = 0;
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onDrawSlots(GuiContainerEvent.DrawSlotEvent.Pre event) {
        if (!Utils.inSkyblock) return;
        if(!event.slot.getHasStack()) return;
        ItemStack stack = event.slot.getStack();
        int x = event.slot.xDisplayPosition;
        int y = event.slot.yDisplayPosition;
        String n = Utils.cleanColour(stack.getDisplayName());
        if(skyblockfeatures.config.highlightTrash) {
            if(!(stack.getItem() instanceof ItemSkull)) {
                if(n.contains("Dreadlord") || n.contains("Grunt") || n.contains("Rotten") || n.contains("Machine Gun") || n.contains("Skeleton Soldier") || n.contains("Zombie Soldier") || n.contains("Enchanted Bone") || n.contains("Fel Pearl")  || n.contains("Healing VIII")  || n.contains("Super Heavy")  || n.contains("Conjuring") || n.contains("Skeletor") || n.contains("Training")) {
                    Gui.drawRect(x, y, x + 16, y + 1, new Color(255, 0, 0, 255).getRGB());
                    Gui.drawRect(x, y, x + 1, y + 16, new Color(255, 0, 0, 255).getRGB());
                    Gui.drawRect(x+15, y, x+16, y + 16, new Color(255, 0, 0, 255).getRGB());
                    Gui.drawRect(x, y+15, x + 16, y + 16, new Color(255, 85, 0, 255).getRGB());
                }
            }
        }
        
        if (!(event.gui instanceof GuiChest)) return;
        GuiChest inventory = (GuiChest) event.gui;
        Container containerChest = inventory.inventorySlots;
        if (!(containerChest instanceof ContainerChest)) return;
        String displayName = ((ContainerChest) containerChest).getLowerChestInventory().getDisplayName().getUnformattedText().trim();
        if(Utils.inDungeons && ((skyblockfeatures.config.spiritLeapNames && displayName.equals("Spirit Leap")))) {
            if (bloodguy != null) {
                if (stack.getDisplayName().contains(bloodguy.getName())) Gui.drawRect(x, y, x + 16, y + 16, new Color(255, 85, 85, 255).getRGB());
            }
        }
    }
    // Spirit leap names
    @SubscribeEvent
    public void onGuiDrawPost(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (!Utils.inSkyblock) return;
        if (event.gui instanceof GuiChest) {
            GuiChest inventory = (GuiChest) event.gui;
            Container containerChest = inventory.inventorySlots;
            if (containerChest instanceof ContainerChest) {
                ScaledResolution sr = new ScaledResolution(mc);
                FontRenderer fr = mc.fontRendererObj;
                int guiLeft = (sr.getScaledWidth() - 176) / 2;
                int guiTop = (sr.getScaledHeight() - 222) / 2;

                List<Slot> invSlots = inventory.inventorySlots.inventorySlots;
                String displayName = ((ContainerChest) containerChest).getLowerChestInventory().getDisplayName().getUnformattedText().trim();
                int chestSize = inventory.inventorySlots.inventorySlots.size();

                if (Utils.inDungeons && ((skyblockfeatures.config.spiritLeapNames && displayName.equals("Spirit Leap")) || (skyblockfeatures.config.reviveStoneNames && displayName.equals("Revive A Teammate")))) {
                    int people = 0;
                    for (Slot slot : invSlots) {
                        if (slot.inventory == mc.thePlayer.inventory) continue;
                        if (slot.getHasStack()) {
                            ItemStack item = slot.getStack();
                            if (item.getItem() == Items.skull) {
                                people++;

                                //slot is 16x16
                                int x = guiLeft + slot.xDisplayPosition + 8;
                                int y = guiTop + slot.yDisplayPosition;
                                // Move down when chest isn't 6 rows
                                if (chestSize != 90) y += (6 - (chestSize - 36) / 9) * 9;

                                if (people % 2 != 0) {
                                    y -= 15;
                                } else {
                                    y += 20;
                                }

                                Matcher matcher = playerPattern.matcher(StringUtils.stripControlCodes(item.getDisplayName()));
                                if (!matcher.find()) continue;
                                String name = matcher.group(1);
                                if (name.equals("Unknown")) continue;
                                String dungeonClass = "";
                                for (String l : ScoreboardUtil.getSidebarLines()) {
                                    String line = ScoreboardUtil.cleanSB(l);
                                    if (line.contains(name)) {
                                        dungeonClass = line.substring(line.indexOf("[") + 1, line.indexOf("]"));
                                        break;
                                    }
                                }
                                String text = fr.trimStringToWidth(item.getDisplayName().substring(0, 2) + name, 32);
                                x -= fr.getStringWidth(text) / 2;

                                boolean shouldDrawBkg = true;
                                if (skyblockfeatures.usingNEU && !displayName.equals("Revive A Teammate")) {
                                    try {
                                        Class<?> neuClass = Class.forName("io.github.moulberry.notenoughupdates.NotEnoughUpdates");
                                        Field neuInstance = neuClass.getDeclaredField("INSTANCE");
                                        Object neu = neuInstance.get(null);
                                        Field neuConfig = neuClass.getDeclaredField("config");
                                        Object config = neuConfig.get(neu);
                                        Field improvedSBMenu = config.getClass().getDeclaredField("improvedSBMenu");
                                        Object improvedSBMenuS = improvedSBMenu.get(config);
                                        Field enableSbMenus = improvedSBMenuS.getClass().getDeclaredField("enableSbMenus");
                                        boolean customGuiEnabled = enableSbMenus.getBoolean(improvedSBMenuS);
                                        if (customGuiEnabled) shouldDrawBkg = false;
                                    } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException ignored) {
                                    }
                                }

                                
                                double scale =  0.9f;
                                double scaleReset = 1 / scale;
                                GlStateManager.disableLighting();
                                GlStateManager.disableDepth();
                                GlStateManager.disableBlend();
                                GlStateManager.translate(0, 0, 1);
                                if (shouldDrawBkg)Gui.drawRect(x - 2, y - 2, x + fr.getStringWidth(text) + 2, y + fr.FONT_HEIGHT + 2, new Color(47, 40, 40).getRGB());
                                fr.drawStringWithShadow(text, x, y, new Color(255, 255, 255).getRGB());
                                GlStateManager.scale(scale, scale, scale);
                                fr.drawString(dungeonClass, (float) (scaleReset * (x + 7)), (float) (scaleReset * (guiTop + slot.yDisplayPosition + 18)), new Color(255, 255, 0).getRGB(), true);
                                GlStateManager.scale(scaleReset, scaleReset, scaleReset);
                                GlStateManager.translate(0, 0, -1);
                                GlStateManager.enableLighting();
                                GlStateManager.enableDepth();

                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onDrawSlot(GuiContainerEvent.DrawSlotEvent.Pre event) {
        if (!Utils.inSkyblock) return;
        Slot slot = event.slot;
        if (event.container instanceof ContainerChest) {
            ContainerChest cc = (ContainerChest) event.container;
            String displayName = cc.getLowerChestInventory().getDisplayName().getUnformattedText().trim();
            if (slot.getHasStack()) {
                ItemStack item = slot.getStack();
                if (skyblockfeatures.config.spiritLeapNames && displayName.equals("Spirit Leap")) {
                    if (item.getItem() == Item.getItemFromBlock(Blocks.stained_glass_pane)) {
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    static {
        new BlessingViewer();
    }
    
    public static class BlessingViewer extends GuiElement {
  
        public BlessingViewer() {
            super("Blessings Viewer", new FloatPair(0.45052084f, 0.86944443f));
            skyblockfeatures.GUIMANAGER.registerElement(this);
        }
  
        @Override
        public void render() {
            if(Utils.inDungeons && getToggled()) {
                int i = 0;
                GuiPlayerTabOverlay tabList = Minecraft.getMinecraft().ingameGUI.getTabList();
                String footer = tabList.footer.getFormattedText();
                Utils.drawText("§d§lBlessings",0,0);
                i++;
                for (String line : new ArrayList<>(Arrays.asList(footer.split("\n")))) {
                    if(line.contains("Blessing")) {
                        Utils.drawText("§d"+Utils.cleanColour(line), 0, i * ScreenRenderer.fontRenderer.FONT_HEIGHT);
                        i++;
                    }
                }
            }
        }
  
        @Override
        public void demoRender() {
            Utils.drawText("§d§lBlessings",0,0);
            Utils.drawText("§dBlessing of Power XI", 0, 1 * ScreenRenderer.fontRenderer.FONT_HEIGHT);
            Utils.drawText("§dBlessing of Life XIII", 0, 2 * ScreenRenderer.fontRenderer.FONT_HEIGHT);
            Utils.drawText("§dBlessing of Wisdom V", 0, 3 * ScreenRenderer.fontRenderer.FONT_HEIGHT);
            Utils.drawText("§dBlessing of Stone VII", 0, 4 * ScreenRenderer.fontRenderer.FONT_HEIGHT);
        }
  
        @Override
        public boolean getToggled() {
            return Utils.inSkyblock && skyblockfeatures.config.blessingViewer;
        }
  
        @Override
        public int getHeight() {
            return ScreenRenderer.fontRenderer.FONT_HEIGHT*5;
        }
  
        @Override
        public int getWidth() {
            return ScreenRenderer.fontRenderer.getStringWidth("§dBlessing of Life XIII")+12;
        }
    }

    @SubscribeEvent
    public void onKeyInput(GuiScreenEvent.KeyboardInputEvent keyboardInputEvent) {
        GuiScreen screen = Minecraft.getMinecraft().currentScreen;
        if (!skyblockfeatures.config.quickCloseChest || !Utils.inDungeons) return;

        if (screen instanceof GuiChest){
            ContainerChest ch = (ContainerChest) ((GuiChest)screen).inventorySlots;
            if (!("Large Chest".equals(ch.getLowerChestInventory().getName()) || "Chest".equals(ch.getLowerChestInventory().getName()))) return;

            Minecraft.getMinecraft().thePlayer.closeScreen();
        }
    }

    @SubscribeEvent
    public void onMouseInput(GuiScreenEvent.MouseInputEvent.Pre mouseInputEvent) {
        GuiScreen screen = Minecraft.getMinecraft().currentScreen;
        if (!skyblockfeatures.config.quickCloseChest || !Utils.inDungeons) return;
        if (Mouse.getEventButton() == -1) return;

        if (screen instanceof GuiChest){
            ContainerChest ch = (ContainerChest) ((GuiChest)screen).inventorySlots;
            if (!("Large Chest".equals(ch.getLowerChestInventory().getName()) || "Chest".equals(ch.getLowerChestInventory().getName()))) return;

            Minecraft.getMinecraft().thePlayer.closeScreen();
        }
    }
}