package mrfast.skyblockfeatures.features.impl.overlays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import mrfast.skyblockfeatures.skyblockfeatures;

public class CompactChat {

    private String lastMessage = "";
    private int line, amount;

    @SubscribeEvent(priority = EventPriority.LOW)
    public void chat(ClientChatReceivedEvent event) {
        if (!event.isCanceled() && event.type == 0 && skyblockfeatures.config.compactChat) {
            GuiNewChat guiNewChat = Minecraft.getMinecraft().ingameGUI.getChatGUI();
            if (lastMessage.equals(event.message.getUnformattedText())) {
                guiNewChat.deleteChatLine(line);
                amount++;
                lastMessage = event.message.getUnformattedText();
                event.message.appendText(EnumChatFormatting.GRAY + " (" + amount + ")");
            } else {
                amount = 1;
                lastMessage = event.message.getUnformattedText();
            }

            line++;
            if (!event.isCanceled()) {
                guiNewChat.printChatMessageWithOptionalDeletion(event.message, line);
            }

            if (line > 256) {
                line = 0;
            }

            event.setCanceled(true);
        }
    }
}
