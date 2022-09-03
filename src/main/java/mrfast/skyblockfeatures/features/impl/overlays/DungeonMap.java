package mrfast.skyblockfeatures.features.impl.overlays;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.core.structure.FloatPair;
import mrfast.skyblockfeatures.core.structure.GuiElement;
import mrfast.skyblockfeatures.utils.SBInfo;
import mrfast.skyblockfeatures.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec4b;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DungeonMap {
	static String self = "";
	// public static class Player {
	// 	public Double distance;
	// 	public String id;
	// 	public EntityPlayer entity;

	// 	public Player(Double dist,String Identity,EntityPlayer player) {
	// 		distance = dist;
	// 		id = Identity;
	// 		entity = player;
	// 	} 
	// }
// 	-82 44 f1 left
// 20 103 f1 bottom
// 80 102 f1 bottom right
// 80 -102 f1 top right
// -80 -102 f1 top left

	public static int getMapFloorXOffset() {
		switch(Utils.getDungeonFloor()) {
			case 1: return 8;
			case 2: return 0;
			case 3: return -2;
			case 4: return -16;
			case 5: return 0;
		}
		return 0;
	}
	public static int getMapFloorZOffset() {
		switch(Utils.getDungeonFloor()) {
			case 1: return 4;
			case 2: return 0;
			case 3: return 0;
			case 4: return 0;
			case 5: return 0;
		}
		return 0;
	}

	public static void renderOverlay() {
		if(!Utils.inDungeons) return;
        if(!skyblockfeatures.config.dungeonMap) return;
		try {
			ItemStack[] items = Minecraft.getMinecraft().thePlayer.inventory.mainInventory;
			for (ItemStack item : items) {
				if (item != null) {
					if (item.getItem().isMap()) {
						if (item.getItem() instanceof ItemMap) {
							ItemMap mapitem = (ItemMap) item.getItem();
							mapData = mapitem.getMapData(item, Minecraft.getMinecraft().thePlayer.getEntityWorld());
						}
					}
				}
			}
			if (mapData == null) return;
		} catch (Error error) {
			Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Error loading map! Check your console!"));
			return;
		}
		int index = 0;
		for(Entry<String, Vec4b> decoration:mapData.mapDecorations.entrySet()) {
			index++;
			if(index == mapData.mapDecorations.size()) {
				self = decoration.getKey();
			}
		}

		try {
            Utils.drawGraySquareWithBorder(0, 0, 128, 128, 3);
			GlStateManager.pushMatrix();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			Minecraft.getMinecraft().entityRenderer.getMapItemRenderer().renderMap(mapData, true);
			drawPlayersOnMap();
			drawHeadOnMap();
			GlStateManager.popMatrix();
		} catch (Error error) {
			Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Error! Check your console!"));
		}
	}
	static MapData mapData;
	
	@SubscribeEvent
    public void onWorldChange(WorldEvent.Load event) {
		mapData = null;
    }

	public static void drawPlayersOnMap() {
		GlStateManager.pushMatrix();
		int i = 0;
		int k =0;
		int j = 0;
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		float z = 1.0F;
		for (Entry<String,Vec4b> entry : mapData.mapDecorations.entrySet()) {
			if(self == entry.getKey()) continue;
			Vec4b vec4b = entry.getValue();
			GlStateManager.pushMatrix();
			GlStateManager.translate(0, 0, z);
			GlStateManager.translate(i + vec4b.func_176112_b() / 2.0 + 64.0, j + vec4b.func_176113_c() / 2.0 + 64.0,-0.02);
			GlStateManager.rotate((vec4b.func_176111_d() * 360F) / 16.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.scale(4.0, 4.0, 1);
			GlStateManager.translate(-0.125, 0.125, 0.0);
			double b0 = vec4b.func_176110_a();
			double f1 = (b0 % 4) / 4.0;
			double f2 = (Math.floor(b0 / 4)) / 4.0;
			double f3 = (b0 % 4 + 1) / 4.0;
			double f4 = (Math.floor(b0 / 4) + 1) / 4.0;
			worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
			worldrenderer.pos(-1.0D, 1.0D, (double) ((float) k * -0.001F)).tex((double) f1, (double) f2).endVertex();
			worldrenderer.pos(1.0D, 1.0D, (double) ((float) k * -0.001F)).tex((double) f3, (double) f2).endVertex();
			worldrenderer.pos(1.0D, -1.0D, (double) ((float) k * -0.001F)).tex((double) f3, (double) f4).endVertex();
			worldrenderer.pos(-1.0D, -1.0D, (double) ((float) k * -0.001F)).tex((double) f1, (double) f4).endVertex();
			tessellator.draw();
			GlStateManager.popMatrix();
			k++;
			z++;
		}
		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		GlStateManager.scale(0.0F, 0.0F, -0.04F);
		GlStateManager.translate(1.0F, 1.0F, 1.0F);
		GlStateManager.popMatrix();
	};
	public static void drawHeadOnMap() {
		GlStateManager.pushMatrix();
		for (Entry<String,Vec4b> entry : mapData.mapDecorations.entrySet()) {
			if(entry.getKey() == self) {
				Utils.SendMessage(entry.getValue()+"");
				double x = Math.round((Utils.GetMC().thePlayer.posX)/(mapData.scale*0.8))+140+getMapFloorXOffset();
				double z = Math.round((Utils.GetMC().thePlayer.posZ)/(mapData.scale*0.8))+140+getMapFloorZOffset();

				AbstractClientPlayer aplayer = (AbstractClientPlayer) Utils.GetMC().thePlayer;
				ResourceLocation skin = aplayer.getLocationSkin();
				int k = 0;

				if(skin != DefaultPlayerSkin.getDefaultSkin(aplayer.getUniqueID())) { 
					Minecraft.getMinecraft().getTextureManager().bindTexture(skin);

					GlStateManager.pushMatrix();

					GlStateManager.disableDepth();
					GlStateManager.enableBlend();
					GL14.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);

					GlStateManager.translate(x, z, -0.02F);
					GlStateManager.scale(1.0f, 1.0f, 1);
					GlStateManager.rotate(Utils.GetMC().thePlayer.rotationYawHead-180, 0.0F, 0.0F, 1.0F);
					GlStateManager.translate(-0.5F, 0.5F, 0.0F);
					
					Gui.drawRect(-8/2-1,-8/2-1, 8/2+1, 8/2+1, 0xff111111);
					GlStateManager.color(1, 1, 1, 1);

					Tessellator tessellator = Tessellator.getInstance();
					WorldRenderer worldrenderer = tessellator.getWorldRenderer();

					worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
					worldrenderer.pos(-8/2f, 8/2f, 30+((float)k * -0.005F)).tex(8/64f, 8/64f).endVertex();
					worldrenderer.pos(8/2f, 8/2f, 30+((float)k * -0.005F)).tex(16/64f, 8/64f).endVertex();
					worldrenderer.pos(8/2f, -8/2f, 30+((float)k * -0.005F)).tex(16/64f, 16/64f).endVertex();
					worldrenderer.pos(-8/2f, -8/2f, 30+((float)k * -0.005F)).tex(8/64f, 16/64f).endVertex();
					tessellator.draw();

					worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
					worldrenderer.pos(-8/2f, 8/2f, 30+((float)k * -0.005F)+0.001f).tex(8/64f+0.5f, 8/64f).endVertex();
					worldrenderer.pos(8/2f, 8/2f, 30+((float)k * -0.005F)+0.001f).tex(16/64f+0.5f, 8/64f).endVertex();
					worldrenderer.pos(8/2f, -8/2f, 30+((float)k * -0.005F)+0.001f).tex(16/64f+0.5f, 16/64f).endVertex();
					worldrenderer.pos(-8/2f, -8/2f, 30+((float)k * -0.005F)+0.001f).tex(8/64f+0.5f, 16/64f).endVertex();
					tessellator.draw();

					GlStateManager.popMatrix();
				}
			}
		}
		GlStateManager.popMatrix();
		GlStateManager.enableBlend();
		GlStateManager.enableDepth();
	}

	private static final Minecraft mc = Minecraft.getMinecraft();
    
    static {
        new DungeonMapMove();
    }   
    public static class DungeonMapMove extends GuiElement {

        public DungeonMapMove() {
            super("DungeonMap", new FloatPair(0, 5));
            skyblockfeatures.GUIMANAGER.registerElement(this);
        }

        @Override
        public void render() {
            if(mc.thePlayer == null) return;
			renderOverlay();
        }
        

        @Override
        public void demoRender() {
            if(mc.thePlayer == null) return;
        }

        @Override
        public boolean getToggled() {
            return skyblockfeatures.config.dungeonMap;
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
