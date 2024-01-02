package com.bigsagebeast.hero.util;

import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.dungeon.Level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Raycasting {
    // Includes the terminating cell (wall, door, etc)
    public static List<Point> createOrthogonalRay(Level level, Point origin, int range, Compass direction) {
        List<Point> ray = new ArrayList<>();
        Point p = direction.from(origin);
        while (level.withinBounds(p) && ray.size() < range) {
            ray.add(p);
            if (level.obstructiveBesidesMovers(p)) {
                return ray;
            }
            p = new Point(direction.from(p));
        }
        return ray;
    }

    // All movers in the first tile with any movers
    public static List<Entity> findFirstMoversAlongRay(Level level, List<Point> ray) {
        for (Point p : ray) {
            List<Entity> movers = level.getMoversOnTile(p);
            if (!movers.isEmpty()) {
                return movers;
            }
        }
        return Collections.EMPTY_LIST;
    }

    // modifies ray
    public static List<Point> trimRayToEntity(Level level, List<Point> ray, Entity entity) {
        if (entity.containingLevel != level.getName()) {
            throw new RuntimeException("Tried to find entity along ray of the wrong level");
        }
        for (int i=0; i<ray.size(); i++) {
            if (entity.pos == ray.get(i)) {
                while (ray.size() > i+1) {
                    ray.remove(i);
                }
                return ray;
            }
        }
        return ray;
    }

    public static List<Entity> findAllMoversAlongRay(Level level, List<Point> ray) {
        List<Entity> movers = new ArrayList<>();
        for (Point p : ray) {
            movers.addAll(level.getMoversOnTile(p));
        }
        return movers;
    }
}
