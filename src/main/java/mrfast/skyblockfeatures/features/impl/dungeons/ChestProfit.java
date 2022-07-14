package mrfast.skyblockfeatures.features.impl.dungeons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.core.structure.FloatPair;
import mrfast.skyblockfeatures.core.structure.GuiElement;
import mrfast.skyblockfeatures.features.impl.handlers.AuctionData;
import mrfast.skyblockfeatures.utils.ItemUtil;
import mrfast.skyblockfeatures.utils.NumberUtil;
import mrfast.skyblockfeatures.utils.StringUtils;
import mrfast.skyblockfeatures.utils.Utils;
import mrfast.skyblockfeatures.utils.graphics.ScreenRenderer;
import mrfast.skyblockfeatures.utils.graphics.SmartFontRenderer;
import mrfast.skyblockfeatures.utils.graphics.colors.CommonColors;
import mrfast.skyblockfeatures.utils.graphics.colors.CustomColor;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Objects;

import com.mojang.realmsclient.gui.ChatFormatting;

/**
 * Based off of chest profit from code by Quantizr
 * Licensed under GNU GPL v3, with permission given from author
 * @author Quantizr
 */
public class ChestProfit {

    private static final DungeonChestProfitElement element = new DungeonChestProfitElement();

    @SubscribeEvent
    public void onGUIDrawnEvent(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (!Utils.inDungeons) return;
        if (!skyblockfeatures.config.dungeonChestProfit) return;
        if (event.gui instanceof GuiChest) {
            ContainerChest chest = (ContainerChest) ((GuiChest) event.gui).inventorySlots;
            IInventory inv = chest.getLowerChestInventory();
            if (inv.getDisplayName().getUnformattedText().endsWith(" Chest")) {
                DungeonChest chestType = DungeonChest.getFromName(inv.getDisplayName().getUnformattedText());
                if (chestType != null) {
                    ItemStack openChest = inv.getStackInSlot(31);
                    if (openChest != null && openChest.getDisplayName().equals("§aOpen Reward Chest")) {

                        for (String unclean : ItemUtil.getItemLore(openChest)) {
                            String line = StringUtils.stripControlCodes(unclean);
                            if (line.contains("FREE")) {
                                chestType.price = 0;
                                break;
                            } else if (line.contains(" Coins")) {
                                chestType.price = Double.parseDouble(line.substring(0, line.indexOf(" ")).replaceAll(",", ""));
                                break;
                            }
                        }

                        chestType.value = 0;
                        chestType.items.clear();
                        for (int i = 11; i < 16; i++) {
                            ItemStack lootSlot = inv.getStackInSlot(i);
                            String identifier = AuctionData.getIdentifier(lootSlot);
                            if (identifier != null) {
                                Double value = AuctionData.lowestBINs.get(identifier);
                                if (value == null) value = 0D;
                                chestType.value += value;
                                chestType.items.add(new DungeonChestLootItem(lootSlot, identifier, value));
                            }
                        }
                    }
                    if (chestType.items.size() > 0) {
                        ArrayList<String> lines = new ArrayList<>();
                        GlStateManager.color(1, 1, 1, 1);
                        GlStateManager.disableLighting();
                        double profit = chestType.value - chestType.price;
                        for (DungeonChestLootItem item : chestType.items) {
                            lines.add(item.item.getDisplayName() + "§f: §a" + NumberUtil.nf.format(item.value));
                        }
                        lines.add("");
                        lines.add("Profit: §f:" + (profit > 0 ? "a" : "c")+NumberUtil.nf.format(profit));
                        
                        int lineCount = 0;
                        for(String line:lines) {
                            Utils.GetMC().fontRendererObj.drawString(line, 190, lineCount*(Utils.GetMC().fontRendererObj.FONT_HEIGHT+1)+10, -1);
                            lineCount++;
                        }
                        Utils.drawGraySquareWithBorder(180, 0, 150, (lineCount+1)*Utils.GetMC().fontRendererObj.FONT_HEIGHT,3);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onWorldChange(WorldEvent.Load event) {
        for (DungeonChest chest : DungeonChest.values()) {
            chest.reset();
        }
    }

    private enum DungeonChest {
        WOOD("Wood Chest", CommonColors.BROWN),
        GOLD("Gold Chest", CommonColors.YELLOW),
        DIAMOND("Diamond Chest", CommonColors.LIGHT_BLUE),
        EMERALD("Emerald Chest", CommonColors.LIGHT_GREEN),
        OBSIDIAN("Obsidian Chest", CommonColors.BLACK),
        BEDROCK("Bedrock Chest", CommonColors.LIGHT_GRAY);

        public String displayText;
        public CustomColor displayColor;
        public double price;
        public double value;
        public ArrayList<DungeonChestLootItem> items = new ArrayList<>();

        DungeonChest(String displayText, CustomColor color) {
            this.displayText = displayText;
            this.displayColor = color;
        }

        public void reset() {
            this.price = 0;
            this.value = 0;
            this.items.clear();
        }

        public static DungeonChest getFromName(String name) {
            for (DungeonChest chest : values()) {
                if (Objects.equals(chest.displayText, name)) {
                    return chest;
                }
            }
            return null;
        }

    }

    private static class DungeonChestLootItem {

        public ItemStack item;
        public double value;

        public DungeonChestLootItem(ItemStack item, String itemId, double value) {
            this.item = item;
            this.value = value;
        }
    }

    public static class DungeonChestProfitElement extends GuiElement {

        public DungeonChestProfitElement() {
            super("Dungeon Chest Profit", new FloatPair(200, 120));
            skyblockfeatures.GUIMANAGER.registerElement(this);
        }

        @Override
        public void render() {
            if (this.getToggled() && Utils.inDungeons) {

                ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());

                boolean leftAlign = getActualX() < sr.getScaledWidth() / 2f;

                GlStateManager.color(1, 1, 1, 1);
                GlStateManager.disableLighting();

                int drawnLines = 0;
                for (DungeonChest chest : DungeonChest.values()) {
                    if (chest.items.size() == 0) continue;
                    // Utils.drawGradientRect(0, 0, getWidth(), getHeight(), new Color(0,0, 0,100).getRGB(), new Color(0,0, 0,100).getRGB());
                    double profit = chest.value - chest.price;
                    String line = chest.displayText + "§f: §" + (profit > 0 ? "a" : "c") + NumberUtil.format((long) profit);
                    SmartFontRenderer.TextAlignment alignment = leftAlign ? SmartFontRenderer.TextAlignment.LEFT_RIGHT : SmartFontRenderer.TextAlignment.RIGHT_LEFT;
                    ScreenRenderer.fontRenderer.drawString(line, leftAlign ? 0 : getWidth(), drawnLines * ScreenRenderer.fontRenderer.FONT_HEIGHT, chest.displayColor, alignment, SmartFontRenderer.TextShadow.NORMAL);
                    drawnLines++;
                }
            }
        }

        @Override
        public void demoRender() {
            ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());

            boolean leftAlign = getActualX() < sr.getScaledWidth() / 2f;

            GlStateManager.color(1, 1, 1, 1);
            GlStateManager.disableLighting();

            int drawnLines = 0;
            for (int i = 0; i < DungeonChest.values().length; i++) {
                DungeonChest chest = DungeonChest.values()[i];
                String line = chest.displayText + ": §a+300M";
                SmartFontRenderer.TextAlignment alignment = leftAlign ? SmartFontRenderer.TextAlignment.LEFT_RIGHT : SmartFontRenderer.TextAlignment.RIGHT_LEFT;
                ScreenRenderer.fontRenderer.drawString(line, leftAlign ? 0 : getWidth(), drawnLines * ScreenRenderer.fontRenderer.FONT_HEIGHT, chest.displayColor, alignment, SmartFontRenderer.TextShadow.NORMAL);
                drawnLines++;
            }
        }

        @Override
        public boolean getToggled() {
            return skyblockfeatures.config.dungeonChestProfit;
        }

        @Override
        public int getHeight() {
            return ScreenRenderer.fontRenderer.FONT_HEIGHT * DungeonChest.values().length;
        }

        @Override
        public int getWidth() {
            return ScreenRenderer.fontRenderer.getStringWidth("Obsidian Chest: 300M");
        }
    }

}
