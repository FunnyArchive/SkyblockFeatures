package mrfast.skyblockfeatures.features.impl.overlays;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.core.structure.FloatPair;
import mrfast.skyblockfeatures.core.structure.GuiElement;
import mrfast.skyblockfeatures.utils.RenderUtil;
import mrfast.skyblockfeatures.utils.SBInfo;
import mrfast.skyblockfeatures.utils.Utils;
import mrfast.skyblockfeatures.utils.graphics.ScreenRenderer;
import mrfast.skyblockfeatures.utils.graphics.SmartFontRenderer;
import mrfast.skyblockfeatures.utils.graphics.colors.CommonColors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
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
    List<BlockPos> bruiserSpawns = new ArrayList<>(
        Arrays.asList(
            new BlockPos(-595,80,-190),
            new BlockPos(-575, 72, -201),
            new BlockPos(-560, 64, -220),
            new BlockPos(-554, 56, -237),
            new BlockPos(-571, 50, -240),
            new BlockPos(-585, 52, -232),
            new BlockPos(-96, 55, -216),
            new BlockPos(-578, 53, -214),
            new BlockPos(-598, 54, -201),
            // botoom area
            new BlockPos(-532, 37, -223),
            new BlockPos(-520, 37, -235),
            new BlockPos(-530, 37, -246),
            new BlockPos(-515, 38, -250),
            new BlockPos(-516, 38, -264),
            new BlockPos(-513, 37, -279),
            new BlockPos(-524, 44, -268),
            new BlockPos(-536, 48, -252),
            new BlockPos(-526, 37, -294),
            new BlockPos(-514, 38, -304),
            new BlockPos(-526, 38, -317)
        )
    );
    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        if(SBInfo.getInstance().location.contains("Dragons Nest") && skyblockfeatures.config.showZealotSpawns) {
            for(BlockPos pos:zealotSpawns) {
                if(pos != null) {
                    Color color = canSpawnZealots? new Color(0x55FF55):new Color(0xFF5555);
                    highlightBlock(color, pos.getX(),pos.getY(), pos.getZ(), 5.0D,event.partialTicks);
                }
            }
        }
        if(SBInfo.getInstance().location.contains("Bruiser") && skyblockfeatures.config.showZealotSpawns) {
            for(BlockPos pos:bruiserSpawns) {
                if(pos != null) {
                    Color color = canSpawnBruisers? new Color(0x55FF55):new Color(0xFF5555);
                    highlightBlock(color, pos.getX(),pos.getY()+1, pos.getZ(), 5.0D,event.partialTicks);
                }
            }
        }
        // Track Spawn lOcations
        // if(SBInfo.getInstance().location.contains("Bruiser")) {
        //     for(Entity e:bruisers.keySet()) {
        //         BlockPos pos = bruisers.get(e);
        //         MiscFeatures.drawParticleESP(
        //             new Color(0x34c3eb), 
        //                 Math.floor(pos.getX()) - Minecraft.getMinecraft().getRenderManager().viewerPosX,
        //                 Math.floor(pos.getY()) - Minecraft.getMinecraft().getRenderManager().viewerPosY, 
        //                 Math.floor(pos.getZ()) - Minecraft.getMinecraft().getRenderManager().viewerPosZ, event.partialTicks);
        //     }
        // }
    }


    public List<Entity> zealots = new ArrayList<>();
    public HashMap<Entity,BlockPos> bruisers = new HashMap<>();
    

    boolean canSpawnZealots = false;
    boolean canSpawnBruisers = false;

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if(Utils.GetMC().theWorld == null || Utils.GetMC().thePlayer == null) return;
        if(skyblockfeatures.config.showZealotSpawns && Utils.inSkyblock) {
            for(Entity entity:Utils.GetMC().theWorld.loadedEntityList) {
                if(entity instanceof EntityArmorStand && !zealots.contains(entity) && SBInfo.getInstance().location.contains("Dragons Nest")) {
                    if(entity.getCustomNameTag().contains("Zealot")) {
                        zealots.add(entity);
                        startZealotTimer = true;
                    }
                }
                if(entity instanceof EntityArmorStand && SBInfo.getInstance().location.contains("Bruiser") && bruisers.get(entity) == null) {
                    if(entity.getCustomNameTag().contains("Bruiser")) {
                        bruisers.put(entity, entity.getPosition());
                        startBruiserTimer = true;
                    }
                }
            }
        }
    }

    public static void highlightBlock(Color c, double d, double d1, double d2, double size,float ticks) {
        RenderUtil.drawOutlinedFilledBoundingBox(new AxisAlignedBB(d-size, d1+0.1, d2-size, d+size, d1-3, d2+size),c,ticks);
    }

    public static int zealotTicks = 0;
    public static int zealotHalfseconds = 20;
    public static boolean startZealotTimer = false;
    public static  String zealotDisplay = EnumChatFormatting.LIGHT_PURPLE + "Zealot Spawn: " + "10s";

    public static int bruiserTicks = 0;
    public static int bruiserHalfseconds = 20;
    public static boolean startBruiserTimer = false;
    public static  String bruiserDisplay = EnumChatFormatting.LIGHT_PURPLE + "Bruiser Spawn: " + "10s";
    private static final Minecraft mc = Minecraft.getMinecraft();
    RenderManager renderManager = mc.getRenderManager();
    
    @SubscribeEvent
    public void onSeconds(TickEvent.ClientTickEvent event) {
        if(Utils.inSkyblock && startZealotTimer && SBInfo.getInstance().location.contains("Dragons Nest") && skyblockfeatures.config.showZealotSpawns) {
            zealotTicks++;
            if (zealotTicks % 20 == 0) {
                zealotTicks = 0;
                zealotHalfseconds--;
            }
            if(zealotHalfseconds == 0) {
                zealotDisplay = EnumChatFormatting.LIGHT_PURPLE + "Zealot Spawn: " + EnumChatFormatting.GREEN + "Ready!";
                zealotHalfseconds = 20;
                startZealotTimer = false;
                canSpawnZealots = true;
                return;
            } else {
                canSpawnZealots = false;
            }
            zealotDisplay = EnumChatFormatting.LIGHT_PURPLE + "Zealot Spawn: "+zealotHalfseconds/2+"s";
        };
        if(Utils.inSkyblock && startBruiserTimer && SBInfo.getInstance().location.contains("Bruiser Hideout") && skyblockfeatures.config.showZealotSpawns) {
            bruiserTicks++;
            if (bruiserTicks % 20 == 0) {
                bruiserTicks = 0;
                bruiserHalfseconds--;
            }
            if(bruiserHalfseconds == 0) {
                bruiserDisplay = EnumChatFormatting.LIGHT_PURPLE + "Bruiser Spawn: " + EnumChatFormatting.GREEN + "Ready!";
                bruiserHalfseconds = 20;
                startBruiserTimer = false;
                canSpawnBruisers = true;
                return;
            } else {
                canSpawnBruisers = false;
            }
            bruiserDisplay = EnumChatFormatting.LIGHT_PURPLE + "Bruiser Spawn: "+bruiserHalfseconds/2+"s";
        };
    
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
            if (this.getToggled() && Minecraft.getMinecraft().thePlayer != null && mc.theWorld != null && (SBInfo.getInstance().location.contains("Dragons Nest") || SBInfo.getInstance().location.contains("Bruiser Hideout"))) {
                mc.fontRendererObj.drawStringWithShadow(zealotDisplay, 0, 0, 0xFFFFFF);
                mc.fontRendererObj.drawStringWithShadow(bruiserDisplay, 0, (float) (ScreenRenderer.fontRenderer.FONT_HEIGHT+0.1), 0xFFFFFF);
            }
        }
        @Override
        public void demoRender() {
            if(mc.thePlayer == null || !Utils.inSkyblock) return;
            Utils.GetMC().fontRendererObj.drawStringWithShadow(zealotDisplay, 0, 0, 0xFFFFFF);
            Utils.GetMC().fontRendererObj.drawStringWithShadow(bruiserDisplay, 0, (float) (ScreenRenderer.fontRenderer.FONT_HEIGHT+0.1), 0xFFFFFF);
        }

        @Override
        public boolean getToggled() {
            return Utils.inSkyblock && skyblockfeatures.config.showZealotSpawns;
        }

        @Override
        public int getHeight() {
            return (int) (ScreenRenderer.fontRenderer.FONT_HEIGHT*2.2);
        }

        @Override
        public int getWidth() {
            return 12 + ScreenRenderer.fontRenderer.getStringWidth(bruiserDisplay);
        }
    }
}