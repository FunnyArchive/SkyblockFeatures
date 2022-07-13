package mrfast.skyblockfeatures.features.impl.bar;

import net.minecraft.client.Minecraft;
import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.core.structure.FloatPair;
import mrfast.skyblockfeatures.core.structure.GuiElement;
import mrfast.skyblockfeatures.utils.Utils;
import mrfast.skyblockfeatures.utils.graphics.ScreenRenderer;

public class SpeedDisplay {

    private static final Minecraft mc = Minecraft.getMinecraft();

    static {
        new JerryTimerGUI();
    }

    static String display = "123%";

    public static String getSpeed() {
        String text = "";
        String walkSpeed = String.valueOf(Minecraft.getMinecraft().thePlayer.capabilities.getWalkSpeed() * 1000);
        text = walkSpeed.substring(0, Math.min(walkSpeed.length(), 3));
        if (text.endsWith(".")) text = text.substring(0, text.indexOf('.')); //remove trailing periods
        text += "%";
        return text;
    }
    public static class JerryTimerGUI extends GuiElement {
        public JerryTimerGUI() {
            super("Speed Display", new FloatPair(0.375f, 0.975f));
            skyblockfeatures.GUIMANAGER.registerElement(this);
        }

        @Override
        public void render() {
            if(mc.thePlayer == null || !Utils.inSkyblock) return;
            if (this.getToggled() && Minecraft.getMinecraft().thePlayer != null && mc.theWorld != null) {
                Utils.drawTextWithStyle(getSpeed(), 0, 0, 0xFFFFFF);
            }
        }
        @Override
        public void demoRender() {
            if(mc.thePlayer == null || !Utils.inSkyblock) return;
            Utils.drawTextWithStyle("123%", 0, 0, 0xFFFFFF);
        }

        @Override
        public boolean getToggled() {
            return Utils.inSkyblock && skyblockfeatures.config.SpeedDisplay;
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
