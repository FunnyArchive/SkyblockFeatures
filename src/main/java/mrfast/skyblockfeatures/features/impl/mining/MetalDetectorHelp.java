package mrfast.skyblockfeatures.features.impl.mining;

import mrfast.skyblockfeatures.events.SecondPassedEvent;
import mrfast.skyblockfeatures.utils.SBInfo;
import mrfast.skyblockfeatures.utils.Utils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MetalDetectorHelp {
    BlockPos pos1;
    int pos1Dist = 0;

    BlockPos pos2;
    int pos2Dist = 0;
    @SubscribeEvent
	public void onEvent(ClientChatReceivedEvent event) {
		if (event.type == 2 && SBInfo.getInstance().location.contains("Divan")) {
			String actionBar = event.message.getUnformattedText();
            if(Utils.GetMC().thePlayer.getHeldItem() != null) {
                if(Utils.GetMC().thePlayer.getHeldItem().getDisplayName().contains("Detector")) {
                    // 
                    GuiScreen.setClipboardString(actionBar);
                }
            }
        }
    }
}
