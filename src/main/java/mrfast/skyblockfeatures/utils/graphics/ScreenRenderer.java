package mrfast.skyblockfeatures.utils.graphics;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.item.ItemStack;
import mrfast.skyblockfeatures.utils.graphics.colors.CustomColor;

import java.awt.*;

/** ScreenRenderer
 * Extend this class whenever you want to render things on the screen
 * without context as to what they are.
 * The things rendered by this class would not be configurable without
 * them extending overlays!
 *
 * Taken from Wynntils under GNU Affero General Public License v3.0
 * https://github.com/Wynntils/Wynntils/blob/development/LICENSE
 * @author Wynntils
 */
public class ScreenRenderer {

    public static SmartFontRenderer fontRenderer = null;
    public static Minecraft mc;
    public static ScaledResolution screen = null;
    private static boolean rendering = false;
    private static float scale = 1.0f;
    private static float rotation = 0;
    private static boolean mask = false;
    private static boolean scissorTest = false;
    private static Point drawingOrigin = new Point(0, 0); public static Point drawingOrigin() { return drawingOrigin; }
    private static Point transformationOrigin = new Point(0, 0);
    public static void transformationOrigin(int x, int y) {transformationOrigin.x = x; transformationOrigin.y = y;}protected static Point transformationOrigin() {return transformationOrigin;}
    private static RenderItem itemRenderer = null;

    public static boolean isRendering() { return rendering; }
    public static float getScale() { return scale; }
    public static float getRotation() { return rotation; }
    public static boolean isMasking() { return mask; }

    /** refresh
     * Triggered by a slower loop(client tick), refresh
     * updates the screen resolution to match the window
     * size and sets the font renderer in until its ok.
     * Do not call this method from anywhere in the mod!
     */
    public static void refresh() {
        mc = Minecraft.getMinecraft();
        screen = new ScaledResolution(mc);
        if (fontRenderer == null) {
            try {
                fontRenderer = new SmartFontRenderer();
            } catch (Throwable ignored) {
            } finally {
                if (fontRenderer != null) {
                    if (mc.gameSettings.language != null) {
                        fontRenderer.setUnicodeFlag(mc.isUnicode());
                        fontRenderer.setBidiFlag(mc.getLanguageManager().isCurrentLanguageBidirectional());
                    }
                    ((IReloadableResourceManager)mc.getResourceManager()).registerReloadListener(fontRenderer);
                }
            }
        }
        if (itemRenderer == null)
            itemRenderer = Minecraft.getMinecraft().getRenderItem();
    }

    /** float drawString
     * Draws a string using the current fontRenderer
     *
     * @param text the text to render
     * @param x x(from drawingOrigin) to render at
     * @param y y(from drawingOrigin) to render at
     * @param color the starting color to render(without codes, its basically the actual text's color)
     * @param alignment the alignment around {x} and {y} to render the text about
     * @param shadow should the text have a shadow behind it
     * @return the length of the rendered text in pixels(not taking scale into account)
     */
    public float drawString(String text, float x, float y, CustomColor color, SmartFontRenderer.TextAlignment alignment, SmartFontRenderer.TextShadow shadow) {
        if (!rendering) return -1f;
        float f = fontRenderer.drawString(text, drawingOrigin.x + x, drawingOrigin.y + y, color, alignment, shadow);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        return f;
    }

    /**
     * Shorter overload for {{drawString}}
     */
    public float drawString(String text, float x, float y, CustomColor color) {
        return drawString(text, x, y, color, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NORMAL);
    }

    public float drawCenteredString(String text, float x, float y, CustomColor color) {
        return drawString(text, x, y, color, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NORMAL);
    }

    public void color(CustomColor color) {
        color.applyColor();
    }

    public void color(float r, float g, float b, float alpha) {
        GlStateManager.color(r, g, b, alpha);
    }

    public void drawItemStack(ItemStack is, int x, int y) {
        drawItemStack(is, x, y, false, "", true);
    }

    public void drawItemStack(ItemStack is, int x, int y, boolean count) {
        drawItemStack(is, x, y, count, "", true);
    }

    public void drawItemStack(ItemStack is, int x, int y, boolean count, boolean effects) {
        drawItemStack(is, x, y, count, "", effects);
    }

    public void drawItemStack(ItemStack is, int x, int y, String text) {
        drawItemStack(is, x, y, false, text, true);
    }

    public void drawItemStack(ItemStack is, int x, int y, String text, boolean effects) {
        drawItemStack(is, x, y, false, text, effects);
    }

    /**
     * drawItemStack
     * Draws an item
     *
     * @param is      the itemstack to render
     * @param x       x on screen
     * @param y       y on screen
     * @param count   show numbers
     * @param text    custom text
     * @param effects show shimmer
     */
    private void drawItemStack(ItemStack is, int x, int y, boolean count, String text, boolean effects) {
        if (!rendering) return;
        RenderHelper.enableGUIStandardItemLighting();
        itemRenderer.zLevel = 200.0F;
        net.minecraft.client.gui.FontRenderer font = is.getItem().getFontRenderer(is);
        if (font == null) font = fontRenderer;
        if (effects)
            itemRenderer.renderItemAndEffectIntoGUI(is, x + drawingOrigin.x, y + drawingOrigin.y);
        else
            itemRenderer.renderItemIntoGUI(is, x + drawingOrigin.x, y + drawingOrigin.y);
        itemRenderer.renderItemOverlayIntoGUI(font, is, x + drawingOrigin.x, y + drawingOrigin.y, text.isEmpty() ? count ? Integer.toString(is.stackSize) : null : text);
        itemRenderer.zLevel = 0.0F;
        RenderHelper.disableStandardItemLighting();
    }

    public static void setRendering(boolean status) {
        rendering = status;
    }

}