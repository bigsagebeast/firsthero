package com.bigsagebeast.hero.ui;

import com.bigsagebeast.hero.GameLoop;
import com.bigsagebeast.hero.Graphics;
import com.bigsagebeast.hero.engine.WindowEngine;
import com.bigsagebeast.hero.module.RoguelikeModule;

public class UIManager {
    private static final int WIDTH_IN_CHARS = 46;
    private static final int RIGHT_SIDE_WIDTH_IN_PIXELS = RoguelikeModule.FONT_SIZE * WIDTH_IN_CHARS;
    private static final int LOWER_BOX_HEIGHT_IN_CHARS = 22;
    private static final int LOWER_BOX_HEIGHT_IN_PIXELS = RoguelikeModule.FONT_SIZE * LOWER_BOX_HEIGHT_IN_CHARS;
    private static final int STATS_WIDTH_IN_CHARS = 16;
    private static final int STATS_WIDTH_IN_PIXELS = RoguelikeModule.FONT_SIZE * STATS_WIDTH_IN_CHARS;
    private static final int EQUIPMENT_WIDTH_IN_CHARS = WIDTH_IN_CHARS - STATS_WIDTH_IN_CHARS - 1;
    private static final int EQUIPMENT_WIDTH_IN_PIXELS = RoguelikeModule.FONT_SIZE * EQUIPMENT_WIDTH_IN_CHARS;
    private static final int PROGRESS_HEIGHT_IN_PIXELS = RoguelikeModule.FONT_SIZE;
    private static final int STATBOX_HEIGHT_IN_CHARS = 10;
    private static final int STATBOX_HEIGHT_IN_PIXELS = RoguelikeModule.FONT_SIZE * STATBOX_HEIGHT_IN_CHARS;

    public static final int MIN_RESIZE_X = RIGHT_SIDE_WIDTH_IN_PIXELS + 100;
    public static final int MIN_RESIZE_Y = LOWER_BOX_HEIGHT_IN_PIXELS + 350;

    public static final String NAME_ANNOUNCEMENTS = "announcements";
    public static final String NAME_STATS = "stats";
    public static final String NAME_STATBOX = "statbox";
    public static final String NAME_EQUIPMENT = "equipent";
    public static final String NAME_MAIN_WINDOW = "mainWindow";
    public static final String NAME_HIT_POINTS = "hitpoints";
    public static final String NAME_EXPERIENCE = "experience";

    public static void resize(int width, int height) {
        WindowEngine.createFrameBuffer(NAME_STATBOX,
                width - RIGHT_SIDE_WIDTH_IN_PIXELS, 0,
                RIGHT_SIDE_WIDTH_IN_PIXELS, STATBOX_HEIGHT_IN_PIXELS);
        WindowEngine.createFrameBuffer(NAME_ANNOUNCEMENTS,
                width - RIGHT_SIDE_WIDTH_IN_PIXELS, -STATBOX_HEIGHT_IN_PIXELS,
                RIGHT_SIDE_WIDTH_IN_PIXELS, height - LOWER_BOX_HEIGHT_IN_PIXELS);
        /*
        WindowEngine.createFrameBuffer(NAME_STATS,
                width - RIGHT_SIDE_WIDTH_IN_PIXELS, -(height - LOWER_BOX_HEIGHT_IN_PIXELS) - STATBOX_HEIGHT_IN_PIXELS,
                STATS_WIDTH_IN_PIXELS, LOWER_BOX_HEIGHT_IN_PIXELS);
         */
        WindowEngine.createFrameBuffer(NAME_EQUIPMENT,
                width - RIGHT_SIDE_WIDTH_IN_PIXELS, -(height - LOWER_BOX_HEIGHT_IN_PIXELS) - STATBOX_HEIGHT_IN_PIXELS,
                RIGHT_SIDE_WIDTH_IN_PIXELS, LOWER_BOX_HEIGHT_IN_PIXELS);
        WindowEngine.createFrameBuffer(NAME_MAIN_WINDOW,
                0, (2 * PROGRESS_HEIGHT_IN_PIXELS),
                width - RIGHT_SIDE_WIDTH_IN_PIXELS, height);
        WindowEngine.createFrameBuffer(NAME_EXPERIENCE,
                0, -(height - (2 * PROGRESS_HEIGHT_IN_PIXELS)),
                width - RIGHT_SIDE_WIDTH_IN_PIXELS, PROGRESS_HEIGHT_IN_PIXELS);
        WindowEngine.createFrameBuffer(NAME_HIT_POINTS,
                0, -(height - (1 * PROGRESS_HEIGHT_IN_PIXELS)),
                width - RIGHT_SIDE_WIDTH_IN_PIXELS, PROGRESS_HEIGHT_IN_PIXELS);

        WindowEngine.setAllDirty();
        GameLoop.glyphEngine.dirty();
        if (RoguelikeModule.announceWindow != null) {
            RoguelikeModule.announceWindow.update();
        }
        GameLoop.uiEngine.recompile();

        // TODO should be centralized in a List of UIWindows
        if (RoguelikeModule.hitPointWindow != null) {
            RoguelikeModule.hitPointWindow.update();
            RoguelikeModule.experienceWindow.update();
        }
    }

    public static void render(Graphics g) {

    }
}
