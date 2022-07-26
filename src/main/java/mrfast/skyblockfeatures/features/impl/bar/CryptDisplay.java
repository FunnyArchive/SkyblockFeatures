package mrfast.skyblockfeatures.features.impl.bar;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.core.structure.FloatPair;
import mrfast.skyblockfeatures.core.structure.GuiElement;
import mrfast.skyblockfeatures.utils.TabListUtils;
import mrfast.skyblockfeatures.utils.Utils;
import mrfast.skyblockfeatures.utils.graphics.ScreenRenderer;

public class CryptDisplay {

    private static final Minecraft mc = Minecraft.getMinecraft();

    static {
        new JerryTimerGUI();
    }

    static String display = "Secrets";


    
    public static class JerryTimerGUI extends GuiElement {

        public JerryTimerGUI() {
            super("Crypt Display", new FloatPair(0.45052084f, 0.86944443f));
            skyblockfeatures.GUIMANAGER.registerElement(this);
        }

        private static final Pattern cryptsPattern = Pattern.compile("§r Crypts: §r§6(?<crypts>\\d+)§r");
        
        @Override
        public void render() {
            if(mc.thePlayer == null || !Utils.inDungeons) return;
            int crypts = 0;
            for (NetworkPlayerInfo pi : TabListUtils.getTabEntries()) {
                try {
                    String name = mc.ingameGUI.getTabList().getPlayerName(pi);
                    if (name.contains("Crypts:")) {
                        Matcher matcher = cryptsPattern.matcher(name);
                        if (matcher.find()) {
                            crypts = Integer.parseInt(matcher.group("crypts"));
                            continue;
                        }
                    }
                } catch (NumberFormatException ignored) {
                }
            }

            int color = 0xeb4034;

            if(crypts < 5) {
                color = 0xeb4034;
            } else {
                color = 0x55FF55;
            }
            float scale = 2f;
            if (this.getToggled() && Minecraft.getMinecraft().thePlayer != null && mc.theWorld != null) {
                GlStateManager.scale(scale, scale, 0);
                ScreenRenderer.fontRenderer.drawString("Crypts: "+crypts, 0, 0, color);
                GlStateManager.scale(1/scale, 1/scale, 0);
            }
        }
        @Override
        public void demoRender() {
            if(mc.thePlayer == null || !Utils.inSkyblock) return;

            int color = 0xeb4034;
            float scale = 2f;
            GlStateManager.scale(scale, scale, 0);
            ScreenRenderer.fontRenderer.drawString("Crypts: 2", 0, 0, color);
            GlStateManager.scale(1/scale, 1/scale, 0);
        }

        @Override
        public boolean getToggled() {
            return Utils.inSkyblock && skyblockfeatures.config.cryptCount;
        }

        @Override
        public int getHeight() {
            return ScreenRenderer.fontRenderer.FONT_HEIGHT*2;
        }

        @Override
        public int getWidth() {
            return ScreenRenderer.fontRenderer.getStringWidth("§6Estimated Secret C");
        }
    }
}
