package com.churchofcoyote.hero.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.churchofcoyote.hero.Graphics;
import com.churchofcoyote.hero.util.Point;

import java.util.HashMap;

public class WindowEngine {
    private static HashMap<String, FrameBuffer> frameBuffers = new HashMap<>();
    private static HashMap<String, Boolean> isDirty = new HashMap<>();
    private static HashMap<String, Point> offset = new HashMap<>();
    private static HashMap<String, Point> size = new HashMap<>();

    public static void createFrameBuffer(String key) {
        createFrameBuffer(key, 0, 0, Graphics.width, Graphics.height);
    }

    public static void createFrameBuffer(String key, int offsetX, int offsetY, int width, int height) {
        if (frameBuffers.containsKey(key)) {
            frameBuffers.get(key).dispose();
        }
        FrameBuffer fb = new FrameBuffer(Pixmap.Format.RGBA8888, Graphics.width, Graphics.height, false);
        frameBuffers.put(key, fb);
        offset.put(key, new Point(offsetX, offsetY));
        size.put(key, new Point(width, height));
        setDirty(key);
    }

    public static FrameBuffer get(String key) {
        if (!frameBuffers.containsKey(key)) {
            throw new RuntimeException("Tried to access uninitialized framebuffer " + key);
        }
        return frameBuffers.get(key);
    }

    public static boolean isDirty(String key) {
        return isDirty.get(key);
    }

    public static void setDirty(String key) {
        isDirty.put(key, Boolean.TRUE);
        frameBuffers.get(key).begin();
        Gdx.gl.glClearColor(0, 0, 0, 0); // Set the clear color
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        frameBuffers.get(key).end();
    }

    public static void setClean(String key) {
        isDirty.put(key, Boolean.FALSE);
    }

    public static void setAllClean() {
        for (String key : isDirty.keySet()) {
            isDirty.put(key, Boolean.FALSE);
        }
    }

    public static void setAllDirty() {
        for (String key : isDirty.keySet()) {
            isDirty.put(key, Boolean.TRUE);
        }
    }

    public static void render(Graphics g) {
        for (String key : frameBuffers.keySet()) {
            Point offsetPoint = offset.get(key);
            g.batch().draw(frameBuffers.get(key).getColorBufferTexture(), offsetPoint.x, offsetPoint.y, Graphics.width, Graphics.height);
        }
    }
}
