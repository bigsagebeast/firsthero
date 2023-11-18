package com.churchofcoyote.hero.roguelike.world;

import com.churchofcoyote.hero.engine.asciitile.Glyph;
import com.churchofcoyote.hero.glyphtile.PaletteEntry;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class ItemType {
    public String name;
    public Glyph glyph;
    public BodyPart equipmentFor;
    public String glyphName;
    public PaletteEntry palette;
    Set<Consumer<Entity>> setup = new HashSet<>();
}
