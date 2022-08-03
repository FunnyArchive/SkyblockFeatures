package mrfast.skyblockfeatures.features.impl.overlays;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mojang.realmsclient.gui.ChatFormatting;

import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.core.structure.FloatPair;
import mrfast.skyblockfeatures.core.structure.GuiElement;
import mrfast.skyblockfeatures.events.SecondPassedEvent;
import mrfast.skyblockfeatures.features.impl.handlers.AuctionData;
import mrfast.skyblockfeatures.utils.NumberUtil;
import mrfast.skyblockfeatures.utils.SBInfo;
import mrfast.skyblockfeatures.utils.Utils;
import mrfast.skyblockfeatures.utils.graphics.ScreenRenderer;
import mrfast.skyblockfeatures.utils.graphics.SmartFontRenderer;
import mrfast.skyblockfeatures.utils.graphics.colors.CommonColors;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class GemstoneMiningOverlay {
    private static final Minecraft mc = Minecraft.getMinecraft();
    static int seconds = 0;
    static boolean start = false;

    public static List<Gemstone> gemstones = new ArrayList<>();
    @SubscribeEvent
    public void onload(WorldEvent.Load event) {
        seconds = 0;
        start = false;
        gemstones.clear();
    }

    public class Gemstone {
        public Long time;
        public String item_name;
        public Integer amount;

        public Gemstone(Long t,String i,Integer a) {
            time = t;
            item_name = i;
            amount = a;
        }
    }

    @SubscribeEvent
    public void onSecond(SecondPassedEvent event) {
        if(Minecraft.getMinecraft().thePlayer != null && Minecraft.getMinecraft().theWorld != null && skyblockfeatures.config.gemstoneTracker) {
            List<Gemstone> gemstonesToRemove = new ArrayList<>();

            for(Gemstone gemstone:gemstones) {
                if((new Date()).getTime()-gemstone.time > 5*60*1000) gemstonesToRemove.add(gemstone);
            }
            for(Gemstone gemstone:gemstonesToRemove) {
                gemstones.remove(gemstone);
            }
            if(start) {
                seconds++;
            }
        }
    }
    @SubscribeEvent
    public void onDrawContainerTitle(ClientChatReceivedEvent event) {
        String message = event.message.getUnformattedText();
        if (message.contains("PRISTINE!") && skyblockfeatures.config.gemstoneTracker) {
            start = true;
            message = message.toUpperCase();
            String itemName = message.split(" ")[4]+"_"+message.split(" ")[5]+"_GEM";
            gemstones.add(new Gemstone((new Date()).getTime(), itemName,Integer.parseInt(message.replaceAll("[^0-9]", ""))));
        }
    }

    static {
        new GemstoneMiningGUI();
    }

    public static class GemstoneMiningGUI extends GuiElement {
        public GemstoneMiningGUI() {
            super("Gemstone GUI", new FloatPair(0.45052084f, 0.86944443f));
            skyblockfeatures.GUIMANAGER.registerElement(this);
        }

        @Override
        public void render() {
            try {
                if(mc.thePlayer == null || !Utils.inSkyblock || !getToggled() || !SBInfo.getInstance().getLocation().equals("crystal_hollows")) return;
                int total = 0;
                for(Gemstone gemstone:gemstones) {
                    if(AuctionData.bazaarPrices.get(gemstone.item_name) != null) {
                        total += AuctionData.bazaarPrices.get(gemstone.item_name)*gemstone.amount;
                    }
                }
                String[] lines = {
                    ChatFormatting.LIGHT_PURPLE+""+ChatFormatting.BOLD+"Gemstone Mining Info",
                    ChatFormatting.LIGHT_PURPLE+"Time Spent Mining: "+ChatFormatting.GREEN+Utils.secondsToTime(seconds),
                    ChatFormatting.LIGHT_PURPLE+"Gemstone Coins Per hour: §6"+NumberUtil.nf.format(total*12),
                    ChatFormatting.LIGHT_PURPLE+"Pristine Count: §a"+gemstones.size()
                };
                int lineCount = 0;
                for(String line:lines) {
                    ScreenRenderer.fontRenderer.drawString(line, 0, lineCount*(mc.fontRendererObj.FONT_HEIGHT),CommonColors.WHITE, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NORMAL);
                    lineCount++;
                }
            } catch (Exception e) {
                //TODO: handle exception
            }
        }
        @Override
        public void demoRender() {
            if(mc.thePlayer == null || !Utils.inSkyblock) return;
            String[] lines = {
                ChatFormatting.LIGHT_PURPLE+""+ChatFormatting.BOLD+"Gemstone Mining Info",
                ChatFormatting.LIGHT_PURPLE+"Time Spent Mining: 19m 27s",
                ChatFormatting.LIGHT_PURPLE+"Gemstone Coins Per hour: §6123,456",
                ChatFormatting.LIGHT_PURPLE+"Pristine Count: §a3"
            };
            int lineCount = 0;
            for(String line:lines) {
                ScreenRenderer.fontRenderer.drawString(line, 0, lineCount*(mc.fontRendererObj.FONT_HEIGHT),CommonColors.WHITE, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NORMAL);
                lineCount++;
            }
        }

        @Override
        public boolean getToggled() {
            return Utils.inSkyblock && skyblockfeatures.config.gemstoneTracker;
        }

        @Override
        public int getHeight() {
            return ScreenRenderer.fontRenderer.FONT_HEIGHT*4;
        }

        @Override
        public int getWidth() {
            return ScreenRenderer.fontRenderer.getStringWidth("Gemstone Coins Per hour: §6123,456");
        }
    }
}
