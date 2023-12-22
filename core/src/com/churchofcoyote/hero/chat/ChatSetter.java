package com.churchofcoyote.hero.chat;

public class ChatSetter {
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
