package com.bigsagebeast.hero.roguelike.game;

import com.bigsagebeast.hero.enums.Stat;

import java.util.HashMap;

public class Statblock {
    public HashMap<Stat, Integer> map = new HashMap<Stat, Integer>();
    public static final int MIN_STAT = 1;
    public static final int MAX_STAT = 99;

    public int speed = 100;

    public int dr = 0; // defense rating
    public int dt = 0; // defense thickness

    public Statblock(int baseline) {
        for (Stat stat : Stat.values()) {
            map.put(stat, baseline);
        }

        dr = 0;
        dt = 0;
    }

    public int get(Stat stat) {
        return map.get(stat);
    }

    public void set(Stat stat, int val) {
        map.put(stat, normalize(stat, val));
    }

    public void change(Stat stat, int delta) {
        set(stat, normalize(stat, get(stat) + delta));
    }

    public static int normalize(Stat stat, int val) {
        // avatar can have a value of 0, as a treat
        if (stat == Stat.AVATAR) {
            return Math.min(Math.max(val, 0), MAX_STAT);
        } else {
            return Math.min(Math.max(val, MIN_STAT), MAX_STAT);
        }
    }
}
