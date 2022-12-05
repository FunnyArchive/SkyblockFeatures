package mrfast.skyblockfeatures.features.impl.dungeons;

import java.util.ArrayList;
import java.util.HashMap;

import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.events.GuiContainerEvent;
import mrfast.skyblockfeatures.features.impl.handlers.AuctionData;
import mrfast.skyblockfeatures.utils.ItemUtil;
import mrfast.skyblockfeatures.utils.NumberUtil;
import mrfast.skyblockfeatures.utils.StringUtils;
import mrfast.skyblockfeatures.utils.Utils;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Based off of chest profit from code by Quantizr
 * Licensed under GNU GPL v3, with permission given from author
 * @author Quantizr
 */
public class ChestProfit {

    @SubscribeEvent
    public void onGUIDrawnEvent(GuiContainerEvent.TitleDrawnEvent.Post event) {
        if (!skyblockfeatures.config.dungeonChestProfit || !Utils.inSkyblock) return;
        if (event.gui instanceof GuiChest) {
            HashMap<ItemStack,Double> items = new HashMap<ItemStack,Double>();    
            ContainerChest chest = (ContainerChest) ((GuiChest) event.gui).inventorySlots;
            IInventory inv = chest.getLowerChestInventory();
            if (inv.getDisplayName().getUnformattedText().endsWith(" Chest")) {
                int chestValue = 0;
                int price = 0;
                ItemStack openChest = inv.getStackInSlot(31);
                if (openChest != null && openChest.getDisplayName().equals("§aOpen Reward Chest")) {
                    for (String unclean : ItemUtil.getItemLore(openChest)) {
                        String line = StringUtils.stripControlCodes(unclean);
                        if (line.contains("FREE")) {
                            price = 0;
                            break;
                        } else if (line.contains(" Coins")) {
                            price = Integer.parseInt(line.replaceAll("[^0-9]", ""));
                            break;
                        }
                    }

                    chestValue = 0;
                    for (int i = 11; i < 16; i++) {
                        ItemStack lootSlot = inv.getStackInSlot(i);
                        String identifier = AuctionData.getIdentifier(lootSlot);
                        if (identifier != null) {
                            Double value = AuctionData.averageLowestBINs.get(identifier);
                            if (value == null || identifier.contains("ENCHANTMENT_")) {
                                value = AuctionData.bazaarPrices.get(identifier);
                            }
                            chestValue += value;
                            items.put(lootSlot, value);
                        }
                    }
                }
                if (items.size() > 0) {
                    ArrayList<String> lines = new ArrayList<>();
                    GlStateManager.color(1, 1, 1, 1);
                    GlStateManager.disableLighting();
                    Utils.drawGraySquareWithBorder(180, 0, 150, (items.keySet().size()+4)*Utils.GetMC().fontRendererObj.FONT_HEIGHT,3);

                    double profit = chestValue - price;
                    for (ItemStack item : items.keySet()) {
                        String name = item.getDisplayName().contains("Enchanted")?ItemUtil.getItemLore(item).get(0):item.getDisplayName();
                        lines.add(name + "§f: §a" + NumberUtil.nf.format(items.get(item)));
                    }
                    lines.add("");
                    lines.add("Profit: §" + (profit > 0 ? "a" : "c")+NumberUtil.nf.format(profit));
                    
                    int lineCount = 0;
                    for(String line:lines) {
                        Utils.GetMC().fontRendererObj.drawString(line, 190, lineCount*(Utils.GetMC().fontRendererObj.FONT_HEIGHT+1)+10, -1);
                        lineCount++;
                    }
                }
            }
        }
    }
}
