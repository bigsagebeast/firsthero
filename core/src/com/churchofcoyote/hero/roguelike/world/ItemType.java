package com.churchofcoyote.hero.roguelike.world;

import com.churchofcoyote.hero.glyphtile.PaletteEntry;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class ItemType {
    public String keyName;
    public String name;
    public String unidentifiedName;
    //public Glyph glyph;
    public BodyPart equipmentFor;
    public String glyphName;
    public PaletteEntry palette;
    public ItemCategory category;
    public int level = -1;
    public boolean stackable = false;
    Set<Consumer<Entity>> setup = new HashSet<>();
}
