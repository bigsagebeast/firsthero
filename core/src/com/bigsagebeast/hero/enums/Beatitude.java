package com.bigsagebeast.hero.enums;

import com.badlogic.gdx.graphics.Color;

public enum Beatitude {
    BLESSED("blessed", Color.GREEN),
    UNCURSED("uncursed", Color.WHITE),
    CURSED("cursed", Color.RED);

    public String description;
    public Color color;
    Beatitude(String description, Color color) {
        this.description = description;
        this.color = color;
    }
}
