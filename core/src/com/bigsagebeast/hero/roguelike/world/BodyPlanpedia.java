package com.bigsagebeast.hero.roguelike.world;

import com.bigsagebeast.hero.SetupException;

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
        humanoid.addPart(BodyPart.HANDS);
        humanoid.addPart(BodyPart.FEET);
        humanoid.addPart(BodyPart.LEFT_RING);
        humanoid.addPart(BodyPart.RIGHT_RING);
        humanoid.addPart(BodyPart.RANGED_WEAPON);
        humanoid.addPart(BodyPart.RANGED_AMMO);
        addBodyPlan("humanoid", humanoid);
    }

    public void addBodyPlan(String name, BodyPlan plan) {
        table.put(name, plan);
    }

    public BodyPlan getBodyPlan(String name) {
        return table.get(name);
    }
}
