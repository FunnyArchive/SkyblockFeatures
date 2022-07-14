package mrfast.skyblockfeatures.features.impl.misc;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.features.impl.handlers.AuctionData;
import mrfast.skyblockfeatures.utils.ItemUtil;
import mrfast.skyblockfeatures.utils.NumberUtil;
import mrfast.skyblockfeatures.utils.SBInfo;
import mrfast.skyblockfeatures.utils.StringUtils;
import mrfast.skyblockfeatures.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ItemFeatures {

    private final static Minecraft mc = Minecraft.getMinecraft();

    public static final HashMap<String, Double> sellPrices = new HashMap<>();
    public static final HashMap<String, Integer> bitCosts = new HashMap<>();

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onTooltip(ItemTooltipEvent event) {
        if (!Utils.inSkyblock) return;

        ItemStack item = event.itemStack;

        NBTTagCompound extraAttr = ItemUtil.getExtraAttributes(item);
        String itemId = ItemUtil.getSkyBlockItemID(extraAttr);

        boolean isSuperpairsReward = false;

        if (item != null && mc.thePlayer.openContainer != null && StringUtils.startsWith(SBInfo.getInstance().lastOpenContainerName, "Superpairs (")) {
            if (StringUtils.stripControlCodes(ItemUtil.getDisplayName(item)).equals("Enchanted Book")) {
                List<String> lore = ItemUtil.getItemLore(item);
                if (lore.size() >= 3) {
                    if (lore.get(0).equals("§8Item Reward") && lore.get(1).isEmpty()) {
                        String line2 = StringUtils.stripControlCodes(lore.get(2));
                        String enchantName = line2.substring(0, line2.lastIndexOf(" ")).replaceAll(" ", "_").toUpperCase();
                        itemId = "ENCHANTED_BOOK-" + enchantName + "-" + item.stackSize;
                        isSuperpairsReward = true;
                    }
                }
            }
        }
        
        if (itemId != null) {
            if (skyblockfeatures.config.showLowestBINPrice) {
                String auctionIdentifier = isSuperpairsReward ? itemId : AuctionData.getIdentifier(item);
                if (auctionIdentifier != null) {
                    // this might actually have multiple items as the price
                    Double valuePer = AuctionData.lowestBINs.get(auctionIdentifier);
                    if (valuePer != null) {
                        if (skyblockfeatures.config.showLowestBINPrice) {
                            String total = isSuperpairsReward ? NumberUtil.nf.format(valuePer) : NumberUtil.nf.format(valuePer * item.stackSize);
                            event.toolTip.add("§6Lowest BIN Price: §b" + total + (item.stackSize > 1 && !isSuperpairsReward ? " §7(" + NumberUtil.nf.format(Math.round(valuePer)) + " each§7)" : ""));
                        }
                    }
                }
            }

            if (skyblockfeatures.config.egg) {
                NBTTagCompound extraAttributes = ItemUtil.getExtraAttributes(item);

                if (extraAttributes != null) {
                    if (extraAttributes.hasKey("blocks_walked")) {
                        int walked = extraAttributes.getInteger("blocks_walked");
                        event.toolTip.add("§e" + NumberUtil.nf.format(walked)+" blocks walked");
                    }
                }
            }

            if (skyblockfeatures.config.showNPCSellPrice) {
                Double valuePer = sellPrices.get(itemId);
                if (valuePer != null) event.toolTip.add("§6NPC Value: §b" + NumberUtil.nf.format(valuePer * item.stackSize) + (item.stackSize > 1 ? " §7(" + NumberUtil.nf.format(valuePer) + " each§7)" : ""));
            }
            if(AuctionFeatures.items.containsKey(item)) event.toolTip.add("§6BIN Flip Profit: §a"+ NumberUtil.nf.format(Math.round(AuctionFeatures.items.get(item)* item.stackSize)));
        }
    }

    @SubscribeEvent
    public void onEntitySpawn(EntityJoinWorldEvent event) {
        if (!Utils.inSkyblock) return;
        if (!(event.entity instanceof EntityFishHook) || !skyblockfeatures.config.hideFishingHooks) return;
        if (((EntityFishHook) event.entity).angler instanceof EntityOtherPlayerMP) {
            event.entity.setDead();
            event.setCanceled(true);
        }
    }
}
