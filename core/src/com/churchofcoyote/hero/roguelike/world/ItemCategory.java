package com.churchofcoyote.hero.roguelike.world;

import java.util.ArrayList;
import java.util.List;

public class ItemCategory {

    public static ItemCategory CATEGORY_ONE_HANDED_WEAPONS = new ItemCategory("One-Handed Weapons");
    public static ItemCategory CATEGORY_TWO_HANDED_WEAPONS = new ItemCategory("Two-Handed Weapons");
    public static ItemCategory CATEGORY_SHIELDS = new ItemCategory("Shields");
    public static ItemCategory CATEGORY_GOLD = new ItemCategory("Gold");

    public static List<ItemCategory> categories = new ArrayList<>();

    static {
        categories.add(CATEGORY_ONE_HANDED_WEAPONS);
        categories.add(CATEGORY_TWO_HANDED_WEAPONS);
        categories.add(CATEGORY_SHIELDS);
        categories.add(CATEGORY_GOLD);
    }


    private String name;
    public ItemCategory(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
}
