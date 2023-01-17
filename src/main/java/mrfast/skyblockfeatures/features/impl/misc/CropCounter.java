package mrfast.skyblockfeatures.features.impl.misc;

import java.util.List;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.core.structure.FloatPair;
import mrfast.skyblockfeatures.core.structure.GuiElement;
import mrfast.skyblockfeatures.utils.ItemUtil;
import mrfast.skyblockfeatures.utils.Utils;
import mrfast.skyblockfeatures.utils.graphics.ScreenRenderer;

public class CropCounter {
    private static final Minecraft mc = Minecraft.getMinecraft();
    static String count = "0";
    static int oldCount = 0;
    static int cropsPerSecond = 0;
    static int ticks = 0;

    @SubscribeEvent
    public void onSeconds(TickEvent.ClientTickEvent event) {
        if(mc.thePlayer == null) return;

        if (!skyblockfeatures.config.Counter) { return; }
        if(!Utils.inSkyblock) { return; }

        ItemStack item = mc.thePlayer.getHeldItem();

        if(item == null) return;

        if(!item.getDisplayName().contains("Hoe")) return;
        ticks++;
        List<String> lore = ItemUtil.getItemLore(item);
        for (int i = 0; i < lore.size(); i++) {
            String line = lore.get(i);
            if(line.contains("Counter: ")) {
                count = line.replace("Counter: ", "");
                int counter = Integer.parseInt(count.replaceAll("[^0-9]",""));
                
                if(ticks >= 10) {
                    ticks = 0;
                    if(oldCount == 0) {
                        oldCount = counter;
                    } else {
                        cropsPerSecond = (counter-oldCount)*2;
                        oldCount = counter;
                    }
                }
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
                    for(String hoe: hoes.split(", ")) {
                        if(Utils.cleanColour(item.getDisplayName()).contains(hoe)) {
                            mc.fontRendererObj.drawStringWithShadow(ChatFormatting.RED+"Counter: "+ChatFormatting.YELLOW+count, 0, 0, 0xFFFFFF);   
                            mc.fontRendererObj.drawStringWithShadow(ChatFormatting.RED+"Crops Per Second: "+ChatFormatting.YELLOW+cropsPerSecond, 0, ScreenRenderer.fontRenderer.FONT_HEIGHT, 0xFFFFFF);   
                        }
                    }
                } catch (Exception e) {
                    //TODO: handle exception
                }
            }
        }
        @Override
        public void demoRender() {
            if(mc.thePlayer == null) return;
            mc.fontRendererObj.drawStringWithShadow(ChatFormatting.RED+"Counter: "+ChatFormatting.YELLOW+"19,302", 0, 0, 0xFFFFFF);   
            mc.fontRendererObj.drawStringWithShadow(ChatFormatting.RED+"Crops Per Second: "+ChatFormatting.YELLOW+"0", 0, ScreenRenderer.fontRenderer.FONT_HEIGHT, 0xFFFFFF);   
        }

        @Override
        public boolean getToggled() {
            return Utils.inSkyblock && skyblockfeatures.config.Counter;
        }

        @Override
        public int getHeight() {
            return ScreenRenderer.fontRenderer.FONT_HEIGHT*2;
        }

        @Override
        public int getWidth() {
            return 12 + ScreenRenderer.fontRenderer.getStringWidth(ChatFormatting.RED+"Crops Per Second: "+ChatFormatting.YELLOW+"102");
        }
    }
}
