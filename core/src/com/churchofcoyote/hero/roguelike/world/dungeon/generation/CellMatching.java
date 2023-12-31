package com.churchofcoyote.hero.roguelike.world.dungeon.generation;

public enum CellMatching {
    INTERIOR,
    EXTERIOR_VALID,
    EXTERIOR_INVALID;

    public String toString() {
        return this.name();
    }
}
