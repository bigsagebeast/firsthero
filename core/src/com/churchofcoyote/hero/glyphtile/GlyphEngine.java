package com.churchofcoyote.hero.glyphtile;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.churchofcoyote.hero.*;
import com.churchofcoyote.hero.engine.WindowEngine;
import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.roguelike.world.dungeon.Level;
import com.churchofcoyote.hero.roguelike.world.Terrain;
import com.churchofcoyote.hero.ui.UIManager;
import com.churchofcoyote.hero.util.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GlyphEngine implements GameLogic {
    public static int GLYPH_WIDTH = 16;
    public static int GLYPH_HEIGHT = 24;

    static final int RENDER_OFFSET_X = 8;
    static final int RENDER_OFFSET_Y = 16;

    public float zoom = 1.0f;

    // TODO convert to pos?
    int offsetX = 0;
    int offsetY = 0;

    // store last margins from resize
    int marginX;
    int marginY;

    private GlyphGrid grid;

    private FrameBuffer buffer;
    private TextureRegion texRegion;
    private TerrainGlyph terrainGlyph;
    private EntityGlyph entityGlyph;
    private Level level;

    private Boolean dirty = true;

    public GlyphEngine() {
    }

    public void initialize() throws SetupException{
        GlyphIndex.initialize();
        Palette.initialize();

        loadGlyphFile("tiles/terrain.gly");
        loadGlyphFile("tiles/humanoid.gly");
        loadGlyphFile("tiles/animal.gly");
        loadGlyphFile("tiles/items.gly");
        loadGlyphFile("tiles/armor.gly");
        loadGlyphFile("tiles/misc.gly");
        loadGlyphFile("tiles/feature.gly");
        loadGlyphFile("tiles/aurex.gly");
    }

    public void initializeLevel(Level level) {
        // TODO caching?
        this.level = level;
        grid = new GlyphGrid(level.getWidth(), level.getHeight());

        // should be initialized by now, since it's static
        terrainGlyph = new TerrainGlyph(Terrain.map);
        entityGlyph = new EntityGlyph();
    }

    private void loadGlyphFile(String filename) throws SetupException {
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

    boolean matchesBlockType(int x, int y, String blockType, String[] matchingBlocks) {
        if (x < 0 || x >= level.getWidth() || y < 0 || y >= level.getHeight()) {
            return true;
        }
        String blockCategory = level.cell(x, y).terrain.getBlockCategory();
        if (blockCategory == null) {
            return false;
        }
        if (blockCategory.equals(blockType)) {
            return true;
        }
        if (matchingBlocks != null && Arrays.asList(matchingBlocks).contains(blockCategory)) {
            return true;
        }
        return false;
    }

    private float tileWidth () {
        return GLYPH_WIDTH * zoom;
    }

    private float tileHeight () {
        return GLYPH_HEIGHT * zoom;
    }

    private void compile() {
        long start = System.currentTimeMillis();

        Point size = WindowEngine.getSize(UIManager.NAME_MAIN_WINDOW);
        //int widthInTiles = (int)((size.x / 2 - 1) / tileWidth);
        //int heightInTiles = (int)((size.y / 2 - 1) / tileHeight);
        int widthInTiles = (int)((size.x / 2 - 1) / tileWidth());
        int heightInTiles = (int)((size.y / 2 - 1) / tileHeight());

        if (isDirty()) {
            for (int x = offsetX - widthInTiles; x <= offsetX + widthInTiles; x++) {
                for (int y = offsetY - heightInTiles; y <= offsetY + heightInTiles; y++) {
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
                        String[] matchingBlocks = t.getMatchingBlocks();
                        int blockDirection = 0;
                        if (t.getBlockCategory() != null) {
                            blockDirection = BlockJoin.calculate(
                                    matchesBlockType(x, y-1, blockCategory, matchingBlocks),
                                    matchesBlockType(x-1, y, blockCategory, matchingBlocks),
                                    matchesBlockType(x+1, y, blockCategory, matchingBlocks),
                                    matchesBlockType(x, y+1, blockCategory, matchingBlocks)
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
                        // TODO: Figure out a better solution for displaying items that aren't presently visible?
                        else if (itemTiles.size() > 0/* && level.cell(x, y).visible()*/) {
                            grid.put(itemTiles.get(0), x, y);
                            for (int i=1; i<itemTiles.size(); i++) {
                                grid.addBackground(itemTiles.get(i), x, y);
                            }
                        } else {
                            grid.put(terrainGlyph, x, y);
                        }

                        grid.setJitter(x, y, level.getJitterAt(new Point(x, y)));
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
        offsetX = Game.getPlayerEntity().pos.x + 1;
        offsetY = Game.getPlayerEntity().pos.y + 1;
    }

    @Override
    public void render(Graphics g, GraphicsState gState) {
        if (level == null) {
            return;
        }
        Point size = WindowEngine.getSize("mainWindow");
        float tileWidth = GLYPH_WIDTH * zoom;
        float tileHeight = GLYPH_HEIGHT * zoom;
        int widthInTiles = (int)((size.x - 0) / (tileWidth * 2)) * 2;
        int heightInTiles = (int)((size.y - 0) / (tileHeight * 2)) * 2;
        int widthInPixels = (int)(widthInTiles * tileWidth);
        int heightInPixels = (int)(heightInTiles * tileHeight);
        marginX = (size.x - widthInPixels) / 2;
        marginY = (size.y - heightInPixels) / 2;

        if (buffer == null || isDirty()) {
            compile();
            long start = System.currentTimeMillis();
            if (buffer != null) {
                buffer.dispose();
            }
            try {
                buffer = new FrameBuffer(Pixmap.Format.RGBA8888, Graphics.width, Graphics.height, false);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage() + " (width: " + Graphics.width + ", height: " + Graphics.height + ")", e);
            }
            texRegion = new TextureRegion(buffer.getColorBufferTexture(), 0, 0, size.x, size.y);

            g.endBatch();
            buffer.begin();
            g.startBatch();

            for (int x = -2; x < widthInTiles+2; x++) {
                for (int y = -2; y < heightInTiles+2; y++) {
                    if (grid.withinBounds((int)(x + offsetX - widthInTiles/2), (int)(y + offsetY - heightInTiles/2))) {
                        // TODO background
                        GlyphTile glyph = grid.get((int)(x + offsetX - widthInTiles/2), (int)(y + offsetY - heightInTiles/2));
                        if (glyph != null) {
                            int cellX = (int)(x + offsetX - widthInTiles/2);
                            int cellY = (int)(y + offsetY - heightInTiles/2);
                            float jitterPotential = grid.getJitter(cellX, cellY);
                            int jitterX = 0, jitterY = 0;
                            if (jitterPotential > 0.0f) {
                                float jitterPower = 0.1f;
                                double jitterMagnitude = Math.pow(Game.random.nextFloat() * jitterPotential, jitterPower);
                                double angle = Game.random.nextFloat() * 2 * Math.PI;
                                jitterX = (int) (jitterMagnitude * Math.cos(angle));
                                jitterY = (int) (jitterMagnitude * Math.sin(angle));
                            }
                            Texture drawTexture = level.cell(cellX, cellY).visible() ? glyph.texture : glyph.grayTexture;
                            g.batch().draw(drawTexture,
                                    RENDER_OFFSET_X + x * GlyphEngine.GLYPH_WIDTH * zoom + marginX + jitterX,
                                    RENDER_OFFSET_Y + y * GlyphEngine.GLYPH_HEIGHT * zoom + marginY + jitterY,
                                    GlyphEngine.GLYPH_WIDTH * zoom, GlyphEngine.GLYPH_HEIGHT * zoom);
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
        g.endBatch();
        WindowEngine.setDirty(UIManager.NAME_MAIN_WINDOW);
        FrameBuffer fb = WindowEngine.get(UIManager.NAME_MAIN_WINDOW);
        fb.begin();
        g.startBatch();
        texRegion.flip(false, true);
        g.batch().draw(texRegion, 0, 0, size.x, size.y);
        g.endBatch();
        fb.end();
        g.startBatch();
    }

    public void destroyEntity(Entity e) {
        entityGlyph.forget(e);
    }

    public float getTileCenterPixelX(int x, int y) {
        // TODO need to get the margin
        return (x - leftTile() + 0.5f) * tileWidth() + marginX + RENDER_OFFSET_X;
    }

    public float getTileCenterPixelY(int x, int y) {
        // TODO what is this extra offset???
        float extraOffset = -30f;
        return (y - topTile() + 0.5f) * tileHeight() + marginY + RENDER_OFFSET_Y + extraOffset;
    }

    public Point getTileCenterPixel(Point p) {
        return new Point((int)((p.x - leftTile() + 0.5f) * tileWidth() + marginX + RENDER_OFFSET_X),
                (int)((p.y - topTile() + 0.5f) * tileHeight() + marginY + RENDER_OFFSET_Y));
    }

    public Point getTileCenterPixelInWindow(Point p) {
        // TODO what is this extra offset???
        float extraOffset = -30f;
        Point windowOffset = WindowEngine.getOffset(UIManager.NAME_MAIN_WINDOW);
        return new Point((int)((p.x - leftTile() + 0.5f) * tileWidth() + marginX + RENDER_OFFSET_X + windowOffset.x),
                (int)((p.y - topTile() + 0.5f) * tileHeight() + marginY + RENDER_OFFSET_Y + windowOffset.y + extraOffset));
    }

    public float getTilePixelX(int x, int y) {
        // TODO need to get the margin
        return (x - leftTile()) * tileWidth() + marginX + RENDER_OFFSET_X;
    }

    public float getTilePixelY(int x, int y) {
        return (y - topTile()) * tileHeight() + marginY + RENDER_OFFSET_Y;
    }

    public Point getTilePixel(Point p) {
        Point windowOffset = WindowEngine.getOffset(UIManager.NAME_MAIN_WINDOW);
        return new Point((int)((p.x - leftTile()) * tileWidth() + marginX + RENDER_OFFSET_X + windowOffset.x),
                (int)((p.y - topTile()) * tileHeight() + marginY + RENDER_OFFSET_Y) + windowOffset.y);
    }

    public void zoom(float zoom) {
        this.zoom = zoom;
    }

    private int leftTile() {
        Point size = WindowEngine.getSize(UIManager.NAME_MAIN_WINDOW);
        return offsetX - (int)((size.x / tileWidth())/2);
    }

    private int topTile() {
        Point size = WindowEngine.getSize(UIManager.NAME_MAIN_WINDOW);
        return offsetY - (int)((size.y / tileHeight())/2);
    }

}
