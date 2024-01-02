package com.bigsagebeast.hero.roguelike.world;

import java.util.HashMap;

public class BodyPart {

    public static BodyPart PRIMARY_HAND = new BodyPart("primary hand", "Primary", 0);
    public static BodyPart OFF_HAND = new BodyPart("off hand", "Offhand", 1);

    public static BodyPart HEAD = new BodyPart("head", "Head", 2);
    public static BodyPart TORSO = new BodyPart("torso", "Torso", 3);
    public static BodyPart LEGS = new BodyPart("legs", "Legs", 4);
    public static BodyPart HANDS = new BodyPart("hands", "Hands", 5);
    public static BodyPart FEET = new BodyPart("feet", "Feet", 6);

    public static BodyPart LEFT_RING = new BodyPart("left ring", "l.ring", 6);
    public static BodyPart RIGHT_RING = new BodyPart("right ring", "r.ring", 6);

    public static BodyPart RANGED_WEAPON = new BodyPart("ranged weapon", "Ranged", 7);
    public static BodyPart RANGED_AMMO = new BodyPart("ammunition", "Ammo", 8);

    // do not put on a body part
    public static BodyPart ANY_HAND = new BodyPart("any hand", "Any Hand", -1);
    public static BodyPart TWO_HAND = new BodyPart("two hand", "Two Hand", -1);
    public static BodyPart RING = new BodyPart("ring", "ring", -1);

    private static HashMap<String, BodyPart> bodyPartMap;

    private String name;
    private String abbrev;
    private int index;

    // TODO: This is for deserialization, but do we need to serialize these?
    private BodyPart() {}
    private BodyPart(String name, String abbrev, int index) {
        if (bodyPartMap == null) {
            bodyPartMap = new HashMap<>();
        }
        this.name = name;
        this.abbrev = abbrev;
        this.index = index;
        bodyPartMap.put(name, this);
    }

    public static BodyPart getPart(String key) { return bodyPartMap.get(key); }

    public String getName()
    {
        return name;
    }

    public String getAbbrev()
    {
        return abbrev;
    }

    public int getIndex() { return index; }
}
