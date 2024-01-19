package com.bigsagebeast.hero.chat;

import java.util.ArrayList;

public class ChatLink {
    public String text;
    public String nextPage = "";
    public boolean terminal = false;

    public ArrayList<ChatComparator> tests = new ArrayList<>();
    public ArrayList<ChatSetter> setters = new ArrayList<>();

    public Object[] codeArgs;
    public String codeClass;
    public String codeMethod;
    public Runnable runnable;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (terminal) {
            sb.append("(TERMINAL: \"");
        }
        sb.append("(" + nextPage + ": \"");
        if (text != null) {
            sb.append(text);
        }
        sb.append("\"");
        if (!tests.isEmpty()) {
            sb.append(" Tests: [");
            for (ChatComparator test : tests) {
                sb.append(test.toString());
            }
            sb.append("]");
        }
        if (!tests.isEmpty()) {
            sb.append(" Setters: [");
            for (ChatSetter setter : setters) {
                sb.append(setter.toString());
            }
            sb.append("]");
        }
        if (codeMethod != null) {
            sb.append(" Invocation: " + codeClass + "." + codeMethod + "(");
            for (Object o : codeArgs) {
                sb.append("\"" + o.toString() + "\" ");
            }
            sb.append(")");
        }
        sb.append(")");
        return sb.toString();
    }
}
