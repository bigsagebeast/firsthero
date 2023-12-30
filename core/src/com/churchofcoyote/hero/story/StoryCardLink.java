package com.churchofcoyote.hero.story;

import java.util.ArrayList;
import java.util.List;

public class StoryCardLink {
    StoryCardType type;
    String key;
    List<StoryLinkRequirement> requirements = new ArrayList<>();
    String backKey;
    StoryLinkSeekType seekType = StoryLinkSeekType.OPTIONAL;

    public StoryCardLink() {}

    public StoryCardLink(StoryCardType type, StoryLinkSeekType seekType, String key, String backKey) {
        this.type = type;
        this.seekType = seekType;
        this.key = key;
        this.backKey = backKey;
    }
}
