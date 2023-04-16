package mrfast.skyblockfeatures.features.impl.misc;

import java.awt.Color;

import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.events.BlockChangeEvent;
import mrfast.skyblockfeatures.events.SecondPassedEvent;
import mrfast.skyblockfeatures.utils.Utils;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLog;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;

public class TreecapCooldown {
    public static int seconds = 2;
    public static  String display = EnumChatFormatting.BLUE + "Treecapitator: " + EnumChatFormatting.GREEN + "Ready!";
    private static final Minecraft mc = Minecraft.getMinecraft();
    RenderManager renderManager = mc.getRenderManager();
    
    @SubscribeEvent
    public void onPlayerInteractEvent(BlockChangeEvent event) {
        if(!Utils.inSkyblock || mc.thePlayer.getHeldItem() == null || !skyblockfeatures.config.treecapitatorCooldown) return; 

        if(mc.thePlayer.getHeldItem().getDisplayName().toLowerCase().contains("treecapitator") && event.update.getBlock() instanceof BlockAir && event.old.getBlock() instanceof BlockLog && Utils.GetMC().thePlayer.getDistanceSq(event.pos) < 10) {
            if(ready) {
                seconds = 2;
                ready = false;
            }
        }
    }

    @SubscribeEvent
	public void onTick(RenderTickEvent event) {
		if(!Utils.inSkyblock || !skyblockfeatures.config.treecapitatorCooldown || Minecraft.getMinecraft().currentScreen instanceof GuiScreen) return;
		
		ItemStack item = Minecraft.getMinecraft().thePlayer.getHeldItem();
		
		if (item == null) return;
		
		if (item.getDisplayName().contains("Treecapitator")) {
			
			if (!ready) {
    			ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
    			int guiLeft = (sr.getScaledWidth() - 176) / 2;
    			int guiTop = (sr.getScaledHeight() - 222) / 2;
    			
                float x = guiLeft + 82.5f;
    			int y = guiTop + (int) 120; 

                if(seconds <= 10) x = guiLeft + 85;
    			
    			Minecraft.getMinecraft().fontRendererObj.drawString(seconds+"", x, y, new Color(255, 85, 85).getRGB(), true);
			} else {
    			ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
    			int guiLeft = (sr.getScaledWidth() - 176) / 2;
    			int guiTop = (sr.getScaledHeight() - 222) / 2;
    			
    			int x = guiLeft + 85;
    			int y = guiTop + (int) 120;
    			
    			Minecraft.getMinecraft().fontRendererObj.drawString("✔", x, y, new Color(85, 255, 85).getRGB(), true);
			}
		}
	}

    static boolean ready = false;
    @SubscribeEvent
    public void onSeconds(SecondPassedEvent event) {
        if(!Utils.inSkyblock) { return; }
        if (seconds < 3 && seconds > 0) {
            seconds--;
        }
        if (seconds == 0) {
            ready = true;
            return;
        }
    }

}
