package com.bigsagebeast.hero.util;

import com.bigsagebeast.hero.enums.Gender;
import com.bigsagebeast.hero.story.StoryCard;

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

    public static String substitute(String string, Gender gender1, Gender gender2) {
        if (string == null) {
            return null;
        }
        if (gender1 == null) {
            gender1 = Gender.AGENDER;
        }
        if (gender2 == null) {
            gender2 = Gender.AGENDER;
        }
        string = string.replace("%1a", gender1.a());
        string = string.replace("%1s", gender1.s());
        string = string.replace("%1o", gender1.o());
        string = string.replace("%1p", gender1.p());
        string = string.replace("%1r", gender1.r());
        string = string.replace("%1A", capitalize(gender1.a()));
        string = string.replace("%1S", capitalize(gender1.s()));
        string = string.replace("%1O", capitalize(gender1.o()));
        string = string.replace("%1P", capitalize(gender1.p()));
        string = string.replace("%1R", capitalize(gender1.r()));
        string = string.replace("%2a", gender2.a());
        string = string.replace("%2s", gender2.s());
        string = string.replace("%2o", gender2.o());
        string = string.replace("%2p", gender2.p());
        string = string.replace("%2r", gender2.r());
        string = string.replace("%2A", capitalize(gender2.a()));
        string = string.replace("%2S", capitalize(gender2.s()));
        string = string.replace("%2O", capitalize(gender2.o()));
        string = string.replace("%2P", capitalize(gender2.p()));
        string = string.replace("%2R", capitalize(gender2.r()));
        return string;
    }

    public static String aOrAn(String name) {
        boolean startsWithVowel = "aeiouAEIOU".indexOf(name.charAt(0)) != -1;
        return startsWithVowel ? "an" : "a";
    }
}
