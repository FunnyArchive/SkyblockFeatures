package mrfast.skyblockfeatures.features.impl.overlays;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.mojang.realmsclient.gui.ChatFormatting;

import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.core.structure.FloatPair;
import mrfast.skyblockfeatures.core.structure.GuiElement;
import mrfast.skyblockfeatures.utils.SBInfo;
import mrfast.skyblockfeatures.utils.Utils;
import mrfast.skyblockfeatures.utils.graphics.ScreenRenderer;
import net.minecraft.client.Minecraft;

public class MiscOverlays {
    public static Minecraft mc = Utils.GetMC();
    static {
        new timeOverlay();
        new dayCounter();
    }   
    public static String getTime() {
        return new SimpleDateFormat("hh:mm:ss").format(new Date());
    }
    public static class timeOverlay extends GuiElement {
        public timeOverlay() {
            super("timeOverlay", new FloatPair(0.6125f, 0.975f));
            skyblockfeatures.GUIMANAGER.registerElement(this);
        }

        @Override
        public void render() {
            if(mc.thePlayer == null || !Utils.inSkyblock) return;
            if (this.getToggled() && Minecraft.getMinecraft().thePlayer != null && mc.theWorld != null) {
                Utils.drawTextWithStyle3("["+getTime()+"]", 0, 0);
            }
        }

        @Override
        public void demoRender() {
            if(mc.thePlayer == null || !Utils.inSkyblock) return;
            Utils.drawTextWithStyle3("["+getTime()+"]", 0, 0);
        }

        @Override
        public boolean getToggled() {
            return skyblockfeatures.config.clock;
        }

        @Override
        public int getHeight() {
            return ScreenRenderer.fontRenderer.FONT_HEIGHT;
        }

        @Override
        public int getWidth() {
            return ScreenRenderer.fontRenderer.getStringWidth("["+getTime()+"]");
        }
    }

    public static class dayCounter extends GuiElement {
        public dayCounter() {
            super("dayCounter", new FloatPair(0.6125f, 0.675f));
            skyblockfeatures.GUIMANAGER.registerElement(this);
        }

        @Override
        public void render() {
            if(mc.thePlayer == null || !Utils.inSkyblock || Utils.GetMC().theWorld==null || SBInfo.getInstance().getLocation()==null) return;
            if (SBInfo.getInstance().getLocation().equals("crystal_hollows") && skyblockfeatures.config.dayTracker) {
                Long time = Utils.GetMC().theWorld.getWorldTime();
                Double timeDouble = time.doubleValue()/20/60/20;
                Double day = (Math.round(timeDouble*100.0))/100.0;
                Utils.drawTextWithStyle3(ChatFormatting.GREEN+"Day "+day, 0, 0);
            }
        }

        @Override
        public void demoRender() {
            if(mc.thePlayer == null || !Utils.inSkyblock) return;
            Utils.drawTextWithStyle3(ChatFormatting.GREEN+"Day 2.12", 0, 0);
        }

        @Override
        public boolean getToggled() {
            return skyblockfeatures.config.dayTracker;
        }

        @Override
        public int getHeight() {
            return ScreenRenderer.fontRenderer.FONT_HEIGHT;
        }

        @Override
        public int getWidth() {
            return ScreenRenderer.fontRenderer.getStringWidth("["+getTime()+"]");
        }
    }
}
