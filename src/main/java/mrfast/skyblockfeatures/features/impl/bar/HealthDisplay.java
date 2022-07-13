package mrfast.skyblockfeatures.features.impl.bar;

import net.minecraft.client.Minecraft;
import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.core.structure.FloatPair;
import mrfast.skyblockfeatures.core.structure.GuiElement;
import mrfast.skyblockfeatures.utils.Utils;
import mrfast.skyblockfeatures.utils.graphics.ScreenRenderer;

public class HealthDisplay {

    private static final Minecraft mc = Minecraft.getMinecraft();

    static {
        new JerryTimerGUI();
    }

    static String display = Utils.Health+"/"+Utils.maxHealth;
    public static class JerryTimerGUI extends GuiElement {
        public JerryTimerGUI() {
            super("Health Display", new FloatPair(0.40520838f, 0.9134259f));
            skyblockfeatures.GUIMANAGER.registerElement(this);
        }

        @Override
        public void render() {
            if(mc.thePlayer == null || !Utils.inSkyblock) return;
            display = Utils.Health+"/"+Utils.maxHealth;
            if (this.getToggled() && Minecraft.getMinecraft().thePlayer != null && mc.theWorld != null) {
                Utils.drawTextWithStyle(display, 0, 0, 0xFF5555);
            }
        }
        @Override
        public void demoRender() {
            if(mc.thePlayer == null || !Utils.inSkyblock) return;
            display = Utils.Health+"/"+Utils.maxHealth;
            Utils.drawTextWithStyle(display, 0, 0, 0xFF5555);
        }

        @Override
        public boolean getToggled() {
            return Utils.inSkyblock && skyblockfeatures.config.HealthDisplay;
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
