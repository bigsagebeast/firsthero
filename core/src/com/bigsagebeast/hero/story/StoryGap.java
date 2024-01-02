package com.bigsagebeast.hero.story;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StoryGap {
    public StoryCardType type;
    public List<StoryCard> link = new ArrayList<>();
    public List<String> linkName = new ArrayList<>();
    //public List<String> reflexiveLinkName = new ArrayList<>();
    public List<StoryLinkRequirement> requirements = new ArrayList<>();
    HashMap<String, String> hardLinks = new HashMap<>();
    List<String> terminalHardLinks = new ArrayList<>();

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
        for (String hl : hardLinks.keySet()) {
            sb.append(" HL:" + hl + ":" + hardLinks.get(hl));
        }
        for (String thl : terminalHardLinks) {
            sb.append(" THL:" + thl);
        }
        sb.append(")");
        return sb.toString();
    }

    public int getLongestHardLink() {
        int longest = terminalHardLinks.isEmpty() ? -1 : 0;
        for (String hardLink : hardLinks.keySet()) {
            int length = 1 + hardLink.length() - hardLink.replace(".", "").length();
            if (length > longest) {
                longest = length;
            }
        }
        return longest;
    }
}
