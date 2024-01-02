package com.bigsagebeast.hero.story;

import java.util.ArrayList;
import java.util.List;

public class StoryCardLink {
    public StoryCardType type;
    public String key;
    public List<StoryLinkRequirement> requirements = new ArrayList<>();
    public String backKey;
    public StoryLinkSeekType seekType = StoryLinkSeekType.OPTIONAL;
    public boolean doDescribe = true;

    public StoryCardLink() {}

    public StoryCardLink(StoryCardType type, StoryLinkSeekType seekType, String key, String backKey, boolean doDescribe) {
        this.type = type;
        this.seekType = seekType;
        this.key = key;
        this.backKey = backKey;
        this.doDescribe = doDescribe;
    }
    public StoryCardLink(StoryCardType type, StoryLinkSeekType seekType, String key, String backKey) {
        this(type, seekType, key, backKey, true);
    }
}
