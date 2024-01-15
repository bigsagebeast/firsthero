package com.bigsagebeast.hero.chat;

import com.badlogic.gdx.Gdx;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ChatLoader {
    // Load text from files
    // Turn text into StoryPage

    public static ChatPage createPage(ChatBook book, String text) {
        ChatPage page = new ChatPage();

        ObjectMapper om = new ObjectMapper();
        om.getFactory().enable(JsonParser.Feature.ALLOW_COMMENTS);

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
        om.getFactory().enable(JsonParser.Feature.ALLOW_COMMENTS);
        try {
            JsonNode rootNode = om.readTree(new BufferedReader(new InputStreamReader(Gdx.files.internal(filePath).read()))).get("pages");

            if (rootNode != null && rootNode.isObject()) {
                rootNode.fields().forEachRemaining(entry -> {
                    ChatPage chatPage = new ChatPage();
                    chatPage.key = entry.getKey();
                    JsonNode pageContent = entry.getValue();
                    chatPage.text = pageContent.get("text").asText();
                    if (pageContent.get("inheritLinks") != null) {
                        chatPage.inheritLinks = pageContent.get("inheritLinks").asText();
                    }

                    chatPage.links = new ArrayList<>();
                    JsonNode linksArray = pageContent.get("links");
                    if (linksArray != null && linksArray.isArray()) {
                        linksArray.elements().forEachRemaining(link -> {
                            ChatLink chatLink = om.convertValue(link, ChatLink.class);
                            chatPage.links.add(chatLink);
                        });
                    }
                    book.add(chatPage);
                });
            }
        } catch (IOException e) {
            // TODO setupexception
            throw new RuntimeException(e);
        }
    }
}
