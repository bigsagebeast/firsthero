package com.churchofcoyote.hero.story;

import java.util.ArrayList;
import java.util.List;

public class StoryGap {
    public StoryCardType type;
    public List<StoryCard> link = new ArrayList<>();
    public List<String> linkName = new ArrayList<>();
    //public List<String> reflexiveLinkName = new ArrayList<>();
    public List<StoryLinkRequirement> requirements = new ArrayList<>();

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append(type.toString());
        sb.append(": ");
        for (int i=0; i<link.size(); i++) {
            StoryCard sc = link.get(i);
            String reflexive = linkName.get(i);
            sb.append("(link:" + sc.definition.title + ":" + reflexive + ")");
        }
        for (StoryLinkRequirement slr : requirements) {
            sb.append(slr.toString());
        }
        sb.append(")");
        return sb.toString();
    }
}
