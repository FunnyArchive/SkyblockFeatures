package mrfast.skyblockfeatures.features.impl.trackers;

import java.util.ArrayList;
import java.util.List;

import com.mojang.realmsclient.gui.ChatFormatting;

import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.core.structure.FloatPair;
import mrfast.skyblockfeatures.core.structure.GuiElement;
import mrfast.skyblockfeatures.events.GuiContainerEvent;
import mrfast.skyblockfeatures.events.SecondPassedEvent;
import mrfast.skyblockfeatures.features.impl.handlers.AuctionData;
import mrfast.skyblockfeatures.utils.NumberUtil;
import mrfast.skyblockfeatures.utils.Utils;
import mrfast.skyblockfeatures.utils.graphics.ScreenRenderer;
import mrfast.skyblockfeatures.utils.graphics.SmartFontRenderer;
import mrfast.skyblockfeatures.utils.graphics.colors.CommonColors;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class IceTreasureTracker {
    private static final Minecraft mc = Minecraft.getMinecraft();

    static int iceTreasuresMined = 0;
    static int redGift = 0;
    static int whiteGift = 0;
    static int greenGift = 0;
    static int talisman = 0;
    static int fragment = 0;

    static double totalMoney = 0;
    static double redGiftTotal = 0;
    static double whiteGiftTotal = 0;
    static double greenGiftTotal = 0;
    static double fragmentTotal = 0;
    static double talismanTotal = 0;

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
        if(raw.contains("FROZEN TREASURE")) {
            seconds = 300;
            hidden = false;
            if(raw.contains("Green Gift")) greenGift++;
            if(raw.contains("Red Gift")) redGift++;
            if(raw.contains("White Gift")) whiteGift++;
            if(raw.contains("Fragment")) fragment++;
            if(raw.contains("Talisman")) talisman++;
            iceTreasuresMined++;
        }
    }

    @SubscribeEvent
    public void onSecond(SecondPassedEvent event) {
        if(Minecraft.getMinecraft().thePlayer != null && Minecraft.getMinecraft().theWorld != null) {
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
    List<String> items = new ArrayList<String>();
    @SubscribeEvent
    public void onDrawSlot(SecondPassedEvent event) {
        if(Utils.GetMC().thePlayer == null || !Utils.inSkyblock) return;
        try {
            double redGiftValue = Math.floor(AuctionData.bazaarPrices.get("RED_GIFT"));
            double greenGiftValue = Math.floor(AuctionData.bazaarPrices.get("GREEN_GIFT"));
            double whiteGiftValue = Math.floor(AuctionData.bazaarPrices.get("WHITE_GIFT"));
            double fragmentValue = Math.floor(AuctionData.bazaarPrices.get("GLACIAL_FRAGMENT"));
            double talismanValue = Math.floor(AuctionData.lowestBINs.get("GLACIAL_TALISMAN"));
            
            if(redGiftValue != 0) redGiftTotal = redGift*redGiftValue;
            if(greenGiftValue != 0) greenGiftTotal = greenGift*greenGiftValue;
            if(whiteGiftValue != 0) whiteGiftTotal = whiteGift*whiteGiftValue;
            if(fragmentValue != 0) fragmentTotal = fragment*fragmentValue;
            if(talismanValue != 0) talismanTotal = talisman*talismanValue;

            totalMoney = (redGiftTotal+greenGiftTotal+whiteGiftTotal+fragmentTotal+talismanTotal);

            coinsPerHour = Math.floor(Math.max(3600/totalSeconds,1)*totalMoney);
        } catch (Exception e) {
            //TODO: handle exception
        }
    }
    static {
        new IceTreasureTrackerGui();
    }

    static String display = "";
    public static class IceTreasureTrackerGui extends GuiElement {
        public IceTreasureTrackerGui() {
            super("Ice Treasure Tracker", new FloatPair(0.004166f, 0.41111112f));
            skyblockfeatures.GUIMANAGER.registerElement(this);
        }

        @Override
        public void render() {
            if (this.getToggled() && Minecraft.getMinecraft().thePlayer != null && mc.theWorld != null && !hidden && skyblockfeatures.locationString.contains("Glacial")) {
                String[] lines = {
                    ChatFormatting.GREEN+"Time Elapsed: §r"+Utils.secondsToTime(totalSeconds),
                    ChatFormatting.AQUA+"Treasures Mined: §r"+NumberUtil.nf.format(iceTreasuresMined),
                    ChatFormatting.RED+"Red Gift: §r"+redGift+" §7("+NumberUtil.nf.format(redGiftTotal)+")",
                    ChatFormatting.GREEN+"Green Gift: §r"+greenGift+" §7("+NumberUtil.nf.format(greenGiftTotal)+")",
                    ChatFormatting.WHITE+"White Gift: §r"+whiteGift+" §7("+NumberUtil.nf.format(whiteGiftTotal)+")",
                    ChatFormatting.LIGHT_PURPLE+"Fragment: §r"+fragment+" §7("+NumberUtil.nf.format(fragmentTotal)+")",
                    ChatFormatting.GOLD+"Talisman: §r"+talisman+" §7("+NumberUtil.nf.format(talismanTotal)+")",
                    ChatFormatting.BLUE+"Est. Coins/Hour: §6"+NumberUtil.nf.format(coinsPerHour),
                    ChatFormatting.WHITE+"Total Value: §6"+NumberUtil.nf.format(totalMoney)
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
                ChatFormatting.GREEN+"Time Elapsed: §r"+Utils.secondsToTime(203),
                ChatFormatting.AQUA+"Treasures Mined: §r17",
                ChatFormatting.RED+"Red Gift: §r4",
                ChatFormatting.GREEN+"Green Gift: §r1",
                ChatFormatting.WHITE+"White Gift: §r10",
                ChatFormatting.LIGHT_PURPLE+"Fragment: §r3",
                ChatFormatting.GOLD+"Talisman: §r2", 
                ChatFormatting.WHITE+"Total Value: §6100,000"
            };
            int lineCount = 0;
            for(String line:lines) {
                ScreenRenderer.fontRenderer.drawString(line, 0, lineCount*(mc.fontRendererObj.FONT_HEIGHT),CommonColors.WHITE, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NORMAL);
                lineCount++;
            }
        }

        @Override
        public boolean getToggled() {
            return Utils.inSkyblock && skyblockfeatures.config.IceTreasureTracker;
        }

        @Override
        public int getHeight() {
            return ScreenRenderer.fontRenderer.FONT_HEIGHT*7;
        }

        @Override
        public int getWidth() {
            return ScreenRenderer.fontRenderer.getStringWidth("Electron Transmitter: 10");
        }
    }
}
