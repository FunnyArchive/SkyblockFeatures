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

package mrfast.skyblockfeatures.features.impl.dungeons;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mojang.realmsclient.gui.ChatFormatting;

import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.events.GuiContainerEvent;
import mrfast.skyblockfeatures.utils.ItemRarity;
import mrfast.skyblockfeatures.utils.ItemUtil;
import mrfast.skyblockfeatures.utils.ScoreboardUtil;
import mrfast.skyblockfeatures.utils.StringUtils;
import mrfast.skyblockfeatures.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class DungeonsFeatures {

    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final Pattern playerPattern = Pattern.compile("(?:\\[.+?] )?(\\w+)");
    public static String dungeonFloor = null;
    public static boolean hasBossSpawned = false;
    private static Entity livid = null;

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START || mc.thePlayer == null || mc.theWorld == null) return;
        if (Utils.inDungeons) {
            if (dungeonFloor == null) {
                for (String s : ScoreboardUtil.getSidebarLines()) {
                    String line = ScoreboardUtil.cleanSB(s);
                    if (line.contains("The Catacombs (")) {
                        dungeonFloor = line.substring(line.indexOf("(") + 1, line.indexOf(")"));
                        break;
                    }
                }
            }
        }
    }

    
    // Show hidden fels

    @SubscribeEvent
    public void onWorldChanges(WorldEvent.Load event) {
        count = 0;
        dungeonFloor = null;
        hasBossSpawned = false;
        bloodguy = null;
        livid = null;
    }

    String delimiter = EnumChatFormatting.AQUA.toString() + EnumChatFormatting.STRIKETHROUGH.toString() + "" + EnumChatFormatting.BOLD + "--------------------------------------";
    int count = 0;
    EntityPlayer bloodguy;
    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onChatMesaage(ClientChatReceivedEvent event) {
        if (!Utils.inDungeons || event.type == 2) return;
        String text = event.message.getUnformattedText();
        for (Map.Entry<EntityPlayer,String> entry : Nametags.players.entrySet()) {
            if(text.contains(entry.getKey().getName()) && text.contains("has obtained Blood Key!")) {
                bloodguy = entry.getKey();
            }
        }

        // if(text.contains(s))
        if(!skyblockfeatures.config.quickStart) return;
        
        if (text.equals("▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬")) {
            count++;
            if(count != 1) {
                ChatComponentText message = new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE+"[SF] "+EnumChatFormatting.GOLD + "Dungeon finished! ");
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


    public Color getColor(Entity entity) {
        EntityItem ent = (EntityItem) entity;
        ItemStack item = (ItemStack) ent.getEntityItem();
        if(ItemUtil.getRarity(item) == ItemRarity.COMMON) {
            return Color.white;
        }else if(ItemUtil.getRarity(item) == ItemRarity.UNCOMMON) {
            return Color.green;
        }else if(ItemUtil.getRarity(item) == ItemRarity.RARE) {
            return Color.blue;
        }else if(ItemUtil.getRarity(item) == ItemRarity.EPIC) {
            return Color.pink;
        }else if(ItemUtil.getRarity(item) == ItemRarity.LEGENDARY) {
            return Color.orange;
        }else if(ItemUtil.getRarity(item) == ItemRarity.MYTHIC) {
            return Color.pink;
        } else {
            return Color.white;
        }
    }

    @SubscribeEvent
    public void onDrawSlots(GuiContainerEvent.DrawSlotEvent.Pre event) {
        if (!Utils.inSkyblock) return;
        if (!(event.gui instanceof GuiChest)) return;
        GuiChest inventory = (GuiChest) event.gui;
        Container containerChest = inventory.inventorySlots;
        if (!(containerChest instanceof ContainerChest)) return;
        String displayName = ((ContainerChest) containerChest).getLowerChestInventory().getDisplayName().getUnformattedText().trim();
        if(Utils.inDungeons && ((skyblockfeatures.config.spiritLeapNames && displayName.equals("Spirit Leap")))) {
            if (event.slot.getHasStack() && bloodguy != null) {
                ItemStack stack = event.slot.getStack();
                int x = event.slot.xDisplayPosition;
                int y = event.slot.yDisplayPosition;
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
}