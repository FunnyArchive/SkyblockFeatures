package mrfast.skyblockfeatures.features.impl.misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.JsonObject;
import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.core.AuctionUtil;
import mrfast.skyblockfeatures.core.structure.FloatPair;
import mrfast.skyblockfeatures.core.structure.GuiElement;
import mrfast.skyblockfeatures.events.SecondPassedEvent;
import mrfast.skyblockfeatures.features.impl.handlers.AuctionData;
import mrfast.skyblockfeatures.utils.ItemUtil;
import mrfast.skyblockfeatures.utils.Utils;
import mrfast.skyblockfeatures.utils.graphics.ScreenRenderer;
import mrfast.skyblockfeatures.utils.graphics.SmartFontRenderer;
import mrfast.skyblockfeatures.utils.graphics.colors.CommonColors;

public class CropCounter {
    private static final Minecraft mc = Minecraft.getMinecraft();
    static String count = "0";
    public static JsonObject auctionPricesAvgLowestBinJson = null;

    @SubscribeEvent
    public void onSeconds(SecondPassedEvent event) {
        if(mc.thePlayer == null) return;
        
        if (!skyblockfeatures.config.Counter) { return; }
        if(!Utils.inSkyblock) { return; }

        ItemStack item = mc.thePlayer.getHeldItem();

        if(item == null) return;

        if(!item.getDisplayName().contains("Hoe")) return;
        
        List<String> lore = ItemUtil.getItemLore(item);
        for (int i = 0; i < lore.size(); i++) {
            String line = lore.get(i);
            if(line.contains("Counter: ")) {
                count = line.replace("Counter: ", "");
            }
        }
    }
    
    static {
        new CropCounterGUI();
    }   
    public static class CropCounterGUI extends GuiElement {
        public CropCounterGUI() {
            super("CropCounter", new FloatPair(0, 5));
            skyblockfeatures.GUIMANAGER.registerElement(this);
        }

        @Override
        public void render() {
            if(mc.thePlayer == null) return;
            if (this.getToggled() && Minecraft.getMinecraft().thePlayer != null && mc.theWorld != null) {
                try {
                    ItemStack item = mc.thePlayer.getHeldItem();
                    if(item == null) return;
                    String hoes = "Euclides, Gauss, Pythagorean, Turing, Newton";
                    if(hoes.contains(Utils.cleanColour(item.getDisplayName()).split(" ")[0])) mc.fontRendererObj.drawString(ChatFormatting.RED+"Counter: "+ChatFormatting.YELLOW+ count, 0, 0, 0xFFFFFF, true);   
                } catch (Exception e) {
                    //TODO: handle exception
                }
            }
        }
        @Override
        public void demoRender() {
            if(mc.thePlayer == null) return;
            ScreenRenderer.fontRenderer.drawString(ChatFormatting.RED+"Counter: "+ChatFormatting.YELLOW+ count, 0, 0, CommonColors.WHITE, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NORMAL);
        }

        @Override
        public boolean getToggled() {
            return Utils.inSkyblock && skyblockfeatures.config.Counter;
        }

        @Override
        public int getHeight() {
            return ScreenRenderer.fontRenderer.FONT_HEIGHT;
        }

        @Override
        public int getWidth() {
            return 12 + ScreenRenderer.fontRenderer.getStringWidth(ChatFormatting.RED+"Counter: "+ChatFormatting.YELLOW+ count);
        }
    }
}
