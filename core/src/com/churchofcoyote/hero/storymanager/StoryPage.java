package com.churchofcoyote.hero.storymanager;

import java.util.List;

public class StoryPage {
    public String key;
    public String text;
    public List<StoryLink> links;

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[" + key + ": \"" + text + "\"");
        if (links != null) {
            for (StoryLink link : links) {
                sb.append(" ");
                sb.append(link.toString());
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
