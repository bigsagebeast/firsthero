package com.churchofcoyote.hero.roguelike.world;
import org.eclipse.collections.impl.map.mutable.primitive.IntObjectHashMap;

public class EntityTracker {
    public static int lastCreated = 0;
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
        return entities.get(id);
    }
}
