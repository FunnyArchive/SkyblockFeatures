/*
 * skyblockfeatures - Hypixel Skyblock Quality of Life Mod
 * Copyright (C) 2021 skyblockfeatures
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package mrfast.skyblockfeatures.features.impl.misc;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.utils.Utils;

import java.text.NumberFormat;
import java.util.WeakHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DamageSplash {

    private static final WeakHashMap<EntityLivingBase, ChatComponentText> replacementMap = new WeakHashMap<>();

    private static final EnumChatFormatting[] coloursHypixel = {EnumChatFormatting.WHITE, EnumChatFormatting.YELLOW, EnumChatFormatting.GOLD, EnumChatFormatting.RED, EnumChatFormatting.RED, EnumChatFormatting.WHITE};

    private static final char STAR = '\u2727';
    private static final Pattern PATTERN_CRIT = Pattern.compile("\u00a7f"+STAR+"((?:\u00a7.\\d)+)\u00a7."+STAR+"(.*)");
    private static final Pattern PATTERN_NO_CRIT = Pattern.compile("\u00a77(\\d+)(.*)");

    public static IChatComponent replaceName(EntityLivingBase entity) {
        if(!entity.hasCustomName() || skyblockfeatures.config.PrettyDamage == 0) return entity.getDisplayName();

        IChatComponent name = entity.getDisplayName();

        if(replacementMap.containsKey(entity)) {
            ChatComponentText component = replacementMap.get(entity);
            if(component == null) return name;
            return component;
        }

        String formatted = name.getFormattedText();

        boolean crit = false;
        String numbers;
        String prefix;
        String suffix;

        Matcher matcherCrit = PATTERN_CRIT.matcher(formatted);
        if(matcherCrit.matches()) {
            crit = true;
            numbers = Utils.cleanColour(matcherCrit.group(1));
            prefix = "\u00a7f"+STAR;
            suffix = "\u00a7f"+STAR+matcherCrit.group(2);
        } else {
            Matcher matcherNoCrit = PATTERN_NO_CRIT.matcher(formatted);
            if(matcherNoCrit.matches()) {
                numbers = matcherNoCrit.group(1);
                prefix = "\u00A77";
                suffix = "\u00A7r"+matcherNoCrit.group(2);
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
