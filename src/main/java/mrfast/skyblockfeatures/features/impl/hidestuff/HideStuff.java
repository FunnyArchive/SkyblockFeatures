package mrfast.skyblockfeatures.features.impl.hidestuff;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.utils.Utils;

public class HideStuff {
	@SubscribeEvent
	void renderHealth(RenderGameOverlayEvent.Pre event) {
		if(Utils.inSkyblock) {
			if (event.type.equals(RenderGameOverlayEvent.ElementType.FOOD) && skyblockfeatures.config.hungerbar) {
				event.setCanceled(true);
			}
			if (event.type.equals(RenderGameOverlayEvent.ElementType.HEALTH) && skyblockfeatures.config.healthsbar) {
				event.setCanceled(true);
			}
			if (event.type.equals(RenderGameOverlayEvent.ElementType.ARMOR) && skyblockfeatures.config.armorbar) {
				event.setCanceled(true);
			}
		}
	}
}
