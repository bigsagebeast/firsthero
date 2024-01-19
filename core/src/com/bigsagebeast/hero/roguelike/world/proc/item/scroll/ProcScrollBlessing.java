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

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ProcScrollBlessing extends ImmutableProc {
    public int countMax;
    public int count;
    public boolean isCursed;

    @Override
    public Boolean targetForRead(Entity entity) { return Boolean.TRUE; }

    @Override
    public Boolean preBeRead(Entity entity, Entity actor) {
        return Boolean.TRUE;
    }

    @Override
    public void postBeRead(Entity entity, Entity actor) {
        if (entity.getItem().beatitude == Beatitude.BLESSED) {
            countMax = 3;
            count = 1;
        } else {
            countMax = 1;
            count = 1;
        }
        isCursed = entity.getItem().beatitude == Beatitude.CURSED;
        openInventoryToBless();
    }
    public void openInventoryToBless() {
        Collection<Entity> inventory = Game.getPlayerEntity().getInventoryEntities();
        Collection<Entity> equipment = Game.getPlayerEntity().getEquippedEntities();
        DialogueBox box = new DialogueBox()
                .withFooterClosable()
                .withTitle(String.format("Select item to bless - %d of %d", count, countMax))
                .withAllowLetters(true)
                .withMargins(60, 60);
        boolean anyUnsafeTarget = false;
        equipment = equipment.stream().filter(ent ->
                        (!ent.getItem().identifiedBeatitude && ent.getItemType().hasBeatitude) || ent.getItem().beatitude != Beatitude.BLESSED)
                .collect(Collectors.toList());
        inventory = inventory.stream().filter(ent ->
                        (!ent.getItem().identifiedBeatitude && ent.getItemType().hasBeatitude) || ent.getItem().beatitude != Beatitude.BLESSED)
                .collect(Collectors.toList());
        if (!equipment.isEmpty()) {
            anyUnsafeTarget = true;
            box.addHeader("*** Equipment ***");
            for (Entity ent : equipment) {
                box.addItem(ent.getVisibleNameIndefiniteOrSpecific(), ent);
            }
        }

        for (ItemCategory cat : ItemCategory.categories) {
            List<Entity> ents = inventory.stream().filter(e -> Itempedia.get(e.itemTypeKey).category == cat).collect(Collectors.toList());
            if (ents.size() > 0) {
                box.addHeader("*** " + cat.getName() + " ***");
                anyUnsafeTarget = true;
            }
            for (Entity ent : ents) {
                box.addItem(ent.getVisibleNameIndefiniteOrSpecific(), ent);
            }
        }
        if (!anyUnsafeTarget) {
            box.addHeader("No items remaining to bless.");
        }
        GameLoop.dialogueBoxModule.openDialogueBox(box, this::handleInventoryToBlessResponse);
    }

    public void handleInventoryToBlessResponse(Object chosenEntity) {
        Entity e = (Entity)chosenEntity;
        if (!isCursed) {
            if (e != null && e.getItem().beatitude != Beatitude.BLESSED) {
                GameEntities.changeItemBeatitude(e, Beatitude.BLESSED);
            } else {
                Game.announce("Nothing happens.");
            }
        } else {
            if (e != null && e.getItem().beatitude != Beatitude.CURSED) {
                Game.announce("Something was wrong with that scroll!");
                GameEntities.changeItemBeatitude(e, Beatitude.CURSED);
            } else {
                Game.announce("Nothing happens.");
            }
        }
        if (++count <= countMax) {
            openInventoryToBless();
        }
    }
}
