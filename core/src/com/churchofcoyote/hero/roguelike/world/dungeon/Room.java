package com.churchofcoyote.hero.roguelike.world.dungeon;

import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.world.Entity;

public class Room {
    public int roomId;
    public RoomType roomType;
    public boolean visited = false;

    public Room(RoomType roomType) {
        this.roomType = roomType;
    }

    public void enter(Entity actor) {
        if (actor == Game.getPlayerEntity()) {
            if (!visited) {
                Game.announce(roomType.entranceMessage);
            }
            visited = true;
        }
    }

    public void leave(Entity actor) {

    }
}
