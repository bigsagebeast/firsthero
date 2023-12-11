package com.churchofcoyote.hero.storymanager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StoryLoader {
    // Load text from files
    // Turn text into StoryPage

    public static StoryPage createPage(StoryBook book, String text) {
        StoryPage page = new StoryPage();

        ObjectMapper om = new ObjectMapper();

        try {
            page = om.readValue(text, StoryPage.class);
            book.put(page.key, page);
        } catch (JsonProcessingException e) {
            // TODO setupexception
            throw new RuntimeException(e);
        }

        return page;
    }

    public static void createPages(StoryBook book, String filePath) {
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
                StoryPage storyPage = om.convertValue(pageMap, StoryPage.class);

                // Explicitly deserialize the "links" field as a list of StoryLink objects
                List<Map<String, Object>> linksList = (List<Map<String, Object>>) pageMap.get("links");
                if (linksList != null) {
                    List<StoryLink> storyLinks = new ArrayList<>();
                    for (Map<String, Object> linkMap : linksList) {
                        StoryLink storyLink = om.convertValue(linkMap, StoryLink.class);
                        storyLinks.add(storyLink);
                    }
                    storyPage.links = storyLinks;
                }

                book.add(storyPage);
                System.out.println(storyPage);
            }
        } catch (IOException e) {
            // TODO setupexception
            throw new RuntimeException(e);
        }
    }
}
