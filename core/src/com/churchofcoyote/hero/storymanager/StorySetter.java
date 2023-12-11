package com.churchofcoyote.hero.storymanager;

public class StorySetter {
    public enum Operation {
        SETSTR,
        REMSTR,
        SETINT,
        REMINT,
        ADDINT,
        SUBINT,
    }
    public Operation op;
    public String var;
    public Object value;

    @Override
    public String toString() {
        return "[" + var + " " + op.name() + " " + value.toString() + "]";
    }
}
