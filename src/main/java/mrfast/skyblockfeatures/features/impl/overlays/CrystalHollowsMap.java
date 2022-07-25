package mrfast.skyblockfeatures.features.impl.overlays;

import org.lwjgl.opengl.GL11;

import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.core.structure.FloatPair;
import mrfast.skyblockfeatures.core.structure.GuiElement;
import mrfast.skyblockfeatures.utils.SBInfo;
import mrfast.skyblockfeatures.utils.Utils;
import mrfast.skyblockfeatures.utils.graphics.ScreenRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CrystalHollowsMap {
    public static final ResourceLocation map = new ResourceLocation("skyblockfeatures","CrystalHollowsMap.png");
    public static final ResourceLocation playerIcon = new ResourceLocation("skyblockfeatures","mapIcon.png");

    public static void drawMap() {
        if(Utils.GetMC().thePlayer == null || !Utils.inSkyblock || !SBInfo.getInstance().getLocation().equals("crystal_hollows")) return;
        Utils.GetMC().getTextureManager().bindTexture(map);
        GlStateManager.color(1, 1, 1, 1);
        Utils.drawTexturedRect(0, 0, 512/4,512/4, 0, 1, 0, 1, GL11.GL_NEAREST);
        drawPlayer();
    }

    public static void drawPlayer() {
        Utils.GetMC().getTextureManager().bindTexture(playerIcon);
        EntityPlayerSP player = Utils.GetMC().thePlayer;
        double x = Math.round((player.posX-202)/4.8);
        double z = Math.round((player.posZ-202)/4.8);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, z, 0);
        GlStateManager.rotate(player.rotationYawHead-180, 0, 0, 1);
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
            if (Minecraft.getMinecraft().thePlayer != null && Minecraft.getMinecraft().theWorld != null && this.getToggled()) {
                drawMap();
            }
        }
        @Override
        public void demoRender() {
            drawMap();
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
