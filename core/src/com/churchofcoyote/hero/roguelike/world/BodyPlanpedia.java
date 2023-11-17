package com.churchofcoyote.hero.roguelike.world;

import com.churchofcoyote.hero.SetupException;

import java.util.Dictionary;
import java.util.Hashtable;

public class BodyPlanpedia {
    private static BodyPlanpedia instance;
    private Dictionary<String, BodyPlan> table = new Hashtable<>();

    public static void initialize() throws SetupException {
        if (instance != null) {
            throw new SetupException();
        }
        instance = new BodyPlanpedia();
    }

    public static BodyPlanpedia getInstance() {
        return instance;
    }

    public BodyPlanpedia() throws SetupException {
        BodyPlan humanoid = new BodyPlan();
        humanoid.addPart(BodyPart.PRIMARY_HAND);
        humanoid.addPart(BodyPart.OFF_HAND);
        humanoid.addPart(BodyPart.HEAD);
        humanoid.addPart(BodyPart.TORSO);
        humanoid.addPart(BodyPart.LEGS);
        humanoid.addPart(BodyPart.HANDS);
        humanoid.addPart(BodyPart.FEET);
        addBodyPlan("humanoid", humanoid);
    }

    public void addBodyPlan(String name, BodyPlan plan) {
        table.put(name, plan);
    }

    public BodyPlan getBodyPlan(String name) {
        return table.get(name);
    }
}
