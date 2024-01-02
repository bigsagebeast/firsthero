package com.bigsagebeast.hero.glyphtile;

import java.util.HashMap;

public class GlyphIndex {
    private static GlyphIndex instance;

    public HashMap<String, BaseGlyph[]> index = new HashMap<>();

    public static void initialize() {
        if (instance != null)
            throw new RuntimeException();
        instance = new GlyphIndex();
    }

    public static GlyphIndex getInstance() {
        return instance;
    }

    public static void add(String glyphName, BaseGlyph glyph) {
        if (!instance.index.containsKey(glyphName)) {
            instance.index.put(glyphName, new BaseGlyph[BlockJoin.SIZE]);
        }
        instance.index.get(glyphName)[0] = glyph;
    }

    public static void add(String glyphName, BaseGlyph glyph, int blockJoin) {
        if (!instance.index.containsKey(glyphName)) {
            instance.index.put(glyphName, new BaseGlyph[BlockJoin.SIZE]);
        }
        instance.index.get(glyphName)[blockJoin] = glyph;
    }

    public static BaseGlyph get(String glyphName) {
        return get(glyphName, 0);
    }
    public static BaseGlyph get(String glyphName, int blockJoin) {
        if (instance.index.get(glyphName) == null)
        {
            throw new RuntimeException("Glyph index doesn't contain an entry for " + glyphName);
        }
        return instance.index.get(glyphName)[blockJoin];
    }
}
