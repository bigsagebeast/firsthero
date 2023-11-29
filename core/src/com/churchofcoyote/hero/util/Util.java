package com.churchofcoyote.hero.util;

public class Util {
    public static String repeat(String str, int length) {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<length; i++)
            sb.append(str);
        return sb.toString();
    }
}
