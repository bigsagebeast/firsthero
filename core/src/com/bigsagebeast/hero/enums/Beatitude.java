package com.bigsagebeast.hero.enums;

public enum Beatitude {
    BLESSED("blessed"),
    UNCURSED("uncursed"),
    CURSED("cursed");

    public String description;
    Beatitude(String description) {
        this.description = description;
    }
}
