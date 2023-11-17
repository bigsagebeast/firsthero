package com.churchofcoyote.hero.roguelike.world;

import com.churchofcoyote.hero.SetupException;

import java.util.ArrayList;

public class BodyPlan {
    private ArrayList<BodyPart> parts = new ArrayList<>();

    public void addPart(BodyPart p) throws SetupException {
        if (parts.contains(p)) {
            throw new SetupException("Can't add the same body part twice!");
        }
        parts.add(p);
    }

    public Iterable<BodyPart> getParts() {
        return parts;
    }

    public boolean hasPart(BodyPart bp) { return parts.contains(bp); }
}
