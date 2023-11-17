package com.churchofcoyote.hero.roguelike.world;

public class BodyPart {

    public static BodyPart PRIMARY_HAND = new BodyPart("primary hand", "Primary");
    public static BodyPart OFF_HAND = new BodyPart("off hand", "Offhand");

    public static BodyPart HEAD = new BodyPart("head", "Head");
    public static BodyPart TORSO = new BodyPart("torso", "Torso");
    public static BodyPart LEGS = new BodyPart("legs", "Legs");
    public static BodyPart HANDS = new BodyPart("hands", "Hands");
    public static BodyPart FEET = new BodyPart("feet", "Feet");

    // do not put on a body part
    public static BodyPart ANY_HAND = new BodyPart("any hand", "Any Hand");
    public static BodyPart TWO_HAND = new BodyPart("two hand", "Two Hand");

    private String name;
    private String abbrev;

    public BodyPart(String name, String abbrev) {
        this.name = name;
        this.abbrev = abbrev;
    }

    public String getName()
    {
        return name;
    }

    public String getAbbrev()
    {
        return abbrev;
    }
}
