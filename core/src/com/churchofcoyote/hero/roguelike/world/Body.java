package com.churchofcoyote.hero.roguelike.world;

import java.util.HashMap;

public class Body {
    public BodyPlan bodyPlan;
    public HashMap<BodyPart, Entity> equipment = new HashMap<>();

    public Body(String bodyPlanName) {
        if (bodyPlanName != null) {
            bodyPlan = BodyPlanpedia.getInstance().getBodyPlan(bodyPlanName);
            for (BodyPart bp : bodyPlan.getParts()) {
                equipment.put(bp, null);
            }
        }
    }
}
