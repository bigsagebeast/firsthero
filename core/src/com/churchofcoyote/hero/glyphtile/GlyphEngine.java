package com.churchofcoyote.hero.glyphtile;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.churchofcoyote.hero.GameLogic;
import com.churchofcoyote.hero.GameState;
import com.churchofcoyote.hero.Graphics;
import com.churchofcoyote.hero.GraphicsState;

public class GlyphEngine implements GameLogic {
    public static int GLYPH_WIDTH = 16;
    public static int GLYPH_HEIGHT = 24;

    public GlyphEngine() {
        GlyphIndex.initialize();
        Palette.initialize();

        loadGlyphFile("tiles/terrain.gly");
    }

    private void loadGlyphFile(String filename) {
        GlyphFile file = new GlyphFile(filename);
        TextureData data = file.texture.getTextureData();
        data.prepare();
        Pixmap sheetPixmap = data.consumePixmap();

        for (int row = 0; row < file.rows; row++) {
            for (int column = 0; column < file.columns; column++) {
                if (file.glyphName[row][column] != null) {
                    TextureRegion glyphRegion = new TextureRegion(file.texture,
                            column * GLYPH_WIDTH, row * GLYPH_HEIGHT);

                    Pixmap pixmap = new Pixmap(GLYPH_WIDTH, GLYPH_HEIGHT, data.getFormat());
                    pixmap.drawPixmap(sheetPixmap,
                            column * GLYPH_WIDTH, row * GLYPH_HEIGHT, GLYPH_WIDTH, GLYPH_HEIGHT,
                            0, 0, GLYPH_WIDTH, GLYPH_HEIGHT);

                    BaseGlyph baseGlyph = new BaseGlyph(pixmap);
                    GlyphIndex.add(file.glyphName[row][column], baseGlyph);
                }
            }
        }
    }

    @Override
    public void update(GameState state) {

    }

    @Override
    public void render(Graphics g, GraphicsState gState) {
        GlyphTile grass = GlyphIndex.get("wall:n").create(Palette.COLOR_WHITE, Palette.COLOR_BROWN, Palette.COLOR_BROWN, Palette.COLOR_TRANSPARENT);
        g.batch().draw(grass.texture, 20, 20);
    }
}
