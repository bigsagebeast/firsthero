package com.churchofcoyote.hero.chat;

public class ChatComparator {
    public Operation op;
    public String variableA;
    public String variableB;
    public Object valueA;
    public Object valueB;

    public enum Operation {
        EQSTR,
        NESTR,
        EQ,
        NE,
        GT,
        GTE,
        LT,
        LTE,
        EXSTR, // exists? don't use operands
        NEXSTR, // doesn't exist? don't use operands
        EX, // exists? don't use operands
        NEX; // doesn't exist? don't use operands

        public boolean operate(int a, int b) {
            if (this == EQ) {
                return a == b;
            } else if (this == NE) {
                return a != b;
            } else if (this == GT) {
                return a > b;
            } else if (this == GTE) {
                return a >= b;
            } else if (this == LT) {
                return a < b;
            } else if (this == LTE) {
                return a <= b;
            } else {
                throw new RuntimeException("Tried to run comparison with " + this.name());
            }
        }

        public boolean operate(String a, String b) {
            if (this == EQSTR) {
                return a.equals(b);
            } else if (this == NESTR) {
                return !a.equals(b);
            } else {
                throw new RuntimeException("Invalid string comparison: " + a + " " + this.name() + " " + b);
            }
        }
    }
}
