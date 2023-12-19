package com.churchofcoyote.hero.roguelike.world.dungeon.generation;

public enum AutomataStatus {
    TRUE(true, false),
    FALSE(false, false),
    ALWAYS_FALSE(false, true),
    ALWAYS_TRUE(true, true),
    RANDOM(false, false);
    public boolean isWall, isImmutable;
    AutomataStatus(boolean isWall, boolean isImmutable) {
        this.isWall = isWall;
        this.isImmutable = isImmutable;
    }
}
