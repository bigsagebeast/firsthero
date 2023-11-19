package com.churchofcoyote.hero.roguelike.game;

import com.churchofcoyote.hero.GameLoop;
import com.churchofcoyote.hero.dialogue.DialogueBox;
import com.churchofcoyote.hero.roguelike.world.BodyPart;
import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.roguelike.world.ItemCategory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Inventory {

    private BodyPart chosenBodyPart;

    public void doWield() {
        HashMap<Integer, Entity> mapping = new HashMap<>();
        int mappingIndex = 0;

        HashMap<BodyPart, Entity> equipment = Game.getPlayerEntity().body.equipment;
        DialogueBox box = new DialogueBox()
                .withFooterClosable()
                .withMargins(60, 60);

        for (BodyPart bp : Game.getPlayerEntity().body.bodyPlan.getParts()) {
            box.addItem(bp.getName(), bp);
        }
        GameLoop.dialogueBoxModule.openDialogueBox(box, this::handleWieldResponse);
    }

    public void handleWieldResponse(Object chosenBodyPart) {
        BodyPart bp = (BodyPart)chosenBodyPart;
        this.chosenBodyPart = bp;
        List<BodyPart> equippable = new ArrayList<>();
        if (bp == BodyPart.PRIMARY_HAND || bp == BodyPart.PRIMARY_HAND) {
            equippable.add(BodyPart.ANY_HAND);
            equippable.add(BodyPart.TWO_HAND);
        }
        openInventoryToEquip(equippable);
    }

    public void openInventoryToEquip(List<BodyPart> bodyParts) {
        //HashMap<Integer, Entity> mapping = new HashMap<>();
        //int mappingIndex = 0;

        List<Entity> inventory = Game.getPlayerEntity().inventory;
        DialogueBox box = new DialogueBox()
                .withFooterClosable()
                .withMargins(60, 60);
        for (ItemCategory cat : ItemCategory.categories) {
            List<Entity> ents = inventory.stream().filter(e -> e.itemType.category == cat).collect(Collectors.toList());
            if (ents.size() > 0) {
                box.addHeader("*** " + cat.getName() + " ***");
            }
            for (Entity ent : ents) {
                box.addItem(ent.name, ent);
            }
        }
        GameLoop.dialogueBoxModule.openDialogueBox(box, this::handleInventoryToEquipResponse);
    }

    public void handleInventoryToEquipResponse(Object chosenEntity) {
        Entity e = (Entity)chosenEntity;
        if (e != null) {
            Game.getPlayerEntity().equip(e, chosenBodyPart);
        }
    }


    public void openInventory() {
        //HashMap<Integer, Entity> mapping = new HashMap<>();
        //int mappingIndex = 0;

        List<Entity> inventory = Game.getPlayerEntity().inventory;
        DialogueBox box = new DialogueBox()
                .withFooterClosable()
                .withMargins(60, 60);
        for (ItemCategory cat : ItemCategory.categories) {
            List<Entity> ents = inventory.stream().filter(e -> e.itemType.category == cat).collect(Collectors.toList());
            if (ents.size() > 0) {
                box.addHeader("*** " + cat.getName() + " ***");
            }
            for (Entity ent : ents) {
//                mapping.put(mappingIndex, ent);
                box.addItem(ent.name, ent);
//                mappingIndex++;
            }
        }
        GameLoop.dialogueBoxModule.openDialogueBox(box, this::handleInventoryResponse);
    }

    public void handleInventoryResponse(Object chosenEntity) {
        Entity e = (Entity)chosenEntity;
        if (e != null) {
            System.out.println("responded with option " + e.name);
        }
    }
}
