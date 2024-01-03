package com.bigsagebeast.hero.enums;

import com.badlogic.gdx.graphics.Color;

public enum Satiation {
    DEAD("Starved", Color.FIREBRICK, 0, null, "You have died of hunger..."),
    STARVING("Starving", Color.RED, 500, null, "You are starving!"),
    HUNGRY("Hungry", Color.YELLOW, 1000, "You are no longer starving, but you're still hungry.", "You are hungry."),
    PECKISH("Peckish", Color.WHITE, 2000, "You are only a little peckish now.", "You feel a little hungry."),
    FULL(null, null, 4000, "You aren't hungry anymore.", "You no longer feel so full."),
    SATIATED("Satiated", Color.WHITE, 5000, "You are satiated.", "You're not so stuffed anymore."),
    STUFFED("Stuffed", Color.WHITE, 99999, "You are stuffed!", null);

    public static int startingSatiation = 3500;
    public String description;
    public Color statusColor;
    public int topThreshold;
    public String messageUp;
    public String messageDown;

    Satiation(String description, Color statusColor, int topThreshold, String messageUp, String messageDown) {
        this.description = description;
        this.statusColor = statusColor;
        this.topThreshold = topThreshold;
        this.messageUp = messageUp;
        this.messageDown = messageDown;
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
