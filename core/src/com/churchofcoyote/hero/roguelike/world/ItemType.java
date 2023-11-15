package com.churchofcoyote.hero.roguelike.world;

import com.churchofcoyote.hero.engine.asciitile.Glyph;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class ItemType {
    public String name;
    public Glyph glyph;
    Set<Consumer<Entity>> setup = new HashSet<>();
}
