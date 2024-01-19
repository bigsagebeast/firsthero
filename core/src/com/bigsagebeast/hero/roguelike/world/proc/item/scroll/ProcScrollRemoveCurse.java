package com.bigsagebeast.hero.roguelike.world.proc.item.scroll;

import com.bigsagebeast.hero.GameLoop;
import com.bigsagebeast.hero.dialogue.DialogueBox;
import com.bigsagebeast.hero.enums.Beatitude;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.game.GameEntities;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.ItemCategory;
import com.bigsagebeast.hero.roguelike.world.Itempedia;
import com.bigsagebeast.hero.roguelike.world.proc.ImmutableProc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ProcScrollRemoveCurse extends ImmutableProc {

    @Override
    public Boolean targetForRead(Entity entity) { return Boolean.TRUE; }

    @Override
    public Boolean preBeRead(Entity entity, Entity actor) {
        return Boolean.TRUE;
    }

    @Override
    public void postBeRead(Entity entity, Entity actor) {
        entity.identifyItemType();
        List<Entity> targets;
        switch (entity.getItem().beatitude) {
            case CURSED:
                targets = selectNonCursedToCurse(1);
                if (targets.isEmpty()) {
                    Game.announce("Nothing happens.");
                } else {
                    Game.announce("Something was wrong with that scroll!");
                    for (Entity e : targets) {
                        GameEntities.changeItemBeatitude(e, Beatitude.CURSED);
                    }
                }
                break;
            case UNCURSED:
                targets = selectCursedToUncurse(1);
                if (targets.isEmpty()) {
                    Game.announce("Nothing happens.");
                } else {
                    for (Entity e : targets) {
                        GameEntities.changeItemBeatitude(e, Beatitude.UNCURSED);
                    }
                }
                break;
            case BLESSED:
                targets = selectCursedToUncurse(3);
                if (targets.isEmpty()) {
                    Game.announce("Nothing happens.");
                } else {
                    for (Entity e : targets) {
                        GameEntities.changeItemBeatitude(e, Beatitude.UNCURSED);
                    }
                }
                break;
        }
    }

    public List<Entity> selectNonCursedToCurse(int count) {
        List<Entity> inventory = new ArrayList<>(Game.getPlayerEntity().getInventoryEntities());
        List<Entity> equipment = new ArrayList<>(Game.getPlayerEntity().getEquippedEntities());
        inventory.removeIf(e -> e.getItem().beatitude == Beatitude.CURSED);
        equipment.removeIf(e -> e.getItem().beatitude == Beatitude.CURSED);
        Collections.shuffle(inventory);
        Collections.shuffle(equipment);
        equipment.addAll(inventory);
        List<Entity> selected = new ArrayList<>();
        for (int i=0; i<count && !equipment.isEmpty(); i++) {
            selected.add(equipment.remove(0));
        }
        return selected;
    }

    public List<Entity> selectCursedToUncurse(int count) {
        List<Entity> inventory = new ArrayList<>(Game.getPlayerEntity().getInventoryEntities());
        List<Entity> equipment = new ArrayList<>(Game.getPlayerEntity().getEquippedEntities());
        inventory.removeIf(e -> e.getItem().beatitude != Beatitude.CURSED);
        equipment.removeIf(e -> e.getItem().beatitude != Beatitude.CURSED);
        Collections.shuffle(inventory);
        Collections.shuffle(equipment);
        equipment.addAll(inventory);
        List<Entity> selected = new ArrayList<>();
        for (int i = 0; i < count && !equipment.isEmpty(); i++) {
            selected.add(equipment.remove(0));
        }
        return selected;
    }
}
