/*
 * skyblockfeatures - Hypixel Skyblock Quality of Life Mod
 * Copyright (C) 2021 skyblockfeatures
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package mrfast.skyblockfeatures.utils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import com.google.common.base.Splitter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import mrfast.skyblockfeatures.events.PacketEvent;
import mrfast.skyblockfeatures.features.impl.handlers.TextRenderer;
import mrfast.skyblockfeatures.utils.graphics.colors.ColorFactory;
import mrfast.skyblockfeatures.utils.graphics.colors.CustomColor;
import mrfast.skyblockfeatures.utils.graphics.colors.RainbowColor;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.Matrix4f;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class Utils {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static boolean inSkyblock = false;
    public static boolean inDungeons = false;
    public static boolean inWorkshop = false;
    public static boolean shouldBypassVolume = false;
    public static int Health;
    public static int maxHealth;

    public static int Mana;
    public static int maxMana;

    public static int Defence;
    public static Utils INSTANCE = new Utils();
    public Map<UUID, Boolean> glowingCache = new HashMap<>();
    static Random random = new Random();
    

    public static boolean isOnHypixel() {
        try {
            if (mc != null && mc.theWorld != null && !mc.isSingleplayer()) {
                if (mc.thePlayer != null && mc.thePlayer.getClientBrand() != null) {
                    if (mc.thePlayer.getClientBrand().toLowerCase().contains("hypixel")) return true;
                }
                if (mc.getCurrentServerData() != null) return mc.getCurrentServerData().serverIP.toLowerCase().contains("hypixel");
            }
            return false;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    

    public static boolean isBot(EntityPlayer entity)
    {
        if (entity.getUniqueID().toString().startsWith(entity.getName()))
            return true;
        if (!StringUtils.stripControlCodes(entity.getGameProfile().getName()).equals(entity.getName()))
            return true;
        if (entity.getGameProfile().getId() != entity.getUniqueID())
            return true;

        return false;
    }

    /**
     * Taken from Danker's Skyblock Mod under GPL 3.0 license
     * https://github.com/bowser0000/SkyblockMod/blob/master/LICENSE
     * @author bowser0000
    */
    public static void checkForSkyblock() {
        try {
            if (isOnHypixel()) {
                if(mc.theWorld.getScoreboard() == null) return;
                ScoreObjective scoreboardObj = mc.theWorld.getScoreboard().getObjectiveInDisplaySlot(1);
                if (scoreboardObj != null) {
                    String scObjName = ScoreboardUtil.cleanSB(scoreboardObj.getDisplayName());
                    if (scObjName.contains("SKYBLOCK")) {
                        inSkyblock = true;
                        return;
                    }
                }
            }
            inSkyblock = false;
        } catch (NoSuchMethodError e) {
            //TODO: handle exception
        }
    }

    public static void drawItemStackWithText(ItemStack stack, int x, int y, String text) {
        if(stack == null)return;

        RenderItem itemRender = Minecraft.getMinecraft().getRenderItem();

        RenderHelper.enableGUIStandardItemLighting();
        itemRender.zLevel = -145; //Negates the z-offset of the below method.
        itemRender.renderItemAndEffectIntoGUI(stack, x, y);
        itemRender.renderItemOverlayIntoGUI(Minecraft.getMinecraft().fontRendererObj, stack, x, y, text);
        itemRender.zLevel = 0;
        RenderHelper.disableStandardItemLighting();
    }

    public static void drawTexturedRect(float x, float y, float width, float height) {
        drawTexturedRect(x, y, width, height, 0, 1, 0 , 1);
    }

    public static void drawTexturedRect(float x, float y, float width, float height, int filter) {
        drawTexturedRect(x, y, width, height, 0, 1, 0 , 1, filter);
    }

    public static void drawTexturedRect(float x, float y, float width, float height, float uMin, float uMax, float vMin, float vMax) {
        drawTexturedRect(x, y, width, height, uMin, uMax, vMin , vMax, GL11.GL_NEAREST);
    }

    public static void drawTexturedRect(float x, float y, float width, float height, float uMin, float uMax, float vMin, float vMax, int filter) {
        GlStateManager.enableBlend();
        GL14.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);

        drawTexturedRectNoBlend(x, y, width, height, uMin, uMax, vMin, vMax, filter);

        GlStateManager.disableBlend();
    }

    public static String getInternalNameForItem(ItemStack stack) {
        if(stack == null) return null;
        NBTTagCompound tag = stack.getTagCompound();
        return getInternalnameFromNBT(tag);
    }

    public final static Gson gson = new GsonBuilder().setPrettyPrinting().create();;

    public static String getInternalnameFromNBT(NBTTagCompound tag) {
        String internalname = null;
        if(tag != null && tag.hasKey("ExtraAttributes", 10)) {
            NBTTagCompound ea = tag.getCompoundTag("ExtraAttributes");

            if(ea.hasKey("id", 8)) {
                internalname = ea.getString("id").replaceAll(":", "-");
            } else {
                return null;
            }

            if("PET".equals(internalname)) {
                String petInfo = ea.getString("petInfo");
                if(petInfo.length() > 0) {
                    JsonObject petInfoObject = gson.fromJson(petInfo, JsonObject.class);
                    internalname = petInfoObject.get("type").getAsString();
                    String tier = petInfoObject.get("tier").getAsString();
                    switch(tier) {
                        case "COMMON":
                            internalname += ";0"; break;
                        case "UNCOMMON":
                            internalname += ";1"; break;
                        case "RARE":
                            internalname += ";2"; break;
                        case "EPIC":
                            internalname += ";3"; break;
                        case "LEGENDARY":
                            internalname += ";4"; break;
                        case "MYTHIC":
                            internalname += ";5"; break;
                    }
                }
            }
            if("ENCHANTED_BOOK".equals(internalname)) {
                NBTTagCompound enchants = ea.getCompoundTag("enchantments");

                for(String enchname : enchants.getKeySet()) {
                    internalname = enchname.toUpperCase() + ";" + enchants.getInteger(enchname);
                    break;
                }
            }
        }

        return internalname;
    }


    public static void drawItemStack(ItemStack stack, int x, int y) {
        if(stack == null) return;

        drawItemStackWithText(stack, x, y, null);
    }

    public static String fixBrokenAPIColour(String in) {
        return in.replaceAll("(?i)\\u00C2(\\u00A7.)", "$1");
    }

    public static void drawStringScaled(String str, FontRenderer fr, float x, float y, boolean shadow, int colour, float factor) {
        GlStateManager.scale(factor, factor, 1);
        fr.drawString(str, x/factor, y/factor, colour, shadow);
        GlStateManager.scale(1/factor, 1/factor, 1);
    }

    public static void drawStringCenteredScaledMaxWidth(String str, FontRenderer fr, float x, float y, boolean shadow, int len, int colour) {
        int strLen = fr.getStringWidth(str);
        float factor = len/(float)strLen;
        factor = Math.min(1, factor);
        int newLen = Math.min(strLen, len);

        float fontHeight = 8*factor;

        drawStringScaled(str, fr, x-newLen/2, y-fontHeight/2, shadow, colour, factor);
    }



    public static String capitalizeString(String string) {
		String[] words = string.split("_");
		
		for (int i = 0; i < words.length; i++) {
			words[i] = words[i].substring(0, 1).toUpperCase() + words[i].substring(1).toLowerCase();
		}
		
		return String.join(" ", words);
	}
	

    public static int[] skillXPPerLevel = {0, 50, 125, 200, 300, 500, 750, 1000, 1500, 2000, 3500, 5000, 7500, 10000, 15000, 20000, 30000, 50000,
										   75000, 100000, 200000, 300000, 400000, 500000, 600000, 700000, 800000, 900000, 1000000, 1100000,
										   1200000, 1300000, 1400000, 1500000, 1600000, 1700000, 1800000, 1900000, 2000000, 2100000, 2200000,
										   2300000, 2400000, 2500000, 2600000, 2750000, 2900000, 3100000, 3400000, 3700000, 4000000, 4300000,
										   4600000, 4900000, 5200000, 5500000, 5800000, 6100000, 6400000, 6700000, 7000000};
	static int[] dungeonsXPPerLevel = {0, 50, 75, 110, 160, 230, 330, 470, 670, 950, 1340, 1890, 2665, 3760, 5260, 7380, 10300, 14400,
									  20000, 27600, 38000, 52500, 71500, 97000, 132000, 180000, 243000, 328000, 445000, 600000, 800000,
									  1065000, 1410000, 1900000, 2500000, 3300000, 4300000, 5600000, 7200000, 9200000, 12000000, 15000000,
									  19000000, 24000000, 30000000, 38000000, 48000000, 60000000, 75000000, 93000000, 116250000};

    public static double getPercentage(int num1, int num2) {
		if (num2 == 0) return 0D;
		double result = ((double) num1 * 100D) / (double) num2;
		result = Math.round(result * 100D) / 100D;
		return result;
	}

    public static double xpToDungeonsLevel(double xp) {
		for (int i = 0, xpAdded = 0; i < dungeonsXPPerLevel.length; i++) {
			xpAdded += dungeonsXPPerLevel[i];
			if (xp < xpAdded) {
				double level =  (i - 1) + (xp - (xpAdded - dungeonsXPPerLevel[i])) / dungeonsXPPerLevel[i];
				return (double) Math.round(level * 100) / 100;
			}
		}
		return 50D;
	}

    public static void highlightBlock(BlockPos blockpos, Color color, float partialTicks, boolean depth) {
        Entity viewing_from = Minecraft.getMinecraft().getRenderViewEntity();

        double x_fix = viewing_from.lastTickPosX + ((viewing_from.posX - viewing_from.lastTickPosX) * partialTicks);
        double y_fix = viewing_from.lastTickPosY + ((viewing_from.posY - viewing_from.lastTickPosY) * partialTicks);
        double z_fix = viewing_from.lastTickPosZ + ((viewing_from.posZ - viewing_from.lastTickPosZ) * partialTicks);

        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();
        GlStateManager.translate(-x_fix, -y_fix, -z_fix);

        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableTexture2D();

        if (!depth) {
            GlStateManager.disableDepth(); GL11.glDisable(GL11.GL_DEPTH_TEST);
            GlStateManager.depthMask(false);
        }
        GlStateManager.color(color.getRed() /255.0f, color.getGreen() / 255.0f, color.getBlue()/ 255.0f, color.getAlpha()/ 255.0f);

        GlStateManager.translate(blockpos.getX(), blockpos.getY(), blockpos.getZ());

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex3d(0, 0, 0);
        GL11.glVertex3d(0, 0, 1);
        GL11.glVertex3d(0, 1, 1);
        GL11.glVertex3d(0, 1, 0); // TOP LEFT / BOTTOM LEFT / TOP RIGHT/ BOTTOM RIGHT

        GL11.glVertex3d(1, 0, 1);
        GL11.glVertex3d(1, 0, 0);
        GL11.glVertex3d(1, 1, 0);
        GL11.glVertex3d(1, 1, 1);

        GL11.glVertex3d(0, 1, 1);
        GL11.glVertex3d(0, 0, 1);
        GL11.glVertex3d(1, 0, 1);
        GL11.glVertex3d(1, 1, 1); // TOP LEFT / BOTTOM LEFT / TOP RIGHT/ BOTTOM RIGHT

        GL11.glVertex3d(0, 0, 0);
        GL11.glVertex3d(0, 1, 0);
        GL11.glVertex3d(1, 1, 0);
        GL11.glVertex3d(1, 0, 0);

        GL11.glVertex3d(0,1,0);
        GL11.glVertex3d(0,1,1);
        GL11.glVertex3d(1,1,1);
        GL11.glVertex3d(1,1,0);

        GL11.glVertex3d(0,0,1);
        GL11.glVertex3d(0,0,0);
        GL11.glVertex3d(1,0,0);
        GL11.glVertex3d(1,0,1);



        GL11.glEnd();


        if (!depth) {
            GlStateManager.disableDepth();
            GlStateManager.depthMask(true);
        }
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
        GlStateManager.popAttrib();


//...

    }

    public static void restoreGLOptions() {
        if (depthEnabled) {
            GlStateManager.enableDepth();
        }
        if (!alphaEnabled) {
            GlStateManager.disableAlpha();
        }
        if (!blendEnabled) {
            GlStateManager.disableBlend();
        }
        GlStateManager.blendFunc(blendFunctionSrcFactor, blendFunctionDstFactor);
    }

    public static JsonObject getJsonFromNBTEntry(NBTTagCompound tag) {
        if(tag.getKeySet().size() == 0) return null;

        int id = tag.getShort("id");
        int damage = tag.getShort("Damage");
        int count = tag.getShort("Count");
        tag = tag.getCompoundTag("tag");

        if(id == 141) id = 391; //for some reason hypixel thinks carrots have id 141

        String internalname = getInternalnameFromNBT(tag);
        if(internalname == null) return null;

        NBTTagCompound display = tag.getCompoundTag("display");
        String[] lore = getLoreFromNBT(tag);

        Item itemMc = Item.getItemById(id);
        String itemid = "null";
        if(itemMc != null) {
            itemid = itemMc.getRegistryName();
        }
        String displayname = display.getString("Name");
        JsonObject item = new JsonObject();
        item.addProperty("internalname", internalname);
        item.addProperty("itemid", itemid);
        item.addProperty("displayname", displayname);

        if(tag != null && tag.hasKey("ExtraAttributes", 10)) {
            NBTTagCompound ea = tag.getCompoundTag("ExtraAttributes");

            byte[] bytes = null;
            for(String key : ea.getKeySet()) {
                if(key.endsWith("backpack_data") || key.equals("new_year_cake_bag_data")) {
                    bytes = ea.getByteArray(key);
                    break;
                }
            }
            if(bytes != null) {
                JsonArray bytesArr = new JsonArray();
                for(byte b : bytes) {
                    bytesArr.add(new JsonPrimitive(b));
                }
                item.add("item_contents", bytesArr);
            }
            if(ea.hasKey("dungeon_item_level")) {
                item.addProperty("dungeon_item_level", ea.getInteger("dungeon_item_level"));
            }
        }

        if(lore != null && lore.length > 0) {
            JsonArray jsonLore = new JsonArray();
            for (String line : lore) {
                jsonLore.add(new JsonPrimitive(line));
            }
            item.add("lore", jsonLore);
        }

        item.addProperty("damage", damage);
        if(count > 1) item.addProperty("count", count);
        item.addProperty("nbttag", tag.toString());

        return item;
    }

    public static String[] getLoreFromNBT(NBTTagCompound tag) {
        String[] lore = new String[0];
        NBTTagCompound display = tag.getCompoundTag("display");

        if(display.hasKey("Lore", 9)) {
            NBTTagList list = display.getTagList("Lore", 8);
            lore = new String[list.tagCount()];
            for(int k=0; k<list.tagCount(); k++) {
                lore[k] = list.getStringTagAt(k);
            }
        }
        return lore;
    }


    private static boolean depthEnabled;
    private static boolean blendEnabled;
    private static boolean alphaEnabled;
    private static int blendFunctionSrcFactor;
    private static int blendFunctionDstFactor;

    public static void enableStandardGLOptions() {
        depthEnabled = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
        blendEnabled = GL11.glIsEnabled(GL11.GL_BLEND);
        alphaEnabled = GL11.glIsEnabled(GL11.GL_ALPHA_TEST);
        blendFunctionSrcFactor = GL11.glGetInteger(GL11.GL_BLEND_SRC);
        blendFunctionDstFactor = GL11.glGetInteger(GL11.GL_BLEND_DST);

        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableAlpha();
        GlStateManager.color(1, 1, 1, 1);
    }

    public static void drawTextWithStyle(String text, float x, float y, int color) {
        Minecraft.getMinecraft().fontRendererObj.drawString(text,1, 0, 0x000000, false);
        Minecraft.getMinecraft().fontRendererObj.drawString(text, -1, 0, 0x000000, false);
        Minecraft.getMinecraft().fontRendererObj.drawString(text, 0, 1, 0x000000, false);
        Minecraft.getMinecraft().fontRendererObj.drawString(text, 0, -1, 0x000000, false);
        Minecraft.getMinecraft().fontRendererObj.drawString(text, 0, 0, color, false);
    }

    public static void drawTextWithStyle2(String text, float x, float y, int color) {
        Minecraft.getMinecraft().fontRendererObj.drawString(text,x+1, y, 0x000000, false);
        Minecraft.getMinecraft().fontRendererObj.drawString(text, x-1, y, 0x000000, false);
        Minecraft.getMinecraft().fontRendererObj.drawString(text, x, y+1, 0x000000, false);
        Minecraft.getMinecraft().fontRendererObj.drawString(text, x, y-1, 0x000000, false);
        Minecraft.getMinecraft().fontRendererObj.drawString(text, x, y, color, false);
    }

    public static int getAlpha(int color) {
        return (color >> 24 & 255);
    }
    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)§[0-9A-FK-OR]");
    public static String stripColor(final String input) {
        return STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
    }


    public static Matrix4f createProjectionMatrix(int width, int height) {
        Matrix4f projMatrix  = new Matrix4f();
        projMatrix.setIdentity();
        projMatrix.m00 = 2.0F / (float)width;
        projMatrix.m11 = 2.0F / (float)(-height);
        projMatrix.m22 = -0.0020001999F;
        projMatrix.m33 = 1.0F;
        projMatrix.m03 = -1.0F;
        projMatrix.m13 = 1.0F;
        projMatrix.m23 = -1.0001999F;
        return projMatrix;
    }

    

    public static void applyHex(int col) {
		GlStateManager.color(((col >> 16) & 0xFF) / 255f,
				((col >> 8) & 0xFF) / 255f,
				(col & 0xFF) / 255f,
				((col >> 24) & 0xFF) / 255f);
	}

    public static void drawOnSlot(int size, int xSlotPos, int ySlotPos, int colour) {
		ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
		int guiLeft = (sr.getScaledWidth() - 176) / 2;
		int guiTop = (sr.getScaledHeight() - 222) / 2;
		int x = guiLeft + xSlotPos;
		int y = guiTop + ySlotPos;
		// Move down when chest isn't 6 rows
		if (size != 90) y += (6 - (size - 36) / 9) * 9;
		
		GL11.glTranslated(0, 0, 1);
		Gui.drawRect(x, y, x + 16, y + 16, colour);
		GL11.glTranslated(0, 0, -1);
	}

    public static double xpToSkillLevel(double xp, int limit) {
		for (int i = 0, xpAdded = 0; i < limit + 1; i++) {
			xpAdded += skillXPPerLevel[i];
			if (xp < xpAdded) {
				return (i - 1) + (xp - (xpAdded - skillXPPerLevel[i])) / skillXPPerLevel[i];
			}
		}
		return limit;
	}

    public static void drawStringCentered(String str, FontRenderer fr, float x, float y, boolean shadow, int colour) {
        int strLen = fr.getStringWidth(str);

        float x2 = x - strLen/2f;
        float y2 = y - fr.FONT_HEIGHT/2f;

        GL11.glTranslatef(x2, y2, 0);
        fr.drawString(str, 0, 0, colour, shadow);
        GL11.glTranslatef(-x2, -y2, 0);
    }

    public static String cleanColourNotModifiers(String in) {
        return in.replaceAll("(?i)\\u00A7[0-9a-f]", "");
    }
	

    public static void drawFilledBoundingBox(AxisAlignedBB p_181561_0_, float alpha) {
        Color c = new Color(0x00e9ff, true);

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.disableTexture2D();

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        GlStateManager.color(c.getRed()/255f, c.getGreen()/255f, c.getBlue()/255f, c.getAlpha()/255f*alpha);

        //vertical
        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.minY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.minY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.minY, p_181561_0_.maxZ).endVertex();
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.minY, p_181561_0_.maxZ).endVertex();
        tessellator.draw();
        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.maxY, p_181561_0_.maxZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.maxY, p_181561_0_.maxZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.maxY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.maxY, p_181561_0_.minZ).endVertex();
        tessellator.draw();


        GlStateManager.color(c.getRed()/255f*0.8f, c.getGreen()/255f*0.8f, c.getBlue()/255f*0.8f, c.getAlpha()/255f*alpha);

        //x
        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.minY, p_181561_0_.maxZ).endVertex();
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.maxY, p_181561_0_.maxZ).endVertex();
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.maxY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.minY, p_181561_0_.minZ).endVertex();
        tessellator.draw();
        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.minY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.maxY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.maxY, p_181561_0_.maxZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.minY, p_181561_0_.maxZ).endVertex();
        tessellator.draw();


        GlStateManager.color(c.getRed()/255f*0.9f, c.getGreen()/255f*0.9f, c.getBlue()/255f*0.9f, c.getAlpha()/255f*alpha);
        //z
        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.maxY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.maxY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.minY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.minY, p_181561_0_.minZ).endVertex();
        tessellator.draw();
        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.minY, p_181561_0_.maxZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.minY, p_181561_0_.maxZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.maxY, p_181561_0_.maxZ).endVertex();
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.maxY, p_181561_0_.maxZ).endVertex();
        tessellator.draw();
    }

    public static void drawTexturedRectNoBlend(float x, float y, float width, float height, float uMin, float uMax, float vMin, float vMax, int filter) {
        GlStateManager.enableTexture2D();

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, filter);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, filter);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer
                .pos(x, y+height, 0.0D)
                .tex(uMin, vMax).endVertex();
        worldrenderer
                .pos(x+width, y+height, 0.0D)
                .tex(uMax, vMax).endVertex();
        worldrenderer
                .pos(x+width, y, 0.0D)
                .tex(uMax, vMin).endVertex();
        worldrenderer
                .pos(x, y, 0.0D)
                .tex(uMin, vMin).endVertex();
        tessellator.draw();

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
    }

    public static void drawRectNoBlend(int left, int top, int right, int bottom, int color) {
        if (left < right) {
            int i = left;
            left = right;
            right = i;
        }

        if (top < bottom) {
            int j = top;
            top = bottom;
            bottom = j;
        }

        float f3 = (float)(color >> 24 & 255) / 255.0F;
        float f = (float)(color >> 16 & 255) / 255.0F;
        float f1 = (float)(color >> 8 & 255) / 255.0F;
        float f2 = (float)(color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.disableTexture2D();
        GlStateManager.color(f, f1, f2, f3);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos((double)left, (double)bottom, 0.0D).endVertex();
        worldrenderer.pos((double)right, (double)bottom, 0.0D).endVertex();
        worldrenderer.pos((double)right, (double)top, 0.0D).endVertex();
        worldrenderer.pos((double)left, (double)top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
    }

    public static void drawHoveringText(List<String> textLines, final int mouseX, final int mouseY, final int screenWidth, final int screenHeight, final int maxTextWidth, FontRenderer font) {
        if (!textLines.isEmpty()) {
            GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            int tooltipTextWidth = 0;

            for (String textLine : textLines) {
                int textLineWidth = font.getStringWidth(textLine);

                if (textLineWidth > tooltipTextWidth) {
                    tooltipTextWidth = textLineWidth;
                }
            }

            boolean needsWrap = false;

            int titleLinesCount = 1;
            int tooltipX = mouseX + 12;
            if (tooltipX + tooltipTextWidth + 4 > screenWidth) {
                tooltipX = mouseX - 16 - tooltipTextWidth;
                if (tooltipX < 4) // if the tooltip doesn't fit on the screen
                {
                    if (mouseX > screenWidth / 2) {
                        tooltipTextWidth = mouseX - 12 - 8;
                    } else {
                        tooltipTextWidth = screenWidth - 16 - mouseX;
                    }
                    needsWrap = true;
                }
            }

            if (maxTextWidth > 0 && tooltipTextWidth > maxTextWidth) {
                tooltipTextWidth = maxTextWidth;
                needsWrap = true;
            }

            if (needsWrap) {
                int wrappedTooltipWidth = 0;
                List<String> wrappedTextLines = new ArrayList<String>();
                for (int i = 0; i < textLines.size(); i++) {
                    String textLine = textLines.get(i);
                    List<String> wrappedLine = font.listFormattedStringToWidth(textLine, tooltipTextWidth);
                    if (i == 0) {
                        titleLinesCount = wrappedLine.size();
                    }

                    for (String line : wrappedLine) {
                        int lineWidth = font.getStringWidth(line);
                        if (lineWidth > wrappedTooltipWidth) {
                            wrappedTooltipWidth = lineWidth;
                        }
                        wrappedTextLines.add(line);
                    }
                }
                tooltipTextWidth = wrappedTooltipWidth;
                textLines = wrappedTextLines;

                if (mouseX > screenWidth / 2) {
                    tooltipX = mouseX - 16 - tooltipTextWidth;
                } else {
                    tooltipX = mouseX + 12;
                }
            }

            int tooltipY = mouseY - 12;
            int tooltipHeight = 8;

            if (textLines.size() > 1) {
                tooltipHeight += (textLines.size() - 1) * 10;
                if (textLines.size() > titleLinesCount) {
                    tooltipHeight += 2; // gap between title lines and next lines
                }
            }

            if (tooltipY + tooltipHeight + 6 > screenHeight) {
                tooltipY = screenHeight - tooltipHeight - 6;
            }

            final int zLevel = 300;
            final int backgroundColor = 0xF0100010;
            RenderUtil.drawGradientRect(zLevel, tooltipX - 3, tooltipY - 4, tooltipX + tooltipTextWidth + 3, tooltipY - 3, backgroundColor, backgroundColor);
            RenderUtil.drawGradientRect(zLevel, tooltipX - 3, tooltipY + tooltipHeight + 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 4, backgroundColor, backgroundColor);
            RenderUtil.drawGradientRect(zLevel, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
            RenderUtil.drawGradientRect(zLevel, tooltipX - 4, tooltipY - 3, tooltipX - 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
            RenderUtil.drawGradientRect(zLevel, tooltipX + tooltipTextWidth + 3, tooltipY - 3, tooltipX + tooltipTextWidth + 4, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
            final int borderColorStart = 0x505000FF;
            final int borderColorEnd = (borderColorStart & 0xFEFEFE) >> 1 | borderColorStart & 0xFF000000;
            RenderUtil.drawGradientRect(zLevel, tooltipX - 3, tooltipY - 3 + 1, tooltipX - 3 + 1, tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd);
            RenderUtil.drawGradientRect(zLevel, tooltipX + tooltipTextWidth + 2, tooltipY - 3 + 1, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd);
            RenderUtil.drawGradientRect(zLevel, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY - 3 + 1, borderColorStart, borderColorStart);
            RenderUtil.drawGradientRect(zLevel, tooltipX - 3, tooltipY + tooltipHeight + 2, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, borderColorEnd, borderColorEnd);

            for (int lineNumber = 0; lineNumber < textLines.size(); ++lineNumber) {
                String line = textLines.get(lineNumber);
                font.drawStringWithShadow(line, (float) tooltipX, (float) tooltipY, -1);

                if (lineNumber + 1 == titleLinesCount) {
                    tooltipY += 2;
                }

                tooltipY += 10;
            }

            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.enableRescaleNormal();
        }
        GlStateManager.disableLighting();
    }

    public static Splitter PATH_SPLITTER = Splitter.on(".").omitEmptyStrings().limit(2);
    public static JsonElement getElement(JsonElement element, String path) {
        List<String> path_split = PATH_SPLITTER.splitToList(path);
        if(element instanceof JsonObject) {
            JsonElement e = element.getAsJsonObject().get(path_split.get(0));
            if(path_split.size() > 1) {
                return getElement(e, path_split.get(1));
            } else {
                return e;
            }
        } else {
            return element;
        }
    }


    public static ItemStack createItemStack(Item item, String displayname, String... lore) {
        ItemStack stack = new ItemStack(item, 1, 0);
        NBTTagCompound tag = new NBTTagCompound();
        NBTTagCompound display = new NBTTagCompound();
        NBTTagList Lore = new NBTTagList();

        for(String line : lore) {
            Lore.appendTag(new NBTTagString(line));
        }

        display.setString("Name", displayname);
        display.setTag("Lore", Lore);

        tag.setTag("display", display);
        tag.setInteger("HideFlags", 254);

        stack.setTagCompound(tag);

        return stack;
    }

    /**
     * Taken from Danker's Skyblock Mod under GPL 3.0 license
     * https://github.com/bowser0000/SkyblockMod/blob/master/LICENSE
     * @author bowser0000
     */
    public static void checkForDungeons() {
        if (inSkyblock) {
            List<String> scoreboard = ScoreboardUtil.getSidebarLines();
            for (String s : scoreboard) {
                String sCleaned = ScoreboardUtil.cleanSB(s);
                if ((sCleaned.contains("The Catacombs") && !sCleaned.contains("Queue")) || sCleaned.contains("Cleared:")) {
                    inDungeons = true;
                    return;
                }
            }
        }
        inDungeons = false;
    }

    private static LinkedList<Integer> guiScales = new LinkedList<>();
    public static ScaledResolution pushGuiScale(int scale) {
        if(scale < 0) {
            if(guiScales.size() > 0) {
                guiScales.pop();
            }
        } else {
            if(scale == 0) {
                guiScales.push(Minecraft.getMinecraft().gameSettings.guiScale);
            } else {
                guiScales.push(scale);
            }
        }

        int newScale = guiScales.size() > 0 ? Math.max(0, Math.min(4, guiScales.peek())) : Minecraft.getMinecraft().gameSettings.guiScale;
        if(newScale == 0) newScale = Minecraft.getMinecraft().gameSettings.guiScale;

        int oldScale = Minecraft.getMinecraft().gameSettings.guiScale;
        Minecraft.getMinecraft().gameSettings.guiScale = newScale;
        ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
        Minecraft.getMinecraft().gameSettings.guiScale = oldScale;

        if(guiScales.size() > 0) {
            GlStateManager.viewport(0, 0, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
            GlStateManager.matrixMode(GL11.GL_PROJECTION);
            GlStateManager.loadIdentity();
            GlStateManager.ortho(0.0D,
                    scaledresolution.getScaledWidth_double(),
                    scaledresolution.getScaledHeight_double(), 0.0D, 1000.0D, 3000.0D);
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
            GlStateManager.loadIdentity();
            GlStateManager.translate(0.0F, 0.0F, -2000.0F);
        } else {
                GlStateManager.matrixMode(GL11.GL_PROJECTION);
                GlStateManager.loadIdentity();
                GlStateManager.ortho(0.0D,
                        scaledresolution.getScaledWidth_double(),
                        scaledresolution.getScaledHeight_double(), 0.0D, 1000.0D, 3000.0D);
                GlStateManager.matrixMode(GL11.GL_MODELVIEW);
                GlStateManager.loadIdentity();
                GlStateManager.translate(0.0F, 0.0F, -2000.0F);
            }

        return scaledresolution;
    }

    public static void checkForWorkshop() {
        if (inSkyblock) {
            List<String> scoreboard = ScoreboardUtil.getSidebarLines();
            for (String s : scoreboard) {
                String sCleaned = ScoreboardUtil.cleanSB(s);
                if (sCleaned.contains("Jerry's Workshop") || sCleaned.contains("Jerry Pond")) {
                    inWorkshop = true;
                    return;
                }
            }
        }
        inWorkshop = false;
        
    }

    public static Slot getSlotUnderMouse(GuiContainer gui) {
        return ObfuscationReflectionHelper.getPrivateValue(GuiContainer.class, gui, "theSlot", "field_147006_u");
    }

    public static Iterable<BlockPos> getBlocksWithinRangeAtSameY(BlockPos center, int radius, int y) {
        BlockPos corner1 = new BlockPos(center.getX() - radius, y, center.getZ() - radius);
        BlockPos corner2 = new BlockPos(center.getX() + radius, y, center.getZ() + radius);
        return BlockPos.getAllInBox(corner1, corner2);
    }

    public static Random getRandom() {
        return random;
    }

    public static void SendMessage(String string)
    {
        if (Utils.GetMC().ingameGUI != null || Utils.GetMC().thePlayer == null)
            Utils.GetMC().ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(string));
    }

    public static BlockPos getNearbyBlock(Minecraft mc, BlockPos pos, Block... blockTypes) {
		if (pos == null) return null;
		BlockPos pos1 = new BlockPos(pos.getX() - 2, pos.getY() - 3, pos.getZ() - 2);
		BlockPos pos2 = new BlockPos(pos.getX() + 2, pos.getY() + 3, pos.getZ() + 2);
		
		BlockPos closestBlock = null;
		double closestBlockDistance = 99;
		Iterable<BlockPos> blocks = BlockPos.getAllInBox(pos1, pos2);
		
		for (BlockPos block : blocks) {
			for (Block blockType : blockTypes) {
				if (mc.theWorld.getBlockState(block).getBlock() == blockType && block.distanceSq(pos) < closestBlockDistance) {
					closestBlock = block;
					closestBlockDistance = block.distanceSq(pos);
				}
			}
		}
		
		return closestBlock;
	}

    private static char[] c = new char[]{'K', 'M', 'B', 'T'};
    public static String shortNumberFormat(double n, int iteration) {
        double d = ((long) n / 100) / 10.0;
        boolean isRound = (d * 10) %10 == 0;
        return (d < 1000?
                ((d > 99.9 || isRound || (!isRound && d > 9.99)?
                        (int) d * 10 / 10 : d + ""
                ) + "" + c[iteration])
                : shortNumberFormat(d, iteration+1));
    }

    public static BlockPos getFirstBlockPosAfterVectors(Minecraft mc, Vec3 pos1, Vec3 pos2, int strength, int distance) {
		double x = pos2.xCoord - pos1.xCoord;
		double y = pos2.yCoord - pos1.yCoord;
		double z = pos2.zCoord - pos1.zCoord;
		
		for (int i = strength; i < distance * strength; i++) { // Start at least 1 strength away
			double newX = pos1.xCoord + ((x / strength) * i);
			double newY = pos1.yCoord + ((y / strength) * i);
			double newZ = pos1.zCoord + ((z / strength) * i);
			
			BlockPos newBlock = new BlockPos(newX, newY, newZ);
			if (mc.theWorld.getBlockState(newBlock).getBlock() != Blocks.air) {
				return newBlock;
			}
		}
		
		return null;
	}


    public static void draw3DLine(Vec3 pos1, Vec3 pos2, int colourInt, int lineWidth, boolean depth, float partialTicks) {
		Entity render = Minecraft.getMinecraft().getRenderViewEntity();
		WorldRenderer worldRenderer = Tessellator.getInstance().getWorldRenderer();
		Color colour = new Color(colourInt);
		
		double realX = render.lastTickPosX + (render.posX - render.lastTickPosX) * partialTicks;
		double realY = render.lastTickPosY + (render.posY - render.lastTickPosY) * partialTicks;
		double realZ = render.lastTickPosZ + (render.posZ - render.lastTickPosZ) * partialTicks;
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(-realX, -realY, -realZ);
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		GL11.glLineWidth(lineWidth);
		if (!depth) {
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GlStateManager.depthMask(false);
		}
		GlStateManager.color(colour.getRed() / 255f, colour.getGreen() / 255f, colour.getBlue() / 255f, colour.getAlpha() / 255f);
		worldRenderer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
		
		worldRenderer.pos(pos1.xCoord, pos1.yCoord, pos1.zCoord).endVertex();
		worldRenderer.pos(pos2.xCoord, pos2.yCoord, pos2.zCoord).endVertex();
		Tessellator.getInstance().draw();

		GlStateManager.translate(realX, realY, realZ);
		if (!depth) {
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GlStateManager.depthMask(true);
		}
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.popMatrix();
	}
    
    public static String cleanColour(String in) {
        return in.replaceAll("(?i)\\u00A7.", "");
    }

    public static void drawTitle(String text) {
		Minecraft mc = Minecraft.getMinecraft();
		ScaledResolution scaledResolution = new ScaledResolution(mc);
		
		int height = scaledResolution.getScaledHeight();
		int width = scaledResolution.getScaledWidth();
		int drawHeight = 0;
		String[] splitText = text.split("\n");
		for (String title : splitText) {
			int textLength = mc.fontRendererObj.getStringWidth(title);

			double scale = 4;
			if (textLength * scale > (width * 0.9F)) {
				scale = (width * 0.9F) / (float) textLength;
			}

			int titleX = (int) ((width / 2) - (textLength * scale / 2));
			int titleY = (int) ((height * 0.45) / scale) + (int) (drawHeight * scale);
			new TextRenderer(mc, title, titleX, titleY, scale);
			drawHeight += mc.fontRendererObj.FONT_HEIGHT;
		}
	}
    
    public static Minecraft GetMC()
    {
        return mc;
    }

    public static boolean isInTablist(EntityPlayer player) {
        if (mc.isSingleplayer()) {
            return true;
        }
        for (NetworkPlayerInfo pi : mc.getNetHandler().getPlayerInfoMap()) {
            if (pi.getGameProfile().getName().equalsIgnoreCase(player.getName())) {
                return true;
            }
        }
        return false;
    }

    public static void renderItem(ItemStack item, float x, float y) {
        GlStateManager.enableRescaleNormal();
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.enableDepth();

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);
        Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(item, 0, 0);
        GlStateManager.popMatrix();

        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
    }


    /**
     * Taken from SkyblockAddons under MIT License
     * https://github.com/BiscuitDevelopment/SkyblockAddons/blob/master/LICENSE
     * @author BiscuitDevelopment
     */
    public static void playLoudSound(String sound, double pitch) {
        shouldBypassVolume = true;
        mc.thePlayer.playSound(sound, 1, (float) pitch);
        shouldBypassVolume = false;
    }

    /**
     * Checks if an object is equal to any of the other objects
     * @param object Object to compare
     * @param other Objects being compared
     * @return boolean
     */
    public static boolean equalsOneOf(@Nullable Object object, @NotNull Object... other) {
        for (Object obj : other) {
            if (Objects.equals(object, obj)) return true;
        }
        return false;
    }

    public static CustomColor customColorFromString(String string) {
        if (string == null) throw new NullPointerException("Argument cannot be null!");
        if (string.startsWith("rainbow(")) {
            return RainbowColor.fromString(string);
        }

        CustomColor color;
        try {
            color = getCustomColorFromColor(ColorFactory.web(string));
        } catch (IllegalArgumentException e) {
            try {
                color = CustomColor.fromInt(Integer.parseInt(string));
            } catch(NumberFormatException ignored) {
                throw e;
            }
        }
        return color;
    }

    public static CustomColor getCustomColorFromColor(Color color) {
        return CustomColor.fromInt(color.getRGB());
    }

    

    /**
     * Cancels a chat packet and posts the chat event to the event bus if other mods need it
     * @param ReceivePacketEvent packet to cancel
     */
    public static void cancelChatPacket(PacketEvent.ReceiveEvent ReceivePacketEvent) {
        if (!(ReceivePacketEvent.packet instanceof S02PacketChat)) return;
        ReceivePacketEvent.setCanceled(true);
        S02PacketChat packet = ((S02PacketChat) ReceivePacketEvent.packet);
        MinecraftForge.EVENT_BUS.post(new ClientChatReceivedEvent(packet.getType(), packet.getChatComponent()));
    }

    public static void entityESPBox(Entity entity) {
		GL11.glBlendFunc(770, 771);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glLineWidth(2.0F);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		GL11.glColor4d(1, 0, 0, 0.5F);
		Minecraft.getMinecraft().getRenderManager();
		RenderGlobal.drawSelectionBoundingBox(new AxisAlignedBB(
				entity.getEntityBoundingBox().minX - 0.05 - entity.posX
						+ (entity.posX - Minecraft.getMinecraft().getRenderManager().viewerPosX),
				entity.getEntityBoundingBox().minY - entity.posY
						+ (entity.posY - Minecraft.getMinecraft().getRenderManager().viewerPosY),
				entity.getEntityBoundingBox().minZ - 0.05 - entity.posZ
						+ (entity.posZ - Minecraft.getMinecraft().getRenderManager().viewerPosZ),
				entity.getEntityBoundingBox().maxX + 0.05 - entity.posX
						+ (entity.posX - Minecraft.getMinecraft().getRenderManager().viewerPosX),
				entity.getEntityBoundingBox().maxY + 0.1 - entity.posY
						+ (entity.posY - Minecraft.getMinecraft().getRenderManager().viewerPosY),
				entity.getEntityBoundingBox().maxZ + 0.05 - entity.posZ
						+ (entity.posZ - Minecraft.getMinecraft().getRenderManager().viewerPosZ)));
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);
		GL11.glDisable(GL11.GL_BLEND);
	}

    public final static void drawBox(AxisAlignedBB axisAlignedBB) {
    	GL11.glBegin(GL11.GL_LINES);
    	{
    		GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ);
    		GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ);
    		
    		GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ);
    		GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ);
    		
    		GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ);
    		GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
    		
    		GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ);
    		GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
    		
    		
    		GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ);
    		GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ);
    		
    		GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
    		GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
    		
    		GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ);
    		GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
    		
    		GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ);
    		GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
    		
    		
    		GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ);
    		GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ);
    		
    		GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ);
    		GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ);
    		
    		GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ);
    		GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ);
    		
    		GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ);
    		GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ);
    	}
    	GL11.glEnd();
    }

    public static boolean isNPC(Entity entity) {
        if(entity instanceof EntityPlayer) {
            EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
            return entity.getUniqueID().version() == 2 && entityLivingBase.getHealth() == 20.0F && !entityLivingBase.isPlayerSleeping() && Utils.inSkyblock;
        } else return false;
        
    }

    public static void drawRect(double left, double top, double right, double bottom, int color) {
        if (left < right) {
            double i = left;
            left = right;
            right = i;
        }

        if (top < bottom) {
            double j = top;
            top = bottom;
            bottom = j;
        }

        float f3 = (float)(color >> 24 & 255) / 255.0F;
        float f = (float)(color >> 16 & 255) / 255.0F;
        float f1 = (float)(color >> 8 & 255) / 255.0F;
        float f2 = (float)(color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(f, f1, f2, f3);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(left, bottom, 0.0D).endVertex();
        worldrenderer.pos(right, bottom, 0.0D).endVertex();
        worldrenderer.pos(right, top, 0.0D).endVertex();
        worldrenderer.pos(left, top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawFilled3DBox(AxisAlignedBB aabb, int colourInt, boolean translucent, boolean depth, float partialTicks) {
		Entity render = Minecraft.getMinecraft().getRenderViewEntity();
		WorldRenderer worldRenderer = Tessellator.getInstance().getWorldRenderer();
		Color colour = new Color(colourInt);

		double realX = render.lastTickPosX + (render.posX - render.lastTickPosX) * partialTicks;
		double realY = render.lastTickPosY + (render.posY - render.lastTickPosY) * partialTicks;
		double realZ = render.lastTickPosZ + (render.posZ - render.lastTickPosZ) * partialTicks;

		GlStateManager.pushMatrix();
		GlStateManager.pushAttrib();
		GlStateManager.translate(-realX, -realY, -realZ);
		GlStateManager.disableTexture2D();
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		GlStateManager.disableCull();
		GlStateManager.tryBlendFuncSeparate(770, translucent ? 1 : 771, 1, 0);
		if (!depth) {
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GlStateManager.depthMask(false);
		}
		GlStateManager.color(colour.getRed() / 255f, colour.getGreen() / 255f, colour.getBlue() / 255f, colour.getAlpha() / 255f);
		worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
		// Bottom
		worldRenderer.pos(aabb.minX, aabb.minY, aabb.minZ).endVertex();
		worldRenderer.pos(aabb.maxX, aabb.minY, aabb.minZ).endVertex();
		worldRenderer.pos(aabb.maxX, aabb.minY, aabb.maxZ).endVertex();
		worldRenderer.pos(aabb.minX, aabb.minY, aabb.maxZ).endVertex();
		// Top
		worldRenderer.pos(aabb.minX, aabb.maxY, aabb.minZ).endVertex();
		worldRenderer.pos(aabb.maxX, aabb.maxY, aabb.minZ).endVertex();
		worldRenderer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).endVertex();
		worldRenderer.pos(aabb.minX, aabb.maxY, aabb.maxZ).endVertex();
		// West
		worldRenderer.pos(aabb.minX, aabb.minY, aabb.minZ).endVertex();
		worldRenderer.pos(aabb.minX, aabb.maxY, aabb.minZ).endVertex();
		worldRenderer.pos(aabb.minX, aabb.maxY, aabb.maxZ).endVertex();
		worldRenderer.pos(aabb.minX, aabb.minY, aabb.maxZ).endVertex();
		// East
		worldRenderer.pos(aabb.maxX, aabb.minY, aabb.minZ).endVertex();
		worldRenderer.pos(aabb.maxX, aabb.maxY, aabb.minZ).endVertex();
		worldRenderer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).endVertex();
		worldRenderer.pos(aabb.maxX, aabb.minY, aabb.maxZ).endVertex();
		// North
		worldRenderer.pos(aabb.minX, aabb.minY, aabb.minZ).endVertex();
		worldRenderer.pos(aabb.maxX, aabb.minY, aabb.minZ).endVertex();
		worldRenderer.pos(aabb.maxX, aabb.maxY, aabb.minZ).endVertex();
		worldRenderer.pos(aabb.minX, aabb.maxY, aabb.minZ).endVertex();
		// South
		worldRenderer.pos(aabb.minX, aabb.minY, aabb.maxZ).endVertex();
		worldRenderer.pos(aabb.maxX, aabb.minY, aabb.maxZ).endVertex();
		worldRenderer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).endVertex();
		worldRenderer.pos(aabb.minX, aabb.maxY, aabb.maxZ).endVertex();
		Tessellator.getInstance().draw();

		GlStateManager.translate(realX, realY, realZ);
		if (!depth) {
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GlStateManager.depthMask(true);
		}
		GlStateManager.enableCull();
		GlStateManager.disableAlpha();
		GlStateManager.disableBlend();
		GlStateManager.enableTexture2D();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.popAttrib();
		GlStateManager.popMatrix();
	}
    
    public static void drawSelectionBoundingBox(AxisAlignedBB boundingBox)
	{
    	Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(3, DefaultVertexFormats.POSITION);
        worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
        worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ)
			.endVertex();
        worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ)
			.endVertex();
        worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ)
			.endVertex();
        worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ)
			.endVertex();
		tessellator.draw();
		worldRenderer.begin(3, DefaultVertexFormats.POSITION);
		worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ)
			.endVertex();
		worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ)
			.endVertex();
		worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ)
			.endVertex();
		worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ)
			.endVertex();
		worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ)
			.endVertex();
		tessellator.draw();
		worldRenderer.begin(1, DefaultVertexFormats.POSITION);
		worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ)
			.endVertex();
		worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ)
			.endVertex();
		worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ)
			.endVertex();
		worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ)
			.endVertex();
		worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ)
			.endVertex();
		worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ)
			.endVertex();
		worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ)
			.endVertex();
		worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ)
			.endVertex();
		tessellator.draw();
	}


    public static void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor)
    {
        float f = (float)(startColor >> 24 & 255) / 255.0F;
        float f1 = (float)(startColor >> 16 & 255) / 255.0F;
        float f2 = (float)(startColor >> 8 & 255) / 255.0F;
        float f3 = (float)(startColor & 255) / 255.0F;
        float f4 = (float)(endColor >> 24 & 255) / 255.0F;
        float f5 = (float)(endColor >> 16 & 255) / 255.0F;
        float f6 = (float)(endColor >> 8 & 255) / 255.0F;
        float f7 = (float)(endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos((double)right, (double)top, (double)-1).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos((double)left, (double)top, (double)-1).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos((double)left, (double)bottom, (double)-1).color(f5, f6, f7, f4).endVertex();
        worldrenderer.pos((double)right, (double)bottom, (double)-1).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void setTimeout(Runnable code, int ms) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                code.run();
            }
        }, ms);
    }

    public static final ResourceLocation grayBox = new ResourceLocation("skyblockfeatures","grayBox.png");
    public static void drawGraySquare(int x,int y,int width,int height) {
        Utils.GetMC().getTextureManager().bindTexture(grayBox);
        GL11.glColor4d(70, 70, 70, 0.5);
        GlStateManager.disableLighting();
        Utils.drawTexturedRect(x, y, width, height, 0, 1, 0, 1, GL11.GL_NEAREST);
    }

    public static void drawGraySquareWithBorder(int x,int y,int width,int height,int borderWidth) {
        drawGraySquare(x,y,width,height);

        Utils.GetMC().getTextureManager().bindTexture(grayBox);
        GL11.glColor4d(1, 1, 1, 0.8);
        GlStateManager.disableLighting();

        // Top
        Utils.drawTexturedRect(x, y, width, borderWidth, 0, 1, 0, 1, GL11.GL_NEAREST);

        // Right
        Utils.drawTexturedRect(x+width-borderWidth, y, borderWidth, height, 0, 1, 0, 1, GL11.GL_NEAREST);

        // Bottom
        Utils.drawTexturedRect(x, y+height-borderWidth, width, borderWidth, 0, 1, 0, 1, GL11.GL_NEAREST);

        // Left
        Utils.drawTexturedRect(x, y, borderWidth, height, 0, 1, 0, 1, GL11.GL_NEAREST);
    }
    
}