package mrfast.skyblockfeatures.features.impl.ItemFeatures;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.lwjgl.input.Keyboard;

import com.mojang.realmsclient.gui.ChatFormatting;

import jline.Terminal;
import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.commands.TerminalCommand;
import mrfast.skyblockfeatures.commands.getNbtCommand;
import mrfast.skyblockfeatures.core.Config;
import mrfast.skyblockfeatures.events.ChestSlotClickedEvent;
import mrfast.skyblockfeatures.events.GuiContainerEvent;
import mrfast.skyblockfeatures.events.GuiContainerEvent.TitleDrawnEvent;
import mrfast.skyblockfeatures.utils.ItemUtil;
import mrfast.skyblockfeatures.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
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
        if(Utils.inSkyblock && skyblockfeatures.config.showSkyblockID) {
            for(int i = 0; i < event.toolTip.size(); i++) {
                String line = Utils.cleanColour(event.toolTip.get(i));
                if(line.contains("minecraft:")) {
                    event.toolTip.add(i+1,ChatFormatting.DARK_GRAY+"ID: "+ItemUtil.getSkyBlockItemID(event.itemStack));
                    if(Utils.GetMC().thePlayer.getName().equals("Skyblock_Lobby")) {
                        if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)&&Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
                            NBTTagCompound tag = event.itemStack.getTagCompound();
                            if(tag!=null) {
                                event.toolTip.add(i+1,ChatFormatting.DARK_GRAY+"DATA: "+getNbtCommand.prettyPrintNBT(tag));
                            }
                        } else if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
                            NBTTagCompound tag = ItemUtil.getExtraAttributes(event.itemStack);
                            if(tag!=null) {
                                event.toolTip.add(i+1,ChatFormatting.DARK_GRAY+"DATA: "+getNbtCommand.prettyPrintNBT(tag));
                            }
                        }
                    }
                    break;
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
                            event.inventory.setInventorySlotContents(event.slot.slotNumber, new ItemStack(Blocks.stained_glass_pane, 1, 5).setStackDisplayName(ChatFormatting.RESET+""));
                            if(TerminalCommand.clicked.size() == 14) {
                                Utils.SendMessage(ChatFormatting.GREEN+"You completed 'Correct all the panes!' in "+(Math.floor((System.currentTimeMillis()-TerminalCommand.start)/10)/100)+"s");
                                mc.thePlayer.closeScreen();
                            }
                            if(TerminalCommand.start == 0) {
                                TerminalCommand.start = System.currentTimeMillis();
                            }
                        }
                        if(event.item.getUnlocalizedName().contains("lime") && TerminalCommand.clicked.contains(event.slot.slotNumber)) {
                            Utils.playLoudSound("note.pling", 2);
                            TerminalCommand.clicked.remove(TerminalCommand.clicked.indexOf(event.slot.slotNumber));
                            event.inventory.setInventorySlotContents(event.slot.slotNumber, new ItemStack(Blocks.stained_glass_pane, 1, 14).setStackDisplayName(ChatFormatting.RESET+""));
                        }
                    }
                }
            }
            if(event.inventoryName.contains("Maze")) {
                if(event.slot.slotNumber == TerminalCommand.mazeSlots[TerminalCommand.mazeSlots.length-(int) TerminalCommand.mazeIndex]) {
                    if(event.item.getUnlocalizedName().contains("white")) {
                        Utils.playLoudSound("note.pling", 2);
                        TerminalCommand.clicked.add(event.slot.slotNumber);
                        event.inventory.setInventorySlotContents(event.slot.slotNumber, new ItemStack(Blocks.stained_glass_pane, 1, 5).setStackDisplayName(ChatFormatting.RESET+""));
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
            if(event.inventoryName.contains("Click in order") && event.item.getUnlocalizedName().contains("red")) {
                if(event.item.stackSize==TerminalCommand.orderNumber) {
                    if(TerminalCommand.orderNumber==14) {
                        Utils.SendMessage(ChatFormatting.GREEN+"You completed 'Click in order!' in "+(Math.floor((System.currentTimeMillis()-TerminalCommand.start)/10)/100)+"s");
                        mc.thePlayer.closeScreen();
                        TerminalCommand.orderNumber = 1;
                    }
				    event.inventory.setInventorySlotContents(event.slot.slotNumber, new ItemStack(Blocks.stained_glass_pane, event.item.stackSize, 5).setStackDisplayName(ChatFormatting.RESET+""));
                    Utils.playLoudSound("note.pling", 2);
                    TerminalCommand.orderNumber++;
                    if(TerminalCommand.start == 0) {
                        TerminalCommand.start = System.currentTimeMillis();
                    }
                } else {
                    mc.thePlayer.closeScreen();
                    Utils.SendMessage(ChatFormatting.RED+"You failed 'Click in order!'");
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

    // Debug Mode to see all the slots ids
    @SubscribeEvent
    public void onDrawSlots(GuiContainerEvent.DrawSlotEvent.Pre event) {
        if (event.gui instanceof GuiChest ) {
            GuiChest gui = (GuiChest) event.gui;
            ContainerChest chest = (ContainerChest) gui.inventorySlots;
            if(Utils.GetMC().thePlayer.getName().equals("Skyblock_Lobby")) {
                if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)&&Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
                    for(int i=0;i<chest.inventorySlots.size();i++) {
                        
                        int x = chest.inventorySlots.get(i).xDisplayPosition;
                        int y = chest.inventorySlots.get(i).yDisplayPosition;
                        GlStateManager.pushMatrix();
                        GlStateManager.translate(0, 0, 700);
                        Utils.drawTextWithStyle3(ChatFormatting.GREEN+""+i+"", x+6, y+6);
                        GlStateManager.popMatrix();
                    }
                }
            }
        }
    }
}
