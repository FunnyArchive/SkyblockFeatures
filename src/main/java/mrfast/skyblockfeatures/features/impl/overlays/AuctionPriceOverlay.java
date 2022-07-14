package mrfast.skyblockfeatures.features.impl.overlays;

import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.events.GuiContainerEvent;
import mrfast.skyblockfeatures.utils.SBInfo;
import mrfast.skyblockfeatures.utils.Utils;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AuctionPriceOverlay {

    public static ItemStack lastAuctionedStack;

    @SubscribeEvent
    public void onSlotClick(GuiContainerEvent.SlotClickEvent event) {
        if (!Utils.inSkyblock || !skyblockfeatures.config.auctionGuis) return;

        if (event.gui instanceof GuiChest) {
            if (Utils.equalsOneOf(SBInfo.getInstance().lastOpenContainerName, "Create Auction", "Create BIN Auction") && event.slotId == 31) {
                ItemStack auctionItem = event.container.getSlot(13).getStack();
                if (auctionItem != null) {
                    if (auctionItem.getDisplayName().equals("§a§l§nAUCTION FOR ITEM:")) {
                        lastAuctionedStack = auctionItem;
                    }
                }
            }
            if (Utils.equalsOneOf(SBInfo.getInstance().lastOpenContainerName, "Confirm Auction", "Confirm BIN Auction")) {
                if (event.slotId == 11) {
                    lastAuctionedStack = null;
                }
            }
        }
    }
}
