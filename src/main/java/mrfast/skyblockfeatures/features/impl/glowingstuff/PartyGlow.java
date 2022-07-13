package mrfast.skyblockfeatures.features.impl.glowingstuff;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.utils.StringUtils;
import mrfast.skyblockfeatures.utils.Utils;

public class PartyGlow {
    public static boolean gettingParty = false;
    public static int Delimiter = 0;
    public static boolean disbanding = false;
    public static boolean inviting = false;
    public static boolean joining = false;
    public static boolean failInviting = false;
    public static List<String> party = new ArrayList<>();
    public static List<String> repartyFailList = new ArrayList<>();
    public static Thread partyThread = null;

    public static void dostuff() {
        if(skyblockfeatures.config.glowingParty && Utils.GetMC().thePlayer != null) {
            party.clear();
            repartyFailList.clear();

            partyThread = new Thread(() -> {
                try {
                    if(!gettingParty) skyblockfeatures.sendMessageQueue.add("/pl");
                    gettingParty = true;
                    while (gettingParty) {
                        Thread.sleep(10);
                    }
                    if (party.size() == 0) {
                        return;
                    };
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

            partyThread.start();
        }
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        dostuff();
    }

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        if (!Utils.isOnHypixel()) return;
        String unformatted = StringUtils.stripControlCodes(event.message.getUnformattedText());
        if(
        unformatted.contains("joined the party.") ||
        unformatted.contains("left the party.")
        )
        dostuff();
    }
}
