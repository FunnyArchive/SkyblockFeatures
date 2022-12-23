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
import mrfast.skyblockfeatures.utils.RenderUtil;

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
                  highlightBlock(new Color(255,85,255), entity.posX-0.5D, 1.5D+entity.posY, entity.posZ-0.5D, 1.0D,event.partialTicks);
               } else {
                  highlightBlock(Color.GREEN, entity.posX-0.5D, 1.5D + entity.posY, entity.posZ-0.5D, 1.0D,event.partialTicks);
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
}
