package com.churchofcoyote.hero.glyphtile;

import java.util.HashMap;

public class GlyphIndex {
    private static GlyphIndex instance;

    public HashMap<String, BaseGlyph> index = new HashMap<>();

    public static void initialize() {
        if (instance != null)
            throw new RuntimeException();
        instance = new GlyphIndex();
    }

    public static GlyphIndex getInstance() {
        return instance;
    }

    public static void add(String glyphName, BaseGlyph glyph) {
        instance.index.put(glyphName, glyph);
    }

    public static BaseGlyph get(String glyphName) {
        return instance.index.get(glyphName);
    }
}
