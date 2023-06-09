package mrfast.skyblockfeatures.utils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.util.vector.Vector2f;

import com.mojang.realmsclient.gui.ChatFormatting;

import gg.essential.elementa.components.UIRoundedRectangle;
import gg.essential.universal.UMatrixStack;
import mrfast.skyblockfeatures.utils.graphics.ScreenRenderer;
import mrfast.skyblockfeatures.utils.graphics.SmartFontRenderer;
import mrfast.skyblockfeatures.utils.graphics.colors.CommonColors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

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

    public static void drawTexturedRect(float x, float y, float width, float height, float uMin, float uMax, float vMin, float vMax, int filter) {
        GlStateManager.enableBlend();
        GL14.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
        drawTexturedRectNoBlend(x, y, width, height, uMin, uMax, vMin, vMax, filter);
        GlStateManager.disableBlend();
    }

    public static String capitalizeString(String string) {
        string = cleanColour(string);
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
    
    public static double randomNumber(int min,int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    public static void drawText(String text, float x, float y) {
        ScreenRenderer.fontRenderer.drawString(text, x, y, CommonColors.WHITE, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.OUTLINE);
    }
    
    public static void drawTextWithStyle(String text, float x, float y, int color) {
        Minecraft.getMinecraft().fontRendererObj.drawString(text,1, 0, 0x000000, false);
        Minecraft.getMinecraft().fontRendererObj.drawString(text, -1, 0, 0x000000, false);
        Minecraft.getMinecraft().fontRendererObj.drawString(text, 0, 1, 0x000000, false);
        Minecraft.getMinecraft().fontRendererObj.drawString(text, 0, -1, 0x000000, false);
        Minecraft.getMinecraft().fontRendererObj.drawString(text, 0, 0, color, false);
    }

    public static void drawTextWithStyle2(String text, float x, float y) {
        String shadowText = Utils.cleanColour(text);
        Minecraft.getMinecraft().fontRendererObj.drawString(shadowText,1, 0, 0x000000, false);
        Minecraft.getMinecraft().fontRendererObj.drawString(shadowText, -1, 0, 0x000000, false);
        Minecraft.getMinecraft().fontRendererObj.drawString(shadowText, 0, 1, 0x000000, false);
        Minecraft.getMinecraft().fontRendererObj.drawString(shadowText, 0, -1, 0x000000, false);
        Minecraft.getMinecraft().fontRendererObj.drawString(text, 0, 0, 0xFFFFFF, false);
    }

    public static void drawTextWithStyle3(String text, float x, float y) {
        String shadowText = Utils.cleanColour(text);
        Minecraft.getMinecraft().fontRendererObj.drawString(shadowText,x+1, y, 0x000000, false);
        Minecraft.getMinecraft().fontRendererObj.drawString(shadowText, x-1, y, 0x000000, false);
        Minecraft.getMinecraft().fontRendererObj.drawString(shadowText, x, y+1, 0x000000, false);
        Minecraft.getMinecraft().fontRendererObj.drawString(shadowText, x, y-1, 0x000000, false);
        Minecraft.getMinecraft().fontRendererObj.drawString(text, x, y, 0xFFFFFF, false);
    }

    public static String msToDuration(long value) {
        long currentTime = System.currentTimeMillis();
        long age = currentTime - value;
        String ageString = DurationFormatUtils.formatDuration(age, "d") + "d";
        if ("0d".equals(ageString)) {
            ageString = DurationFormatUtils.formatDuration(age, "H") + "h";
            if ("0h".equals(ageString)) {
                ageString = DurationFormatUtils.formatDuration(age, "m") + "m";
                if ("0m".equals(ageString)) {
                    ageString = DurationFormatUtils.formatDuration(age, "s") + "s";
                    if ("0s".equals(ageString)) {
                        ageString = age + "ms";
                    }
                }
            }
        }
        return ageString;
    }
    
    public static String secondsToTime(int seconds) {
        String time = "";
        int sec = seconds % 60;
        int min = (seconds / 60)%60;
        int hours = (seconds/60)/60;
        if(seconds>3600) time=Math.floor(hours)+"h ";
        if(seconds>60) time+=Math.floor(min)+"m ";
        time+=Math.floor(sec)+"s";
        return time.replace(".0", "");
    }

    public static String[] getListOfPlayerUsernames() {
        final Collection<NetworkPlayerInfo> players = Utils.GetMC().getNetHandler().getPlayerInfoMap();
        final List<String> list = new ArrayList<>();
        for (final NetworkPlayerInfo info : players) {
            if(!info.getGameProfile().getName().contains("!")) list.add(info.getGameProfile().getName());
        }
        return list.toArray(new String[0]);
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

    public static String cleanColourNotModifiers(String in) {
        return in.replaceAll("(?i)\\u00A7[0-9a-f]", "");
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


    public static void SendMessage(String string) {
        if (Utils.GetMC().ingameGUI != null || Utils.GetMC().thePlayer == null) {
            Utils.GetMC().ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(ChatFormatting.AQUA+"[SBF] "+ChatFormatting.RESET+string));
        }
    }
    public static void SendMessage(Integer string) {
        if (Utils.GetMC().ingameGUI != null || Utils.GetMC().thePlayer == null) {
            Utils.GetMC().ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(ChatFormatting.AQUA+"[SBF] "+ChatFormatting.RESET+string));
        }
    }
    public static void SendMessage(Double string) {
        if (Utils.GetMC().ingameGUI != null || Utils.GetMC().thePlayer == null) {
            Utils.GetMC().ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(ChatFormatting.AQUA+"[SBF] "+ChatFormatting.RESET+string));
        }
    }
    public static void SendMessage(IChatComponent msg) {
        if (Utils.GetMC().ingameGUI != null || Utils.GetMC().thePlayer == null) {
            ChatComponentText prefix = new ChatComponentText(EnumChatFormatting.AQUA+"[SBF] "+EnumChatFormatting.RESET);
            Utils.GetMC().thePlayer.addChatMessage(
                new ChatComponentText("")
                .appendSibling(prefix)
                .appendSibling(msg)
            );
        }
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
    
    public static String cleanColour(String in) {
        return in.replaceAll("(?i)\\u00A7.", "");
    }
    
    public static Minecraft GetMC()
    {
        return mc;
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
    
	public static int getDungeonFloor() {
        String floor="";
        for (String l : ScoreboardUtil.getSidebarLines()) {
            String line = ScoreboardUtil.cleanSB(l);
            if(line.contains("Catacombs")) {
                floor = line;
            }
        }

        if(floor.replaceAll("[^0-9]", "") != "") {
            return Integer.parseInt(floor.replaceAll("[^0-9]", ""));
        } else {
            return 0;
        }
    }

    public static boolean isNPC(Entity entity) {
        if(entity instanceof EntityPlayer) {
            EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
            return entity.getUniqueID().version() == 2 && entityLivingBase.getHealth() == 20.0F && !entityLivingBase.isPlayerSleeping() && Utils.inSkyblock;
        } else return false;
    }

    public static void setTimeout(Runnable code, int ms) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                code.run();
            }
        }, ms);
    }

    public static void drawLine(int x1, int y1, int x2, int y2,Color color,float width) {
        GlStateManager.disableLighting();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        
		Vector2f vec = new Vector2f(x2 - x1, y2 - y1);
		vec.normalise(vec);
		Vector2f side = new Vector2f(vec.y, -vec.x);
        
		GL11.glLineWidth(width);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GlStateManager.color(color.getRed(), color.getGreen(), color.getBlue(),(float) 0.3);

		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        worldrenderer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
        worldrenderer.pos(x1 - side.x + side.x, y1 - side.y + side.y, 0.0D).endVertex();
        worldrenderer.pos(x2 - side.x + side.x, y2 - side.y + side.y, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
	}
    

    public static void drawLineInGui(int x1, int y1, int x2, int y2,Color color,float width,double d) {
        // GlStateManager.color(0x33 / 255f, 0xee / 255f, 0xdd / 255f, 1f);
        GlStateManager.disableLighting();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.translate(0, 0, 700);

		Vector2f vec = new Vector2f(x2 - x1, y2 - y1);
		vec.normalise(vec);
		Vector2f side = new Vector2f(vec.y, -vec.x);
        
		GL11.glLineWidth(width);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GlStateManager.color(color.getRed(), color.getGreen(), color.getBlue(),(float) d);

		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        worldrenderer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
        worldrenderer.pos(x1 - side.x + side.x, y1 - side.y + side.y, 0.0D).endVertex();
        worldrenderer.pos(x2 - side.x + side.x, y2 - side.y + side.y, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.translate(0, 0, -700);
        GlStateManager.enableTexture2D();
	}

    public static void drawGraySquareWithBorder(int x,int y,int width,int height,int borderWidth) {
        UIRoundedRectangle.Companion.drawRoundedRectangle(new UMatrixStack(),x, y, x+width, height+2, 5, new Color(0,0,0,125));
        UIRoundedRectangle.Companion.drawRoundedRectangle(new UMatrixStack(),x-2, y-2, x+width+2, height+2+2, 5, new Color(55,55,55,125));
    }
    public static void drawGraySquare(int x,int y,int width,int height,int borderWidth, Color c) {
        UIRoundedRectangle.Companion.drawRoundedRectangle(new UMatrixStack(),x, y, x+width, height, 5, c);
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
    public static final double DEG_TO_RAD = Math.PI / 180.0;
    public static final double RAD_TO_DEG = 180.0 / Math.PI;

    public static Vec3 getVectorFromRotation(float yaw, float pitch) {
        float f = MathHelper.cos(-yaw * (float) DEG_TO_RAD - (float) Math.PI);
        float f1 = MathHelper.sin(-yaw * (float) DEG_TO_RAD - (float) Math.PI);
        float f2 = -MathHelper.cos(-pitch * (float) DEG_TO_RAD);
        float f3 = MathHelper.sin(-pitch * (float) DEG_TO_RAD);
        return new Vec3( f1 * f2, f3, f * f2);
    }
    
    public static List<Vec3> vectorsToRaytrace (int vectorQuantity) {
        //real # of vectors is vectorQuantity^2
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.thePlayer;
        List<Vec3> vectorList = new ArrayList<>();
        //get vector location of player's eyes
        Vec3 eyes = new Vec3(player.posX, player.posY + (double)player.getEyeHeight(), player.posZ);
        float aspectRatio = (float) mc.displayWidth / (float) mc.displayHeight;

        //Vertical FOV: Minecraft FOV setting multiplied by FOV modifier (sprinting, speed effect, etc)
        double fovV = mc.gameSettings.fovSetting * mc.thePlayer.getFovModifier();
        //Horizontal FOV: Thanks Minecraft for being weird and making it this complicated to calculate
        double fovH = Math.atan(aspectRatio * Math.tan(fovV * DEG_TO_RAD / 2)) * 2 * RAD_TO_DEG;

        float verticalSpacing = (float) (fovV * 0.8 / vectorQuantity); // * 0.8 to leave some boundary space
        float horizontalSpacing = (float) (fovH * 0.9 / vectorQuantity); // * 0.9 to leave some boundary space

        float playerYaw = player.rotationYaw;
        float playerPitch = player.rotationPitch;

        if (mc.gameSettings.thirdPersonView == 2) {
            //dumb but easy method of modifying vector direction if player is in reverse 3rd person
            //does not account for the increased 3rd person FOV, but all vectors are within player view so who cares
            playerYaw = playerYaw + 180.0F;
            playerPitch = -playerPitch;
        }

        for (float h = (float) -(vectorQuantity - 1) / 2; h <= (float) (vectorQuantity - 1) / 2; h++) {
            for (float v = (float) -(vectorQuantity - 1) / 2; v <= (float) (vectorQuantity - 1) / 2; v++) {
                float yaw = h * horizontalSpacing;
                float pitch = v * verticalSpacing;

                /*
                yaw and pitch are evenly spread out, but yaw needs to be scaled because MC FOV stretching weird.
                "* ((playerPitch*playerPitch/8100)+1)" because yaw otherwise doesn't get complete scan at higher pitches.
                "/ (Math.abs(v/(vectorQuantity))+1)" because Higher FOVs do not stretch out the corners of the screen as
                much as the rest of the screen, which would otherwise cause corner vectors to be outside FOV
                */
                float yawScaled = yaw  * ((playerPitch*playerPitch/8100)+1) / (Math.abs(v/(vectorQuantity))+1);

                //turn rotation into vector
                Vec3 direction = getVectorFromRotation(yawScaled + playerYaw, pitch + playerPitch);

                //add the new direction vector * 64 (meaning when the vector is raytraced, it will return the first
                // block up to 64 blocks away) to the eyes vector to create the vector which will be raytraced
                vectorList.add(eyes.addVector(direction.xCoord * 64, direction.yCoord * 64, direction.zCoord * 64));
            }
        }
        return vectorList;
    }

    public static Iterable<BlockPos> getBlocksWithinRangeAtSameY(BlockPos center, int radius, int y) {
        BlockPos corner1 = new BlockPos(center.getX() - radius, y, center.getZ() - radius);
        BlockPos corner2 = new BlockPos(center.getX() + radius, y, center.getZ() + radius);
        return BlockPos.getAllInBox(corner1, corner2);
    }
}