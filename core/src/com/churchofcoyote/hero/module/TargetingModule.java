package com.churchofcoyote.hero.module;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.churchofcoyote.hero.GameLoop;
import com.churchofcoyote.hero.GameState;
import com.churchofcoyote.hero.Graphics;
import com.churchofcoyote.hero.GraphicsState;
import com.churchofcoyote.hero.engine.WindowEngine;
import com.churchofcoyote.hero.glyphtile.GlyphEngine;
import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.text.TextBlock;
import com.churchofcoyote.hero.ui.UIManager;
import com.churchofcoyote.hero.util.Fov;
import com.churchofcoyote.hero.util.Point;

import java.util.ArrayList;
import java.util.List;

public class TargetingModule extends Module {

    public TargetMode targetMode;
    public Point targetTile;
    public TextBlock targetBlockParent;
    public TextBlock instructions;
    public TextBlock description;
    public ArrayList<TextBlock> targetBlocks = new ArrayList<>();
    boolean dirty = true;

    public TargetingModule() {
    }

    public void begin(TargetMode targetMode) {
        WindowEngine.setDirty(UIManager.NAME_MAIN_WINDOW);
        targetTile = Game.getPlayerEntity().pos;
        this.targetMode = targetMode;
        // TODO draw black boxes over the top and bottom of the main window
        instructions = new TextBlock("'t' to select target, space to cancel", UIManager.NAME_MAIN_WINDOW, RoguelikeModule.FONT_SIZE,
                0, 0, 20, 0, Color.WHITE);
        instructions.compile();
        description = new TextBlock("Target description", UIManager.NAME_MAIN_WINDOW, RoguelikeModule.FONT_SIZE,
                0, 0, 20, 0, Color.WHITE);
        instructions.compile();
        description.compile();
        GameLoop.uiEngine.addBlock(instructions);
        GameLoop.uiEngine.addBlock(description);

        super.start();
    }

    @Override
    public void update(GameState state) {
        if (!dirty) {
            return;
        }
        // TODO fix positioning
        Point windowSize = WindowEngine.getSize(UIManager.NAME_MAIN_WINDOW);
        Point windowOffset = WindowEngine.getOffset(UIManager.NAME_MAIN_WINDOW);
        description.pixelOffsetY = windowOffset.y + RoguelikeModule.FONT_SIZE;
        instructions.pixelOffsetY = windowSize.y - RoguelikeModule.FONT_SIZE;
        instructions.compile();
        description.compile();
        WindowEngine.setDirty(UIManager.NAME_MAIN_WINDOW);
        if (targetBlockParent != null) {
            targetBlockParent.close();
        }

        if (Game.getLevel().cell(targetTile).visible()) {
            Entity targetMover = Game.getLevel().moverAt(targetTile.x, targetTile.y);
            if (targetMover != null) {
                // TODO fetch a text block, child it to description
                description.text = "You see a " + targetMover.getVisibleName() + ".";
            } else {
                // TODO fetch a text block etc
                List<Entity> targetItems = Game.getLevel().getItemsOnTile(targetTile);
                if (targetItems.size() > 1) {
                    description.text = "You see " + targetItems.get(0).getVisibleNameSingularOrVague() + " and " + (targetItems.size() - 1) + " other items.";
                } else if (targetItems.size() == 1) {
                    description.text = "You see " + targetItems.get(0).getVisibleNameSingularOrVague() + ".";
                } else {
                    description.text = "You see " + Game.getLevel().cell(targetTile).terrain.getDescription() + ".";
                }
            }
        } else {
            description.text = "You can't see there.";
        }

        targetBlockParent = new TextBlock("", UIManager.NAME_MAIN_WINDOW, 12, 0, 0, 0, 0, Color.WHITE);
        //ArrayList<Point> ray = new ArrayList<>();
        //ray.add(targetTile);

        // Permissive = we can see it through FOV calculation, so use a more permissive LOS calculation
        // Not permissive = we think we can't see it, so accept a simple failure
        boolean permissive = Game.getLevel().cell(targetTile).visible();
        Point origin = Game.getPlayerEntity().pos;
        List<Point> ray = Fov.findRay(Game.getLevel(), origin, targetTile, permissive);

        boolean valid = true;
        for (int i=0; i<ray.size(); i++) {
            Point currentRay = ray.get(i);
            if (currentRay == null) {
                valid = false;
                continue;
            }
            Point centerPixel = GameLoop.glyphEngine.getTileCenterPixelInWindow(currentRay);
            float fontSize = RoguelikeModule.FONT_SIZE * 1.2f * GameLoop.glyphEngine.zoom;

            String symbol;
            if (i == ray.size()-1) {
                symbol = "X";
            } else {
                int deltaX = Math.abs(origin.x - targetTile.x);
                int deltaY = Math.abs(origin.y - targetTile.y);
                if (deltaX >= deltaY) {
                    symbol = "-";
                } else {
                    symbol = "|";
                }

                // this didn't look good
                /*
                if (i > 0) {
                    Point curPoint = ray.get(i);
                    Point lastPoint = ray.get(i-1) != null ? ray.get(i-1) : ray.get(i-2);
                    int deltaX = curPoint.x - lastPoint.x;
                    int deltaY = curPoint.y - lastPoint.y;
                    if (deltaX == 0 && deltaY != 0) symbol = "|";
                    if (deltaX != 0 && deltaY == 0) symbol = "-";
                    if (deltaX * deltaY == 1) symbol = "\\";
                    if (deltaX * deltaY == -1) symbol = "/";
                }
                 */
            }
            Color color = Color.GREEN;
            if (!valid) {
                color = Color.RED;
            }

            // TODO better font centering calculation
            TextBlock tb = new TextBlock(symbol, null,
                    fontSize,
                    0, 0, centerPixel.x - (fontSize / 3), centerPixel.y - (fontSize / 3),
                    color);
            // ugly!
            if (tb.text == "|") {
                tb.pixelOffsetX += (fontSize / 3);
            }

            targetBlocks.add(tb);
            targetBlockParent.addChild(tb);
        }

        targetBlockParent.compile();
        GameLoop.uiEngine.addBlock(targetBlockParent);

        dirty = false;
    }

