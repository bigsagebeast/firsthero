package com.churchofcoyote.hero.roguelike.world.dungeon.generation;

public enum AutomataStatus {
    WALL(true, false),
    FLOOR(false, false),
    ALWAYS_FLOOR(false, true),
    ALWAYS_WALL(true, true),
    RANDOM(false, false);
    public boolean isWall, isImmutable;
    AutomataStatus(boolean isWall, boolean isImmutable) {
        this.isWall = isWall;
        this.isImmutable = isImmutable;
    }
}
