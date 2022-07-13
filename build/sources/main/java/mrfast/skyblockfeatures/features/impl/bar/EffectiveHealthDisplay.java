package mrfast.skyblockfeatures.features.impl.bar;

import net.minecraft.client.Minecraft;
import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.core.structure.FloatPair;
import mrfast.skyblockfeatures.core.structure.GuiElement;
import mrfast.skyblockfeatures.utils.Utils;
import mrfast.skyblockfeatures.utils.graphics.ScreenRenderer;

public class EffectiveHealthDisplay {

    private static final Minecraft mc = Minecraft.getMinecraft();

    static {
        new JerryTimerGUI();
    }

    static String display = "1234";

    public static int getSpeed() {
        return Math.round(Utils.Health * (1f+ Utils.Defence / 1000f ));
    }
    public static class JerryTimerGUI extends GuiElement {
        public JerryTimerGUI() {
            super("Effective Health Display", new FloatPair(0.6125f, 0.975f));
            skyblockfeatures.GUIMANAGER.registerElement(this);
        }

        @Override
        public void render() {
            if(mc.thePlayer == null || !Utils.inSkyblock) return;
            if (this.getToggled() && Minecraft.getMinecraft().thePlayer != null && mc.theWorld != null) {
                Utils.drawTextWithStyle(getSpeed()+"", 0, 0, 0x00AA00);
            }
        }
        @Override
        public void demoRender() {
            if(mc.thePlayer == null || !Utils.inSkyblock) return;
            Utils.drawTextWithStyle("1234", 0, 0, 0x00AA00);
        }

        @Override
        public boolean getToggled() {
            return Utils.inSkyblock && skyblockfeatures.config.EffectiveHealthDisplay;
        }

        @Override
        public int getHeight() {
            return ScreenRenderer.fontRenderer.FONT_HEIGHT;
        }

        @Override
        public int getWidth() {
            return ScreenRenderer.fontRenderer.getStringWidth(display);
        }
    }
}
