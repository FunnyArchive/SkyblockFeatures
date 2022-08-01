package mrfast.skyblockfeatures.features.impl.overlays;

import java.util.HashMap;

import org.lwjgl.opengl.GL11;

import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.core.structure.FloatPair;
import mrfast.skyblockfeatures.core.structure.GuiElement;
import mrfast.skyblockfeatures.events.SecondPassedEvent;
import mrfast.skyblockfeatures.utils.SBInfo;
import mrfast.skyblockfeatures.utils.Utils;
import mrfast.skyblockfeatures.utils.graphics.ScreenRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import java.awt.Color;

public class CrystalHollowsMap {
    public static final ResourceLocation map = new ResourceLocation("skyblockfeatures","map/CrystalHollowsMap.png");
    public static final ResourceLocation playerIcon = new ResourceLocation("skyblockfeatures","map/mapIcon.png");
    public static final ResourceLocation playerIcon2 = new ResourceLocation("skyblockfeatures","map/mapIcon2.png");

    static boolean loaded = false;
    static boolean start = false;   
    static int ticks = 0;
    public static HashMap<String,BlockPos> locations = new HashMap<>();
    @SubscribeEvent
    public void onload(WorldEvent.Load event) {
        locations.clear();
        loaded = false;
        ticks = 0;
        start = false;
        if(Minecraft.getMinecraft().thePlayer != null && Minecraft.getMinecraft().theWorld != null && skyblockfeatures.config.CrystalHollowsMap) {
            start = true;
        }
    }
    
    
    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if(start && Minecraft.getMinecraft().thePlayer != null && Minecraft.getMinecraft().theWorld != null && skyblockfeatures.config.CrystalHollowsMap) {
            ticks++;
            if(ticks >= 40) {
                loaded = true;
                ticks = 0;
            }
            BlockPos location = Utils.GetMC().thePlayer.getPosition().up(2);
            String position = skyblockfeatures.locationString.toLowerCase();
            if(position.contains("lost precursor city") && !locations.containsKey("§fCity")) locations.put("§fCity",location);
            if(position.contains("khazaddm") && !locations.containsKey("§cBal")) locations.put("§cBal",location);
            if(position.contains("mines of divan") && !locations.containsKey("§6Divan")) locations.put("§6Divan",location);
            if(position.contains("jungle temple") && !locations.containsKey("§aTemple")) locations.put("§aTemple",location);
            if(position.contains("goblin queen's den") && !locations.containsKey("§2Queen")) locations.put("§2Queen",location);
        }
    }


    static {
        new CHMap();
    }   
    public static class CHMap extends GuiElement {
        public CHMap() {
            super("CrystalHollowsMap", new FloatPair(0, 5));
            skyblockfeatures.GUIMANAGER.registerElement(this);
        }

        @Override
        public void render() {
            try {
                if (loaded && Minecraft.getMinecraft().thePlayer != null && Utils.inSkyblock && Minecraft.getMinecraft().theWorld != null && this.getToggled() && SBInfo.getInstance().getLocation().equals("crystal_hollows")) {
                    GlStateManager.pushMatrix(); 
                        GlStateManager.enableBlend();
                        GlStateManager.color(1, 1, 1, 1);
                        GlStateManager.pushMatrix();
                            Utils.GetMC().getTextureManager().bindTexture(map);
                            Utils.drawTexturedRect(0, 0, 512/4,512/4, 0, 1, 0, 1, GL11.GL_NEAREST);
                        GlStateManager.popMatrix();
    
                        for(String name:locations.keySet()) {
                            ResourceLocation locationIcon = new ResourceLocation("skyblockfeatures","map/locations/"+Utils.cleanColour(name.toLowerCase())+".png");
                            BlockPos position = locations.get(name);
                            double locationX = Math.round((position.getX()-202)/4.9);
                            double locationZ = Math.round((position.getZ()-202)/4.9);
                            GlStateManager.color(1, 1, 1, 1);
                            Utils.GetMC().getTextureManager().bindTexture(locationIcon);
                            GlStateManager.pushMatrix();
                                Utils.drawTexturedRect((float)(locationX-3.5),(float) (locationZ-4), 7, 8, 0, 1, 0, 1, GL11.GL_NEAREST);
                                int textWidth = ScreenRenderer.fontRenderer.getStringWidth(name);
                                GlStateManager.translate(locationX-textWidth/2, locationZ-10, 0);
                                Utils.drawTextWithStyle2(name,0, 0, 0xFFFFFF);
                                GlStateManager.translate(-locationX+textWidth/2, -locationZ+10, 0);
                            GlStateManager.popMatrix();
                        }

                        EntityPlayerSP player = Utils.GetMC().thePlayer;
                        double x = Math.round((player.posX-202)/4.9);
                        double z = Math.round((player.posZ-202)/4.9);
    
                        GlStateManager.color(1, 1, 1, 1);
                        Utils.GetMC().getTextureManager().bindTexture(playerIcon);
                        GlStateManager.pushMatrix();
                            GlStateManager.translate(x, z, 0);
                            GlStateManager.rotate(player.rotationYawHead-180, 0, 0, 1);
                            GlStateManager.translate(-x, -z, 0);
                            Utils.drawTexturedRect((float)(x-2.5),(float) (z-3.5), 5, 7, 0, 1, 0, 1, GL11.GL_NEAREST);
                        GlStateManager.popMatrix();
                    GlStateManager.popMatrix();
                }
            } catch (Exception e) {
                //TODO: handle exception
            }
        }
        @Override
        public void demoRender() {
            GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                GlStateManager.color(1, 1, 1, 1);
                GlStateManager.pushMatrix();
                    Utils.GetMC().getTextureManager().bindTexture(map);
                    Utils.drawTexturedRect(0, 0, 512/4,512/4, 0, 1, 0, 1, GL11.GL_NEAREST);
                GlStateManager.popMatrix();

                double x = Math.round((323-202)/4.9);
                double z = Math.round((621-202)/4.9);
                GlStateManager.pushMatrix();
                    Utils.GetMC().getTextureManager().bindTexture(playerIcon);
                    GlStateManager.translate(x, z, 0);
                    GlStateManager.rotate(-128, 0, 0, 1);
                    GlStateManager.translate(-x, -z, 0);
                    Utils.drawTexturedRect((float)(x-2.5),(float) (z-3.5), 5, 7, 0, 1, 0, 1, GL11.GL_NEAREST);
                GlStateManager.popMatrix();
            GlStateManager.popMatrix();
        }

        @Override
        public boolean getToggled() {
            return skyblockfeatures.config.CrystalHollowsMap && Utils.inSkyblock;
        }

        @Override
        public int getHeight() {
            return 128;
        }

        @Override
        public int getWidth() {
            return 128;
        }
    }
}