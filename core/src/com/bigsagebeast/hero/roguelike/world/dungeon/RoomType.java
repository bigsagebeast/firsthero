package com.bigsagebeast.hero.roguelike.world.dungeon;

import com.bigsagebeast.hero.roguelike.world.LoadProc;
import com.bigsagebeast.hero.roguelike.world.dungeon.generation.SpecialSpawner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class RoomType {
    public static RoomType GENERIC_UPSTAIR = new RoomType(null, null);
    public static RoomType GENERIC_DOWNSTAIR = new RoomType(null, null);
    public static RoomType GENERIC_ROOM = new RoomType(null, null);
    public static RoomType GENERIC_CAVERN = new RoomType(null, null);
    public static RoomType UNDERGROUND_RIVER = new RoomType("Underground River", "You approach an underground river.");
    public static RoomType SUBDUNGEON_UNASSIGNED = new RoomType("UNASSIGNED SUBDUNGEON", "ERR: UNASSIGNED SUBDUNGEON ROOM");

    public static RoomType GENERIC_ANY = new RoomType("INVALID ROOM TYPE GENERIC_ANY", null, false); // just means "GENERIC_ROOM or GENERIC_CAVERN"
    public static RoomType ANY = new RoomType("ANY", null, false); // means 'select ANY room type at all' - risky?

    public static RoomType POOL = new RoomType("Reflecting Pool", "A pool ripples quietly in this room.");
    public static RoomType FORGE = new RoomType("Forge", "You enter the heat of an underground forge.");
    public static RoomType MOSSY = new RoomType(null, "Moss grows thick on the walls.");
    public static RoomType UNDERGROUND_GROVE = new RoomType("Grove", "You approach an underground grove.");

    public static RoomType FRACTAL_COPPER = new RoomType("Fractal Copper", "The ceiling is made of a fractal copper pattern that bubbles as you watch it.");
    public static RoomType ROT_SPAWNER = new RoomType("Rotting Chamber", "Rot and filth cover all corners of this room, past your ankles. It roils and shifts threateningly as you watch.");

    public static RoomType HP_REGEN_UP = new RoomType("Healing aura", "This room makes you feel revitalized, as though all magic is directed to that purpose.");
    public static RoomType SP_REGEN_UP = new RoomType("Powerful aura", "This room crackles with dry energy. Vital forces seem weak compared to raw power.");
    public static RoomType DP_REGEN_UP = new RoomType("Holy aura", "In this room, you feel a powerful connection to your divine self!");

    public String roomName;
    public String entranceMessage;
    public boolean specialCorridors;
    public List<SpecialSpawner> spawners = new ArrayList<>();
    public List<LoadProc> procLoaders = new ArrayList<>();

    static {
        SpecialSpawner fireSpawner = SpecialSpawner.newRegen();
        fireSpawner.spawnMTTH = 500;
        fireSpawner.tags = Arrays.asList("fire", "generic-fantasy");
        fireSpawner.threatModifier = -1;
        FORGE.spawners.add(fireSpawner);

        SpecialSpawner waterSpawner = SpecialSpawner.newRegen();
        waterSpawner.spawnMTTH = 500;
        waterSpawner.tags = Arrays.asList("water", "generic-fantasy");
        waterSpawner.threatModifier = -1;
        POOL.spawners.add(waterSpawner);
        UNDERGROUND_RIVER.spawners.add(waterSpawner);

        SpecialSpawner naturaeSpawner = SpecialSpawner.newRegen();
        naturaeSpawner.spawnMTTH = 500;
        naturaeSpawner.tags = Arrays.asList("naturae", "generic-fantasy");
        naturaeSpawner.threatModifier = -1;
        MOSSY.spawners.add(naturaeSpawner);
        UNDERGROUND_GROVE.spawners.add(naturaeSpawner);

        SpecialSpawner copperSpawner = SpecialSpawner.newRegen();
        copperSpawner.summoned = true;
        copperSpawner.spawnMTTH = 200;
        copperSpawner.tags = Arrays.asList("tech");
        copperSpawner.spawnNearPlayer = true;
        HashMap<String, String> paradoxWispLoadProcMap = new HashMap<>();
        paradoxWispLoadProcMap.put("spawnMTTH", "3");
        paradoxWispLoadProcMap.put("quantity", "2");
        paradoxWispLoadProcMap.put("minionKey", "paradox.wisp");
        LoadProc paradoxWispLoadProc = new LoadProc("ProcSummonMinions", paradoxWispLoadProcMap);
        copperSpawner.loadProcs.add(paradoxWispLoadProc);
        FRACTAL_COPPER.spawners.add(copperSpawner);

        HashMap<String, String> rotStalkerLoadProcMap = new HashMap<>();
        rotStalkerLoadProcMap.put("moverKey", "construct.rotstalker");
        rotStalkerLoadProcMap.put("maxSpawnTimer", "20");
        rotStalkerLoadProcMap.put("spawnMessage", "The rot forms itself into an upright shape!");
        LoadProc rotSpawnerLoadProc = new LoadProc("room.ProcRoomSpawnWhenPlayerPresent", rotStalkerLoadProcMap);
        ROT_SPAWNER.procLoaders.add(rotSpawnerLoadProc);

        HashMap<String, String> hpRegenUpLoadProcMap = new HashMap<>();
        hpRegenUpLoadProcMap.put("hpMultiplier", "2.0");
        hpRegenUpLoadProcMap.put("spMultiplier", "0");
        LoadProc hpRegenUpLoadProc = new LoadProc("room.ProcRoomRegenMultipliers", hpRegenUpLoadProcMap);
        HP_REGEN_UP.procLoaders.add(hpRegenUpLoadProc);

        HashMap<String, String> spRegenUpLoadProcMap = new HashMap<>();
        spRegenUpLoadProcMap.put("hpMultiplier", "0");
        spRegenUpLoadProcMap.put("spMultiplier", "2.0");
        LoadProc spRegenUpLoadProc = new LoadProc("room.ProcRoomRegenMultipliers", spRegenUpLoadProcMap);
        SP_REGEN_UP.procLoaders.add(spRegenUpLoadProc);

        HashMap<String, String> dpRegenUpLoadProcMap = new HashMap<>();
        dpRegenUpLoadProcMap.put("dpMultiplier", "2.0");
        LoadProc dpRegenUpLoadProc = new LoadProc("room.ProcRoomRegenMultipliers", dpRegenUpLoadProcMap);
        DP_REGEN_UP.procLoaders.add(dpRegenUpLoadProc);
    }

    public RoomType() {
    }

    public RoomType(String roomName, String entranceMessage) {
        this(roomName, entranceMessage, false);
    }

    public RoomType(String roomName, String entranceMessage, boolean specialCorridors) {
        this.roomName = roomName;
        this.entranceMessage = entranceMessage;
        this.specialCorridors = specialCorridors;
    }
}