    @Override
    public void render(Graphics g, GraphicsState gState) {
    }

    @Override
    public boolean keyDown(int keycode, boolean shift, boolean ctrl, boolean alt) {
        dirty = true;
        switch (keycode) {
            case Input.Keys.SPACE:
                close();
            case Input.Keys.LEFT:
            case Input.Keys.NUMPAD_4:
                moveTarget(-1, 0);
                break;
            case Input.Keys.RIGHT:
            case Input.Keys.NUMPAD_6:
                moveTarget(1, 0);
                break;
            case Input.Keys.UP:
            case Input.Keys.NUMPAD_8:
                moveTarget(0, -1);
                break;
            case Input.Keys.DOWN:
            case Input.Keys.NUMPAD_2:
                moveTarget(0, 1);
                break;
            case Input.Keys.HOME:
            case Input.Keys.NUMPAD_7:
                moveTarget(-1, -1);
                break;
            case Input.Keys.END:
            case Input.Keys.NUMPAD_1:
                moveTarget(-1, 1);
                break;
            case Input.Keys.PAGE_UP:
            case Input.Keys.NUMPAD_9:
                moveTarget(1, -1);
                break;
            case Input.Keys.PAGE_DOWN:
            case Input.Keys.NUMPAD_3:
                moveTarget(1, 1);
                break;
        }
        return true;
    }

    public void moveTarget(int x, int y) {
        targetTile = new Point(targetTile.x + x, targetTile.y + y);
    }

    public void close() {
        WindowEngine.setDirty(UIManager.NAME_MAIN_WINDOW);
        targetBlockParent.close();
        targetBlockParent = null;
        targetBlocks.clear();
        instructions.close();
        instructions = null;
        description.close();
        description = null;
        end();
    }


    public class TargetMode {
        public boolean lineFromPlayer;
        public boolean trackMovers;
        public float maxRange;

        public TargetMode(boolean lineFromPlayer, boolean trackMovers, float maxRange) {
            this.lineFromPlayer = lineFromPlayer;
            this.trackMovers = trackMovers;
            this.maxRange = maxRange;
        }
    }
}
