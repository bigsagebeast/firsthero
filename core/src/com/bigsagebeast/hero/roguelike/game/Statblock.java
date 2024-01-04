package com.bigsagebeast.hero.roguelike.game;

public class Statblock {
    public int str = 20; // strength
    public int tou = 20; // toughness
    public int dex = 20; // dexterity
    public int agi = 20; // agility
    public int per = 20; // perception
    public int wil = 20; // will
    public int arc = 20; // arcanum
    public int ava = 20; // avatar

    public int speed = 100;

    public int dr = 0; // defense rating
    public int dt = 0; // defense thickness

    public Statblock(int baseline) {
        str = baseline;
        tou = baseline;
        dex = baseline;
        agi = baseline;
        per = baseline;
        wil = baseline;
        arc = baseline;
        ava = baseline;

        dr = 0;
        dt = 0;
    }

    public float hitPointsPerLevel() {
        return tou / 2.0f;
    }

    public float spellPointsPerLevel() {
        return (wil + arc) / 4.0f;
    }

    public float divinePoints() {
        return ava * 50;
    }
}
