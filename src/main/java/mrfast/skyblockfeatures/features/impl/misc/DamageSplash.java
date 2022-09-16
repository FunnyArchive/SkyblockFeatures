package mrfast.skyblockfeatures.features.impl.misc;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.utils.Utils;

import java.text.NumberFormat;
import java.util.WeakHashMap;

public class DamageSplash {

    private static final WeakHashMap<EntityLivingBase, ChatComponentText> replacementMap = new WeakHashMap<>();

    private static final EnumChatFormatting[] coloursHypixel = {EnumChatFormatting.WHITE, EnumChatFormatting.YELLOW, EnumChatFormatting.GOLD, EnumChatFormatting.RED, EnumChatFormatting.RED, EnumChatFormatting.WHITE};

    private static final char STAR = '\u2727';

    public static IChatComponent replaceName(EntityLivingBase entity) {
        if(!entity.hasCustomName() || skyblockfeatures.config.PrettyDamage == 0) return entity.getDisplayName();

        IChatComponent name = entity.getDisplayName();

        if(replacementMap.containsKey(entity)) {
            ChatComponentText component = replacementMap.get(entity);
            if(component == null) return name;
            return component;
        }

        String unformatted = Utils.cleanColour(name.getUnformattedText());
        
        boolean crit = false;
        String numbers;
        String prefix;
        String suffix;
        if(unformatted.startsWith("✧") && unformatted.endsWith("✧")) {
            crit = true;
            numbers = unformatted.replaceAll("[^0-9]", "");
            prefix = "\u00a7f"+STAR;
            suffix = "\u00a7f"+STAR;
        } else {
            if(unformatted.replaceAll(",","").replaceAll("[0-9]", "").length() == 0) {
                numbers = (unformatted.replaceAll("[^0-9]", ""));
                prefix = "\u00A77";
                suffix = "\u00A7r";
            } else {
                replacementMap.put(entity, null);
                return name;
            }
        }

        StringBuilder newFormatted = new StringBuilder();

        try {
            int number = Integer.parseInt(numbers);

            if(number > 999 && skyblockfeatures.config.PrettyDamage == 1) {
                newFormatted.append(Utils.shortNumberFormat(number, 0));
            }
            else if(number > 999 && skyblockfeatures.config.PrettyDamage == 2) {
                newFormatted.append(NumberFormat.getIntegerInstance().format(number));
            } else {
                return name;
            }
        } catch(NumberFormatException e) {
            replacementMap.put(entity, null);
            return name;
        }

        if(crit) {
            StringBuilder newFormattedCrit = new StringBuilder();

            int colourIndex = 0;
            for(char c : newFormatted.toString().toCharArray()) {
                if(c == ',') {
                    newFormattedCrit.append(EnumChatFormatting.LIGHT_PURPLE);
                } else {
                    newFormattedCrit.append(coloursHypixel[colourIndex++ % coloursHypixel.length]);
                }
                newFormattedCrit.append(c);
            }

            newFormatted = newFormattedCrit;
        }

        ChatComponentText finalComponent = new ChatComponentText(prefix+newFormatted.toString()+suffix);

        replacementMap.put(entity, finalComponent);
        return finalComponent;
    }
}
