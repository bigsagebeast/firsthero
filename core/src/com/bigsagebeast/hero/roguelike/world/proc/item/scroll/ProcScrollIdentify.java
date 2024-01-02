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
        entity.identifyItem();
        openInventoryToIdentify();
    }


    public void openInventoryToIdentify() {
        Collection<Entity> inventory = Game.getPlayerEntity().getInventoryEntities();
        DialogueBox box = new DialogueBox()
                .withFooterClosable()
                .withTitle("Select item to identify")
                .withMargins(60, 60);
        boolean anyUnidentified = false;
        for (ItemCategory cat : ItemCategory.categories) {
            List<Entity> ents = inventory.stream().filter(e -> Itempedia.get(e.itemTypeKey).category == cat).collect(Collectors.toList());
            List<Entity> unidentified = new ArrayList<>();
            for (Entity ent : ents) {
                if (ent.getItemType().identityHidden && !ent.getItemType().identified)
                unidentified.add(ent);
            }
            if (unidentified.size() > 0) {
                box.addHeader("*** " + cat.getName() + " ***");
                anyUnidentified = true;
            }
            for (Entity ent : unidentified) {
                box.addItem(ent.getVisibleNameSingularOrSpecific(), ent);
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
            e.identifyItem();
        }
    }


}
