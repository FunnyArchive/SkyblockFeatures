package mrfast.skyblockfeatures.features.impl.overlays;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.vecmath.Vector2d;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.core.structure.FloatPair;
import mrfast.skyblockfeatures.core.structure.GuiElement;
import mrfast.skyblockfeatures.utils.SBInfo;
import mrfast.skyblockfeatures.utils.Utils;
import mrfast.skyblockfeatures.utils.graphics.ScreenRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class CrystalHollowsMap {
    // Map Asset Inspired by Skyblock Extra's
    public static final ResourceLocation map = new ResourceLocation("skyblockfeatures","map/CrystalHollowsMap.png");
    public static final ResourceLocation playerIcon = new ResourceLocation("skyblockfeatures","map/mapIcon.png");
    public static final ResourceLocation playerIcon2 = new ResourceLocation("skyblockfeatures","map/mapIcon2.png");

    static boolean loaded = false;
    static boolean start = false;   
    static int ticks = 0;
    public static HashMap<String,BlockPos> locations = new HashMap<>();
    public static List<Vector2d> playerBreadcrumbs = new ArrayList<>();
    @SubscribeEvent
    public void onload(WorldEvent.Load event) {
        try {
            locations.clear();
            playerBreadcrumbs.clear();
            loaded = false;
            ticks = 0;
            start = false;
            if(Minecraft.getMinecraft().thePlayer != null && Minecraft.getMinecraft().theWorld != null && skyblockfeatures.config.CrystalHollowsMap) {
                start = true;
            }
        } catch(Exception e) {
            
        }
    }
    
    
    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if(start && Minecraft.getMinecraft().thePlayer != null && Minecraft.getMinecraft().theWorld != null && skyblockfeatures.config.CrystalHollowsMap) {
            if(SBInfo.getInstance().getLocation()!=null) {
                if(!SBInfo.getInstance().getLocation().equals("crystal_hollows")) return;
            }
            ticks++;
            BlockPos location = Utils.GetMC().thePlayer.getPosition().up(2);
            if(ticks%4==0) {
                Vector2d vector = new Vector2d((Utils.GetMC().thePlayer.posX-202)/4.9,(Utils.GetMC().thePlayer.posZ-202)/4.9);
                if(!playerBreadcrumbs.contains(vector)) {
                    if(playerBreadcrumbs.size()>1) {
                        Vector2d p1 = playerBreadcrumbs.get(playerBreadcrumbs.size()-1);
                        Vector2d p2 = vector;
                        double distance = Math.sqrt(Math.pow(Math.abs(p1.y-p2.y),2)+Math.pow(Math.abs(p1.x-p2.x),2));
                        if(distance<20) {
                            if(playerBreadcrumbs.size()>5000) {
                                playerBreadcrumbs.remove(0);
                            }
                            playerBreadcrumbs.add(vector);
                        }
                    } else {
                        playerBreadcrumbs.add(vector);
                    }
                }
                for(Entity entity:Utils.GetMC().theWorld.loadedEntityList) {
                    if(!entity.hasCustomName()) continue;
                    if(entity.getCustomNameTag().contains("Corleone") && !locations.containsKey("§5Corleone") && Utils.GetMC().thePlayer.canEntityBeSeen(entity)) {
                        locations.put("§5Corleone",entity.getPosition().up(2));
                    }
                }
            }
            if(ticks >= 40) {
                loaded = true;
                ticks = 0;
            }
            String position = skyblockfeatures.locationString.toLowerCase();
            if(position.contains("lost precursor city") && !locations.containsKey("§fCity")) locations.put("§fCity",location);
            if(position.contains("khazaddm") && !locations.containsKey("§cBal")) locations.put("§cBal",location);
            if(position.contains("mines of divan") && !locations.containsKey("§6Divan")) locations.put("§6Divan",location);
            if(position.contains("jungle temple") && !locations.containsKey("§aTemple")) locations.put("§aTemple",location);
            if(position.contains("goblin queen's den") && !locations.containsKey("§2Queen")) locations.put("§2Queen",location);
        }
    }

    static double lastPlayerX = 0;
    static double lastPlayerZ = 0;
    static double lastPlayerR = 0;

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

                        GlStateManager.pushMatrix();
                        for(int i=1;i<playerBreadcrumbs.size();i++) {
                            if(i<playerBreadcrumbs.size()-1) {
                                Utils.drawLine((int) playerBreadcrumbs.get(i).x, (int) playerBreadcrumbs.get(i).y,(int)  playerBreadcrumbs.get(i+1).x,(int)  playerBreadcrumbs.get(i+1).y, new Color(0,0,0),5);
                            }
                        }
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
                                Utils.drawTextWithStyle2(name,0, 0);
                                GlStateManager.translate(-locationX+textWidth/2, -locationZ+10, 0);
                            GlStateManager.popMatrix();
                        }

                        EntityPlayerSP player = Utils.GetMC().thePlayer;
                        double x = lastPlayerX;
                        double z = lastPlayerZ;
                        double newX = Math.round((player.posX-202)/4.9);
                        double newZ = Math.round((player.posZ-202)/4.9);
                        double deltaX = newX-x;
                        double deltaZ = newZ-z;

                        x+=deltaX/50;
                        z+=deltaZ/50;

                        lastPlayerX = x;
                        lastPlayerZ = z;
                        if(skyblockfeatures.config.CrystalHollowsMapHeads) {
                            AbstractClientPlayer aplayer = (AbstractClientPlayer) player;
                            ResourceLocation skin = aplayer.getLocationSkin();
                            GlStateManager.pushMatrix();
                            DrawHead(x, z, skin, (float) player.rotationYawHead);
                            GlStateManager.popMatrix();
                        } else {
                            GlStateManager.color(1, 1, 1, 1);
                            Utils.GetMC().getTextureManager().bindTexture(playerIcon);
                            GlStateManager.pushMatrix();
                                GlStateManager.translate(x, z, 0);
                                GlStateManager.rotate(player.rotationYawHead-180, 0, 0, 1);
                                GlStateManager.translate(-x, -z, 0);
                                Utils.drawTexturedRect((float)(x-2.5),(float) (z-3.5), 5, 7, 0, 1, 0, 1, GL11.GL_NEAREST);
                            GlStateManager.popMatrix();
                        }
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

    public static void DrawHead(Double x,Double z,ResourceLocation skin, Float rotation) {
        GlStateManager.pushMatrix();
		Minecraft.getMinecraft().getTextureManager().bindTexture(skin);

		GlStateManager.disableDepth();
		GlStateManager.enableBlend();
		GL14.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GlStateManager.translate(x, z, -0.02F);
		GlStateManager.rotate(rotation, 0.0F, 0.0F, 1.0F);
		
        GlStateManager.scale(0.75, 0.75, 1);
		Gui.drawRect(-8/2-1,-8/2-1, 8/2+1, 8/2+1, 0xff111111);
		GlStateManager.color(1, 1, 1, 1);

		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
		worldrenderer.pos(-8/2f, 8/2f, 30).tex(8/64f, 8/64f).endVertex();
		worldrenderer.pos(8/2f, 8/2f, 30).tex(16/64f, 8/64f).endVertex();
		worldrenderer.pos(8/2f, -8/2f, 30).tex(16/64f, 16/64f).endVertex();
		worldrenderer.pos(-8/2f, -8/2f, 30).tex(8/64f, 16/64f).endVertex();
		tessellator.draw();

		worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
		worldrenderer.pos(-8/2f, 8/2f, 30+0.001f).tex(8/64f+0.5f, 8/64f).endVertex();
		worldrenderer.pos(8/2f, 8/2f, 30+0.001f).tex(16/64f+0.5f, 8/64f).endVertex();
		worldrenderer.pos(8/2f, -8/2f, 30+0.001f).tex(16/64f+0.5f, 16/64f).endVertex();
		worldrenderer.pos(-8/2f, -8/2f, 30+0.001f).tex(8/64f+0.5f, 16/64f).endVertex();
		tessellator.draw();

		GlStateManager.popMatrix();
	}
}