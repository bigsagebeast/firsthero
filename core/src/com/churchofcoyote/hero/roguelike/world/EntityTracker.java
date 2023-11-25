package com.churchofcoyote.hero.roguelike.world;
import org.eclipse.collections.impl.map.mutable.primitive.IntObjectHashMap;

public class EntityTracker {
    public static final int NONE = -1;
    public static int lastCreated = 1;
    public static IntObjectHashMap<Entity> entities = new IntObjectHashMap<>();

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
        return entities.get(id);
    }

    public static void load(Entity ent) {
        if (ent.entityId > lastCreated) {
            throw new RuntimeException("Loading entity with too-high entityId! " + ent.entityId + " > " + lastCreated);
        }
        entities.put(ent.entityId, ent);
    }
}
