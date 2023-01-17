package mrfast.skyblockfeatures.features.impl.ItemFeatures;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.mojang.realmsclient.gui.ChatFormatting;

import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.commands.TerminalCommand;
import mrfast.skyblockfeatures.events.ChestSlotClickedEvent;
import mrfast.skyblockfeatures.events.GuiContainerEvent;
import mrfast.skyblockfeatures.events.GuiContainerEvent.TitleDrawnEvent;
import mrfast.skyblockfeatures.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HideGlass {
    public static Minecraft mc = Minecraft.getMinecraft();
    public static boolean on = true;

    @SubscribeEvent
    public void onChatMessage(ClientChatReceivedEvent event) {
        try {
            if(!skyblockfeatures.config.timestamps || event.type == 2) return;
            final String timestamp = new SimpleDateFormat("hh:mm").format(new Date());
            final IChatComponent msg =
                    new ChatComponentText("")
                            .appendText(ChatFormatting.DARK_GRAY+"["+timestamp+"] ")
                            .appendSibling(event.message);
            event.message = msg;
        } catch (Exception e) {
            //TODO: handle exception
        }
    }

    // Just cuz i think it looks better idk
    @SubscribeEvent
    public void onTooltipLow(ItemTooltipEvent event) {
        if(event.itemStack.getItem() instanceof ItemArmor && !Utils.inSkyblock) {
            ItemArmor a = (ItemArmor) event.itemStack.getItem();
            
            for(int i = 0; i < event.toolTip.size(); i++) {
                String line = Utils.cleanColour(event.toolTip.get(i));
                if(line.contains("ility")) {
                    event.toolTip.add(i+1, ChatFormatting.GRAY+"When equipped:");
                    if(a.getArmorMaterial() == ArmorMaterial.DIAMOND) {
                        event.toolTip.add(i+2, ChatFormatting.BLUE+" +2 Armor Toughness");
                        event.toolTip.add(i+3, ChatFormatting.BLUE+" +"+a.damageReduceAmount+" Armor");
                        event.toolTip.add(i+4, event.toolTip.get(i));
                        event.toolTip.set(i, "");
                        return;
                    }
                    event.toolTip.add(i+2, ChatFormatting.BLUE+" +"+a.damageReduceAmount+" Armor");
                    event.toolTip.add(i+3, event.toolTip.get(i));
                    event.toolTip.set(i, "");
                    return;
                } else if(line.contains("minecraft")) {
                    event.toolTip.add(i+1, ChatFormatting.GRAY+"When equipped:");
                    if(a.getArmorMaterial() == ArmorMaterial.DIAMOND) {
                        event.toolTip.add(i+2, ChatFormatting.BLUE+" +2 Armor Toughness");
                        event.toolTip.add(i+3, ChatFormatting.BLUE+" +"+a.damageReduceAmount+" Armor");
                        event.toolTip.add(i+4, event.toolTip.get(i));
                        event.toolTip.set(i, "");
                        return;
                    }
                    event.toolTip.add(i+2, ChatFormatting.BLUE+" +"+a.damageReduceAmount+" Armor");
                    event.toolTip.add(i+3, event.toolTip.get(i));
                    event.toolTip.set(i, "");
                    return;
                }
            }
        }

        if(Utils.inSkyblock && isEmptyGlassPane(event.itemStack)) {
            event.toolTip.clear();
        }
    }

    public static boolean isEmptyGlassPane(ItemStack itemStack) {
        return itemStack != null && (itemStack.getItem() == Item.getItemFromBlock(Blocks.stained_glass_pane)
                || itemStack.getItem() == Item.getItemFromBlock(Blocks.glass_pane)) && itemStack.hasDisplayName() && Utils.cleanColour(itemStack.getDisplayName().trim()).isEmpty();
    }

    @SubscribeEvent
    public void onGuiClose(GuiContainerEvent.CloseWindowEvent event) {
        TerminalCommand.start = 0;
        TerminalCommand.mazeIndex = 1;
    }
    
    @SubscribeEvent
    public void onSlotClick(ChestSlotClickedEvent event) {
        if(event.inventoryName.contains(ChatFormatting.GREEN+"✯")) {
            if(event.inventoryName.contains("Correct Panes")) {
                for(int slot : TerminalCommand.paneSlots) {
                    if(event.slot.slotNumber == slot) {
                        if(event.item.getUnlocalizedName().contains("red")) {
                            Utils.playLoudSound("note.pling", 2);
                            TerminalCommand.clicked.add(event.slot.slotNumber);
                            event.inventory.setInventorySlotContents(event.slot.slotNumber, new ItemStack(Blocks.stained_glass_pane, 1, 5).setStackDisplayName(" "));
                            if(TerminalCommand.clicked.size() == 14) {
                                Utils.SendMessage(ChatFormatting.GREEN+"You completed 'Correct all the panes!' in "+(Math.floor((System.currentTimeMillis()-TerminalCommand.start)/10)/100)+"s");
                                mc.thePlayer.closeScreen();
                            }
                            if(TerminalCommand.start == 0) {
                                TerminalCommand.start = System.currentTimeMillis();
                            }
                        }
                    }
                }
            }
            if(event.inventoryName.contains("Maze")) {
                if(event.slot.slotNumber == TerminalCommand.mazeSlots[TerminalCommand.mazeSlots.length-(int) TerminalCommand.mazeIndex]) {
                    if(event.item.getUnlocalizedName().contains("white")) {
                        Utils.playLoudSound("note.pling", 2);
                        TerminalCommand.clicked.add(event.slot.slotNumber);
                        event.inventory.setInventorySlotContents(event.slot.slotNumber, new ItemStack(Blocks.stained_glass_pane, 1, 5).setStackDisplayName(" "));
                        if(TerminalCommand.clicked.size() == TerminalCommand.mazeSlots.length) {
                            Utils.SendMessage(ChatFormatting.GREEN+"You completed 'Maze!' in "+(Math.floor((System.currentTimeMillis()-TerminalCommand.start)/10)/100)+"s");
                            mc.thePlayer.closeScreen();
                            TerminalCommand.mazeIndex = 0;
                        }
                        if(TerminalCommand.start == 0) {
                            TerminalCommand.start = System.currentTimeMillis();
                        }
                        TerminalCommand.mazeIndex++;
                    }
                }
            }
        }
        try {
            if(event.inventoryName.contains("✯")) event.setCanceled(true);
        } catch (Exception e) {
            //TODO: handle exception
        }
    }

    @SubscribeEvent
    public void onTitleDrawn(TitleDrawnEvent event) {
        if(!(event.gui instanceof GuiChest)) return;
        GuiChest gui = (GuiChest) event.gui;
        ContainerChest chest = (ContainerChest) gui.inventorySlots;
        IInventory inv = chest.getLowerChestInventory();
        String chestName = inv.getDisplayName().getUnformattedText().trim();
        if(chestName.contains("✯") && TerminalCommand.start!=0) {
            Utils.GetMC().fontRendererObj.drawString(chestName+" "+(Math.floor((System.currentTimeMillis()-TerminalCommand.start)/10)/100)+"s", 8, 6, 0);
        }
    }

}
