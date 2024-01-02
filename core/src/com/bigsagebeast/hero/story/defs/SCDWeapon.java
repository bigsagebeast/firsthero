package com.bigsagebeast.hero.story.defs;

import com.bigsagebeast.hero.story.*;

public class SCDWeapon extends StoryCardDefinition {
    public SCDWeapon() {
        type = StoryCardType.THING_TYPE;
        addLink(new StoryCardLink(StoryCardType.PRECONDITION, StoryLinkSeekType.NO_SEEK, "precondition", "precondition", false));
        addLink(new StoryCardLink(StoryCardType.PERSON_TYPE, StoryLinkSeekType.REQUIRED, "wielder", "wielding"));

        addDefaultDescSelf("There is a weapon, %1n");
        addDefaultDescSelf("There is a weapon called %1n");
        addDefaultDescLink("wielder", "was wielded by %2n");
        addDefaultDescLink("wielder", "was carried by %2n");
    }

    @Override
    public String introToStreamingConnector() {
        return "which";
    }

    @Override
    public void giveName(StoryCard storyCard) {
        if (forceName == null) {
            storyCard.shortName = "Nightbringer";
        } else {
            storyCard.shortName = forceName;
        }
    }

}
