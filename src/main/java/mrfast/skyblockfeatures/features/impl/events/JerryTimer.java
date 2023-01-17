package mrfast.skyblockfeatures.features.impl.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.core.structure.FloatPair;
import mrfast.skyblockfeatures.core.structure.GuiElement;
import mrfast.skyblockfeatures.events.SecondPassedEvent;
import mrfast.skyblockfeatures.utils.Utils;
import mrfast.skyblockfeatures.utils.graphics.ScreenRenderer;

public class JerryTimer {
    public static int seconds = 360;
    public static  String display = EnumChatFormatting.LIGHT_PURPLE + "Jerry: " + "6:00";
    private static final Minecraft mc = Minecraft.getMinecraft();
    RenderManager renderManager = mc.getRenderManager();
    
    @SubscribeEvent
    public void onSeconds(SecondPassedEvent event) {
        if (!skyblockfeatures.config.jerry) { return; }
        if(!Utils.inSkyblock) { return; }
        if (seconds < 361 && seconds > 0) {
            seconds--;
        }
        if (seconds == 0) {
            display = EnumChatFormatting.LIGHT_PURPLE + "Jerry: " + EnumChatFormatting.GREEN + "Ready!";
            return;
        }
        int secondsDisplay = seconds % 60;
        if (("" + seconds % 60).length() == 1) {
            display = EnumChatFormatting.LIGHT_PURPLE + "Jerry: " + EnumChatFormatting.RED + seconds / 60 + ":0" + secondsDisplay;
        } else {
            display = EnumChatFormatting.LIGHT_PURPLE + "Jerry: " + EnumChatFormatting.RED + seconds / 60 + ":" + secondsDisplay;
        }
    }
    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        if (!skyblockfeatures.config.jerry) { return; }
        String unformatted = event.message.getUnformattedText();
        if (unformatted.contains("☺") && unformatted.contains("Jerry") && !unformatted.contains("Jerry Box")) {
            seconds = 359;
        }
    }
    static {
        new JerryTimerGUI();
    }   
    public static class JerryTimerGUI extends GuiElement {
        public JerryTimerGUI() {
            super("Jerry Timer", new FloatPair(0, 5));
            skyblockfeatures.GUIMANAGER.registerElement(this);
        }

        @Override
        public void render() {
            if(mc.thePlayer == null || !Utils.inSkyblock) return;
            if (this.getToggled() && Minecraft.getMinecraft().thePlayer != null && mc.theWorld != null) {
                mc.fontRendererObj.drawString(display, 0, 0, 0xFFFFFF, true);
            }
        }
        @Override
        public void demoRender() {
            if(mc.thePlayer == null || !Utils.inSkyblock) return;
            mc.fontRendererObj.drawString(display, 0, 0, 0xFFFFFF, true);
        }

        @Override
        public boolean getToggled() {
            return Utils.inSkyblock && !Utils.inDungeons && skyblockfeatures.config.jerry;
        }

        @Override
        public int getHeight() {
            return ScreenRenderer.fontRenderer.FONT_HEIGHT;
        }

        @Override
        public int getWidth() {
            return 12 + ScreenRenderer.fontRenderer.getStringWidth(display);
        }
    }
}
