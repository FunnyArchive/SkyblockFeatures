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

}
