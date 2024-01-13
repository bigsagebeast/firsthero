package com.bigsagebeast.hero.enums;

public enum WeaponType {
    THIN_BLADE("thin blade"),
    BROAD_BLADE("broad blade"),
    AXE("axe"),
    BLUDGEON("bludgeon"),
    POLEARM("polearm");

    public String name;

    WeaponType(String name) {
        this.name = name;
    }
}
