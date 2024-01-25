package com.bigsagebeast.hero.enums;

public enum DamageType {
    PIERCING("piercing damage"),
    SLASHING("slashing damage"),
    BLUDGEONING("bludgeoning damage"),
    MAGIC("basic magic"), // generic like Magic Missile
    FIRE("fire"),
    WATER("water"),
    ELECTRIC("electricity"),
    NATURAE("naturae"),
    ICE("ice"),
    POISON("poison"),
    ACID("acid");

    public String description;

    DamageType(String description) {
        this.description = description;
    }
}
