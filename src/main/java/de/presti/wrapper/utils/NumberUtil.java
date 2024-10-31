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
        s = s.replace("subscribers", "");
        s = s.replace("views", "");
        s = s.trim();
        s = s.replace("B", "00000000");
        s = s.replace("M", "00000");
        s = s.replace("K", "00");
        s = s.trim();

        if (!s.contains(".")) {
            s += "0";
        } else {
            s = s.replace(".", "");
        }

        return Long.parseLong(s.replaceAll("[^0-9]", ""));
    }

    /**
     * Extract the actual relative Time as a milliseconds long from a string.
     * @param s the string.
     * @return the extracted actual relative time as a milliseconds long.
     */
    public static long extractRelativeTime(String s) {
        long time = -1;
        if (s.contains("seconds ago")) {
            time = Long.parseLong(s.replace(" seconds ago", ""));
        } else if (s.contains("minutes ago")) {
            time = Long.parseLong(s.replace(" minutes ago", "")) * 60;
        } else if (s.contains("hours ago")) {
            time = Long.parseLong(s.replace(" hours ago", "")) * 60 * 60;
        } else if (s.contains("days ago")) {
            time = Long.parseLong(s.replace(" days ago", "")) * 60 * 60 * 24;
        } else if (s.contains("weeks ago")) {
            time = Long.parseLong(s.replace(" weeks ago", "")) * 60 * 60 * 24 * 7;
        } else if (s.contains("months ago")) {
            time = Long.parseLong(s.replace(" months ago", "")) * 60 * 60 * 24 * 30;
        } else if (s.contains("years ago")) {
            time = Long.parseLong(s.replace(" years ago", "")) * 60 * 60 * 24 * 365;
        }

        if (time > 0) {
            time *= 1000;
        }

        return time;
    }

    /**
     * Extract the length based on a string. Example 1:30 would be 90.
     * @param s the string.
     * @return the time string in seconds.
     */
    public static long extractLength(String s) {
        String[] split = s.split(":");
        long hours = 0;
        long minutes = 0;
        long seconds = 0;
        if (split.length == 3) {
            hours = Long.parseLong(split[0]);
            minutes = Long.parseLong(split[1]);
            seconds = Long.parseLong(split[2]);
        } else if (split.length == 2) {
            minutes = Long.parseLong(split[0]);
            seconds = Long.parseLong(split[1]);
        }

        return hours * 3600 + minutes * 60 + seconds;
    }
}
