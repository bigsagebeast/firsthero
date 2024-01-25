package com.bigsagebeast.hero.roguelike.world;

import com.bigsagebeast.hero.glyphtile.PaletteEntry;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.proc.item.ProcArmor;
import com.bigsagebeast.hero.roguelike.world.proc.item.ProcWeaponAmmo;
import com.bigsagebeast.hero.roguelike.world.proc.item.ProcWeaponMelee;
import com.bigsagebeast.hero.roguelike.world.proc.item.ProcWeaponRanged;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class ItemType {
    public String file;
    public String keyName;
    public String idenGroup;
    public String unidGroup;
    public String name;
    public String pluralName;
    public String unidentifiedName;
    public String unidentifiedPluralName;
    public String unidDescription = "Placeholder Unid Description";
    public String description = "Placeholder Description";
    public boolean isFeature = false;
    public BodyPart equipmentFor;
    public String glyphName;
    public String[] glyphNames;
    public boolean hide = false;
    public PaletteEntry palette;
    public ItemCategory category;
    public int level = -1;
    public boolean stackable = true;
    public boolean identityHidden = false;
    public boolean identified = false;
    public boolean hasBeatitude = true;
    public boolean hasStatsToIdentify = false;
    public int curseChance = 10;
    public int blessChance = 10;
    public int minCount = 1;
    public int maxCount = 1;
    public int frequency = 100;
    public int sortOrder = 1;
    public boolean hideWalkOver = false;
    public float weight = 0.0f;
    Set<Consumer<Entity>> setup = new HashSet<>();
    public List<LoadProc> procLoaders = new ArrayList<>();
    public List<String> tags = new ArrayList<>();

    // How many have spawned this game?
    public int spawnCount = 0;
}
