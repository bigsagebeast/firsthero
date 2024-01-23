package com.bigsagebeast.hero.roguelike.world.dungeon;

import com.bigsagebeast.hero.roguelike.world.LoadProc;
import com.bigsagebeast.hero.roguelike.world.dungeon.generation.SpecialSpawner;
import com.bigsagebeast.hero.roguelike.world.proc.Proc;
import com.bigsagebeast.hero.util.Point;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Entity;

import java.util.ArrayList;

public class Room {
    public int roomId;
    public RoomType roomType;
    public ArrayList<Proc> procs = new ArrayList<>();
    public boolean visited = false;
    public Point centerPoint;
    public ArrayList<SpecialSpawner> spawners = new ArrayList<>();

    public Room(RoomType roomType, Point centerPoint) {
        setRoomType(roomType);
        this.centerPoint = centerPoint;
    }

    public void enter(Entity actor) {
        if (actor == Game.getPlayerEntity()) {
            //if (!visited) {
                if (roomType.entranceMessage != null) {
                    Game.announceDescriptive(roomType.entranceMessage);
                }
            //}
            visited = true;
        }
    }

    public void leave(Entity actor) {

    }

    public void setRoomType(RoomType type) {
        this.roomType = type;
        spawners.clear();
        for (SpecialSpawner spawner : type.spawners) {
            if (spawner.regen) {
                spawners.add(spawner.clone());
            }
        }
        for (LoadProc procLoader : type.procLoaders) {
            procLoader.apply(this);
        }
    }

    public void addProc(Proc proc) {
        procs.add(proc);
        proc.initialize(this);
    }

    public String toString() {
        return "(" + roomId + ": " + roomType.roomName + ", visited=" + visited + ", center " + centerPoint + ")";
    }
}
