package mrfast.skyblockfeatures.features.impl.overlays;

import java.util.ArrayList;
import java.util.Iterator;

import com.mojang.realmsclient.gui.ChatFormatting;

import java.awt.Color;

import net.minecraft.block.Block;
import net.minecraft.block.BlockIce;
import net.minecraft.block.BlockPackedIce;
import net.minecraft.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.utils.RenderUtil;
import mrfast.skyblockfeatures.utils.Utils;

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
      Minecraft mc = Minecraft.getMinecraft();
      if(mc.theWorld == null || Utils.inDungeons) return;
      if(skyblockfeatures.config.icecaveHighlightWalls) GlStateManager.disableDepth();
      for(Entity entity:mc.theWorld.loadedEntityList) {
         if (skyblockfeatures.config.presentWaypoints && entity instanceof EntityArmorStand && !skyblockfeatures.locationString.contains("Glacial")&& ((EntityArmorStand)entity).getCurrentArmor(3) != null && ((EntityArmorStand)entity).getCurrentArmor(3).serializeNBT().getCompoundTag("tag").getCompoundTag("SkullOwner").getCompoundTag("Properties").toString().contains("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTBmNTM5ODUxMGIxYTA1YWZjNWIyMDFlYWQ4YmZjNTgzZTU3ZDcyMDJmNTE5M2IwYjc2MWZjYmQwYWUyIn19fQ=")) {
            boolean isPlayerGift = false;
            for(Entity otherEntity:mc.theWorld.loadedEntityList) {
               if(otherEntity instanceof EntityArmorStand && otherEntity.getDistanceToEntity(entity)<0.5 && otherEntity.getName().contains("From: ")) {
                  isPlayerGift = true;
               }
            }
            if (!sessionSouls.contains(entity) && !isPlayerGift) {
               highlightBlock(Color.YELLOW, entity.posX-0.5, entity.posY+1.5, entity.posZ-0.5, 1.0D,event.partialTicks);
            }
         }
         if(skyblockfeatures.locationString.contains("Glacial")) {
            Block blockstate = mc.theWorld.getBlockState(entity.getPosition()).getBlock();
            if(skyblockfeatures.config.icecaveHighlight && (blockstate instanceof BlockIce || blockstate instanceof BlockPackedIce) && entity instanceof EntityArmorStand && ((EntityArmorStand)entity).getCurrentArmor(3) != null) {
               String texture = ((EntityArmorStand)entity).getCurrentArmor(3).serializeNBT().getCompoundTag("tag").getCompoundTag("display").getString("Name");
               Vec3 StringPos = new Vec3(entity.posX, entity.posY+3, entity.posZ);

               // White gift
               if (texture.contains("White Gift")) {
                  highlightBlock(Color.WHITE, entity.posX-0.5, entity.posY+1.5, entity.posZ-0.5, 1.0D,event.partialTicks);
                  RenderUtil.draw3DString(StringPos, ChatFormatting.WHITE+"White Gift", 0, event.partialTicks);
               }
               // Green Gift
               else if (texture.contains("Green Gift")) {
                  highlightBlock(Color.GREEN, entity.posX-0.5, entity.posY+1.5, entity.posZ-0.5, 1.0D,event.partialTicks);
                  RenderUtil.draw3DString(StringPos, ChatFormatting.GREEN+"Green Gift", 0, event.partialTicks);
               }
               // Red Gift
               else if (texture.contains("Red Gift")) {
                  highlightBlock(Color.RED, entity.posX-0.5, entity.posY+1.5, entity.posZ-0.5, 1.0D,event.partialTicks);
                  RenderUtil.draw3DString(StringPos, ChatFormatting.RED+"Red Gift", 0, event.partialTicks);
               }
               // Glacial Talisman
               else if (texture.contains("Talisman")) {
                  highlightBlock(Color.ORANGE, entity.posX-0.5, entity.posY+1.5, entity.posZ-0.5, 1.0D,event.partialTicks);
                  RenderUtil.draw3DString(StringPos, ChatFormatting.GOLD+"Talisman", 0, event.partialTicks);
               }
               // Glacial Frag
               else if (texture.contains("Fragment")) {
                  highlightBlock(Color.MAGENTA, entity.posX-0.5, entity.posY+1.5, entity.posZ-0.5, 1.0D,event.partialTicks);
                  RenderUtil.draw3DString(StringPos, ChatFormatting.LIGHT_PURPLE+"Frag", 0, event.partialTicks);
               }
               // Packed Ice
               else if (texture.contains("Enchanted Ice")) {
                  highlightBlock(new Color(0x0a0d61), entity.posX-0.5, entity.posY+1.5, entity.posZ-0.5, 1.0D,event.partialTicks);
                  RenderUtil.draw3DString(StringPos, ChatFormatting.DARK_BLUE+"E. Ice", 0, event.partialTicks);
               }
               // Enchanted Packed Ice
               else if (texture.contains("Enchanted Packed Ice")) {
                  highlightBlock(new Color(0x361ba6), entity.posX-0.5, entity.posY+1.5, entity.posZ-0.5, 1.0D,event.partialTicks);
                  RenderUtil.draw3DString(StringPos, ChatFormatting.DARK_BLUE+"E. Packed Ice", 0, event.partialTicks);
               }
               // Highlight everything else gray
               else {
                  highlightBlock(Color.lightGray, entity.posX-0.5, entity.posY+1.5, entity.posZ-0.5, 1.0D,event.partialTicks);
                  RenderUtil.draw3DString(StringPos, ChatFormatting.GRAY+"Trash", 0, event.partialTicks);
               }
            }
         }
      }
      if(skyblockfeatures.config.icecaveHighlightWalls) GlStateManager.enableDepth();
   }

   public static void highlightBlock(Color c, double d, double d1, double d2, double size,float ticks) {
      RenderUtil.drawOutlinedFilledBoundingBox(new AxisAlignedBB(d, d1, d2, d+size, d1+size, d2+size),c,ticks);
   }
}

