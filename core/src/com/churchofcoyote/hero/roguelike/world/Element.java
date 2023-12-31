package com.churchofcoyote.hero.roguelike.world;

import java.util.ArrayList;
import java.util.HashMap;

public enum Element {
    FIRE("fire", "F"),
    WATER("water", "W"),
    LIGHTNING("lightning", "L"),
    NATURAE("naturae", "N");

    public String name;
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

    Element(String name, String symbol) {
        this.name = name;
        this.symbol = symbol;
    }
}
