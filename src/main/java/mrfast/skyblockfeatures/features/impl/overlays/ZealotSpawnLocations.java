package mrfast.skyblockfeatures.features.impl.overlays;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.core.structure.FloatPair;
import mrfast.skyblockfeatures.core.structure.GuiElement;
import mrfast.skyblockfeatures.utils.SBInfo;
import mrfast.skyblockfeatures.utils.Utils;
import mrfast.skyblockfeatures.utils.graphics.ScreenRenderer;
import mrfast.skyblockfeatures.utils.graphics.SmartFontRenderer;
import mrfast.skyblockfeatures.utils.graphics.colors.CommonColors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ZealotSpawnLocations {
    List<BlockPos> zealotSpawns = new ArrayList<>(
        Arrays.asList(
        new BlockPos(-646,5,-274),
        new BlockPos(-633,5,-277),
        new BlockPos(-639,7,-305),
        new BlockPos(-631,5,-327),
        new BlockPos(-619,6,-313),
        new BlockPos(-665,10,-313),
        new BlockPos(-632,5,-260),
        new BlockPos(-630,7,-229),
        new BlockPos(-647,5,-221),
        new BlockPos(-684,5,-261),
        new BlockPos(-699,6,-263),
        new BlockPos(-683,5,-292),
        new BlockPos(-698,5,-319),
        new BlockPos(-714,5,-289),
        new BlockPos(-732,5,-295),
        new BlockPos(-731,6,-275)));

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        if(SBInfo.getInstance().location.contains("Dragons Nest") && skyblockfeatures.config.showZealotSpawns) {
            for(BlockPos pos:zealotSpawns) {
                Color color = canSpawnZealots? new Color(0x55FF55):new Color(0xFF5555);
                drawParticleESP(color, pos.getX() - Minecraft.getMinecraft().getRenderManager().viewerPosX,pos.getY() - Minecraft.getMinecraft().getRenderManager().viewerPosY, pos.getZ() - Minecraft.getMinecraft().getRenderManager().viewerPosZ, 5.0D);
            }
        }
    }


    public List<Entity> zealots = new ArrayList<>();
    boolean canSpawnZealots = false;
    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if(SBInfo.getInstance().location.contains("Dragons Nest") && skyblockfeatures.config.showZealotSpawns) {
            for(Entity entity:Utils.GetMC().theWorld.loadedEntityList) {
                if(entity instanceof EntityArmorStand && !zealots.contains(entity)) {
                    if(entity.getCustomNameTag().contains("Zealot")) {
                        zealots.add(entity);
                        startTimer = true;
                    }
                }
            }
        }
    }

    public static void drawParticleESP(Color c, double d, double d1, double d2, double size) {
        GlStateManager.enableBlend();
        GlStateManager.disableLighting();
        GlStateManager.enableCull();
        GlStateManager.disableTexture2D();
        drawBoundingBox(c, new AxisAlignedBB(d-size, d1+0.1, d2-size, d+size, d1-3, d2+size));
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.disableCull();
     }

    public static void drawBoundingBox(Color c, AxisAlignedBB aa) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        int color = c.getRGB();
        float a = (float)(color >> 24 & 255) / 255.0F;
        a = (float)((double)a * 0.15D);
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

    public static int ticks = 0;
    public static int halfseconds = 20;
    public static boolean startTimer = false;
    public static  String display = EnumChatFormatting.LIGHT_PURPLE + "Zealot Spawn: " + "10s";
    private static final Minecraft mc = Minecraft.getMinecraft();
    RenderManager renderManager = mc.getRenderManager();
    
    @SubscribeEvent
    public void onSeconds(TickEvent.ClientTickEvent event) {
        if(!Utils.inSkyblock || !startTimer || !SBInfo.getInstance().location.contains("Dragons Nest") || !skyblockfeatures.config.showZealotSpawns) return;
        ticks++;
        if (ticks % 20 == 0) {
            ticks = 0;
            halfseconds--;
        }
        if(halfseconds == 0) {
            display = EnumChatFormatting.LIGHT_PURPLE + "Zealot Spawn: " + EnumChatFormatting.GREEN + "Ready!";
            halfseconds = 20;
            startTimer = false;
            canSpawnZealots = true;
            return;
        } else {
            canSpawnZealots = false;
        }
        display = EnumChatFormatting.LIGHT_PURPLE + "Zealot Spawn: "+halfseconds/2+"s";
    }

    static {
        new JerryTimerGUI();
    }   
    public static class JerryTimerGUI extends GuiElement {
        public JerryTimerGUI() {
            super("Zealot Timer", new FloatPair(0, 5));
            skyblockfeatures.GUIMANAGER.registerElement(this);
        }

        @Override
        public void render() {
            if(mc.thePlayer == null || !Utils.inSkyblock) return;
            if (this.getToggled() && Minecraft.getMinecraft().thePlayer != null && mc.theWorld != null) {
                mc.fontRendererObj.drawString(display, 0, 0, 0xFFFFFF, true);
            }
        }
        @Override
        public void demoRender() {
            if(mc.thePlayer == null || !Utils.inSkyblock) return;
            ScreenRenderer.fontRenderer.drawString(display, 0, 0, CommonColors.WHITE, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NORMAL);
        }

        @Override
        public boolean getToggled() {
            return Utils.inSkyblock && SBInfo.getInstance().location.contains("Dragons Nest") && skyblockfeatures.config.showZealotSpawns;
        }

        @Override
        public int getHeight() {
            return (int) (ScreenRenderer.fontRenderer.FONT_HEIGHT*1.2);
        }

        @Override
        public int getWidth() {
            return 12 + ScreenRenderer.fontRenderer.getStringWidth(display);
        }
    }
}