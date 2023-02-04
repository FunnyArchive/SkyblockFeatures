package mrfast.skyblockfeatures.features.impl.render;

import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.utils.SBInfo;
import mrfast.skyblockfeatures.utils.Utils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class DynamicFullbright {
    @SubscribeEvent
    public void onTick(ClientTickEvent event) {
        if(skyblockfeatures.config.DynamicFullbright) {
            String loc = SBInfo.getInstance().getLocation();
            try {
                if(loc.equals("dynamic") || loc.equals("crystal_hollows") || Utils.inDungeons) {
                    Utils.GetMC().gameSettings.gammaSetting=(skyblockfeatures.config.DynamicFullbrightDisabled/20);
                } else {
                    Utils.GetMC().gameSettings.gammaSetting=skyblockfeatures.config.DynamicFullbrightElsewhere/20;
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        if(skyblockfeatures.config.fullbright) {
            Utils.GetMC().gameSettings.gammaSetting=100;
        }
    }
}
