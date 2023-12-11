package com.churchofcoyote.hero.storymanager;

import java.util.HashMap;

public class StoryBook {
    public HashMap<String, StoryPage> pages = new HashMap<>();

    public StoryPage get(String key) {
        return pages.get(key);
    }

    public void put(String key, StoryPage page) {
        pages.put(key, page);
    }

    public void add(StoryPage page) {
        pages.put(page.key, page);
    }
}
