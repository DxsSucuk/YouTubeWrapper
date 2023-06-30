package de.presti.wrapper.utils;

/**
 * Utility meant to fix up the suffixes YT adds on subscriber counts for example.
 */
public class NumberUtil {

    /**
     * Extract the actual long out of the formatted String.
     * @param s the formatted String-
     * @return the extracted actual long.
     */
    public static Long extractLong(String s) {
        s = s.replace(" subscribers", "");
        s = s.replace("M", "00000");
        s = s.replace("K", "00");
        if (!s.contains(".")) {
            s += "0";
        }

        s = s.replace(".", "");
        return Long.parseLong(s.replaceAll("[^0-9]", ""));
    }

}
