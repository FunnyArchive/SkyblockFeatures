package mrfast.skyblockfeatures.features.impl.trackers;

import com.mojang.realmsclient.gui.ChatFormatting;

import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.core.structure.FloatPair;
import mrfast.skyblockfeatures.core.structure.GuiElement;
import mrfast.skyblockfeatures.events.SecondPassedEvent;
import mrfast.skyblockfeatures.features.impl.handlers.AuctionData;
import mrfast.skyblockfeatures.utils.NumberUtil;
import mrfast.skyblockfeatures.utils.SBInfo;
import mrfast.skyblockfeatures.utils.Utils;
import mrfast.skyblockfeatures.utils.graphics.ScreenRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class GhostTracker {
    private static final Minecraft mc = Minecraft.getMinecraft();

    static int Volta = 0;
    static int Sorrow = 0;
    static int Plasma = 0;
    static int Boots = 0;

    static boolean hidden = true;
    static int kills = 0;
    static int oldKills = 0;
    static int seconds = 0;
    static int totalSeconds = 0;
    
    @SubscribeEvent
    public void onload(WorldEvent.Load event) {
        try {
            seconds = 0;
            kills = 0;
            oldKills = 0;
            hidden = true;
            totalSeconds = 0;
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    @SubscribeEvent
    public void onSecond(SecondPassedEvent event) {
        if(Minecraft.getMinecraft().thePlayer != null && Minecraft.getMinecraft().theWorld != null) {
            if(oldKills == 0) {
                oldKills = kills;
            }
            if(!hidden) {
                totalSeconds++;
            }
            if(seconds >= 60) {
                if(oldKills == kills) {
                    hidden = true;
                    totalSeconds=0;
                }
                oldKills = kills;
                seconds = 0;
            }
        }
    }

    @SubscribeEvent
    public void onEntityDeath(LivingDeathEvent event) {
        Entity entity = event.entity;
        if(entity instanceof EntityCreeper) {
            if(Utils.GetMC().thePlayer.getDistanceToEntity(entity) < 10) {
                hidden = false;
                kills++;
            }
        }
    }

    @SubscribeEvent
    public void onDrawSlot(SecondPassedEvent event) {
        if(Utils.GetMC().thePlayer == null || !Utils.inSkyblock) return;
        for(int i=0;i<Utils.GetMC().thePlayer.inventory.mainInventory.length;i++) {
            if(Utils.GetMC().thePlayer.inventory.mainInventory[i] != null) {
                if(i == 0) {
                    Volta = 0;
                    Sorrow = 0;
                    Plasma = 0;
                    Boots = 0;
                }
                try {
                    ItemStack stack = Utils.GetMC().thePlayer.inventory.mainInventory[i];
                    String name = Utils.cleanColour(stack.getDisplayName());
        
                    if(AuctionData.getIdentifier(stack) != null) {
                        if(name.contains("Volta")) {
                            Volta+=stack.stackSize;
                        }
                        if(name.contains("Sorrow")) {
                            Sorrow+=stack.stackSize;
                        }
                        if(name.contains("Plasma")) {
                            Plasma+=stack.stackSize;
                        }
                        if(name.contains("Ghostly")) {
                            Boots+=stack.stackSize;
                        }
                    }
                } catch (Exception e) {
                    //TODO: handle exception
                }
            }
        }
    }
    static {
        new AutomatonTrackerGUI();
    }

    static String display = "";
    public static class AutomatonTrackerGUI extends GuiElement {
        public AutomatonTrackerGUI() {
            super("Ghost Tracker", new FloatPair(0.45052084f, 0.86944443f));
            skyblockfeatures.GUIMANAGER.registerElement(this);
        }

        @Override
        public void render() {
            if (this.getToggled() && Minecraft.getMinecraft().thePlayer != null && mc.theWorld != null && !hidden) {
                if(!SBInfo.getInstance().location.contains("Mist")) return;
                String[] lines = {
                    ChatFormatting.GREEN+"Time Elapsed: §r"+Utils.secondsToTime(totalSeconds),
                    ChatFormatting.GREEN+"Ghosts Killed: §r"+NumberUtil.nf.format(kills),
                    ChatFormatting.BLUE+"Volta: §r"+Volta,
                    ChatFormatting.BLUE+"Sorrow: §r"+Sorrow,
                    ChatFormatting.GOLD+"Plasma: §r"+Plasma,
                    ChatFormatting.LIGHT_PURPLE+"Ghostly Boots: §r"+Boots,
                    ChatFormatting.YELLOW+"Ghosts/Sorrow: §r"+(Sorrow>0?Math.round(kills/Sorrow):"Undefined"),
                };
                int lineCount = 0;
                for(String line:lines) {
                    Utils.GetMC().fontRendererObj.drawStringWithShadow(line, 0, lineCount*(mc.fontRendererObj.FONT_HEIGHT),0xFFFFFF);
                    lineCount++;
                }
            }
        }
        @Override
        public void demoRender() {
            if(mc.thePlayer == null || !Utils.inSkyblock) return;
            String[] lines = {
                ChatFormatting.GREEN+"Time Elapsed: §r27m 3s",
                ChatFormatting.GREEN+"Ghosts Killed: §r203",
                ChatFormatting.BLUE+"Volta: §r3",
                ChatFormatting.BLUE+"Sorrow: §r1",
                ChatFormatting.GOLD+"Plasma: §r2",
                ChatFormatting.LIGHT_PURPLE+"Ghostly Boots: §r1",
                ChatFormatting.YELLOW+"Ghosts/Sorrow: §r129",
            };
            int lineCount = 0;
            for(String line:lines) {
                Utils.GetMC().fontRendererObj.drawStringWithShadow(line, 0, lineCount*(mc.fontRendererObj.FONT_HEIGHT),0xFFFFFF);
                lineCount++;
            }
        }

        @Override
        public boolean getToggled() {
            return Utils.inSkyblock && skyblockfeatures.config.ghostTracker;
        }

        @Override
        public int getHeight() {
            return ScreenRenderer.fontRenderer.FONT_HEIGHT*7;
        }

        @Override
        public int getWidth() {
            return ScreenRenderer.fontRenderer.getStringWidth("Electron Transmitter: 10");
        }
    }
}
