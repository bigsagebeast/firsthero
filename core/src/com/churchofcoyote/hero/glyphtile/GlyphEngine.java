package com.churchofcoyote.hero.glyphtile;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.churchofcoyote.hero.*;
import com.churchofcoyote.hero.module.RoguelikeModule;
import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.roguelike.world.Level;
import com.churchofcoyote.hero.roguelike.world.Terrain;
import com.churchofcoyote.hero.util.Point;

import java.util.ArrayList;
import java.util.List;

public class GlyphEngine implements GameLogic {
    public static int GLYPH_WIDTH = 16;
    public static int GLYPH_HEIGHT = 24;

    static final int SCREEN_TILE_WIDTH = 45;
    static final int SCREEN_TILE_HEIGHT = 31;

    static final int RENDER_OFFSET_X = 8;
    static final int RENDER_OFFSET_Y = 16;

    // TODO convert to pos?
    int offsetX = 0;
    int offsetY = 0;

    private GlyphGrid grid;

    private FrameBuffer buffer;
    private TextureRegion texRegion;
    private TerrainGlyph terrainGlyph;
    private EntityGlyph entityGlyph;
    private Level level;

    private Boolean dirty = true;

    public GlyphEngine() {
        GlyphIndex.initialize();
        Palette.initialize();

        loadGlyphFile("tiles/terrain.gly");
    }

    public void initializeLevel(Level level) {
        // TODO caching?
        this.level = level;
        grid = new GlyphGrid(level.getWidth(), level.getHeight());

        // should be initialized by now, since it's static
        terrainGlyph = new TerrainGlyph(Terrain.map);
        entityGlyph = new EntityGlyph();
    }

    private void loadGlyphFile(String filename) {
        GlyphFile file = new GlyphFile(filename);
        TextureData data = file.texture.getTextureData();
        data.prepare();
        Pixmap sheetPixmap = data.consumePixmap();

        for (int row = 0; row < file.rows; row++) {
            for (int column = 0; column < file.columns; column++) {
                if (file.glyphName[row][column] != null) {
                    String[] nameSplit = file.glyphName[row][column].split(":");
                    String blockJoinStr = (nameSplit.length > 1) ? nameSplit[1] : "";

                    int blockJoin = BlockJoin.calculate(
                            blockJoinStr.contains("n"),
                            blockJoinStr.contains("w"),
                            blockJoinStr.contains("e"),
                            blockJoinStr.contains("s"));

                    Pixmap pixmap = new Pixmap(GLYPH_WIDTH, GLYPH_HEIGHT, data.getFormat());
                    pixmap.drawPixmap(sheetPixmap,
                            column * GLYPH_WIDTH, row * GLYPH_HEIGHT, GLYPH_WIDTH, GLYPH_HEIGHT,
                            0, 0, GLYPH_WIDTH, GLYPH_HEIGHT);

                    BaseGlyph baseGlyph = new BaseGlyph(pixmap);

                    GlyphIndex.add(nameSplit[0], baseGlyph, blockJoin);
                }
            }
        }
    }

    private boolean isDirty() {
        return true;
    }

    public void dirty() {
        this.dirty = true;
    }

    boolean matchesBlockType(int x, int y, String blockType) {
        if (x < 0 || x >= level.getWidth() || y < 0 || y >= level.getHeight()) {
            return true;
        }
        if (level.cell(x, y).terrain.getBlockCategory() == null) {
            return false;
        }
        return level.cell(x, y).terrain.getBlockCategory().equals(blockType);
    }

