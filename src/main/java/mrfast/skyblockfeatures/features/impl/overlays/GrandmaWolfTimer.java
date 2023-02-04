package mrfast.skyblockfeatures.features.impl.overlays;
import java.util.List;

import com.mojang.realmsclient.gui.ChatFormatting;

import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.core.structure.FloatPair;
import mrfast.skyblockfeatures.core.structure.GuiElement;
import mrfast.skyblockfeatures.events.GuiContainerEvent;
import mrfast.skyblockfeatures.utils.ItemUtil;
import mrfast.skyblockfeatures.utils.Utils;
import mrfast.skyblockfeatures.utils.graphics.ScreenRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class GrandmaWolfTimer {
    public static Minecraft mc = Utils.GetMC();
    public static double SecondsRemaining = 0;
    public static double fiveComboSeconds = 0;
    public double TenComboSeconds = 0;
    public double FifteenComboSeconds = 0;
    public double TwentyComboSeconds = 0;
    public double TwentyFiveComboSeconds = 0;
    public double ThirtyComboSeconds = 0;
    public double currentCombo = 0;
    @SubscribeEvent
    public void onEntityDeath(LivingDeathEvent event) {
        if(!skyblockfeatures.config.GrandmaWolfTimer) return;
        if(Utils.GetMC().thePlayer.getDistanceToEntity(event.entity)<10 && Utils.GetMC().thePlayer.canEntityBeSeen(event.entity)) {
            SecondsRemaining = currentCombo;
        }
    }

    @SubscribeEvent
    public void onDrawSlots(GuiContainerEvent.DrawSlotEvent.Pre event) {
        if(!skyblockfeatures.config.GrandmaWolfTimer) return;
        if (event.gui instanceof GuiChest ) {
            GuiChest gui = (GuiChest) event.gui;
            ContainerChest chest = (ContainerChest) gui.inventorySlots;
            IInventory inv = chest.getLowerChestInventory();
            String chestName = inv.getDisplayName().getUnformattedText().trim();
            if(chestName.contains("Pets")) {
                if(event.slot.getHasStack()) {
                    ItemStack stack = event.slot.getStack();
                    if(stack.getDisplayName().contains("Grandma")) {
                        List<String> lore = ItemUtil.getItemLore(stack);
                        for(String line:lore) {
                            String raw = Utils.cleanColour(line);
                            if(!raw.contains("last")) continue;
                            String delayString = raw.split("last")[1];
                            Double secondsDelay = Double.parseDouble(delayString.replaceAll("[^0-9]", ""))/10;
                            if(raw.contains("15 Combo")) {
                                FifteenComboSeconds=secondsDelay;
                            }
                            else if(raw.contains("20 Combo")) {
                                TwentyComboSeconds=secondsDelay;
                            }
                            else if(raw.contains("25 Combo")) {
                                TwentyFiveComboSeconds=secondsDelay;
                            }
                            else if(raw.contains("30 Combo")) {
                                ThirtyComboSeconds=secondsDelay;
                            }
                            else if(raw.contains("10 Combo")) {
                                TenComboSeconds=secondsDelay;
                            }
                            else if(raw.contains("5 Combo")) {
                                fiveComboSeconds=secondsDelay;
                            }
                        }
                        if(((int) (fiveComboSeconds*10))!=skyblockfeatures.config.gMaWolf5Second) {
                            Utils.SendMessage(ChatFormatting.GREEN+"Updated Grandma Wolf combo times");
                        }
                        skyblockfeatures.config.gMaWolf5Second=(int) (fiveComboSeconds*10);
                        skyblockfeatures.config.gMaWolf10Second=(int) (TenComboSeconds*10);
                        skyblockfeatures.config.gMaWolf15Second=(int) (FifteenComboSeconds*10);
                        skyblockfeatures.config.gMaWolf20Second=(int) (TwentyComboSeconds*10);
                        skyblockfeatures.config.gMaWolf25Second=(int) (TwentyFiveComboSeconds*10);
                        skyblockfeatures.config.gMaWolf30Second=(int) (ThirtyComboSeconds*10);
                    }
                }
            }
        }
    }
    @SubscribeEvent
    public void onTick(ClientTickEvent event) {
        if(!skyblockfeatures.config.GrandmaWolfTimer) return;
        System.out.println(skyblockfeatures.config.gMaWolf5Second);
        if(Utils.GetMC().theWorld==null) return;
        if(fiveComboSeconds==0 && skyblockfeatures.config.gMaWolf5Second!=0) {
            fiveComboSeconds=skyblockfeatures.config.gMaWolf5Second/10;
            TenComboSeconds=skyblockfeatures.config.gMaWolf10Second/10;
            FifteenComboSeconds=skyblockfeatures.config.gMaWolf15Second/10;
            TwentyComboSeconds=skyblockfeatures.config.gMaWolf20Second/10;
            TwentyFiveComboSeconds=skyblockfeatures.config.gMaWolf25Second/10;
            ThirtyComboSeconds=skyblockfeatures.config.gMaWolf30Second/10;
        }
        
        if(SecondsRemaining>0.05) SecondsRemaining-=0.05/2;
    }

    @SubscribeEvent
    public void onChatMessage(ClientChatReceivedEvent event) {
        if(!skyblockfeatures.config.GrandmaWolfTimer) return;
        String raw = event.message.getUnformattedText();
        if(raw.contains("Your Kill Combo has expired! You reached a ")) {
            SecondsRemaining=0;
            currentCombo=0;
        }
        if(raw.contains("+5 Kill Combo")) {
            SecondsRemaining=fiveComboSeconds;
            currentCombo = fiveComboSeconds;
        }
        if(raw.contains("+10 Kill Combo")) {
            SecondsRemaining=TenComboSeconds;
            currentCombo = TenComboSeconds;
        }
        if(raw.contains("+15 Kill Combo")) {
            SecondsRemaining=FifteenComboSeconds;
            currentCombo = FifteenComboSeconds;
        }
        if(raw.contains("+20 Kill Combo")) {
            SecondsRemaining=TwentyComboSeconds;
            currentCombo = TwentyComboSeconds;
        }
        if(raw.contains("+25 Kill Combo")) {
            SecondsRemaining=TwentyFiveComboSeconds;
            currentCombo = TwentyFiveComboSeconds;
        }
        if(raw.contains("+30 Kill Combo")) {
            SecondsRemaining=ThirtyComboSeconds;
            currentCombo = ThirtyComboSeconds;
        }
    }

    static {
        new gWolfTimer();
    }   

    public static class gWolfTimer extends GuiElement {
        public gWolfTimer() {
            super("Grandma Wolf Timer", new FloatPair(0.6125f, 0.675f));
            skyblockfeatures.GUIMANAGER.registerElement(this);
        }

        @Override
        public void render() {
            if(mc.thePlayer == null || !Utils.inSkyblock || Utils.GetMC().theWorld==null || !skyblockfeatures.config.GrandmaWolfTimer) return;
            double remaining = Math.floor(SecondsRemaining*100)/100;
            String time = (remaining+"").length()==3?remaining+"0":remaining+"";
            if(SecondsRemaining>0.05) Utils.drawTextWithStyle3(ChatFormatting.GREEN+""+time+"s", 0, 0);
            else if(fiveComboSeconds==0) {
                Utils.drawTextWithStyle3(ChatFormatting.RED+"Open Pets Menu", 0, 0);
            }
        }

        @Override
        public void demoRender() {
            if(mc.thePlayer == null || !Utils.inSkyblock) return;
            Utils.drawTextWithStyle3(ChatFormatting.GREEN+"5.231s", 0, 0);
        }

        @Override
        public boolean getToggled() {
            return skyblockfeatures.config.GrandmaWolfTimer;
        }

        @Override
        public int getHeight() {
            return ScreenRenderer.fontRenderer.FONT_HEIGHT;
        }

        @Override
        public int getWidth() {
            return ScreenRenderer.fontRenderer.getStringWidth("5.231s");
        }
    }
}
