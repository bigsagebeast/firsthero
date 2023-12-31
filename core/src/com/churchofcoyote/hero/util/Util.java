package com.churchofcoyote.hero.util;

import java.util.Locale;

public class Util {
    public static String repeat(String str, int length) {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<length; i++)
            sb.append(str);
        return sb.toString();
    }

    public static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase(Locale.ROOT) + str.substring(1);
    }
}
