package mrfast.skyblockfeatures.features.impl.ItemFeatures;

import java.awt.Color;
import java.io.IOException;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.gui.commandaliases.elements.CleanButton;
import mrfast.skyblockfeatures.utils.graphics.ScreenRenderer;
import mrfast.skyblockfeatures.utils.graphics.SmartFontRenderer;

public class ViewModel {
    public static class AuctionPriceScreen extends GuiScreen {

        public CleanButton undercutButton;
        public static GuiTextField xField;
        public static GuiTextField yField;
        public static GuiTextField zField;
        public SmartFontRenderer fr = ScreenRenderer.fontRenderer;

        @Override
        public void initGui() {
            this.buttonList.clear();
            Keyboard.enableRepeatEvents(true);
            xField = new GuiTextField(0, fontRendererObj, width/2-150, height/2, 80, 20);
            yField = new GuiTextField(0, fontRendererObj, width/2-40, height/2, 80, 20);
            zField = new GuiTextField(0, fontRendererObj, width/2+70, height/2, 80, 20);

            xField.setMaxStringLength(4);
            xField.setValidator((text) -> text.toLowerCase().replaceAll("[^0-9.-]", "").length() == text.length());
            xField.setText(skyblockfeatures.config.armX+"");

            yField.setMaxStringLength(4);
            yField.setValidator((text) -> text.toLowerCase().replaceAll("[^0-9.-]", "").length() == text.length());
            yField.setText(skyblockfeatures.config.armY+"");

            zField.setMaxStringLength(4);
            zField.setValidator((text) -> text.toLowerCase().replaceAll("[^0-9.-]", "").length() == text.length());
            zField.setText(skyblockfeatures.config.armZ+"");
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            drawGradientRect(0, 0, this.width, this.height, new Color(117, 115, 115, 25).getRGB(), new Color(0,0, 0,200).getRGB());
            xField.drawTextBox();
            yField.drawTextBox();
            zField.drawTextBox();
        }

        @Override
        public void updateScreen() {
            xField.updateCursorCounter();
            yField.updateCursorCounter();
            zField.updateCursorCounter();
            skyblockfeatures.config.armX = getX();
            skyblockfeatures.config.armY = getY();
            skyblockfeatures.config.armZ = getZ();
            super.updateScreen();
        }

        @Override
        protected void keyTyped(char typedChar, int keyCode) throws IOException {
            if (keyCode == Keyboard.KEY_ESCAPE) {
                mc.displayGuiScreen(null);
                return;
            }
            xField.textboxKeyTyped(typedChar, keyCode);
            yField.textboxKeyTyped(typedChar, keyCode);
            zField.textboxKeyTyped(typedChar, keyCode);
        }

        @Override
        protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
            xField.mouseClicked(mouseX, mouseY, mouseButton);
            yField.mouseClicked(mouseX, mouseY, mouseButton);
            zField.mouseClicked(mouseX, mouseY, mouseButton);
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }


        @Override
        public void onGuiClosed() {
            Keyboard.enableRepeatEvents(false);
        }

        public static int getX() {
            int z = 0;
            try {
                z = Integer.parseInt(xField.getText());
            } catch (Exception e) {
                //TODO: handle exception
            }
            return z;
        }

        public static int getY() {
            int z = 0;
            try {
                z = Integer.parseInt(yField.getText());
            } catch (Exception e) {
                //TODO: handle exception
            }
            return z;
        }

        public static int getZ() {
            int z = 0;
            try {
                z = Integer.parseInt(zField.getText());
            } catch (Exception e) {
                //TODO: handle exception
            }
            return z;
        }
    }
}
