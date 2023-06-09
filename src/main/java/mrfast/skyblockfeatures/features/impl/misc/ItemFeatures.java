package mrfast.skyblockfeatures.features.impl.misc;

import java.util.HashMap;

import org.lwjgl.input.Keyboard;

import com.google.gson.JsonObject;
import com.mojang.realmsclient.gui.ChatFormatting;

import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.features.impl.handlers.AuctionData;
import mrfast.skyblockfeatures.utils.ItemUtil;
import mrfast.skyblockfeatures.utils.NumberUtil;
import mrfast.skyblockfeatures.utils.Utils;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ItemFeatures {
    public static final HashMap<String, Double> sellPrices = new HashMap<>();
    public static final HashMap<String, Integer> bitCosts = new HashMap<>();
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onTooltip(ItemTooltipEvent event) {
        if (!Utils.inSkyblock) return;

        ItemStack item = event.itemStack;
        NBTTagCompound extraAttr = ItemUtil.getExtraAttributes(item);
        String itemId = ItemUtil.getSkyBlockItemID(extraAttr);
        String itemUUID = ItemUtil.getItemUUID(item);
        if (itemId != null) {
            if (skyblockfeatures.config.egg) {
                NBTTagCompound extraAttributes = ItemUtil.getExtraAttributes(item);

                if (extraAttributes != null) {
                    if (extraAttributes.hasKey("blocks_walked")) {
                        int walked = extraAttributes.getInteger("blocks_walked");
                        event.toolTip.add("§e" + NumberUtil.nf.format(walked)+" blocks walked");
                    }
                }
            }
            if(AuctionFeatures.items.containsKey(item) && skyblockfeatures.config.showPricePaid) {
                long price = Math.round(AuctionFeatures.items.get(item));
                String color = price>0?ChatFormatting.GREEN+"":ChatFormatting.RED+"";
                event.toolTip.add("§6BIN Flip Profit: "+color+NumberUtil.nf.format(price));
            }
        }
        if (itemId != null) {
            if(skyblockfeatures.config.showPriceInfoOnShift) {
                if(!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                    event.toolTip.add("§e§l[SHIFT To Reveal Info]");
                    return;
                }
            }
            if(itemUUID != null) {
                if(AuctionFeatures.pricePaidMap.containsKey(itemUUID)) {
                    event.toolTip.add("§6Price Paid: §d" + NumberUtil.nf.format(AuctionFeatures.pricePaidMap.get(itemUUID)));
                }
            }
            if (skyblockfeatures.config.showLowestBINPrice) {
                String auctionIdentifier = AuctionData.getIdentifier(item);
                if (auctionIdentifier != null && item!=null) {
                    Double valuePer = AuctionData.lowestBINs.get(auctionIdentifier);

                    if (skyblockfeatures.config.showEstimatedPrice && valuePer!=null) {
                        Integer total = (int) ItemUtil.getEstimatedItemValue(item);//Math.floor(valuePer+starValue+enchantValue) * item.stackSize;
                        event.toolTip.add("§6Estimated Price: §d" + NumberUtil.nf.format(total*item.stackSize));
                    }

                    if (skyblockfeatures.config.showLowestBINPrice && valuePer!=null) {
                        String total = NumberUtil.nf.format(valuePer * item.stackSize);
                        event.toolTip.add("§6Lowest BIN Price: §b" + total + (item.stackSize > 1 ? " §7(" + NumberUtil.nf.format(Math.round(valuePer)) + " each§7)" : ""));
                    }
                    
                    valuePer = AuctionData.bazaarPrices.get(auctionIdentifier);
                    if (skyblockfeatures.config.showBazaarPrice && valuePer != null) {
                        String total = NumberUtil.nf.format(valuePer * item.stackSize);
                        event.toolTip.add("§6Lowest Bazaar Price: §9" + total + (item.stackSize > 1 ? " §7(" + NumberUtil.nf.format(Math.round(valuePer)) + " each§7)" : ""));
                    }

                    Double avgValuePer = AuctionData.averageLowestBINs.get(auctionIdentifier);
                    if (skyblockfeatures.config.showAvgLowestBINPrice && avgValuePer!=null) {
                        String total = NumberUtil.nf.format(avgValuePer * item.stackSize);
                        event.toolTip.add("§6Average BIN Price: §3" + total + (item.stackSize > 1 ? " §7(" + NumberUtil.nf.format(Math.round(avgValuePer)) + " each§7)" : ""));
                    }

                    JsonObject auctionData = AuctionData.getItemAuctionInfo(auctionIdentifier);
                    if (skyblockfeatures.config.showSalesPerDay && auctionData!=null) {
                        event.toolTip.add("§6Sales Per Day: §e" + NumberUtil.nf.format(auctionData.get("sales").getAsInt()));
                    }
                }
            }

            if (skyblockfeatures.config.showNPCSellPrice && item!=null) {
                Double valuePer = sellPrices.get(itemId);
                if (valuePer != null) event.toolTip.add("§6NPC Value: §b" + NumberUtil.nf.format(valuePer * item.stackSize) + (item.stackSize > 1 ? " §7(" + NumberUtil.nf.format(valuePer) + " each§7)" : ""));
            }
        }
    }

    @SubscribeEvent
    public void onEntitySpawn(EntityJoinWorldEvent event) {
        if (!(event.entity instanceof EntityFishHook) || !skyblockfeatures.config.hideFishingHooks || !Utils.inSkyblock) return;
        if (((EntityFishHook) event.entity).angler instanceof EntityOtherPlayerMP) {
            event.entity.setDead();
            event.setCanceled(true);
        }
    }
}