    private void compile() {
        long start = System.currentTimeMillis();
        if (isDirty()) {
            for (int x = offsetX - (SCREEN_TILE_WIDTH / 2); x <= offsetX + (SCREEN_TILE_WIDTH / 2); x++) {
                for (int y = offsetY - (SCREEN_TILE_HEIGHT / 2); y <= offsetY + (SCREEN_TILE_HEIGHT / 2); y++) {
                    // could these ever be different?
                    if (grid.withinBounds(x, y) && level.withinBounds(x, y)) {
                        grid.clearBackground(x, y);

                        if (!level.cell(x, y).explored) {
                            grid.put(GlyphTile.BLANK, x, y);
                            continue;
                        }

                        Terrain t = level.cell(x, y).terrain;
                        GlyphTile[] blockGlyphs = terrainGlyph.getGlyphTile(t);
                        String blockCategory = t.getBlockCategory();
                        int blockDirection = 0;
                        if (t.getBlockCategory() != null) {
                            blockDirection = BlockJoin.calculate(
                                    matchesBlockType(x, y-1, blockCategory),
                                    matchesBlockType(x-1, y, blockCategory),
                                    matchesBlockType(x+1, y, blockCategory),
                                    matchesBlockType(x, y+1, blockCategory)
                            );
                        }
                        GlyphTile terrainGlyph = blockGlyphs[blockDirection];

                        List<GlyphTile> itemTiles = new ArrayList<>();
                        for (Entity item : level.getItemsOnTile(new Point(x, y))) {
                            GlyphTile itemTile = entityGlyph.getGlyph(item);
                            itemTiles.add(itemTile);
                        }

                        GlyphTile moverTile = null;
                        Entity e = level.getMoversOnTile(new Point(x, y)).stream().findAny().orElse(null);
                        if (e != null) {
                            moverTile = entityGlyph.getGlyph(e);
                        }

                        if (moverTile != null && level.cell(x, y).visible()) {
                            grid.put(moverTile, x, y);
                            for (GlyphTile itemTile : itemTiles) {
                                grid.addBackground(itemTile, x, y);
                            }
                        }
                        else if (itemTiles.size() > 0 && level.cell(x, y).visible()) {
                            grid.put(itemTiles.get(0), x, y);
                            for (int i=1; i<itemTiles.size(); i++) {
                                grid.addBackground(itemTiles.get(i), x, y);
                            }
                        } else {
                            grid.put(terrainGlyph, x, y);
                        }
                    }
                }
            }
            dirty = false;
        }
        HeroGame.updateTimer("gCom", System.currentTimeMillis() - start);
    }

    @Override
    public void update(GameState state) {
        if (Game.getPlayerEntity() == null) {
            return;
        }
        offsetX = Game.getPlayerEntity().pos.x;
        offsetY = Game.getPlayerEntity().pos.y;
    }

    @Override
    public void render(Graphics g, GraphicsState gState) {
        if (level == null) {
            return;
        }
        if (buffer == null || isDirty()) {
            compile();
            long start = System.currentTimeMillis();
            if (buffer != null) {
                buffer.dispose();
            }
            buffer = new FrameBuffer(Pixmap.Format.RGBA8888, Graphics.WIDTH, Graphics.HEIGHT, false);
            texRegion = new TextureRegion(buffer.getColorBufferTexture(), 0, 0, Graphics.WIDTH, Graphics.HEIGHT);

            g.endBatch();
            buffer.begin();
            g.startBatch();

            for (int x = 0; x < SCREEN_TILE_WIDTH; x++) {
                for (int y = 0; y < SCREEN_TILE_HEIGHT; y++) {
                    if (grid.withinBounds(x + offsetX - SCREEN_TILE_WIDTH/2, y + offsetY - SCREEN_TILE_HEIGHT/2)) {
                        // TODO background
                        GlyphTile glyph = grid.get(x + offsetX - SCREEN_TILE_WIDTH/2, y + offsetY - SCREEN_TILE_HEIGHT/2);
                        if (glyph != null) {
                            Texture drawTexture = level.cell(x + offsetX - SCREEN_TILE_WIDTH/2, y + offsetY - SCREEN_TILE_HEIGHT/2).visible() ? glyph.texture : glyph.grayTexture;
                            g.batch().draw(drawTexture, RENDER_OFFSET_X + x * GlyphEngine.GLYPH_WIDTH, RENDER_OFFSET_Y + y * GlyphEngine.GLYPH_HEIGHT);
                        }
                    }
                    // TODO else?
                }
            }

            g.endBatch();
            buffer.end();
            g.startBatch();
            HeroGame.updateTimer("gDrw", System.currentTimeMillis() - start);
        }
        g.batch().draw(texRegion, 0, 0, g.WIDTH, g.HEIGHT);

    }

    public void destroyEntity(Entity e) {
        entityGlyph.forget(e);
    }

    public float getTilePixelX(int x, int y) {
        return (x - leftTile() + 0.5f) * GLYPH_WIDTH + RENDER_OFFSET_X;
    }

    public float getTilePixelY(int x, int y) {
        return (y - topTile() + 0.5f) * GLYPH_HEIGHT + RENDER_OFFSET_Y;
    }

    private int leftTile() {
        return offsetX - SCREEN_TILE_WIDTH/2;
    }

    private int topTile() {
        return offsetY - SCREEN_TILE_HEIGHT/2;
    }

}
