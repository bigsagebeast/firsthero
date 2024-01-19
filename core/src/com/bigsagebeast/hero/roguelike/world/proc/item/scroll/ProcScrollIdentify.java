package com.bigsagebeast.hero.roguelike.world.proc.item.scroll;

import com.bigsagebeast.hero.GameLoop;
import com.bigsagebeast.hero.dialogue.DialogueBox;
import com.bigsagebeast.hero.enums.Beatitude;
import com.bigsagebeast.hero.glyphtile.EntityGlyph;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.ItemCategory;
import com.bigsagebeast.hero.roguelike.world.Itempedia;
import com.bigsagebeast.hero.roguelike.world.proc.ImmutableProc;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ProcScrollIdentify extends ImmutableProc {
    public int countMax;
    public int count;

    @Override
    public Boolean targetForRead(Entity entity) { return Boolean.TRUE; }

    @Override
    public Boolean preBeRead(Entity entity, Entity actor) {
        return Boolean.TRUE;
    }

    @Override
    public void postBeRead(Entity entity, Entity actor) {
        entity.identifyItemType();
        if (entity.getBeatitude() == Beatitude.BLESSED) {
            identifyAll();
        } else {
            if (entity.getBeatitude() == Beatitude.CURSED) {
                countMax = 3;
                count = 1;
            } else {
                countMax = 1;
                count = 1;
            }
            openInventoryToIdentify();
        }
    }

    public void identifyAll() {
        Collection<Entity> inventory = Game.getPlayerEntity().getInventoryEntities();
        Collection<Entity> equipment = Game.getPlayerEntity().getEquippedEntities();
        equipment.addAll(inventory);
        for (Entity entity : equipment) {
            entity.identifyItemFully(true);
        }
        Game.announceLoud("You identify your inventory fully.");
    }

    public void openInventoryToIdentify() {
        Collection<Entity> inventory = Game.getPlayerEntity().getInventoryEntities();
        Collection<Entity> equipment = Game.getPlayerEntity().getEquippedEntities();
        DialogueBox box = new DialogueBox()
                .withFooterClosable()
                .withTitle(String.format("Select item to identify - %d of %d", count, countMax))
                .withAllowLetters(true)
                .withMargins(60, 60);
        boolean anyUnidentified = false;
        equipment = equipment.stream().filter(ent ->
                        (!ent.getItem().identifiedBeatitude && ent.getItemType().hasBeatitude) || (ent.getItemType().identityHidden && !ent.getItemType().identified))
                .collect(Collectors.toList());
        inventory = inventory.stream().filter(ent ->
                        (!ent.getItem().identifiedBeatitude && ent.getItemType().hasBeatitude) || (ent.getItemType().identityHidden && !ent.getItemType().identified))
                .collect(Collectors.toList());
        if (!equipment.isEmpty()) {
            anyUnidentified = true;
            box.addHeader("*** Equipment ***");
            for (Entity ent : equipment) {
                box.addItem(ent.getNameBlock(), EntityGlyph.getGlyph(ent), ent);
            }
        }

        for (ItemCategory cat : ItemCategory.categories) {
            List<Entity> ents = inventory.stream().filter(e -> Itempedia.get(e.itemTypeKey).category == cat).collect(Collectors.toList());
            if (ents.size() > 0) {
                box.addHeader("*** " + cat.getName() + " ***");
                anyUnidentified = true;
            }
            for (Entity ent : ents) {
                box.addItem(ent.getNameBlock(), EntityGlyph.getGlyph(ent), ent);
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
            if (++count <= countMax) {
                openInventoryToIdentify();
            }
        }
    }


}
