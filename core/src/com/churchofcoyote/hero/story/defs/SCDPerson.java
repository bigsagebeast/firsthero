package com.churchofcoyote.hero.story.defs;

import com.churchofcoyote.hero.story.*;

public class SCDPerson extends StoryCardDefinition {
    public SCDPerson() {
        type = StoryCardType.PERSON_TYPE;
        addLink(new StoryCardLink(StoryCardType.PLACE_TYPE, StoryLinkSeekType.REQUIRED, "home", "resident"));
        addLink(new StoryCardLink(StoryCardType.PRECONDITION, StoryLinkSeekType.NO_SEEK, "precondition", "precondition", false));
        addLink(new StoryCardLink(StoryCardType.THING_TYPE, StoryLinkSeekType.OPTIONAL, "wielding", "wielder"));

        addDefaultDescSelf("There is a person, %1n");
        addDefaultDescSelf("There is someone named %1n");
        addDefaultDescLink("home", "lives in %2n");
        addDefaultDescLink("home", "lives at %2n");
        addDefaultDescLink("wielding", "wields %2n");
    }

    @Override
    public String introToStreamingConnector() {
        return "who";
    }

    @Override
    public void giveName(StoryCard storyCard) {
        if (forceName == null) {
            storyCard.shortName = "G'Chakk";
        } else {
            storyCard.shortName = forceName;
        }
    }

}
