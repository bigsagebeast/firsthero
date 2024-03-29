package com.bigsagebeast.hero.story;

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
    public List<String> nameIntro = new ArrayList<>();
    public List<String> defaultNameIntro = new ArrayList<>();
    public boolean doDescribe = true;
    public String forceName = null;

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
        if (forceName == null) {
            storyCard.shortName = "NAME:" + title;
        } else {
            storyCard.shortName = forceName;
        }
    }

    public String introToStreamingConnector() {
        return "which";
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

    public void addDefaultNameIntro(String intro) {
        defaultNameIntro.add(intro);
    }

    public void addNameIntro(String intro) {
        nameIntro.add(intro);
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

    public String[] getNameIntro(boolean intro) {
        if (intro && !nameIntro.isEmpty()) {
            return nameIntro.toArray(new String[0]);
        }
        if (intro && !defaultNameIntro.isEmpty()) {
            return defaultNameIntro.toArray(new String[0]);
        }
        return null;
    }
}
