package mrfast.skyblockfeatures.features.impl.dungeons;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.utils.ItemUtil;
import mrfast.skyblockfeatures.utils.StringUtils;
import mrfast.skyblockfeatures.utils.Utils;


public class BetterParties {
    boolean done = false;
    boolean canRefresh = true;
    @SubscribeEvent
    public void onKeyInput(GuiScreenEvent.KeyboardInputEvent keyboardInputEvent) {
        GuiScreen screen = Minecraft.getMinecraft().currentScreen;
        if (screen instanceof GuiChest && Keyboard.isKeyDown(skyblockfeatures.reloadAH.getKeyCode()) && !done && canRefresh){
            done = true;
            ContainerChest ch = (ContainerChest) ((GuiChest)screen).inventorySlots;
            if (!ch.getLowerChestInventory().getName().contains("Party Finder")) return;

            Utils.setTimeout(()->{
                Utils.GetMC().playerController.windowClick(Utils.GetMC().thePlayer.openContainer.windowId, 46, 0, 0, Utils.GetMC().thePlayer);
                canRefresh = false;
                Utils.setTimeout(()->{
                    done = false;
                }, 200);
                Utils.setTimeout(()->{
                    canRefresh = true;
                }, 3300);
            }, 100);
        }
    }

    @SubscribeEvent
    public void onGuiPostRender(GuiScreenEvent.DrawScreenEvent.Post rendered) {
        
        if (!(Minecraft.getMinecraft().currentScreen instanceof GuiChest)) return;
        GuiChest chest = (GuiChest) Minecraft.getMinecraft().currentScreen;
        ContainerChest cont = (ContainerChest) chest.inventorySlots;
        String name = cont.getLowerChestInventory().getName();
        if(!skyblockfeatures.config.betterpartys) return;
        if (!"Party Finder".equals(name)) return;

        int i = 222;
        int j = i - 108;
        int ySize = j + (((ContainerChest)(((GuiChest) Minecraft.getMinecraft().currentScreen).inventorySlots)).getLowerChestInventory().getSizeInventory() / 9) * 18;
        int left = (rendered.gui.width - 176) / 2;
        int top = (rendered.gui.height - ySize ) / 2;
        GlStateManager.pushMatrix();
        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        GlStateManager.colorMask(true, true, true, false);
        GlStateManager.translate(left, top, 0);
        FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
        try {

            for (int i1 = 0; i1 < Integer.min(54, cont.inventorySlots.size()); i1++) {
                Slot s = cont.inventorySlots.get(i1);
                if (s.getStack() == null) continue;
                if (s.getStack().getItem() != Items.skull) continue;
                NBTTagCompound nbt = s.getStack().getTagCompound();
                if (nbt == null || nbt.hasNoTags()) continue;
                NBTTagCompound display = nbt.getCompoundTag("display");
                if (display.hasNoTags()) return;
                NBTTagList lore = display.getTagList("Lore", 8);
                int classLvReq = 0;
                int cataLvReq = 0;
                boolean Req = false;
                String note = "";
                for (int n = 0; n < lore.tagCount(); n++) {
                    String str = lore.getStringTagAt(n);
                    if (str.startsWith("§7Dungeon Level Required: §b")) cataLvReq = Integer.parseInt(str.substring(28));
                    if (str.startsWith("§7Class Level Required: §b")) classLvReq = Integer.parseInt(str.substring(26));
                    if (str.startsWith("§7§7Note:")) note = StringUtils.stripControlCodes(str.substring(10));
                    if (str.startsWith("§cRequires")) Req = true;
                }

                int x = s.xDisplayPosition;
                int y = s.yDisplayPosition;
                if (Req) {
                    Gui.drawRect(x, y, x + 16, y + 16, 0x77AA0000);
                } else {

                    if (note.toLowerCase().contains("car")) {
                        fr.drawStringWithShadow("C", x + 1, y + 1, 0xFFFF0000);
                    } else if (note.toLowerCase().replace(" ", "").contains("s/s+")) {
                        fr.drawStringWithShadow("S+", x + 1, y + 1, 0xFFFFFF00);
                    } else if (note.toLowerCase().contains("s+")) {
                        fr.drawStringWithShadow("S+", x + 1, y + 1, 0xFF00FF00);
                    } else if (note.toLowerCase().contains(" s") || note.toLowerCase().contains(" s ")) {
                        fr.drawStringWithShadow("S", x + 1, y + 1, 0xFFFFFF00);
                    } else if (note.toLowerCase().contains("rush")) {
                        fr.drawStringWithShadow("R", x + 1, y + 1, 0xFFFF0000);
                    }
                    fr.drawStringWithShadow("§e"+Integer.max(classLvReq, cataLvReq), x + 1, y + fr.FONT_HEIGHT, 0xFFFFFFFF);
                }

            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        GlStateManager.colorMask(true, true, true, true);
        GlStateManager.popMatrix();
        GlStateManager.enableBlend();
        GlStateManager.enableLighting();
    }
}
