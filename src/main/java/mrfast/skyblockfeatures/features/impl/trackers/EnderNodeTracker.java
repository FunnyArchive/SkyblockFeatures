package mrfast.skyblockfeatures.features.impl.trackers;

import com.mojang.realmsclient.gui.ChatFormatting;

import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.core.structure.FloatPair;
import mrfast.skyblockfeatures.core.structure.GuiElement;
import mrfast.skyblockfeatures.events.SecondPassedEvent;
import mrfast.skyblockfeatures.utils.NumberUtil;
import mrfast.skyblockfeatures.utils.Utils;
import mrfast.skyblockfeatures.utils.graphics.ScreenRenderer;
import mrfast.skyblockfeatures.utils.graphics.SmartFontRenderer;
import mrfast.skyblockfeatures.utils.graphics.colors.CommonColors;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EnderNodeTracker {
    private static final Minecraft mc = Minecraft.getMinecraft();

    static int enderNodesMined = 0;
    static int nests = 0;
    static int eendstone = 0;
    static int eobsidian = 0;
    static int grand = 0;
    static int titanic = 0;

    static boolean hidden = true;
    static int seconds = 0;
    static int totalSeconds = 0;
    static double coinsPerHour = 0;
    @SubscribeEvent
    public void onload(WorldEvent.Load event) {
        try {
            seconds = 0;
            hidden = true;
        } catch(Exception e) {

        }
    }

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        String raw = event.message.getUnformattedText();
        if(raw.contains("ENDER NODE")) {
            seconds = 300;
            hidden = false;
            if(raw.contains("Endermite Nest")) nests++;
            if(raw.contains("Enchanted End Stone")) eendstone++;
            if(raw.contains("Enchanted Obsidian")) eobsidian++;

            if(raw.contains("5x Grand Experience Bottle")) grand+=5;
            else if(raw.contains("Grand Experience Bottle")) grand++;

            if(raw.contains("Grand Titanic Bottle")) titanic++;
            enderNodesMined++;
        }
    }

    @SubscribeEvent
    public void onSecond(SecondPassedEvent event) {
        if(Minecraft.getMinecraft().thePlayer != null && Minecraft.getMinecraft().theWorld != null && skyblockfeatures.config.EnderNodeTracker) {
            if(!hidden) {
                seconds--;
            }
            if(seconds <= 0) {
                hidden = true;
            } else {
                totalSeconds++;
            }
        }
    }

    static {
        new EnderNodeGui();
    }

    static String display = "";
    public static class EnderNodeGui extends GuiElement {
        public EnderNodeGui() {
            super("Ender Node Tracker", new FloatPair(0.004166f, 0.41111112f));
            skyblockfeatures.GUIMANAGER.registerElement(this);
        }

        @Override
        public void render() {
            if (this.getToggled() && Minecraft.getMinecraft().thePlayer != null && mc.theWorld != null && !hidden) {
                String[] lines = {
                    ChatFormatting.LIGHT_PURPLE+"Time Elapsed: §r"+Utils.secondsToTime(totalSeconds),
                    ChatFormatting.AQUA+"Nodes Mined: §r"+NumberUtil.nf.format(enderNodesMined),
                    ChatFormatting.RED+"Endermite Nest: §r"+nests,
                    ChatFormatting.BLUE+"Titanic Exp: §r"+titanic,
                    ChatFormatting.GREEN+"Grand Exp: §r"+grand,
                    ChatFormatting.GREEN+"Ench. Endestone: §r"+eendstone,
                    ChatFormatting.GREEN+"Ench. Obsidian: §r"+eobsidian
                };
                int lineCount = 0;
                for(String line:lines) {
                    ScreenRenderer.fontRenderer.drawString(line, 0, lineCount*(mc.fontRendererObj.FONT_HEIGHT),CommonColors.WHITE, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NORMAL);
                    lineCount++;
                }
            }
        }
        @Override
        public void demoRender() {
            if(mc.thePlayer == null || !Utils.inSkyblock) return;
            String[] lines = {
                ChatFormatting.LIGHT_PURPLE+"Time Elapsed: §r"+Utils.secondsToTime(304),
                ChatFormatting.AQUA+"Nodes Mined: §r107",
                ChatFormatting.RED+"Endermite Nest: §r3",
                ChatFormatting.BLUE+"Titanic Exp: §r2",
                ChatFormatting.GREEN+"Grand Exp: §r9",
                ChatFormatting.GREEN+"Ench. Endestone: §r4",
                ChatFormatting.GREEN+"Ench. Obsidian: §r2"
            };
            int lineCount = 0;
            for(String line:lines) {
                ScreenRenderer.fontRenderer.drawString(line, 0, lineCount*(mc.fontRendererObj.FONT_HEIGHT),CommonColors.WHITE, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NORMAL);
                lineCount++;
            }
        }

        @Override
        public boolean getToggled() {
            return Utils.inSkyblock && skyblockfeatures.config.EnderNodeTracker;
        }

        @Override
        public int getHeight() {
            return ScreenRenderer.fontRenderer.FONT_HEIGHT*7;
        }

        @Override
        public int getWidth() {
            return ScreenRenderer.fontRenderer.getStringWidth("Endermite Nest: §r2910");
        }
    }
}
