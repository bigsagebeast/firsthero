package com.bigsagebeast.hero.roguelike.world;

public enum AmmoType {
    ARROW("arrow"), STONE("stone");
    public String description;
    AmmoType(String description) {
        this.description = description;
    }
}
