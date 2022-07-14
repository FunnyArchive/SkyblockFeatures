package mrfast.skyblockfeatures.utils;

import net.minecraft.util.EnumChatFormatting;

import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Taken from Skyblockcatia under MIT License
 * Modified
 * https://github.com/SteveKunG/SkyBlockcatia/blob/1.8.9/LICENSE.md
 * @author SteveKunG
 */
public enum ItemRarity {
    COMMON("COMMON", EnumChatFormatting.WHITE, new Color(255,255,255),0xFFFFFF, ColorCode.WHITE),
    UNCOMMON("UNCOMMON", EnumChatFormatting.GREEN, new Color(85,255,85),0x55FF55, ColorCode.GREEN),
    RARE("RARE", EnumChatFormatting.BLUE, new Color(85,85,255),0x5555FF, ColorCode.BLUE),
    EPIC("EPIC", EnumChatFormatting.DARK_PURPLE, new Color(190,0,190),0xAA00AA, ColorCode.DARK_PURPLE),
    LEGENDARY("LEGENDARY", EnumChatFormatting.GOLD, new Color(255,170,0),0xFFAA00, ColorCode.GOLD),
    MYTHIC("MYTHIC", EnumChatFormatting.LIGHT_PURPLE, new Color(255,85,255),0xFF55FF, ColorCode.LIGHT_PURPLE),
    SUPREME("SUPREME", EnumChatFormatting.DARK_RED, new Color(170,0,0),0xAA0000, ColorCode.DARK_RED),

    SPECIAL("SPECIAL", EnumChatFormatting.RED, new Color(255,85,85), 0xFF5555, ColorCode.RED),
    VERY_SPECIAL("VERY SPECIAL", EnumChatFormatting.RED, new Color(170,0,0), 0xFF5555, ColorCode.RED);

    private static final ItemRarity[] VALUES = Arrays.stream(values()).sorted(Comparator.comparingInt(ItemRarity::ordinal)).toArray(size -> new ItemRarity[size]);
    private final String name;
    private final EnumChatFormatting baseColor;
    private final Color color;
    private final int code;
    private final ColorCode colorCode;

    static {
        for (ItemRarity rarity : values())
        {
            VALUES[rarity.ordinal()] = rarity;
        }
    }

    ItemRarity(String name, EnumChatFormatting baseColor, Color color, int code, ColorCode colorcode) {
        this.name = name;
        this.baseColor = baseColor;
        this.color = color;
        this.code = (255 << 24) | (code & 0x00FFFFFF);;
        this.colorCode = colorcode;
    }

    public String getName() {
        return this.name;
    }

    public ColorCode getColorCode() {
        return this.colorCode;
    }

    public int getCode() {
        return this.code;
    }

    public EnumChatFormatting getBaseColor() {
        return this.baseColor;
    }

    public Color getColor() {
        return this.color;
    }

    public static ItemRarity byBaseColor(String color) {
        for (ItemRarity rarity : values())
        {
            if (rarity.baseColor.toString().equals(color))
            {
                return rarity;
            }
        }
        return null;
    }

    public ItemRarity getNextRarity() {
        return VALUES[(this.ordinal() + 1) % VALUES.length];
    }
}
