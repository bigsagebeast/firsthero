package com.churchofcoyote.hero.roguelike.game;

import com.churchofcoyote.hero.GameLoop;
import com.churchofcoyote.hero.dialogue.DialogueBox;
import com.churchofcoyote.hero.glyphtile.EntityGlyph;
import com.churchofcoyote.hero.roguelike.world.*;
import com.churchofcoyote.hero.roguelike.world.proc.Proc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Inventory {

    private BodyPart chosenBodyPartForDialogue;

    public void doWield() {

        Entity playerEntity = Game.getPlayerEntity();
        DialogueBox box = new DialogueBox()
                .withFooterClosableAndSelectable()
                .withMargins(60, 60);

        for (BodyPart bp : Game.getPlayerEntity().body.getParts()) {
            String equipmentName;
            if (playerEntity.body.getEquipment(bp) != null) {
                equipmentName = playerEntity.body.getEquipment(bp).getVisibleNameSingularOrSpecific();
            } else {
                // TODO: I don't like this test, it seems like the wielder should have an "is 2h" flag
                if (bp == BodyPart.OFF_HAND && playerEntity.body.getEquipment(BodyPart.PRIMARY_HAND) != null &&
                        playerEntity.body.getEquipment(BodyPart.PRIMARY_HAND).getEquippable().equipmentFor == BodyPart.TWO_HAND)
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
        this.chosenBodyPartForDialogue = bp;
        List<BodyPart> equippable = new ArrayList<>();
        if (bp == BodyPart.PRIMARY_HAND || bp == BodyPart.OFF_HAND) {
            equippable.add(BodyPart.ANY_HAND);
            equippable.add(BodyPart.TWO_HAND);
        } else {
            equippable.add(bp);
        }
        openInventoryToEquip(equippable);
    }

    public void openInventoryToEquip(List<BodyPart> bodyParts) {
        boolean addedAnything = false;
        Collection<Entity> inventory = Game.getPlayerEntity().getInventoryEntities();
        DialogueBox box = new DialogueBox()
                .withFooterClosableAndSelectable()
                .withMargins(60, 60);
        for (ItemCategory cat : ItemCategory.categories) {
            List<Entity> ents = inventory.stream().filter(e -> Itempedia.get(e.itemTypeKey).category == cat &&
                    (bodyParts.contains(BodyPart.ANY_HAND) ||
                            (e.getEquippable() != null && bodyParts.contains(e.getEquippable().equipmentFor))
                    )).collect(Collectors.toList());
            if (ents.size() > 0) {
                box.addHeader(cat.getName());
            }
            for (Entity ent : ents) {
                box.addItem(ent.getVisibleNameSingularOrSpecific(), EntityGlyph.getGlyph(ent), ent);
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
            Game.getPlayerEntity().equip(e, chosenBodyPartForDialogue);
            GameLoop.roguelikeModule.game.passTime(Game.ONE_TURN);
        }
    }


    public void openInventoryToDrop() {
        Collection<Entity> inventory = Game.getPlayerEntity().getInventoryEntities();
        DialogueBox box = new DialogueBox()
                .withFooterClosable()
                .withMargins(60, 60);
        for (ItemCategory cat : ItemCategory.categories) {
            List<Entity> ents = inventory.stream().filter(e -> Itempedia.get(e.itemTypeKey).category == cat).collect(Collectors.toList());
            if (ents.size() > 0) {
                box.addHeader("*** " + cat.getName() + " ***");
            }
            for (Entity ent : ents) {
                box.addItem(ent.getVisibleNameSingularOrSpecific(), ent);
            }
        }
        GameLoop.dialogueBoxModule.openDialogueBox(box, this::handleInventoryToDropResponse);
    }

    public void handleInventoryToDropResponse(Object chosenEntity) {
        Entity e = (Entity)chosenEntity;
        if (e != null) {
            Game.getPlayerEntity().dropItem(e);
            GameLoop.roguelikeModule.game.passTime(Game.ONE_TURN);
        }
    }

    public void doQuaff() {
        Collection<Entity> inventory = Game.getPlayerEntity().getInventoryEntities();
        DialogueBox box = new DialogueBox()
                .withFooterClosable()
                .withMargins(60, 60);
        for (ItemCategory cat : ItemCategory.categories) {
            List<Entity> ents = inventory.stream().filter(e -> Itempedia.get(e.itemTypeKey).category == cat).collect(Collectors.toList());
            List<Entity> quaffableEnts = new ArrayList<>();
            for (Entity ent : ents) {
                boolean isQuaffable = false;
                boolean notQuaffable = false;
                for (Proc p : ent.procs) {
                    Boolean quaffable = p.targetForQuaff(ent);
                    if (quaffable == Boolean.FALSE) {
                        notQuaffable = true;
                    } else if (quaffable == Boolean.TRUE) {
                        isQuaffable = true;
                    }
                }
                if (isQuaffable && !notQuaffable) {
                    quaffableEnts.add(ent);
                }
            }

            if (quaffableEnts.size() > 0) {
                box.addHeader("*** " + cat.getName() + " ***");
            }
            for (Entity ent : quaffableEnts) {
                box.addItem(ent.getVisibleNameSingularOrSpecific(), ent);
            }
        }
        GameLoop.dialogueBoxModule.openDialogueBox(box, this::handleQuaff);
    }

    public void handleQuaff(Object chosenEntity) {
        Entity e = (Entity)chosenEntity;
        if (e != null) {
            Game.getPlayerEntity().quaffItem(e);
        }
    }



    public void openInventory() {
        Collection<Entity> inventory = Game.getPlayerEntity().getInventoryEntities();
        DialogueBox box = new DialogueBox()
                .withFooterClosable()
                .withMargins(60, 60);
        for (ItemCategory cat : ItemCategory.categories) {
            List<Entity> ents = inventory.stream().filter(e -> Itempedia.get(e.itemTypeKey).category == cat).collect(Collectors.toList());
            if (ents.size() > 0) {
                box.addHeader("*** " + cat.getName() + " ***");
            }
            for (Entity ent : ents) {
                box.addItem(ent.getVisibleNameSingularOrSpecific(), ent);
            }
        }
        GameLoop.dialogueBoxModule.openDialogueBox(box, this::handleInventoryResponse);
    }

    public void handleInventoryResponse(Object chosenEntity) {
    }
}
