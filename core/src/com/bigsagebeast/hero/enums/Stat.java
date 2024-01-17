package com.bigsagebeast.hero.enums;

import com.bigsagebeast.hero.util.Util;

import java.util.Locale;

public enum Stat {
    STRENGTH,
    TOUGHNESS,
    DEXTERITY,
    AGILITY,
    PERCEPTION,
    WILLPOWER,
    ARCANUM,
    AVATAR;

    public String description() {
        return name().toLowerCase(Locale.ROOT);
    }

    public static float getScalingWithMinimum(int score, float scaling) {
        return (Math.max(10, score) - 10) * scaling;
    }

    public static float getScaling(int score, float scaling) {
        return (score - 10) * scaling;
    }
}
