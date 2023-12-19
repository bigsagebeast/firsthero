package com.churchofcoyote.hero.roguelike.world.enums;

public enum BodySize {
    XL(1600),
    LARGE(1200),
    MEDIUM(800),
    SMALL(400),
    TINY(200);

    public int corpseSatiation;

    BodySize(int corpseSatiation) {
        this.corpseSatiation = corpseSatiation;
    }
}
