package com.churchofcoyote.hero.storymanager;

import java.util.ArrayList;

public class StoryLink {
    public String text;
    public String nextPage = "";
    public boolean terminal = false;

    public ArrayList<StoryComparator> tests = new ArrayList<>();
    public ArrayList<StorySetter> setters = new ArrayList<>();

    public Object[] codeArgs;
    public String codeClass;
    public String codeMethod;

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
            for (StoryComparator test : tests) {
                sb.append(test.toString());
            }
            sb.append("]");
        }
        if (!tests.isEmpty()) {
            sb.append(" Setters: [");
            for (StorySetter setter : setters) {
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
