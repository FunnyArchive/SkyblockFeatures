package mrfast.skyblockfeatures.features.impl.mining;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.Minecraft;
import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.core.structure.FloatPair;
import mrfast.skyblockfeatures.core.structure.GuiElement;
import mrfast.skyblockfeatures.utils.SBInfo;
import mrfast.skyblockfeatures.utils.TabListUtils;
import mrfast.skyblockfeatures.utils.Utils;
import mrfast.skyblockfeatures.utils.graphics.ScreenRenderer;
import mrfast.skyblockfeatures.utils.graphics.SmartFontRenderer;
import mrfast.skyblockfeatures.utils.graphics.colors.CommonColors;

public class CommisionsTracker {
  private static final Minecraft mc = Minecraft.getMinecraft();

  static {
      new JerryTimerGUI();
  }
  
  public static class JerryTimerGUI extends GuiElement {

      public JerryTimerGUI() {
          super("Commissions Tracker", new FloatPair(0.45052084f, 0.86944443f));
          skyblockfeatures.GUIMANAGER.registerElement(this);
      }

      @Override
      public void render() {
          ArrayList<String> text = new ArrayList<>();
          try {
              if(mc.thePlayer == null || !Utils.inSkyblock || !SBInfo.getInstance().getLocation().equals("mining_3")) return;

              text.add(ChatFormatting.BLUE+""+ChatFormatting.BOLD+"Commissions");
              List<String> commissions = new ArrayList<String>();
              commissions.add(mc.ingameGUI.getTabList().getPlayerName(TabListUtils.getTabEntries().get(50)));
              commissions.add(mc.ingameGUI.getTabList().getPlayerName(TabListUtils.getTabEntries().get(51)));

              if(!Utils.cleanColour(mc.ingameGUI.getTabList().getPlayerName(TabListUtils.getTabEntries().get(52))).isEmpty()) {
                commissions.add(mc.ingameGUI.getTabList().getPlayerName(TabListUtils.getTabEntries().get(52)));
              }
              for(String commission : commissions) {
                commission = Utils.cleanColour(commission);
                Pattern regex = Pattern.compile("(\\d+(?:\\.\\d+)?)");
                Matcher matcher = regex.matcher(commission);
                
                if(matcher.find()) {
                    String[] a = commission.split(" ");
                    String amount = Math.round(getTotal(commission) * (Double.valueOf(matcher.group(1)) / 100))+"";
                    String mid = ChatFormatting.LIGHT_PURPLE+"["+
                    ChatFormatting.GREEN+amount+
                    ChatFormatting.GOLD+"/"+
                    ChatFormatting.GREEN+getTotal(commission)+
                    ChatFormatting.LIGHT_PURPLE+"]";
                    commission = commission.replace(a[a.length-1], mid);
                } else if(commission.contains("DONE")) {
                  commission = commission.replace("DONE", ChatFormatting.GREEN+"DONE");
                }
                text.add(ChatFormatting.AQUA+commission);
              }
            } catch (Exception e) {
              
          }

          for (int i = 0; i < text.size(); i++) {
              ScreenRenderer.fontRenderer.drawString(text.get(i), 0, i * ScreenRenderer.fontRenderer.FONT_HEIGHT, CommonColors.WHITE, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.OUTLINE);
          }
      }

      @Override
      public void demoRender() {
          ArrayList<String> text = new ArrayList<>();
          text.add(ChatFormatting.BLUE+""+ChatFormatting.BOLD+"Commissions");
          text.add(" Upper Mines Titanium: "+ChatFormatting.LIGHT_PURPLE+"["+ChatFormatting.GREEN+"7"+ChatFormatting.GOLD+"/"+ChatFormatting.GREEN+"10"+ChatFormatting.LIGHT_PURPLE+"]");
          text.add(" Goblin Raid: "+ChatFormatting.LIGHT_PURPLE+"["+ChatFormatting.GREEN+"0"+ChatFormatting.GOLD+"/"+ChatFormatting.GREEN+"1"+ChatFormatting.LIGHT_PURPLE+"]");

          for (int i = 0; i < text.size(); i++) {
              ScreenRenderer.fontRenderer.drawString(text.get(i), 0, i * ScreenRenderer.fontRenderer.FONT_HEIGHT, CommonColors.WHITE, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.OUTLINE);
          }
      }

      @Override
      public boolean getToggled() {
          return Utils.inSkyblock && skyblockfeatures.config.CommisionsTracker;
      }

      @Override
      public int getHeight() {
          return ScreenRenderer.fontRenderer.FONT_HEIGHT*3;
      }

      @Override
      public int getWidth() {
          return ScreenRenderer.fontRenderer.getStringWidth("2x Mithril Powder Collector [350/500] ");
      }
  }


  public static int getTotal(String str) {
    if(str.contains("Ice Walker")) return 50;
    if(str.contains("Golden Goblin Slayer")) return 1;
    if(str.contains("Goblin Slayer")) return 100;
    if(str.contains("Powder Ghast Puncher")) return 5;
    if(str.contains("Star Century Puncher")) return 10;
    if(str.contains("2x Mithril Powder Collector")) return 500;

    if(str.contains("Raffle")) {
      if(str.contains("Lucky")) return 20;
      return 1;
    }
    if(str.contains("Goblin Raid")) {
      if(str.contains("Slayer")) return 20;
      return 1;
    }
    if(str.contains("Mithril")) {
      if(str.contains("Miner")) return 500;
      return 350;
    }
    if(str.contains("Titanium")) {
      if(str.contains("Miner")) return 15;
      return 10;
    }

    return 1;
  }

}
