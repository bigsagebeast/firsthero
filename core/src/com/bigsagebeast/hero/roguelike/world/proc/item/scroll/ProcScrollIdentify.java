package com.bigsagebeast.hero.roguelike.world.proc.item.scroll;

import com.bigsagebeast.hero.GameLoop;
import com.bigsagebeast.hero.dialogue.DialogueBox;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.ItemCategory;
import com.bigsagebeast.hero.roguelike.world.Itempedia;
import com.bigsagebeast.hero.roguelike.world.proc.ImmutableProc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ProcScrollIdentify extends ImmutableProc {

    @Override
    public Boolean targetForRead(Entity entity) { return Boolean.TRUE; }

    @Override
    public Boolean preBeRead(Entity entity, Entity actor) {
        return Boolean.TRUE;
    }

    @Override
    public void postBeRead(Entity entity, Entity actor) {
        entity.identifyItemType();
        openInventoryToIdentify();
    }


    public void openInventoryToIdentify() {
        Collection<Entity> inventory = Game.getPlayerEntity().getInventoryEntities();
        Collection<Entity> equipment = Game.getPlayerEntity().getEquippedEntities();
        DialogueBox box = new DialogueBox()
                .withFooterClosable()
                .withTitle("Select item to identify")
                .withAllowLetters(true)
                .withMargins(60, 60);
        boolean anyUnidentified = false;
        equipment = equipment.stream().filter(ent ->
                        (!ent.getItem().identified && ent.getItemType().hasBeatitude) || (ent.getItemType().identityHidden && !ent.getItemType().identified))
                .collect(Collectors.toList());
        inventory = inventory.stream().filter(ent ->
                        (!ent.getItem().identified && ent.getItemType().hasBeatitude) || (ent.getItemType().identityHidden && !ent.getItemType().identified))
                .collect(Collectors.toList());
        if (!equipment.isEmpty()) {
            anyUnidentified = true;
            box.addHeader("*** Equipment ***");
            for (Entity ent : equipment) {
                box.addItem(ent.getVisibleNameIndefiniteOrSpecific(), ent);
            }
        }

        for (ItemCategory cat : ItemCategory.categories) {
            List<Entity> ents = inventory.stream().filter(e -> Itempedia.get(e.itemTypeKey).category == cat).collect(Collectors.toList());
            if (ents.size() > 0) {
                box.addHeader("*** " + cat.getName() + " ***");
                anyUnidentified = true;
            }
            for (Entity ent : ents) {
                box.addItem(ent.getVisibleNameIndefiniteOrSpecific(), ent);
            }
        }
        if (!anyUnidentified) {
            box.addHeader("No unidentified items available.");
        }
        GameLoop.dialogueBoxModule.openDialogueBox(box, this::handleInventoryToIdentifyResponse);
    }

    public void handleInventoryToIdentifyResponse(Object chosenEntity) {
        Entity e = (Entity)chosenEntity;
        if (e != null) {
            e.identifyItemFully();
        }
    }


}
