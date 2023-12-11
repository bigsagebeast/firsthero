package com.churchofcoyote.hero.storymanager;

import java.util.HashMap;

public class StoryBook {
    public static HashMap<String, StoryPage> pages = new HashMap<>();

    public static StoryPage get(String key) {
        return pages.get(key);
    }

    public static void put(String key, StoryPage page) {
        pages.put(key, page);
    }

    public static void add(StoryPage page) {
        pages.put(page.key, page);
    }
}
