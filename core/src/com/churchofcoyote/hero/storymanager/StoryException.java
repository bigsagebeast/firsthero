package com.churchofcoyote.hero.storymanager;

public class StoryException extends RuntimeException {
    public StoryException(String string) {
        super(string);
    }
    public StoryException(Exception e) {
        super(e);
    }
}
