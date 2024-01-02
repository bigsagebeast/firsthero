package com.bigsagebeast.hero.enums;

public enum Ambulation {
    WALKING_SWIMMING, // swimming takes more energy
    WALKING_ONLY, // can't enter water
    SWIMMING_ONLY, // can't enter land
    AMPHIBIOUS, // swimming doesn't take more energy
    FLYING // same as amphibious, but other perks
}
