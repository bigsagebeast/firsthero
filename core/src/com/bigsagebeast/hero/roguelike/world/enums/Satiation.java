package com.bigsagebeast.hero.roguelike.world.enums;

import com.badlogic.gdx.graphics.Color;

public enum Satiation {
    DEAD("Starved", Color.FIREBRICK, 0, "You have died of hunger..."),
    STARVING("Starving", Color.RED, 500, "You are starving!"),
    HUNGRY("Hungry", Color.YELLOW, 1000, "You are hungry."),
    PECKISH("Peckish", Color.WHITE, 2000, "You feel a little hungry."),
    FULL(null, null, 4000, "You aren't hungry."),
    SATIATED("Satiated", Color.WHITE, 5000, "You are satiated."),
    STUFFED("Stuffed", Color.WHITE, 99999, "You are stuffed!");

    public static int startingSatiation = 3500;
    public String description;
    public Color statusColor;
    public int topThreshold;
    public String message;

    Satiation(String description, Color statusColor, int topThreshold, String message) {
        this.description = description;
        this.statusColor = statusColor;
        this.topThreshold = topThreshold;
        this.message = message;
    }

    public static Satiation getStatus(float satiation) {
        if (satiation <= DEAD.topThreshold) {
            return DEAD;
        } else if (satiation <= STARVING.topThreshold) {
            return STARVING;
        } else if (satiation <= HUNGRY.topThreshold) {
            return HUNGRY;
        } else if (satiation <= PECKISH.topThreshold) {
            return PECKISH;
        } else if (satiation <= FULL.topThreshold) {
            return FULL;
        } else if (satiation <= SATIATED.topThreshold) {
            return SATIATED;
        } else {
            return STUFFED;
        }
    }
}
