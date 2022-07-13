package mrfast.skyblockfeatures.features.impl.bar;


import net.minecraft.client.Minecraft;
import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.core.structure.FloatPair;
import mrfast.skyblockfeatures.core.structure.GuiElement;
import mrfast.skyblockfeatures.utils.Utils;
import mrfast.skyblockfeatures.utils.graphics.ScreenRenderer;
import mrfast.skyblockfeatures.utils.graphics.SmartFontRenderer;
import java.util.ArrayList;

import mrfast.skyblockfeatures.utils.graphics.colors.CommonColors;

public class SecretDisplay {

    private static final Minecraft mc = Minecraft.getMinecraft();

    static {
        new JerryTimerGUI();
    }
    static String display = "Secrets";
    public static class JerryTimerGUI extends GuiElement {

        public JerryTimerGUI() {
            super("Secret Display", new FloatPair(0.45052084f, 0.86944443f));
            skyblockfeatures.GUIMANAGER.registerElement(this);
        }

        @Override
        public void render() {
            if(mc.thePlayer == null || !Utils.inDungeons) return;
            int secrets = ActionBarListener.secrets;
            int maxSecrets = ActionBarListener.maxSecrets;

            if (this.getToggled() && Minecraft.getMinecraft().thePlayer != null && mc.theWorld != null) {
                // Utils.drawTextWithStyle(String.valueOf(secrets) + "/"+maxSecrets+" Secrets", 0, 0, secretsColor);
                // GlStateManager.color(1, 1, 1, 1);
                ArrayList<String> text = new ArrayList<>();

                String color = "§c";

                if(secrets == maxSecrets) {
                    color = "§a";
                } else if(secrets > maxSecrets/2) {
                    color = "§e";
                } else {
                    color = "§c";
                }

                text.add("§7Secrets");

                if(secrets == -1) {
                    text.add("§7None");
                } else {
                    text.add(color+secrets+"§7/"+color+maxSecrets);
                }
                

                for (int i = 0; i < text.size(); i++) {
                    ScreenRenderer.fontRenderer.drawString(text.get(i), getWidth()-20, i * ScreenRenderer.fontRenderer.FONT_HEIGHT, CommonColors.WHITE, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.OUTLINE);
                }
            }
        }
        @Override
        public void demoRender() {
            ArrayList<String> text = new ArrayList<>();

            String color = "§c";
            
            text.add("§7Secrets");
            text.add(color+"1"+"§7/"+color+"9");

            for (int i = 0; i < text.size(); i++) {
                ScreenRenderer.fontRenderer.drawString(text.get(i), getWidth()-20, i * ScreenRenderer.fontRenderer.FONT_HEIGHT, CommonColors.WHITE, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.OUTLINE);
            }
        }

        @Override
        public boolean getToggled() {
            return skyblockfeatures.config.SecretsDisplay;
        }

        @Override
        public int getHeight() {
            return ScreenRenderer.fontRenderer.FONT_HEIGHT*2;
        }

        @Override
        public int getWidth() {
            return ScreenRenderer.fontRenderer.getStringWidth("§7Secrets");
        }
    }
}
