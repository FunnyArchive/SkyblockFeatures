// package mrfast.skyblockfeatures.features.impl.dungeons;

// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.regex.Matcher;
// import java.util.regex.Pattern;

// import net.minecraft.client.Minecraft;
// import net.minecraft.client.entity.EntityPlayerSP;
// import net.minecraft.client.gui.ScaledResolution;
// import net.minecraft.client.network.NetworkPlayerInfo;
// import net.minecraft.world.World;
// import net.minecraftforge.event.world.WorldEvent;
// import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
// import mrfast.skyblockfeatures.skyblockfeatures;
// import mrfast.skyblockfeatures.core.structure.FloatPair;
// import mrfast.skyblockfeatures.core.structure.GuiElement;
// import mrfast.skyblockfeatures.utils.ScoreboardUtil;
// import mrfast.skyblockfeatures.utils.TabListUtils;
// import mrfast.skyblockfeatures.utils.Utils;
// import mrfast.skyblockfeatures.utils.graphics.ScreenRenderer;
// import mrfast.skyblockfeatures.utils.graphics.SmartFontRenderer;
// import mrfast.skyblockfeatures.utils.graphics.colors.CommonColors;

// public class ScoreCalculation {
//     public static final Pattern partyAssistSecretsPattern = Pattern.compile("^Party > .+: \\$skyblockfeatures\\$: \\[(?<name>.+)\\] \\((?<secrets>\\d+)\\)$");
//     public static HashMap<String, Integer> rooms = new HashMap<>();
//     public static boolean mimicKilled = false;
//     private static final Minecraft mc = Minecraft.getMinecraft();

//     @SubscribeEvent
//     public void onWorldChange(WorldEvent.Load event) {
//         mimicKilled = false;
//         rooms.clear();
//     }

//     static {
//         new ScoreCalculationElement();
//     }

//     public static class ScoreCalculationElement extends GuiElement {

//         private static final Pattern deathsTabPattern = Pattern.compile("§r§a§lDeaths: §r§f\\((?<deaths>\\d+)\\)§r");
//         private static final Pattern missingPuzzlePattern = Pattern.compile("§r (?<puzzle>.+): §r§7\\[§r§6§l✦§r§7\\]§r");
//         private static final Pattern failedPuzzlePattern = Pattern.compile("§r (?<puzzle>.+): §r§7\\[§r§c§l✖§r§7\\] §r§f\\((?:§r(?<player>.+))?§r§f\\)§r");
//         private static final Pattern cryptsPattern = Pattern.compile("§r Crypts: §r§6(?<crypts>\\d+)§r");
//         // private static final Pattern dungeonClearedPattern = Pattern.compile("Cleared: (?<percentage>\\d+)%");
//         private static final Pattern timeElapsedPattern = Pattern.compile("Time Elapsed: (?:(?<hrs>\\d+)h )?(?:(?<min>\\d+)m )?(?:(?<sec>\\d+)s)?");
        
//         public ScoreCalculationElement() {
//             super("Dungeon Score Estimate", new FloatPair(200, 100));
//             skyblockfeatures.GUIMANAGER.registerElement(this);
//         }
//         double totalSecrets = 0;
//         @Override
//         public void render() {
//             EntityPlayerSP player = mc.thePlayer;
//             World world = mc.theWorld;
//             if (this.getToggled() && Utils.inDungeons && player != null && world != null) {
//                 ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
//                 boolean leftAlign = getActualX() < sr.getScaledWidth() / 2f;

//                 ArrayList<String> text = new ArrayList<>();

//                 int deaths = 0;
//                 int missingPuzzles = 0;
//                 int failedPuzzles = 0;
//                 int foundSecrets = 0;
//                 int crypts = 0;
//                 double secretPercent = 0;

