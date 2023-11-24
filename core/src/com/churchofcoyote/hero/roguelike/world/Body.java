package com.churchofcoyote.hero.roguelike.world;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.stream.Collectors;

public class Body {
    //public BodyPlan bodyPlan;

    //private HashMap<BodyPart, Integer> equipment = new HashMap<>();
    private HashMap<String, Integer> equipment = new HashMap();

    public Body(String bodyPlanName) {
        if (bodyPlanName != null) {
            BodyPlan bodyPlan = BodyPlanpedia.getInstance().getBodyPlan(bodyPlanName);
            for (BodyPart bp : bodyPlan.getParts()) {
                addBodyPart(bp);
            }
        }
    }

    public Collection<BodyPart> getParts() {
        return equipment.keySet().stream().map(BodyPart::getPart)
                .sorted(Comparator.comparingInt(BodyPart::getIndex)).collect(Collectors.toList());
    }

    public Entity getEquipment(BodyPart bp) {
        Integer eid = equipment.get(bp.getName());
        if (eid == null) {
            return null;
        }
        return EntityTracker.get(eid);
    }

    public void putEquipment(BodyPart bp, Integer equipmentId) {
        if (!equipment.containsKey(bp.getName())) {
            throw new RuntimeException("Wrong way to add body part " + bp.getName());
        }
        equipment.put(bp.getName(), equipmentId);
    }

    public boolean hasBodyPart(BodyPart bp) {
        return equipment.containsKey(bp.getName());
    }

    public void addBodyPart(BodyPart bp) {
        equipment.put(bp.getName(), null);
    }

    public void removeBodyPart(BodyPart bp) {
        if (equipment.get(bp.getName()) != null) {
            throw new RuntimeException("Tried to remove a body part with equipment on it: " + bp.getName());
        }
        equipment.remove(bp.getName());
    }
}
