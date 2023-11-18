package com.churchofcoyote.hero.util;

public class QueuedKeypress {
    public int keycode;
    public boolean shift;
    public boolean ctrl;
    public boolean alt;
    public QueuedKeypress(int keycode, boolean shift, boolean ctrl, boolean alt) {
        this.keycode = keycode;
        this.shift = shift;
        this.ctrl = ctrl;
        this.alt = alt;
    }
}
