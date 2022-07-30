package mrfast.skyblockfeatures.features.impl.overlays;

import org.lwjgl.opengl.GL11;

import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.core.structure.FloatPair;
import mrfast.skyblockfeatures.core.structure.GuiElement;
import mrfast.skyblockfeatures.utils.SBInfo;
import mrfast.skyblockfeatures.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class CrystalHollowsMap {
    public static final ResourceLocation map = new ResourceLocation("skyblockfeatures","CrystalHollowsMap.png");
    public static final ResourceLocation playerIcon = new ResourceLocation("skyblockfeatures","mapIcon.png");
    public static final ResourceLocation playerIcon2 = new ResourceLocation("skyblockfeatures","mapIcon2.png");

    public static void drawMap() {
        if(Utils.GetMC().thePlayer == null || Utils.GetMC().theWorld == null || !Utils.inSkyblock || !SBInfo.getInstance().getLocation().equals("crystal_hollows")) return;
        GlStateManager.enableBlend();
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.pushMatrix();
        Utils.GetMC().getTextureManager().bindTexture(map);
        Utils.drawTexturedRect(0, 0, 512/4,512/4, 0, 1, 0, 1, GL11.GL_NEAREST);
        GlStateManager.popMatrix();

        EntityPlayerSP player = Utils.GetMC().thePlayer;
        double x = Math.round((player.posX-202)/4.9);
        double z = Math.round((player.posZ-202)/4.9);

        Utils.GetMC().getTextureManager().bindTexture(playerIcon);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, z, 0);
        GlStateManager.rotate(player.rotationYawHead-180, 0, 0, 1);
        GlStateManager.translate(-x, -z, 0);
        Utils.drawTexturedRect((float)(x-2.5),(float) (z-3.5), 5, 7, 0, 1, 0, 1, GL11.GL_NEAREST);
        GlStateManager.popMatrix();
    }

    public static void drawDemoMap() {
        if(Utils.GetMC().thePlayer == null || !Utils.inSkyblock) return;
        GlStateManager.enableBlend();
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.pushMatrix();
        Utils.GetMC().getTextureManager().bindTexture(map);
        Utils.drawTexturedRect(0, 0, 512/4,512/4, 0, 1, 0, 1, GL11.GL_NEAREST);
        GlStateManager.popMatrix();

        double x = Math.round((323-202)/4.9);
        double z = Math.round((621-202)/4.9);

        Utils.GetMC().getTextureManager().bindTexture(playerIcon);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, z, 0);
        GlStateManager.rotate(-128, 0, 0, 1);
        GlStateManager.translate(-x, -z, 0);
        Utils.drawTexturedRect((float)(x-2.5),(float) (z-3.5), 5, 7, 0, 1, 0, 1, GL11.GL_NEAREST);
        GlStateManager.popMatrix();
    }
    
    static {
        new CHMap();
    }   
    public static class CHMap extends GuiElement {
        public CHMap() {
            super("CrystalHollowsMap", new FloatPair(0, 5));
            skyblockfeatures.GUIMANAGER.registerElement(this);
        }

        @Override
        public void render() {
            if (Minecraft.getMinecraft().thePlayer != null && Utils.inSkyblock && Minecraft.getMinecraft().theWorld != null && this.getToggled()) {
                drawMap();
            }
        }
        @Override
        public void demoRender() {
            drawDemoMap();
        }

        @Override
        public boolean getToggled() {
            return skyblockfeatures.config.CrystalHollowsMap && Utils.inSkyblock;
        }

        @Override
        public int getHeight() {
            return 128;
        }

        @Override
        public int getWidth() {
            return 128;
        }
    }
}