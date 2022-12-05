package mrfast.skyblockfeatures.features.impl.overlays;

import java.util.ArrayList;
import java.util.Iterator;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.utils.RenderUtil;

public class GiftCompassWaypoints {

    public Minecraft mc = Minecraft.getMinecraft();

    public static ArrayList<Entity> sessionSouls = new ArrayList<Entity>();

    @SubscribeEvent
    public void onAttack(AttackEntityEvent event) {
       if (event.target != null && event.target instanceof EntityArmorStand && ((EntityArmorStand)event.target).getCurrentArmor(3) != null && ((EntityArmorStand)event.target).getCurrentArmor(3).serializeNBT().getCompoundTag("tag").getCompoundTag("SkullOwner").getCompoundTag("Properties").toString().contains("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTBmNTM5ODUxMGIxYTA1YWZjNWIyMDFlYWQ4YmZjNTgzZTU3ZDcyMDJmNTE5M2IwYjc2MWZjYmQwYWUyIn19fQ=") && !sessionSouls.contains(event.target)) {
         sessionSouls.add(event.target);
       }
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
         if(!skyblockfeatures.config.presentWaypoints) return;

         Minecraft mc = Minecraft.getMinecraft();

         if (mc.theWorld != null) {
         Iterator<Entity> var3 = mc.theWorld.loadedEntityList.iterator();

         while(var3.hasNext()) {
               Entity entity = (Entity)var3.next();
               if (entity instanceof EntityArmorStand && ((EntityArmorStand)entity).getCurrentArmor(3) != null && ((EntityArmorStand)entity).getCurrentArmor(3).serializeNBT().getCompoundTag("tag").getCompoundTag("SkullOwner").getCompoundTag("Properties").toString().contains("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTBmNTM5ODUxMGIxYTA1YWZjNWIyMDFlYWQ4YmZjNTgzZTU3ZDcyMDJmNTE5M2IwYjc2MWZjYmQwYWUyIn19fQ=")) {
                  if (!sessionSouls.contains(entity)) {
                     highlightBlock(Color.YELLOW, entity.posX-0.5, entity.posY+1.5, entity.posZ-0.5, 1.0D,event.partialTicks);
                  } else {
                     highlightBlock(Color.GREEN, entity.posX-0.5, entity.posY+1.5, entity.posZ-0.5, 1.0D,event.partialTicks);
                  }
               }
            }
         }
      }


      public static void highlightBlock(Color c, double d, double d1, double d2, double size,float ticks) {
         GlStateManager.disableDepth();
         RenderUtil.drawOutlinedFilledBoundingBox(new AxisAlignedBB(d, d1, d2, d+size, d1+size, d2+size),c,ticks);
         GlStateManager.enableDepth();
      }

     public static void drawBoundingBox(Color c, AxisAlignedBB aa) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        int color = c.getRGB();
        float a = (float)(color >> 24 & 255) / 255.0F;
        a = (float)((double)a * 0.2D);
        float r = (float)(color >> 16 & 255) / 255.0F;
        float g = (float)(color >> 8 & 255) / 255.0F;
        float b = (float)(color & 255) / 255.0F;
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).color(r, g, b, a).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).color(r, g, b, a).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).color(r, g, b, a).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).color(r, g, b, a).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).color(r, g, b, a).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).color(r, g, b, a).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).color(r, g, b, a).endVertex();
        tessellator.draw();
     }
}
