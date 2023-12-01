package com.churchofcoyote.hero.module;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.churchofcoyote.hero.GameLoop;
import com.churchofcoyote.hero.GameState;
import com.churchofcoyote.hero.Graphics;
import com.churchofcoyote.hero.GraphicsState;
import com.churchofcoyote.hero.engine.WindowEngine;
import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.text.TextBlock;
import com.churchofcoyote.hero.ui.UIManager;
import com.churchofcoyote.hero.util.Fov;
import com.churchofcoyote.hero.util.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TargetingModule extends Module {

    private static int ANIMATION_STEP_TIME = 1;
    private OperationMode operationMode;
    public TargetMode targetMode;
    public Point targetTile;
    public TextBlock targetBlockParent;
    public TextBlock instructions;
    public TextBlock description;
    public ArrayList<TextBlock> targetBlocks = new ArrayList<>();
    boolean dirty = true;
    Consumer<Point> handler;
    List<Entity> moversInLOS;
    int trackMoverIndex;
    private long animationStart;
    private int lastAnimationStep;
    private TextBlock animationBlock;
    private List<Point> ray;

    public TargetingModule() {
    }

    public void animate(Point origin, Point target) {
        operationMode = OperationMode.ANIMATING;
        animationStart = -1;
        lastAnimationStep = -1;
        ray = Fov.findRay(Game.getLevel(), origin, target, true);
        animationBlock = new TextBlock("", UIManager.NAME_MAIN_WINDOW, 12, 0, 0, Color.WHITE);
        GameLoop.uiEngine.addBlock(animationBlock);
        super.start();
    }

    public void begin(TargetMode targetMode, Consumer<Point> handler) {
        operationMode = OperationMode.TARGETING;
        WindowEngine.setDirty(UIManager.NAME_MAIN_WINDOW);
        moversInLOS = new ArrayList<>();
        for (Entity mover : Game.getLevel().getMovers()) {
            if (Game.getPlayerEntity() != mover && Game.getPlayerEntity().canSee(mover)) {
                moversInLOS.add(mover);
            }
        }
        if (!moversInLOS.isEmpty() && targetMode.trackMovers) {
            trackMoverIndex = 0;
            targetTile = moversInLOS.get(trackMoverIndex).pos;
        } else {
            targetTile = Game.getPlayerEntity().pos;
        }
        this.targetMode = targetMode;
        this.handler = handler;
        // TODO draw black boxes over the top and bottom of the main window
        instructions = new TextBlock("'t' to select target, tab to cycle targets, space to cancel", UIManager.NAME_MAIN_WINDOW, RoguelikeModule.FONT_SIZE,
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
        if (operationMode == OperationMode.TARGETING) {
            updateTarget(state);
        } else {
            updateAnimation(state);
        }
    }

    private void updateTarget(GameState state) {
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
                description.text = "You see " + targetMover.getVisibleNameSingularOrSpecific() + ".";
            } else {
                // TODO fetch a text block etc
                List<Entity> targetItems = Game.getLevel().getItemsOnTile(targetTile);
                if (targetItems.size() > 2) {
                    description.text = "You see " + targetItems.get(0).getVisibleNameSingularOrVague() + " and " + (targetItems.size() - 1) + " other items.";
                } else if (targetItems.size() == 2) {
                    description.text = "You see " + targetItems.get(0).getVisibleNameSingularOrVague() + " and " + (targetItems.size() - 1) + " other item.";
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
        ray = Fov.findRay(Game.getLevel(), origin, targetTile, permissive);

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
                System.out.println("loc: " + currentRay + ", centerPixel: " + centerPixel);
                symbol = "X";
            } else {
                int deltaX = Math.abs(origin.x - targetTile.x);
                int deltaY = Math.abs(origin.y - targetTile.y);
                if (deltaX >= deltaY) {
                    symbol = "-";
                } else {
                    symbol = "|";
                }
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

    private void updateAnimation(GameState state) {
        if (animationStart == -1) {
            animationStart = state.getTick();
        }
        int animationStep = (int)(state.getTick() - animationStart) / ANIMATION_STEP_TIME;
        if (animationStep == lastAnimationStep) {
            return;
        }
        WindowEngine.setDirty(UIManager.NAME_MAIN_WINDOW);
        lastAnimationStep = animationStep;
        System.out.println("animationStep: " + animationStep);
        if (animationStep >= ray.size()) {
            animationBlock.close();
            animationBlock = null;
            end();
            return;
        }
        System.out.println(ray.get(animationStep));

        String symbol;
        // can we precalc this?  No, in case we change it to per-block
        int deltaX = Math.abs(ray.get(0).x - ray.get(ray.size()-1).x);
        int deltaY = Math.abs(ray.get(0).y - ray.get(ray.size()-1).y);
        if (deltaX >= deltaY) {
            symbol = "-";
        } else {
            symbol = "|";
        }
        animationBlock.text = symbol;

        // TODO fix positioning
        if (targetBlockParent != null) {
            targetBlockParent.close();
        }

        Point currentRay = ray.get(animationStep);
        Point centerPixel = GameLoop.glyphEngine.getTileCenterPixelInWindow(currentRay);
        System.out.println("centerPixel: " + centerPixel);
        float fontSize = RoguelikeModule.FONT_SIZE * 1.2f * GameLoop.glyphEngine.zoom;
        // TODO update this calculation at the same time as the targeting one
        animationBlock.pixelOffsetX = centerPixel.x - (fontSize / 3);
        animationBlock.pixelOffsetY = centerPixel.y - (fontSize / 3);
        if (animationBlock.text == "|") {
            animationBlock.pixelOffsetX += (fontSize / 3);
        }

        animationBlock.compile();
    }


    @Override
    public void render(Graphics g, GraphicsState gState) {
    }

    @Override
    public boolean keyDown(int keycode, boolean shift, boolean ctrl, boolean alt) {
        if (operationMode != OperationMode.TARGETING) {
            return true;
        }
        dirty = true;
        switch (keycode) {
            case Input.Keys.SPACE:
            case Input.Keys.ESCAPE:
                select(null);
            case Input.Keys.ENTER:
            case Input.Keys.T:
                select(targetTile);
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
            case Input.Keys.TAB:
                if (!moversInLOS.isEmpty()) {
                    if (shift) {
                        trackMoverIndex = (trackMoverIndex + moversInLOS.size() - 1) % moversInLOS.size();
                    } else {
                        trackMoverIndex = (trackMoverIndex + 1) % moversInLOS.size();
                    }
                    targetTile = moversInLOS.get(trackMoverIndex).pos;
                }
                break;
        }
        return true;
    }

    public void moveTarget(int x, int y) {
        targetTile = new Point(targetTile.x + x, targetTile.y + y);
    }

    public void select(Point p) {
        WindowEngine.setDirty(UIManager.NAME_MAIN_WINDOW);

        if (targetMode.stopAtWall) {
            for (int i=1; i<ray.size(); i++) {
                if (ray.get(i) == null) {
                    p = ray.get(i-1);
                }
            }
        }

        targetBlocks.clear();
        if (targetBlockParent != null) {
            targetBlockParent.close();
            targetBlockParent = null;
            instructions.close();
            instructions = null;
            description.close();
            description = null;
        }
        end();
        if (handler != null) {
            handler.accept(p);
        }
    }

    private enum OperationMode {
        TARGETING,
        ANIMATING
    }

    public class TargetMode {
        public boolean lineFromPlayer;
        public boolean trackMovers;
        public boolean stopAtWall;
        public float maxRange;

        public TargetMode(boolean lineFromPlayer, boolean trackMovers, boolean stopAtWall, float maxRange) {
            this.lineFromPlayer = lineFromPlayer;
            this.trackMovers = trackMovers;
            this.stopAtWall = stopAtWall;
            this.maxRange = maxRange;
        }
    }
}
