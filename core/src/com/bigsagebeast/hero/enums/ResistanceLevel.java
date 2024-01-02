package com.bigsagebeast.hero.enums;

public enum ResistanceLevel {
    IMMUNE(0f),
    VERY_RESISTANT(0.25f),
    RESISTANT(0.5f),
    NORMAL(1f),
    WEAK(2f);

    public float multiplier;

    ResistanceLevel(float multiplier) {
        this.multiplier = multiplier;
    }

    public ResistanceLevel counterToEnum(int counter) {
        if (counter < 0) return WEAK;
        if (counter == 0) return NORMAL;
        if (counter == 1) return RESISTANT;
        if (counter == 2) return VERY_RESISTANT;
        return IMMUNE;
    }
}
