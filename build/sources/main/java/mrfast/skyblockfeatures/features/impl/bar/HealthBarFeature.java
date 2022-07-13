package mrfast.skyblockfeatures.features.impl.bar;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.core.structure.FloatPair;
import mrfast.skyblockfeatures.core.structure.GuiElement;
import mrfast.skyblockfeatures.utils.Utils;

public class HealthBarFeature {

	public static Minecraft mc = Minecraft.getMinecraft();

	static int barColour = 0xFFFF1111;
	

	private static final int WIDTH = 85;
	private static final int HEIGHT = 5;

	static {
        new healththing();
    }

    public static class healththing extends GuiElement {
        public healththing() {
            super("healthbar", new FloatPair(0.40625003f, 0.93194443f));
            skyblockfeatures.GUIMANAGER.registerElement(this);
        }

        @Override
        public void render() {
			if(!Utils.inSkyblock || !skyblockfeatures.config.healthbar) return;
            float currentValue = Utils.GetMC().thePlayer.getHealth();
			float maxValue = Utils.GetMC().thePlayer.getMaxHealth();
			float ratio = MathHelper.clamp_float((currentValue / (float) maxValue), 0, 1);

			GlStateManager.pushMatrix();
			GlStateManager.enableTexture2D();
			GlStateManager.enableBlend();
			GlStateManager.enableAlpha();
			mc.getTextureManager().bindTexture(Gui.icons);
			Utils.applyHex(0xFFFF1111);
			int texYCoord = 74;
			mc.ingameGUI.drawTexturedModalRect(0, 0, 0, texYCoord, WIDTH - 3, HEIGHT);
			mc.ingameGUI.drawTexturedModalRect((WIDTH - 3), 0, 179, texYCoord, 3, HEIGHT);
			int totalWidth = Math.round(WIDTH * ratio);
			int leftoverWidth = MathHelper.clamp_int(totalWidth - (WIDTH - 3), 0, 3);
			mc.ingameGUI.drawTexturedModalRect(0, 0, 0, texYCoord + 5, MathHelper.clamp_int(totalWidth, 0, WIDTH - 3), HEIGHT);
			if(leftoverWidth > 0) {
				mc.ingameGUI.drawTexturedModalRect((WIDTH - 3), 0, 179, texYCoord + 5, leftoverWidth, HEIGHT);
			}
			GlStateManager.popMatrix();
        }

        @Override
        public void demoRender() {
			float ratio = MathHelper.clamp_float((10f / (float) 10f), 0, 1);

			GlStateManager.pushMatrix();
			GlStateManager.enableTexture2D();
			GlStateManager.enableBlend();
			GlStateManager.enableAlpha();
			mc.getTextureManager().bindTexture(Gui.icons);
			Utils.applyHex(0xFFFF1111);
			int texYCoord = 74;
			mc.currentScreen.drawTexturedModalRect(0, 0, 0, texYCoord, WIDTH - 3, HEIGHT);
			mc.currentScreen.drawTexturedModalRect((WIDTH - 3), 0, 179, texYCoord, 3, HEIGHT);
			int totalWidth = Math.round(WIDTH * ratio);
			int leftoverWidth = MathHelper.clamp_int(totalWidth - (WIDTH - 3), 0, 3);
			mc.currentScreen.drawTexturedModalRect(0, 0, 0, texYCoord + 5, MathHelper.clamp_int(totalWidth, 0, WIDTH - 3), HEIGHT);
			if(leftoverWidth > 0) {
				mc.currentScreen.drawTexturedModalRect((WIDTH - 3), 0, 179, texYCoord + 5, leftoverWidth, HEIGHT);
			}
			GlStateManager.popMatrix();
        }

        @Override
        public boolean getToggled() {
            return Utils.inSkyblock && skyblockfeatures.config.healthbar;
        }

        @Override
        public int getHeight() {
            return 5;
        }

        @Override
        public int getWidth() {
            return 85;
        }
    }
}
