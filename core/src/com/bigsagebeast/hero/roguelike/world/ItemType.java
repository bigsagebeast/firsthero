package com.bigsagebeast.hero.roguelike.world;

import com.bigsagebeast.hero.glyphtile.PaletteEntry;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class ItemType {
    public String keyName;
    public String idenGroup;
    public String unidGroup;
    public String name;
    public String pluralName;
    public String unidentifiedName;
    public String unidentifiedPluralName;
    public boolean isFeature = false;
    public BodyPart equipmentFor;
    public String glyphName;
    public PaletteEntry palette;
    public ItemCategory category;
    public int level = -1;
    public boolean stackable = true;
    public boolean identityHidden = false;
    public boolean identified = false;
    public boolean hasBeatitude = true;
    public int minCount = 1;
    public int maxCount = 1;
    public int frequency = 100;
    public int sortOrder = 1;
    public boolean hideWalkOver = false;
    Set<Consumer<Entity>> setup = new HashSet<>();
    public List<LoadProc> procLoaders = new ArrayList<>();
    public List<String> tags = new ArrayList<>();
}