//                 for (NetworkPlayerInfo pi : TabListUtils.getTabEntries()) {
//                     try {
//                         String name = mc.ingameGUI.getTabList().getPlayerName(pi);
//                         if (name.contains("Deaths:")) {
//                             Matcher matcher = deathsTabPattern.matcher(name);
//                             if (matcher.find()) {
//                                 deaths = Integer.parseInt(matcher.group("deaths"));
//                                 continue;
//                             }
//                         }
//                         if (name.contains("✦")) {
//                             Matcher matcher = missingPuzzlePattern.matcher(name);
//                             if (matcher.find()) {
//                                 missingPuzzles++;
//                                 continue;
//                             }
//                         }
//                         if (name.contains("✖")) {
//                             Matcher matcher = failedPuzzlePattern.matcher(name);
//                             if (matcher.find()) {
//                                 failedPuzzles++;
//                                 continue;
//                             }
//                             continue;
//                         }   
//                         if (name.contains("Secrets Found: §r§b")) {
//                             try {
//                                 foundSecrets = Integer.parseInt(Utils.cleanColour(name).replaceAll("[^0-9]", ""));
//                             } catch (Exception e) {
//                                 //TODO: handle exception
//                             }
//                             continue;
//                         } else if (name.contains("Secrets Found:")) {
//                             try {
//                                 secretPercent = Double.parseDouble(Utils.cleanColour(name).replaceAll("[^0-9]", ""))/10;
//                             } catch (Exception e) {
//                                 //TODO: handle exception
//                             }
//                             continue;
//                         } 
//                         if (name.contains("Crypts:")) {
//                             Matcher matcher = cryptsPattern.matcher(name);
//                             if (matcher.find()) {
//                                 crypts = Integer.parseInt(matcher.group("crypts"));
//                                 continue;
//                             }
//                         }
//                     } catch (NumberFormatException ignored) {
//                     }
//                 }
//                 double secondsElapsed = 0.0;
//                 float clearedPercentage = 0;
//                 String dungeonFloor = "E";

//                 for (String lines : ScoreboardUtil.getSidebarLines()) {
//                     String line = ScoreboardUtil.cleanSB(lines);
//                     if (line.startsWith("Cleared:")) {
//                         try {
//                             clearedPercentage = Integer.parseInt(Utils.cleanColour(line).split(" ")[1].replaceAll("[^0-9]", ""));
//                         } catch (Exception e) {
//                             //TODO: handle exception
//                         }
//                         continue;
//                     }
//                     if (line.startsWith("Time Elapsed:")) {
//                         Matcher matcher = timeElapsedPattern.matcher(line);
//                         if (matcher.find()) {
//                             try {
//                                 int minutes= Integer.valueOf(matcher.group("min"));
//                                 int seconds= Integer.valueOf(matcher.group("sec"));
//                                 secondsElapsed = (minutes * 60 + seconds);
//                                 continue;
//                             } catch(NumberFormatException e) {
                                
//                             }
//                         }
//                     }
//                 }

//                 int skillScore = (100 - (2 * deaths) - (14 * (missingPuzzles + failedPuzzles)));
//                 double speedScore = 0;
//                 double countedSeconds = secondsElapsed;
//                 if (countedSeconds <= 1320) {
//                     speedScore = 100.0;
//                 } else if (1320 < countedSeconds && countedSeconds <= 1420) {
//                     speedScore = 232 - 0.1 * countedSeconds;
//                 } else if (1420 < countedSeconds && countedSeconds <= 1820) {
//                     speedScore = 161 - 0.05 * countedSeconds;
//                 } else if (1820 < countedSeconds && countedSeconds <= 3920) {
//                     speedScore = 392 / 3f - 1 / 30f * countedSeconds;
//                 } else speedScore = 0.0;

//                 if(foundSecrets == 1) totalSecrets = Math.floor(foundSecrets*100/secretPercent);

//                 if(totalSecrets >= 100) totalSecrets = Math.floor(totalSecrets/10);

