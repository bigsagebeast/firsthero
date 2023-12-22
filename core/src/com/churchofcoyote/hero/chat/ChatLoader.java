package com.churchofcoyote.hero.chat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChatLoader {
    // Load text from files
    // Turn text into StoryPage

    public static ChatPage createPage(ChatBook book, String text) {
        ChatPage page = new ChatPage();

        ObjectMapper om = new ObjectMapper();

        try {
            page = om.readValue(text, ChatPage.class);
            book.put(page.key, page);
        } catch (JsonProcessingException e) {
            // TODO setupexception
            throw new RuntimeException(e);
        }

        return page;
    }

    public static void createPages(ChatBook book, String filePath) {
        ObjectMapper om = new ObjectMapper();
        try {
            Map<String, List<Map<String, Object>>> jsonMap = om.readValue(
                    new File(filePath),
                    new TypeReference<Map<String, List<Map<String, Object>>>>() {}
            );

            // Extract the "pages" array
            List<Map<String, Object>> pagesList = jsonMap.get("pages");

            // Now you can work with the list of StoryPage objects
            for (Map<String, Object> pageMap : pagesList) {
                ChatPage chatPage = om.convertValue(pageMap, ChatPage.class);

                // Explicitly deserialize the "links" field as a list of StoryLink objects
                List<Map<String, Object>> linksList = (List<Map<String, Object>>) pageMap.get("links");
                if (linksList != null) {
                    List<ChatLink> chatLinks = new ArrayList<>();
                    for (Map<String, Object> linkMap : linksList) {
                        ChatLink chatLink = om.convertValue(linkMap, ChatLink.class);
                        chatLinks.add(chatLink);
                    }
                    chatPage.links = chatLinks;
                }

                book.add(chatPage);
            }
        } catch (IOException e) {
            // TODO setupexception
            throw new RuntimeException(e);
        }
    }
}
