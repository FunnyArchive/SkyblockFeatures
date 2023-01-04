package mrfast.skyblockfeatures.features.impl.overlays;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.WordUtils;

import com.mojang.realmsclient.gui.ChatFormatting;

import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.core.AuctionUtil;
import mrfast.skyblockfeatures.events.GuiContainerEvent;
import mrfast.skyblockfeatures.events.SecondPassedEvent;
import mrfast.skyblockfeatures.features.impl.handlers.AuctionData;
import mrfast.skyblockfeatures.utils.ItemUtil;
import mrfast.skyblockfeatures.utils.NumberUtil;
import mrfast.skyblockfeatures.utils.StringUtils;
import mrfast.skyblockfeatures.utils.Utils;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class MissingTalismans {
    boolean inAccessoryBag = false;
    List<String> OwnedTalismans = new ArrayList<>();
    List<String> MissingTalismans = new ArrayList<>();
    List<String> FinalMissings = new ArrayList<>();
    
    @SubscribeEvent
    public void onSecond(GuiContainerEvent.CloseWindowEvent event) {
        OwnedTalismans.clear();
    }
    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if(!skyblockfeatures.config.showMissingAccessories) return;
        if(Utils.GetMC().currentScreen != null) {
            if(Utils.GetMC().currentScreen instanceof GuiChest) {
                GuiChest gui = (GuiChest) Utils.GetMC().currentScreen;
                ContainerChest chest = (ContainerChest) gui.inventorySlots;
                IInventory inv = chest.getLowerChestInventory();
                String chestName = inv.getDisplayName().getUnformattedText().trim();
                inAccessoryBag = chestName.contains("Accessory Bag");
            }
        } else {
            inAccessoryBag = false;
        }
        if(inAccessoryBag) {
            MissingTalismans.clear();
            FinalMissings.clear();
            for(String name:AuctionData.averageLowestBINs.keySet()) {
                if(name.endsWith("TALISMAN")||name.endsWith("RING")||name.endsWith("ARTIFACT")) {
                    if(name.contains("ETERNAL")) continue;
                    String id = name;
                    String[] args = id.split("_");
                    String accessoryName = id.replace(args[args.length-1], "");
                    if(!OwnedTalismans.contains(accessoryName) && !MissingTalismans.contains(accessoryName)) {
                        MissingTalismans.add(accessoryName);
                        FinalMissings.add(id);
                    }
                }
            }
            if(Utils.GetMC().currentScreen != null) {
                if(Utils.GetMC().currentScreen instanceof GuiChest) {
                    GuiChest gui = (GuiChest) Utils.GetMC().currentScreen;
                    ContainerChest chest = (ContainerChest) gui.inventorySlots;
                    IInventory inv = chest.getLowerChestInventory();
                    for(int i=0;i<53;i++) {
                        if(inv.getStackInSlot(i)!=null && !OwnedTalismans.contains(inv.getStackInSlot(i).getDisplayName())) {
                            try {
                                String id = ItemUtil.getSkyBlockItemID(inv.getStackInSlot(i));
                                String[] args = id.split("_");
                                String accessoryName = id.replace(args[args.length-1], "");
                                OwnedTalismans.add(accessoryName);
                            } catch (Exception e) {
                                // TODO: handle exception
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onDrawContainerTitle(GuiContainerEvent.TitleDrawnEvent.Post event) {
        if(!skyblockfeatures.config.showMissingAccessories) return;
        if (event.gui != null && event.gui instanceof GuiChest) {
            if(inAccessoryBag) {
                if(FinalMissings.size()>0) {
                    Utils.drawGraySquareWithBorder(180, 0, 200, FinalMissings.size()*Utils.GetMC().fontRendererObj.FONT_HEIGHT,3);
                    int index = 0;
                    Utils.GetMC().fontRendererObj.drawString(ChatFormatting.WHITE+"Missing Talismans ("+FinalMissings.size()+")", 190, index*(Utils.GetMC().fontRendererObj.FONT_HEIGHT+1)+10, -1);
                    index++;
                    for(String item:FinalMissings) {
                        String id = StringUtils.capitalizeString(item.toLowerCase()).replace("_", " ");
                        Double value = AuctionData.lowestBINs.get(item);
                        if(value!=null) {
                            String price = ChatFormatting.GOLD+" ("+(NumberUtil.nf.format(value))+")";
                            Utils.GetMC().fontRendererObj.drawString(id+price, 190, index*(Utils.GetMC().fontRendererObj.FONT_HEIGHT+1)+10, -1);
                            index++;
                        } else {
                            Utils.GetMC().fontRendererObj.drawString(id, 190, index*(Utils.GetMC().fontRendererObj.FONT_HEIGHT+1)+10, -1);
                            index++;
                        }
                    }
                }
            }
        }
    }
}
