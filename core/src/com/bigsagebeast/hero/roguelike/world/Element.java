package com.bigsagebeast.hero.roguelike.world;

import java.util.HashMap;

public enum Element {
    FIRE("fire", "F"),
    WATER("water", "W"),
    LIGHTNING("lightning", "L"),
    NATURAE("naturae", "N");

    public String description;
    public String symbol;

    private static HashMap<String, Element> symbols = new HashMap<>();

    static {
        for (Element e : Element.values()) {
            symbols.put(e.symbol, e);
        }
    }

    public static Element lookup(String symbol) {
        return symbols.get(symbol);
    }

    Element(String description, String symbol) {
        this.description = description;
        this.symbol = symbol;
    }
}
