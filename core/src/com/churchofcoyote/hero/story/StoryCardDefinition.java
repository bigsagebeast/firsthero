package com.churchofcoyote.hero.story;

import com.churchofcoyote.hero.roguelike.game.Game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StoryCardDefinition {
    public StoryCardType type;
    public String title;
    public HashMap<String, StoryCardLink> links = new HashMap<>();
    public List<String> tags = new ArrayList<>();
    public List<String> defaultDescSelf = new ArrayList<>();
    public HashMap<String, List<String>> defaultDescLink = new HashMap<>();
    public List<String> descSelf = new ArrayList<>();
    public HashMap<String, List<String>> descLink = new HashMap<>();
    public boolean doDescribe = true;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(SCD:" + title + ":" + type.toString() + ":");
        for (String key : links.keySet()) {
            sb.append("[link:" + key + "]");
        }
        for (String key : tags) {
            sb.append("[tag:" + key + "]");
        }
        sb.append(")");
        return sb.toString();
    }

    public void addLink(StoryCardLink link) {
        links.put(link.key, link);
    }

    public void giveName(StoryCard storyCard) {
        storyCard.shortName = "NAME:" + title;
    }

    public void addDefaultDescSelf(String desc) {
        defaultDescSelf.add(desc);
    }

    public void addDefaultDescLink(String link, String desc) {
        if (!defaultDescLink.containsKey(link)) {
            defaultDescLink.put(link, new ArrayList<>());
        }
        defaultDescLink.get(link).add(desc);
    }

    public void addDescSelf(String desc) {
        descSelf.add(desc);
    }

    public void addDescLink(String link, String desc) {
        if (!descLink.containsKey(link)) {
            descLink.put(link, new ArrayList<>());
        }
        descLink.get(link).add(desc);
    }

    public String[] describeSelf() {
        if (!descSelf.isEmpty()) {
            return descSelf.toArray(new String[0]);
        }
        if (!defaultDescSelf.isEmpty()) {
            return defaultDescSelf.toArray(new String[0]);
        }
        return new String[] {
                "SelfDesc %1N"
        };
    }

    public String[] describeLink(String link) {
        if (descLink.containsKey(link) && !descLink.get(link).isEmpty()) {
            return descLink.get(link).toArray(new String[0]);
        }
        if (defaultDescLink.containsKey(link) && !defaultDescLink.get(link).isEmpty()) {
            return defaultDescLink.get(link).toArray(new String[0]);
        }
        return new String[] { "%1N:" + link + ":%2N" };
    }
}
