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
        openInventoryToUncurse();
    }


    public void openInventoryToUncurse() {
        Collection<Entity> inventory = Game.getPlayerEntity().getInventoryEntities();
        Collection<Entity> equipment = Game.getPlayerEntity().getEquippedEntities();
        DialogueBox box = new DialogueBox()
                .withFooterClosable()
                .withTitle("Select item to uncurse")
                .withAllowLetters(true)
                .withMargins(60, 60);
        boolean anyUnsafeTarget = false;
        equipment = equipment.stream().filter(ent ->
                        (!ent.getItem().identified && ent.getItemType().hasBeatitude) || ent.getItem().beatitude == Beatitude.CURSED)
                .collect(Collectors.toList());
        inventory = inventory.stream().filter(ent ->
                        (!ent.getItem().identified && ent.getItemType().hasBeatitude) || ent.getItem().beatitude == Beatitude.CURSED)
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
            box.addHeader("No cursed items available.");
        }
        GameLoop.dialogueBoxModule.openDialogueBox(box, this::handleInventoryToUncurseResponse);
    }

    public void handleInventoryToUncurseResponse(Object chosenEntity) {
        Entity e = (Entity)chosenEntity;
        if (e != null && e.getItem().beatitude == Beatitude.CURSED) {
            GameEntities.changeItemBeatitude(e, Beatitude.UNCURSED);
        } else {
            Game.announce("Nothing happens.");
        }
    }


}
