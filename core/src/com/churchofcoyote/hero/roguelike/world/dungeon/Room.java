package com.churchofcoyote.hero.roguelike.world.dungeon;

import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.util.Point;

public class Room {
    public int roomId;
    public RoomType roomType;
    public boolean visited = false;
    public Point centerPoint;

    public Room(RoomType roomType, Point centerPoint) {
        this.roomType = roomType;
        this.centerPoint = centerPoint;
    }

    public void enter(Entity actor) {
        if (actor == Game.getPlayerEntity()) {
            if (!visited) {
                if (roomType.entranceMessage != null) {
                    Game.announce(roomType.entranceMessage);
                }
            }
            visited = true;
        }
    }

    public void leave(Entity actor) {

    }

    public String toString() {
        return "(" + roomId + ": " + roomType.roomName + ", visited=" + visited + ", center " + centerPoint + ")";
    }
}
