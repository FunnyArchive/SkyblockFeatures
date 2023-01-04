package mrfast.skyblockfeatures.utils;

public class StringUtils {

    public static String stripControlCodes(String string) {
        return net.minecraft.util.StringUtils.stripControlCodes(string);
    }

    public static boolean startsWith(CharSequence string, CharSequence sequence) {
        return org.apache.commons.lang3.StringUtils.startsWith(string, sequence);
    }

    public static boolean startsWithAny(CharSequence string, CharSequence... sequences) {
        return org.apache.commons.lang3.StringUtils.startsWithAny(string, sequences);
    }

    public static String capitalizeString(String string) {
        char[] chars = string.toLowerCase().toCharArray();
        boolean found = false;
        for (int i = 0; i < chars.length; i++) {
            if (!found && Character.isLetter(chars[i])) {
            chars[i] = Character.toUpperCase(chars[i]);
            found = true;
            } else if (Character.isWhitespace(chars[i]) || chars[i]=='.' || chars[i]=='\'') { // You can add other chars here
            found = false;
            }
        }
        return String.valueOf(chars);
    }
}
