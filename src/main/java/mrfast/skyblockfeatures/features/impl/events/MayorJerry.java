package mrfast.skyblockfeatures.features.impl.events;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.core.GuiManager;
import mrfast.skyblockfeatures.utils.StringUtils;
import mrfast.skyblockfeatures.utils.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mojang.realmsclient.gui.ChatFormatting;

public class MayorJerry {

    private static final Pattern jerryType = Pattern.compile("(\\w+)(?=\\s+Jerry)");

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onChat(ClientChatReceivedEvent event) {
        if (!Utils.inSkyblock) return;
        String unformatted = StringUtils.stripControlCodes(event.message.getUnformattedText());
        if (skyblockfeatures.config.hiddenJerryAlert && unformatted.contains("☺") && unformatted.contains("Jerry") && !unformatted.contains("Jerry Box")) {
            Matcher matcher = jerryType.matcher(event.message.getFormattedText());
            if (matcher.find()) {
                String color = matcher.group(1);
                GuiManager.createTitle("§" + color.toUpperCase() + " JERRY!", 60);
            }
        }
    }

    @SubscribeEvent
    public void onRender3D(RenderWorldLastEvent event) {
        if(!skyblockfeatures.config.hiddenJerryAlert) return;

        for(Entity player : Utils.GetMC().theWorld.loadedEntityList) {
            if(player.hasCustomName()) {
            if(player.getCustomNameTag().contains("Jerry")) {
                double x = interpolate(player.lastTickPosX, player.posX, event.partialTicks) - Utils.GetMC().getRenderManager().viewerPosX;
                double y = interpolate(player.lastTickPosY, player.posY, event.partialTicks) - Utils.GetMC().getRenderManager().viewerPosY;
                double z = interpolate(player.lastTickPosZ, player.posZ, event.partialTicks) - Utils.GetMC().getRenderManager().viewerPosZ;
                // renderNameTag(player, ChatFormatting.GREEN+player.getName(), x , y, z, event.partialTicks);
                renderNameTag(player, ChatFormatting.GREEN+"Jerry", x , y, z, event.partialTicks);
            }
          }
        }
    }

    private double interpolate(double previous, double current, float delta) {
        return (previous + (current - previous) * delta);
    }

    private void renderNameTag(Entity player, String a, double x, double y, double z, float delta) {
        int width = Utils.GetMC().fontRendererObj.getStringWidth(a) / 2;

        float f = 1.6F;
		float f1 = 0.016666668F * f;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y+0.8, z);
        GlStateManager.rotate(-Utils.GetMC().getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(Utils.GetMC().getRenderManager().playerViewX, Utils.GetMC().gameSettings.thirdPersonView == 2 ? -1.0F : 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-f1, -f1, -f1);
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();

        Utils.GetMC().fontRendererObj.drawStringWithShadow(a, -width, -(Utils.GetMC().fontRendererObj.FONT_HEIGHT - 1), 0x7FFF00);

        GlStateManager.enableDepth();
        GlStateManager.popMatrix();
    }

}
