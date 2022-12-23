package mrfast.skyblockfeatures.features.impl.dungeons;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.core.structure.FloatPair;
import mrfast.skyblockfeatures.core.structure.GuiElement;
import mrfast.skyblockfeatures.utils.FontUtils;
import mrfast.skyblockfeatures.utils.StringUtils;
import mrfast.skyblockfeatures.utils.TabListUtils;
import mrfast.skyblockfeatures.utils.Utils;
import mrfast.skyblockfeatures.utils.graphics.ScreenRenderer;
import mrfast.skyblockfeatures.utils.graphics.SmartFontRenderer;
import mrfast.skyblockfeatures.utils.graphics.colors.CommonColors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.network.NetworkPlayerInfo;
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
		playerHeadOffsetX = null;
		playerHeadOffsetY = null;
		playerSkins.clear();
		playerNames.clear();
		dungeonTeammates.clear();
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

	static HashMap<String, NetworkPlayerInfo> dungeonTeammates = new HashMap<String, NetworkPlayerInfo>();
	static HashMap<String, NetworkPlayerInfo> dungeonTeammatesCopy = new HashMap<String, NetworkPlayerInfo>();
	static HashMap<Integer, ResourceLocation> playerSkins = new HashMap<Integer, ResourceLocation>();
	static HashMap<Integer, String> playerNames = new HashMap<Integer, String>();
	static Double playerHeadOffsetX = null;
	static Double playerHeadOffsetY = null;

	// Draw head on map
	public static void DrawHead(Double x,Double z,ResourceLocation skin, Float rotation,String name) {
		String shortName = name.length()>5?name.substring(0, 5):name;
		// Draw Username
		GlStateManager.pushMatrix();
		GlStateManager.scale(0.75f, 0.75f, 0);
		ScreenRenderer.fontRenderer.drawString(shortName,(float) (((x-2)-(FontUtils.getStringWidth(shortName)/3))*1.33), (float) ((z-13)*1.33),CommonColors.WHITE, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NORMAL);
		GlStateManager.popMatrix();
		GlStateManager.pushMatrix();
		Minecraft.getMinecraft().getTextureManager().bindTexture(skin);

		GlStateManager.disableDepth();
		GlStateManager.enableBlend();
		GL14.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GlStateManager.translate(x, z, -0.02F);
		GlStateManager.rotate(rotation, 0.0F, 0.0F, 1.0F);
		
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
	
	static Integer count = 0;
	public static void drawHeadOnMap() {
		int[] intArray = new int[]{5, 9, 13, 17, 1};
		List<NetworkPlayerInfo> tablist = TabListUtils.getTabEntries();
		count++;

		if(count == 200) {
			System.out.println("----------------START-------------");
			for(int i=0;i<intArray.length;i++) {
				NetworkPlayerInfo player = tablist.get(intArray[i]);
				if(player.getDisplayName().getUnformattedText().split(" ").length > 1) {
					String name = StringUtils.stripControlCodes(player.getDisplayName().getUnformattedText().split(" ")[1]);
					System.out.println("icon-"+i+" "+name);
				}
			}
			System.out.println("----------------FINISH-------------");
			count = 0;
			for(int i=0;i<intArray.length;i++) {
				NetworkPlayerInfo player = tablist.get(intArray[i]);
				// Find out whos dead
				if(player.getDisplayName().getUnformattedText().split(" ").length > 1 && player.getDisplayName().getUnformattedText().contains("(DEAD)")) {
					String name = StringUtils.stripControlCodes(player.getDisplayName().getUnformattedText().split(" ")[1]);
					if(name != null && dungeonTeammates.containsKey("icon-"+i)) {	
						playerSkins.clear();
						playerNames.clear();
						dungeonTeammates.clear();
						System.out.println("SOMEONE DIED RESETTING STUFF");
					}
				}
			}
		}
		for(int i=0;i<intArray.length;i++) {
			NetworkPlayerInfo player = tablist.get(intArray[i]);
			if(player.getDisplayName().getUnformattedText().split(" ").length > 1) {
				String name = StringUtils.stripControlCodes(player.getDisplayName().getUnformattedText().split(" ")[1]);
				if(name != null && !dungeonTeammates.containsKey("icon-"+i) && !player.getDisplayName().getUnformattedText().contains("(DEAD)")) {	
					dungeonTeammates.put("icon-"+i,player);
					System.out.println(name+" is icon-"+i);
				}
			}
		}


		try {
		for(Entry<String,NetworkPlayerInfo> entry : dungeonTeammates.entrySet()) {
			// Icon-#
			String entrySelf = entry.getKey().replaceAll("[^0-9]", "");
			GlStateManager.pushMatrix();
			for (Entry<String,Vec4b> mapEntry : mapData.mapDecorations.entrySet()) {
				// Raw icon number
				Integer playerId = Integer.parseInt(mapEntry.getKey().replaceAll("[^0-9]", ""));
				// Draw self head
				if(self != "" && playerId == Integer.parseInt(self.replaceAll("[^0-9]", ""))) {
					EntityPlayer player = Utils.GetMC().thePlayer;
					if(player != null) {
						double x = Math.round((player.posX)/(mapData.scale*0.8));
						double z = Math.round((player.posZ)/(mapData.scale*0.8));
						AbstractClientPlayer aplayer = (AbstractClientPlayer) player;
						ResourceLocation skin = aplayer.getLocationSkin();
						if(playerHeadOffsetX == null) playerHeadOffsetX = Math.abs(x-Math.round((mapEntry.getValue().func_176112_b()/2)+64));
						else x+=playerHeadOffsetX;
						
						if(playerHeadOffsetY == null) playerHeadOffsetY = Math.abs(z-Math.round((mapEntry.getValue().func_176113_c()/2)+64));
						else z+=playerHeadOffsetY;
						
						
						if(skin != DefaultPlayerSkin.getDefaultSkin(aplayer.getUniqueID())) {
							String shortName = aplayer.getName().length()>5?aplayer.getName().substring(0, 5):aplayer.getName();
							DrawHead(x,z,skin,player.rotationYawHead,shortName);
						}
					}
				}

				// if # is same as the icon-#
				else if(playerId == Integer.parseInt(entrySelf)) {
					EntityPlayer player = Utils.GetMC().theWorld.getPlayerEntityByName(entry.getValue().getDisplayName().getUnformattedText().split(" ")[1]);
					if(player != null) {
						double x = Math.round((player.posX)/(mapData.scale*0.8));
						double z = Math.round((player.posZ)/(mapData.scale*0.8));
						AbstractClientPlayer aplayer = (AbstractClientPlayer) player;
						ResourceLocation skin = aplayer.getLocationSkin();
						if(playerHeadOffsetX != null) x+=playerHeadOffsetX;
						if(playerHeadOffsetY != null) z+=playerHeadOffsetY;
						// Fancy Heads people close smooth
						if(skin != DefaultPlayerSkin.getDefaultSkin(aplayer.getUniqueID())) {
							playerSkins.put(playerId, skin);
							if(player.getName() != null) {
								playerNames.put(playerId, player.getName());
							}
							
							String shortName = player.getName().length()>5?player.getName().substring(0, 5):player.getName();
							DrawHead(x,z,skin,player.rotationYawHead,shortName);
						}
					} else {
						// Draw skin just on the icons based off previous data
						if(playerSkins.get(playerId) != null) {
							double x = Math.round((mapEntry.getValue().func_176112_b()/2)+64);
							double z = Math.round((mapEntry.getValue().func_176113_c()/2)+64);
							ResourceLocation skin = playerSkins.get(playerId);
							
							if(skin != null) {
								String shortName = "";
								if(playerNames.get(playerId) != null) {
									String name = playerNames.get(playerId);
									shortName = name.length()>5?name.substring(0, 5):name;
								}
								DrawHead(x,z,skin,(mapEntry.getValue().func_176111_d()* 360F) / 16.0F,shortName);
							}
						}
					}
				}
			}
			GlStateManager.popMatrix();
			GlStateManager.enableBlend();
			GlStateManager.enableDepth();
		}
	} catch (Exception e) {
		System.out.println(e);
		// TODO: handle exception
	}
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
