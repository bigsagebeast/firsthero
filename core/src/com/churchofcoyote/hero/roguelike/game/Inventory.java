package com.churchofcoyote.hero.roguelike.game;

import com.churchofcoyote.hero.GameLoop;
import com.churchofcoyote.hero.dialogue.DialogueBox;
import com.churchofcoyote.hero.glyphtile.EntityGlyph;
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
                .withFooterClosableAndSelectable()
                .withMargins(60, 60);

        for (BodyPart bp : Game.getPlayerEntity().body.bodyPlan.getParts()) {
            String equipmentName;
            if (equipment.get(bp) != null) {
                equipmentName = equipment.get(bp).name;
            } else {
                // TODO: I don't like this test, it seems like the wielder should have an "is 2h" flag
                if (bp == BodyPart.OFF_HAND && equipment.get(BodyPart.PRIMARY_HAND) != null &&
                    equipment.get(BodyPart.PRIMARY_HAND).getEquippable().equipmentFor == BodyPart.TWO_HAND)
                {
                    equipmentName = "(2-handed)";
                } else {
                    equipmentName = "empty";
                }
            }
            box.addItem(String.format("%-16s: %-16s", bp.getName(), equipmentName), bp);
        }
        GameLoop.dialogueBoxModule.openDialogueBox(box, this::handleWieldResponse);
    }

    public void handleWieldResponse(Object response) {
        if (response == null)
        {
            return;
        }
        BodyPart bp = (BodyPart)response;
        this.chosenBodyPart = bp;
        List<BodyPart> equippable = new ArrayList<>();
        if (bp == BodyPart.PRIMARY_HAND || bp == BodyPart.OFF_HAND) {
            equippable.add(BodyPart.ANY_HAND);
            equippable.add(BodyPart.TWO_HAND);
        }
        openInventoryToEquip(equippable);
    }

    public void openInventoryToEquip(List<BodyPart> bodyParts) {
        //HashMap<Integer, Entity> mapping = new HashMap<>();
        //int mappingIndex = 0;

        boolean addedAnything = false;
        List<Entity> inventory = Game.getPlayerEntity().inventory;
        DialogueBox box = new DialogueBox()
                .withFooterClosableAndSelectable()
                .withMargins(60, 60);
        for (ItemCategory cat : ItemCategory.categories) {
            List<Entity> ents = inventory.stream().filter(e -> e.itemType.category == cat &&
                    (bodyParts.contains(BodyPart.PRIMARY_HAND) || bodyParts.contains(e.getEquippable().equipmentFor))).collect(Collectors.toList());
            if (ents.size() > 0) {
                box.addHeader(cat.getName());
            }
            for (Entity ent : ents) {
                box.addItem(ent.name, EntityGlyph.getGlyph(ent), ent);
                addedAnything = true;
            }
        }
        if (!addedAnything) {
            box.addHeader("No inventory available.");
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
