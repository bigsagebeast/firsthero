package com.churchofcoyote.hero.chat;

import java.util.HashMap;

public class ChatBook {
    public static HashMap<String, ChatPage> pages = new HashMap<>();

    public static ChatPage get(String key) {
        return pages.get(key);
    }

    public static void put(String key, ChatPage page) {
        pages.put(key, page);
    }

    public static void add(ChatPage page) {
        pages.put(page.key, page);
    }
}
