package com.bigsagebeast.hero.util;

import com.bigsagebeast.hero.enums.Gender;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.story.StoryCard;
import com.bigsagebeast.hero.text.TextBlock;

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

    public static String formatFloat(float val) {
        if (val == (long)val) {
            return String.format("%d",(long)val);
        }
        return String.format("%s",val);
    }

    public static String substitute(String string, Gender gender) {
        if (string == null) {
            return null;
        }
        if (gender == null) {
            gender = Gender.AGENDER;
        }
        string = string.replace("%a", gender.a());
        string = string.replace("%s", gender.s());
        string = string.replace("%o", gender.o());
        string = string.replace("%p", gender.p());
        string = string.replace("%r", gender.r());
        string = string.replace("%l", gender.l());
        string = string.replace("%A", capitalize(gender.a()));
        string = string.replace("%S", capitalize(gender.s()));
        string = string.replace("%O", capitalize(gender.o()));
        string = string.replace("%P", capitalize(gender.p()));
        string = string.replace("%R", capitalize(gender.r()));
        string = string.replace("%L", capitalize(gender.l()));
        return string;
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
        string = string.replace("%1l", gender1.l());
        string = string.replace("%1A", capitalize(gender1.a()));
        string = string.replace("%1S", capitalize(gender1.s()));
        string = string.replace("%1O", capitalize(gender1.o()));
        string = string.replace("%1P", capitalize(gender1.p()));
        string = string.replace("%1R", capitalize(gender1.r()));
        string = string.replace("%1L", capitalize(gender1.l()));
        string = string.replace("%2a", gender2.a());
        string = string.replace("%2s", gender2.s());
        string = string.replace("%2o", gender2.o());
        string = string.replace("%2p", gender2.p());
        string = string.replace("%2r", gender2.r());
        string = string.replace("%2l", gender2.l());
        string = string.replace("%2A", capitalize(gender2.a()));
        string = string.replace("%2S", capitalize(gender2.s()));
        string = string.replace("%2O", capitalize(gender2.o()));
        string = string.replace("%2P", capitalize(gender2.p()));
        string = string.replace("%2R", capitalize(gender2.r()));
        string = string.replace("%2L", capitalize(gender2.l()));
        return string;
    }

    public static String indefinite(String name) {
        boolean startsWithVowel = "aeiouAEIOU".indexOf(name.charAt(0)) != -1;
        return startsWithVowel ? "an" : "a";
    }

    public static boolean testMTTH(int mtth) {
        float chance = 1.0f / mtth;
        return Game.random.nextFloat() <= chance;
    }

    public static int randomBetween(int min, int max) {
        if (min >= max) {
            return min;
        }
        return min + Game.random.nextInt(max - min + 1);
    }

    public static int randomRound(float input) {
        int amount = (int)input;
        if (Game.random.nextFloat() > (input - amount)) {
            amount++;
        }
        return amount;
    }

    public static float roundPrecision(float input, int precision) {
        return ((int)(input * precision)) / (float)precision;
    }

    public static String weightStringSpaced(float input) {
        return String.format("%5.1fkg", input);
    }

    public static String weightString(float input) {
        return String.format("%1.1fkg", input);
    }
}
