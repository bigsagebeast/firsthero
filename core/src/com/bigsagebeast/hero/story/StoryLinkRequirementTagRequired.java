package com.bigsagebeast.hero.story;

public class StoryLinkRequirementTagRequired extends StoryLinkRequirement {
    public String tag;
    public StoryLinkRequirementTagRequired(String tag) {
        this.tag = tag;
    }
    @Override
    public float weight(StoryCardDefinition target) {
        if (!target.tags.contains(tag)) {
            return 0f;
        }
        return 1f;
    }
    @Override
    public String toString() {
        return "[R:" + tag + "]";
    }
}
