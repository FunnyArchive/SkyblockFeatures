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
