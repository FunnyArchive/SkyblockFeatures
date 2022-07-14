package mrfast.skyblockfeatures.features.impl.overlays;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;

import java.awt.Color;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

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
import mrfast.skyblockfeatures.core.DataFetcher;

public class FairySoulWaypoints {

   public Minecraft mc = Minecraft.getMinecraft();

   @SubscribeEvent
   public void onAttack(AttackEntityEvent event) {
      if (event.target != null && event.target instanceof EntityArmorStand && ((EntityArmorStand)event.target).getCurrentArmor(3) != null && ((EntityArmorStand)event.target).getCurrentArmor(3).serializeNBT().getCompoundTag("tag").getCompoundTag("SkullOwner").getCompoundTag("Properties").toString().contains("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjk2OTIzYWQyNDczMTAwMDdmNmFlNWQzMjZkODQ3YWQ1Mzg2NGNmMTZjMzU2NWExODFkYzhlNmIyMGJlMjM4NyJ9fX0=") && !soullocations.contains(event.target.getPosition().toString())) {
         soullocations.add(event.target.getPosition().toString());
         writeSave();
      }
   }
   
   public static HashSet<String> soullocations = new HashSet<>();

   public FairySoulWaypoints() {
      saveFile = new File(skyblockfeatures.modDir, "fairysouls.json");
      reloadSave();
   }

   private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
   private static File saveFile;

   public static void reloadSave() {
      soullocations.clear();
      JsonArray dataArray;
      try (FileReader in = new FileReader(saveFile)) {
         dataArray = gson.fromJson(in, JsonArray.class);
         soullocations.addAll(Arrays.asList(DataFetcher.getStringArrayFromJsonArray(dataArray)));
      } catch (Exception e) {
         dataArray = new JsonArray();
         try (FileWriter writer = new FileWriter(saveFile)) {
            gson.toJson(dataArray, writer);
         } catch (Exception ex) {
            ex.printStackTrace();
         }
      }
   }

   public static void writeSave() {
      try (FileWriter writer = new FileWriter(saveFile)) {
         JsonArray arr = new JsonArray();
         for (String itemId : soullocations) {
            arr.add(new JsonPrimitive(itemId));
         }
         gson.toJson(arr, writer);
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }

   @SubscribeEvent
   public void onRender(RenderWorldLastEvent event) {
      if(!skyblockfeatures.config.fairy) return;

      Minecraft mc = Minecraft.getMinecraft();

      if (mc.theWorld != null) {
         Iterator<Entity> var3 = mc.theWorld.loadedEntityList.iterator();

         while(var3.hasNext()) {
            Entity entity = (Entity)var3.next();
            if (entity instanceof EntityArmorStand && ((EntityArmorStand)entity).getCurrentArmor(3) != null && ((EntityArmorStand)entity).getCurrentArmor(3).serializeNBT().getCompoundTag("tag").getCompoundTag("SkullOwner").getCompoundTag("Properties").toString().contains("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjk2OTIzYWQyNDczMTAwMDdmNmFlNWQzMjZkODQ3YWQ1Mzg2NGNmMTZjMzU2NWExODFkYzhlNmIyMGJlMjM4NyJ9fX0=")) {
               if (!soullocations.contains(entity.getPosition().toString())) {
                  drawParticleESP(new Color(255,85,255), entity.posX - Minecraft.getMinecraft().getRenderManager().viewerPosX - 0.5D, 1.5D + entity.posY - Minecraft.getMinecraft().getRenderManager().viewerPosY, entity.posZ - Minecraft.getMinecraft().getRenderManager().viewerPosZ - 0.5D, 1.0D);
               } else {
                  drawParticleESP(Color.GREEN, entity.posX - Minecraft.getMinecraft().getRenderManager().viewerPosX - 0.5D, 1.5D + entity.posY - Minecraft.getMinecraft().getRenderManager().viewerPosY, entity.posZ - Minecraft.getMinecraft().getRenderManager().viewerPosZ - 0.5D, 1.0D);
               }
            }
         }
      }
   }


   public static void drawParticleESP(Color c, double d, double d1, double d2, double size) {
      GlStateManager.enableBlend();
      GlStateManager.disableLighting();
      GlStateManager.disableDepth();
      GlStateManager.disableCull();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.disableTexture2D();
      drawBoundingBox(c, new AxisAlignedBB(d + 1.0D, d1 + size, d2 + 1.0D, d, d1, d2));
      GlStateManager.enableTexture2D();
      GlStateManager.enableDepth();
      GlStateManager.disableBlend();
      GlStateManager.enableCull();
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
