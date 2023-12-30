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
}
