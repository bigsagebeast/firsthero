package com.bigsagebeast.hero.story;

public abstract class StoryLinkRequirement {
    public abstract float weight(StoryCardDefinition target); // multiplier; 0 = does not fit
}
