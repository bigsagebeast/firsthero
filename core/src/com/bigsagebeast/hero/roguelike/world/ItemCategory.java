package com.bigsagebeast.hero.roguelike.world;

import java.util.ArrayList;
import java.util.List;

public class ItemCategory {

    public static ItemCategory CATEGORY_ONE_HANDED_WEAPONS = new ItemCategory("One-Handed Weapons", "onehanded");
    public static ItemCategory CATEGORY_TWO_HANDED_WEAPONS = new ItemCategory("Two-Handed Weapons", "twohanded");
    public static ItemCategory CATEGORY_SHIELDS = new ItemCategory("Shields", "shields");
    public static ItemCategory CATEGORY_RANGED = new ItemCategory("Ranged Weapons", "rangedweapon");
    public static ItemCategory CATEGORY_HELMETS = new ItemCategory("Helmets", "head");
    public static ItemCategory CATEGORY_BODY = new ItemCategory("Body Armor", "body");
    public static ItemCategory CATEGORY_AMMO = new ItemCategory("Ammunition", "ammunition");
    public static ItemCategory CATEGORY_POTION = new ItemCategory("Potions", "potion");
    public static ItemCategory CATEGORY_SCROLL = new ItemCategory("Scrolls", "scroll");
    public static ItemCategory CATEGORY_BOOK = new ItemCategory("Books", "book");
    public static ItemCategory CATEGORY_FOOD = new ItemCategory("Food", "food");
    public static ItemCategory CATEGORY_GOLD = new ItemCategory("Gold", "gold");

    public static List<ItemCategory> categories = new ArrayList<>();

    static {
        categories.add(CATEGORY_ONE_HANDED_WEAPONS);
        categories.add(CATEGORY_TWO_HANDED_WEAPONS);
        categories.add(CATEGORY_SHIELDS);
        categories.add(CATEGORY_HELMETS);
        categories.add(CATEGORY_BODY);
        categories.add(CATEGORY_RANGED);
        categories.add(CATEGORY_AMMO);
        categories.add(CATEGORY_POTION);
        categories.add(CATEGORY_SCROLL);
        categories.add(CATEGORY_BOOK);
        categories.add(CATEGORY_FOOD);
        categories.add(CATEGORY_GOLD);
    }

    private String name;
    public String key;
    public ItemCategory(String name, String key) {
        this.name = name;
        this.key = key;
    }
    public String getName() {
        return name;
    }
}
