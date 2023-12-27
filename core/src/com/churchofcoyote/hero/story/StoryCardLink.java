package com.churchofcoyote.hero.story;

import java.util.ArrayList;
import java.util.List;

public class StoryCardLink {
    StoryCardType type;
    String key;
    List<StoryLinkRequirement> requirements = new ArrayList<>();
    String backKey;
}
