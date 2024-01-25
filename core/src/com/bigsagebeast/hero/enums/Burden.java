package com.bigsagebeast.hero.enums;

import com.badlogic.gdx.graphics.Color;

public enum Burden {
    UNBURDENED("", "You are no longer burdened.", Color.WHITE),
    BURDENED("Burdened", "You are now burdened by weight.", Color.WHITE),
    STRAINED("Strained", "You are now strained by weight.", Color.YELLOW),
    OVERLOADED("Overloaded!", "You are now overloaded by weight!", Color.RED);

    Burden(String description, String message, Color color) {
        this.description = description;
        this.message = message;
        this.color = color;
    }
    public String description;
    public String message;
    public Color color;
}
