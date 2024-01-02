package com.bigsagebeast.hero.roguelike.world;

public enum ItemStatus {
    BLESSED("blessed"),
    UNCURSED("uncursed"),
    CURSED("cursed");

    public final String description;
    ItemStatus(String description) {
        this.description = description;
    }
}
