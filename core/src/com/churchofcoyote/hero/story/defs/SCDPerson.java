package com.churchofcoyote.hero.story.defs;

import com.churchofcoyote.hero.story.*;

public class SCDPerson extends StoryCardDefinition {
    public SCDPerson() {
        type = StoryCardType.PERSON_TYPE;
        addLink(new StoryCardLink(StoryCardType.PLACE_TYPE, StoryLinkSeekType.REQUIRED, "home", "resident"));
        addLink(new StoryCardLink(StoryCardType.PRECONDITION, StoryLinkSeekType.NO_SEEK, "precondition", "precondition", false));

        addDefaultDescSelf("There was a person, %1n");
        addDefaultDescSelf("There was someone named %1n");
        addDefaultDescLink("home", "%1n lived in %2n");
        addDefaultDescLink("home", "%1n lived at %2n");
    }

    @Override
    public void giveName(StoryCard storyCard) {
        storyCard.shortName = "G'Chakk";
    }

}
