package mrfast.skyblockfeatures.features.impl.ItemFeatures;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.mojang.realmsclient.gui.ChatFormatting;

import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.commands.TerminalCommand;
import mrfast.skyblockfeatures.events.ChestSlotClickedEvent;
import mrfast.skyblockfeatures.utils.Utils;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
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
        IChatComponent orignal = event.message;
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
        event.message = orignal;
    }

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

        if(isEmptyGlassPane(event.itemStack)) {
            event.toolTip.clear();
        }
    }

    public static boolean isEmptyGlassPane(ItemStack itemStack) {
        return itemStack != null && (itemStack.getItem() == Item.getItemFromBlock(Blocks.stained_glass_pane)
                || itemStack.getItem() == Item.getItemFromBlock(Blocks.glass_pane)) && itemStack.hasDisplayName() && Utils.cleanColour(itemStack.getDisplayName().trim()).isEmpty();
    }
    @SubscribeEvent
    public void onSlotClick(ChestSlotClickedEvent event) {
        if(event.inventoryName.contains("Practice Terminal")) {
            for(int slot : TerminalCommand.slots) {
                if(event.slot.slotNumber == slot) {
                    if(event.item.getUnlocalizedName().contains("red")) {
                        Utils.playLoudSound("note.pling", 2);
                        TerminalCommand.clicked.add(event.slot.slotNumber);
                        TerminalCommand.Terminal.setInventorySlotContents(event.slot.slotNumber, new ItemStack(Blocks.stained_glass_pane, 1, 5).setStackDisplayName(ChatFormatting.RESET+""));
                        if(TerminalCommand.clicked.size() == 14) {
                            Utils.SendMessage(ChatFormatting.GREEN+"You completed 'Correct all the panes!' in "+(System.currentTimeMillis()-TerminalCommand.start)+"ms");
                            mc.thePlayer.closeScreen();
                        }
                        if(TerminalCommand.start == 0) {
                            TerminalCommand.start = System.currentTimeMillis();
                        }
                    }
                }
            }
        }
        try {
            if(event.inventoryName.contains("✯")) event.setCanceled(true);
        } catch (Exception e) {
            //TODO: handle exception
        }
      
        if(isEmptyGlassPane(event.item)) {
            event.setCanceled(true);
        }
    }




    

}
