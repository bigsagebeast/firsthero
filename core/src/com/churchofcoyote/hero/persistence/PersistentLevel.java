package com.churchofcoyote.hero.persistence;

import com.churchofcoyote.hero.roguelike.world.dungeon.Level;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PersistentLevel {
    public byte[] persist(Level level) {
        byte[] bytes = null;



        return bytes;
    }

    private class Header {
        public String name;
        public int width;
        public int height;

        public byte[] persist(Level level) {
            return null;
        }
    }
}
