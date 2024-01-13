package com.bigsagebeast.hero.roguelike.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EntityTracker {
    public static final int NONE = -1;
    public static int lastCreated = 1;
    public static HashMap<Integer, Entity> entities = new HashMap<>();

    public static void cleanUp() {
        List<Integer> keys = new ArrayList<>(entities.keySet());
        for (int key : keys) {
            if (entities.get(key).destroyed) {
                entities.get(key).destroy();
            }
            entities.remove(key);
        }
    }

    public static Entity create() {
        // we may have loaded stuff in
        while (entities.get(lastCreated) != null) {
            lastCreated++;
        }
        Entity e = new Entity(lastCreated);
        entities.put(lastCreated++, e);
        return e;
    }

    public static Entity get(int id) {
        if (id == 0) {
            throw new RuntimeException("Tried to index uninitialized entityId");
        }
        if (id < 0) {
            throw new RuntimeException("Didn't check that entityId was NONE");
        }
        Entity e = entities.get(id);
        if (e != null) {
            if (e.destroyed) {
                entities.remove(id);
                return null;
            }
        }
        return e;
    }

    public static void load(Entity ent) {
        if (ent.entityId > lastCreated) {
            throw new RuntimeException("Loading entity with too-high entityId! " + ent.entityId + " > " + lastCreated);
        }
        entities.put(ent.entityId, ent);
    }
}
