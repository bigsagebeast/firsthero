package com.churchofcoyote.hero.roguelike.world.dungeon.generation;

import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.world.dungeon.RoomType;

import java.util.*;

public class SubDungeonAssigner {
    public RoomNode firstNode;
    public Theme theme;
    private int maxDepth = 0;
    private ArrayList<RoomNode> allRooms = new ArrayList<>();
    private HashMap<ThemeRoom, Integer> themeRoomCount = new HashMap<>();
    public SubDungeonAssigner(RoomNode firstNode, Theme theme) {
        this.firstNode = firstNode;
        this.theme = theme;
    }

    public void assign() {
        calculateDepth();

        HashMap<RoomAndTheme, Float> scores = calculateScores();
        while (!scores.isEmpty()) {
            RoomAndTheme highestScore = scores.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);
            if (highestScore == null) {
                throw new RuntimeException("Failed to retrieve highest scoring RoomAndTheme");
            }
            highestScore.roomNode.room.setRoomType(highestScore.themeRoom.type);
            themeRoomCount.put(highestScore.themeRoom, getThemeRoomCount(highestScore.themeRoom) + 1);
            scores = calculateScores();
        }
    }

    public HashMap<RoomAndTheme, Float> calculateScores() {
        HashMap<RoomAndTheme, Float> scores = new HashMap<>();
        for (RoomNode node : allRooms) {
            if (node.room.roomType != RoomType.SUBDUNGEON_UNASSIGNED) {
                continue;
            }
            for (ThemeRoom themeRoom : theme.rooms) {
                float score = scoreThemeForRoom(node, themeRoom);
                score *= (1.0f + (0.1f * Game.random.nextFloat()));
                scores.put(new RoomAndTheme(node, themeRoom), score);
            }
        }
        return scores;
    }

    public void calculateDepth() {
        LinkedList<RoomNode> queue = new LinkedList<>();
        queue.add(firstNode);
        firstNode.depth = 0;
        while (!queue.isEmpty()) {
            RoomNode node = queue.pop();
            if (!allRooms.contains(node)) {
                allRooms.add(node);
            }
            for (RoomNode neighbor : node.neighbors.keySet()) {
                if (node.neighbors.get(neighbor)) {
                    if (neighbor.depth > node.depth + 1) {
                        neighbor.depth = node.depth + 1;
                        queue.add(neighbor);
                        if (neighbor.depth > maxDepth) {
                            maxDepth = neighbor.depth;
                        }
                    }
                }
            }
        }
    }

    private int getThemeRoomCount(ThemeRoom themeRoom) {
        if (themeRoomCount.get(themeRoom) != null) {
            return themeRoomCount.get(themeRoom);
        } else {
            return 0;
        }
    }

    public float scoreThemeForRoom(RoomNode node, ThemeRoom themeRoom) {
        int existingCount = getThemeRoomCount(themeRoom);
        if (themeRoom.hardCap >= 0 && existingCount >= themeRoom.hardCap) {
            return -1;
        }
        if (themeRoom.depth == 0) {
            if (node.depth == 0) {
                return 999;
            } else {
                return -1;
            }
        }
        if (themeRoom.depth == 5) {
            if (node.depth == maxDepth) {
                return 999;
            } else {
                return -1;
            }
        }
        float depthQuadrant = ((float)node.depth / (float)maxDepth * 4.0f) + 0.5f;
        float distanceFromPreferred = Math.abs(themeRoom.depth - depthQuadrant);
        float distanceScore = 5.0f - distanceFromPreferred;
        float softCapFactor;

        if (existingCount == 0) {
            softCapFactor = 1.0f;
        } else {
            softCapFactor = themeRoom.softCap / (existingCount + 1);
        }

        if (softCapFactor > 1.0f || softCapFactor < 0) {
            softCapFactor = 1.0f;
        }
        return distanceScore * softCapFactor * themeRoom.priority;
    }

    private class RoomAndTheme {
        // used just to build a 2-dimensional array of preference
        public RoomNode roomNode;
        public ThemeRoom themeRoom;
        public RoomAndTheme(RoomNode roomNode, ThemeRoom themeRoom) {
            this.roomNode = roomNode;
            this.themeRoom = themeRoom;
        }
    }
}