//                 double requiredSecrets = getRequiredSecrets(Utils.getDungeonFloor(), false)*totalSecrets;

//                 double secretScore = Math.floor(40 * Math.min(foundSecrets / requiredSecrets,1));
//                 double discoveryScore = Math.floor((60 *  Math.min(clearedPercentage / 95,1)));

//                 int discoveryScoreTotal = (int) (secretScore + discoveryScore);

//                 int total = (int) (skillScore + speedScore + discoveryScoreTotal);

//                 total+=Math.min(crypts,5);

//                 text.add("§6Missing Puzzles:§a " + missingPuzzles);
//                 text.add("§6Secrets Found:§a "+foundSecrets+"/"+(int) (totalSecrets));
//                 text.add("§6Skill Score:§a " + skillScore);
//                 text.add("§6Speed Score:§a " + speedScore);
//                 text.add("§6Discovery Score:§a " + discoveryScoreTotal+" ("+discoveryScore+") "+" ("+secretScore+")");
//                 text.add("§6Estimated Score:§a " + total +" ("+ getScore(total)+")");
//                 for (int i = 0; i < text.size(); i++) {
//                     SmartFontRenderer.TextAlignment alignment = leftAlign ? SmartFontRenderer.TextAlignment.LEFT_RIGHT : SmartFontRenderer.TextAlignment.RIGHT_LEFT;
//                     ScreenRenderer.fontRenderer.drawString(text.get(i), leftAlign ? 0 : getWidth(), i * ScreenRenderer.fontRenderer.FONT_HEIGHT, CommonColors.WHITE, alignment, SmartFontRenderer.TextShadow.NORMAL);
//                 }
//             }
//         }

//         @Override
//         public void demoRender() {
//             ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());

//             boolean leftAlign = getActualX() < sr.getScaledWidth() / 2f;

//             ArrayList<String> text = new ArrayList<>();
//             text.add("§6Secrets Found: 20");
//             text.add("§6Crypts: 20");
//             text.add("§6Deaths: 5");
//             text.add("§6Milestone: ☠❶");

//             for (int i = 0; i < text.size(); i++) {
//                 SmartFontRenderer.TextAlignment alignment = leftAlign ? SmartFontRenderer.TextAlignment.LEFT_RIGHT : SmartFontRenderer.TextAlignment.RIGHT_LEFT;
//                 ScreenRenderer.fontRenderer.drawString(text.get(i), leftAlign ? 0 : getWidth(), i * ScreenRenderer.fontRenderer.FONT_HEIGHT, CommonColors.WHITE, alignment, SmartFontRenderer.TextShadow.NORMAL);
//             }
//         }

//         @Override
//         public int getHeight() {
//             return ScreenRenderer.fontRenderer.FONT_HEIGHT * 4;
//         }

//         @Override
//         public int getWidth() {
//             return ScreenRenderer.fontRenderer.getStringWidth("§6Estimated Secret Count: 99");
//         }

//         @Override
//         public boolean getToggled() {
//             return false;
//         }
//     }

//     public static String getScore(double total) {
//         if(total>=300) return "S+";
//         if(total <= 299 && total >= 270) return "S";
//         if(total <= 269 && total >= 230) return "A";
//         if(total <= 229 && total >= 160) return "B";
//         if(total <= 159 && total >= 100) return "C";
//         if(total <= 99 && total >= 0) return "D";
//         return "Unknown";
//     }
//     // Percent
//     public static double getRequiredSecrets(int floor, boolean masterMode) {
//         if (masterMode) return 1.00; 
//         switch (floor) {
//             case 1:
//                 return 0.30;
//             case 2:
//                 return 0.40;
//             case 3:
//                 return 0.50;
//             case 4:
//                 return 0.60;
//             case 5:
//                 return 0.70;
//             case 6:
//                 return 0.85;
//             default:
//                 return 1.00;
//         }
//     }
// }