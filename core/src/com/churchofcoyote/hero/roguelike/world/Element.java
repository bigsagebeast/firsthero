package com.churchofcoyote.hero.roguelike.world;

import java.util.ArrayList;

public enum Element {
    FIRE("fire"),
    WATER("water"),
    LIGHTNING("lightning"),
    PLANT("plant");

    public String name;

    Element(String name) {
        this.name = name;
    }
}
