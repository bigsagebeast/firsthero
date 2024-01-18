package com.bigsagebeast.hero.roguelike.game;

import com.bigsagebeast.hero.GameLoop;
import com.bigsagebeast.hero.chat.ChatLink;
import com.bigsagebeast.hero.dialogue.ChatBox;
import com.bigsagebeast.hero.dialogue.TextEntryBox;
import com.bigsagebeast.hero.roguelike.world.*;
import com.bigsagebeast.hero.dialogue.DialogueBox;
import com.bigsagebeast.hero.glyphtile.EntityGlyph;
import com.bigsagebeast.hero.roguelike.world.proc.Proc;
import com.bigsagebeast.hero.roguelike.world.proc.item.ProcArmor;
import com.bigsagebeast.hero.roguelike.world.proc.item.ProcWeaponAmmo;
import com.bigsagebeast.hero.roguelike.world.proc.item.ProcWeaponMelee;
import com.bigsagebeast.hero.roguelike.world.proc.item.ProcWeaponRanged;
import com.bigsagebeast.hero.text.TextBlock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class Inventory {

    private static BodyPart chosenBodyPartForDialogue;

    private static Entity promptQuantityEntity;
    private static int promptQuantityMin;
    private static int promptQuantityMax;
    private static int promptQuantityDefault;
    private static BiConsumer<Entity, Integer> promptQuantityHandler;

    public static void doWield() {

        Entity playerEntity = Game.getPlayerEntity();
        DialogueBox box = new DialogueBox()
                .withFooterClosableAndSelectable()
                .withTitle("Select slot to wear or wield an item")
                .withAllowLetters(true)
                .withMargins(60, 60);

        for (BodyPart bp : Game.getPlayerEntity().body.getParts()) {
            String equipmentName = null;
            if (playerEntity.body.getEquipment(bp) != null) {
                //equipmentName = playerEntity.body.getEquipment(bp).getVisibleNameWithQuantity();
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
            TextBlock equipmentNameBlock = null;
            if (equipmentName != null) {
                equipmentNameBlock = new TextBlock(equipmentName);
            } else if (playerEntity.body.getEquipment(bp) != null) {
                equipmentNameBlock = playerEntity.body.getEquipment(bp).getNameBlock();
            }
            TextBlock lineBlock = new TextBlock(String.format("%-13s: ", bp.getName()));
            if (equipmentNameBlock != null) {
                lineBlock.append(equipmentNameBlock);
            }
            //box.addItem(String.format("%-16s: %-16s", bp.getName(), equipmentName), bp);
            box.addItem(lineBlock, bp);
        }
        GameLoop.dialogueBoxModule.openDialogueBox(box, Inventory::handleWieldResponse);
    }

    public static void handleWieldResponse(Object response) {
        if (response == null)
        {
            return;
        }
        BodyPart bp = (BodyPart)response;
        chosenBodyPartForDialogue = bp;
        List<BodyPart> equippable = new ArrayList<>();
        if (bp == BodyPart.PRIMARY_HAND || bp == BodyPart.OFF_HAND) {
            equippable.add(BodyPart.ANY_HAND);
            equippable.add(BodyPart.TWO_HAND);
        } else if (bp == BodyPart.LEFT_RING || bp == BodyPart.RIGHT_RING) {
            equippable.add(BodyPart.RING);
        } else {
            equippable.add(bp);
        }
        openInventoryToEquip(equippable);
    }

    public static void openInventoryToEquip(List<BodyPart> bodyParts) {
        boolean addedAnything = false;
        Collection<Entity> inventory = Game.getPlayerEntity().getInventoryEntities();
        DialogueBox box = new DialogueBox()
                .withFooterClosableAndSelectable()
                .withTitle("Select item to equip") // TODO show selected slot?
                .withAllowLetters(true)
                .withMargins(60, 60);
        // Use player entity as a special value
        box.addItem("Nothing", null, Game.getPlayerEntity());
        for (ItemCategory cat : ItemCategory.categories) {
            /*
            // Removed the ability to equip anything for melee
            List<Entity> ents = inventory.stream().filter(e -> Itempedia.get(e.itemTypeKey).category == cat &&
                    (bodyParts.contains(BodyPart.ANY_HAND) ||
                            (e.getEquippable() != null && bodyParts.contains(e.getEquippable().equipmentFor))
                    )).collect(Collectors.toList());
             */
            List<Entity> ents = inventory.stream().filter(e -> Itempedia.get(e.itemTypeKey).category == cat &&
                            e.getEquippable() != null && bodyParts.contains(e.getEquippable().equipmentFor)
                    ).collect(Collectors.toList());
            if (ents.size() > 0) {
                box.addHeader(cat.getName());
            }
            for (Entity ent : ents) {
                box.addItem(ent.getNameBlock(), EntityGlyph.getGlyph(ent), ent);
                //box.addItem(ent.getVisibleNameIndefiniteOrSpecific(), EntityGlyph.getGlyph(ent), ent);
                addedAnything = true;
            }
        }
        if (!addedAnything) {
            box.addHeader("No inventory available.");
        }
        GameLoop.dialogueBoxModule.openDialogueBox(box, Inventory::handleInventoryToEquipResponse);
    }

    public static void handleInventoryToEquipResponse(Object chosenObject) {
        Entity chosenEntity = (Entity)chosenObject;
        if (chosenEntity == Game.getPlayerEntity()) {
            Game.getPlayerEntity().equip(null, chosenBodyPartForDialogue);
        } else if (chosenEntity != null) {
            Game.getPlayerEntity().equip(chosenEntity, chosenBodyPartForDialogue);
            Game.passTime(Game.ONE_TURN);
        }
    }


    public static void openInventoryToDrop() {
        Collection<Entity> inventory = Game.getPlayerEntity().getInventoryEntities();
        DialogueBox box = new DialogueBox()
                .withFooterClosable()
                .withTitle("Select item to drop")
                .withAllowLetters(true)
                .withMargins(60, 60);
        for (ItemCategory cat : ItemCategory.categories) {
            List<Entity> ents = inventory.stream().filter(e -> Itempedia.get(e.itemTypeKey).category == cat).collect(Collectors.toList());
            if (ents.size() > 0) {
                box.addHeader("*** " + cat.getName() + " ***");
            }
            for (Entity ent : ents) {
                box.addItem(ent.getVisibleNameIndefiniteOrSpecific(), ent);
            }
        }
        GameLoop.dialogueBoxModule.openDialogueBox(box, Inventory::handleInventoryToDropResponse);
    }

    public static void handleInventoryToDropResponse(Object chosenObject) {
        Entity chosenEntity = (Entity)chosenObject;
        if (chosenEntity != null) {
            if (chosenEntity.getItem().quantity == 1) {
                dropWithQuantity(chosenEntity, 1);
            } else {
                promptQuantity("Drop", chosenEntity, Inventory::dropWithQuantity);
            }
        }
    }


    public static void dropWithQuantity(Entity entity, int quantity) {
        if (quantity > 0) {
            Game.getPlayerEntity().dropItemWithQuantity(entity, quantity);
            Game.passTime(Game.ONE_TURN);
        }
    }

    public static void openInventoryToThrow() {
        Collection<Entity> inventory = Game.getPlayerEntity().getInventoryEntities();
        DialogueBox box = new DialogueBox()
                .withFooterClosable()
                .withTitle("Select item to throw")
                .withAllowLetters(true)
                .withMargins(60, 60);

        // potions first
        List<Entity> potionEnts = inventory.stream().filter(e -> Itempedia.get(e.itemTypeKey).category == ItemCategory.CATEGORY_POTION).collect(Collectors.toList());
        if (potionEnts.size() > 0) {
            box.addHeader("*** " + ItemCategory.CATEGORY_POTION.getName() + " ***");
        }
        for (Entity ent : potionEnts) {
            box.addItem(ent.getVisibleNameIndefiniteOrSpecific(), ent);
        }

        // TODO: Special handling for rocks as ammo?

        for (ItemCategory cat : ItemCategory.categories.stream().filter(cat -> cat != ItemCategory.CATEGORY_POTION).collect(Collectors.toList())) {
            List<Entity> ents = inventory.stream().filter(e -> Itempedia.get(e.itemTypeKey).category == cat).collect(Collectors.toList());
            if (ents.size() > 0) {
                box.addHeader("*** " + cat.getName() + " ***");
            }
            for (Entity ent : ents) {
                box.addItem(ent.getVisibleNameIndefiniteOrSpecific(), ent);
            }
        }
        GameLoop.dialogueBoxModule.openDialogueBox(box, Game::handleThrowInventory);
    }


    public static void openFloorToGet() {
        Collection<Entity> floorInventory = Game.getLevel().getItemsOnTile(Game.getPlayerEntity().pos);
        DialogueBox box = new DialogueBox()
                .withFooterClosable()
                .withTitle("Select item to pick up")
                .withAllowLetters(true)
                .withMargins(60, 60);
        for (ItemCategory cat : ItemCategory.categories) {
            List<Entity> ents = floorInventory.stream().filter(e -> Itempedia.get(e.itemTypeKey).category == cat).collect(Collectors.toList());
            if (ents.size() > 0) {
                box.addHeader("*** " + cat.getName() + " ***");
            }
            for (Entity ent : ents) {
                box.addItem(ent.getVisibleNameIndefiniteOrSpecific(), ent);
            }
        }
        GameLoop.dialogueBoxModule.openDialogueBox(box, Inventory::handleFloorToGetResponse);
    }

    public static void handleFloorToGetResponse(Object chosenObject) {
        Entity chosenEntity = (Entity)chosenObject;
        if (chosenEntity != null) {
            if (chosenEntity.getItem().quantity == 1) {
                Game.pickupWithQuantity(chosenEntity, 1);
            } else {
                promptQuantity("Pick up", chosenEntity, Game::pickupWithQuantity);
            }
        }
    }

    public static void openInventoryToEat() {
        DialogueBox box = new DialogueBox()
                .withFooterClosable()
                .withTitle("Select item to eat")
                .withAllowLetters(true)
                .withMargins(60, 60);

        List<Entity> floorFood = new ArrayList<>();
        for (Entity item : Game.getLevel().getItemsOnTile(Game.getPlayerEntity().pos)) {
            Boolean isThisItemEdible = null;
            for (Proc proc : item.procs) {
                Boolean isThisProcEdible = proc.isEdible(item, Game.getPlayerEntity());
                if (isThisProcEdible == Boolean.TRUE && isThisItemEdible != Boolean.FALSE) {
                    isThisItemEdible = Boolean.TRUE;
                } else if (isThisProcEdible == Boolean.FALSE) {
                    isThisItemEdible = Boolean.FALSE;
                }
            }
            if (isThisItemEdible == Boolean.TRUE) {
                floorFood.add(item);
            }
        }

        List<Entity> inventoryFood = new ArrayList<>();
        for (Entity item : Game.getPlayerEntity().getInventoryEntities()) {
            Boolean isThisItemEdible = null;
            for (Proc proc : item.procs) {
                Boolean isThisProcEdible = proc.isEdible(item, Game.getPlayerEntity());
                if (isThisProcEdible == Boolean.TRUE && isThisItemEdible != Boolean.FALSE) {
                    isThisItemEdible = Boolean.TRUE;
                } else if (isThisProcEdible == Boolean.FALSE) {
                    isThisItemEdible = Boolean.FALSE;
                }
            }
            if (isThisItemEdible == Boolean.TRUE) {
                inventoryFood.add(item);
            }
        }

        if (floorFood.size() > 0) {
            for (ItemCategory cat : ItemCategory.categories) {
                List<Entity> ents = floorFood.stream().filter(e -> Itempedia.get(e.itemTypeKey).category == cat).collect(Collectors.toList());
                if (ents.size() > 0) {
                    box.addHeader("*** " + cat.getName() + " (on floor) ***");
                }
                for (Entity ent : ents) {
                    box.addItem(ent.getVisibleNameIndefiniteOrSpecific(), ent);
                }
            }
        }
        if (inventoryFood.size() > 0) {
            for (ItemCategory cat : ItemCategory.categories) {
                List<Entity> ents = inventoryFood.stream().filter(e -> Itempedia.get(e.itemTypeKey).category == cat).collect(Collectors.toList());
                if (ents.size() > 0) {
                    box.addHeader("*** " + cat.getName() + " ***");
                }
                for (Entity ent : ents) {
                    box.addItem(ent.getVisibleNameIndefiniteOrSpecific(), ent);
                }
            }
        }
        GameLoop.dialogueBoxModule.openDialogueBox(box, Inventory::handleInventoryToEatResponse);
    }

    public static void handleInventoryToEatResponse(Object chosenEntity) {
        Entity e = (Entity)chosenEntity;
        if (e != null) {
            Game.getPlayerEntity().eatItem(e);
        }
    }

    public static void doQuaff() {
        Collection<Entity> inventory = Game.getPlayerEntity().getInventoryEntities();
        DialogueBox box = new DialogueBox()
                .withFooterClosable()
                .withTitle("Select item to drink")
                .withAllowLetters(true)
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
                box.addItem(ent.getVisibleNameIndefiniteOrSpecific(), ent);
            }
        }
        GameLoop.dialogueBoxModule.openDialogueBox(box, Inventory::handleQuaff);
    }

    public static void handleQuaff(Object chosenEntity) {
        Entity e = (Entity)chosenEntity;
        if (e != null) {
            Game.getPlayerEntity().quaffItem(e);
        }
    }

    public static void doRead() {
        Collection<Entity> inventory = Game.getPlayerEntity().getInventoryEntities();
        DialogueBox box = new DialogueBox()
                .withFooterClosable()
                .withTitle("Select item to read")
                .withAllowLetters(true)
                .withMargins(60, 60);
        for (ItemCategory cat : ItemCategory.categories) {
            List<Entity> ents = inventory.stream().filter(e -> Itempedia.get(e.itemTypeKey).category == cat).collect(Collectors.toList());
            List<Entity> readableEnts = new ArrayList<>();
            for (Entity ent : ents) {
                boolean isReadable = false;
                boolean notReadable = false;
                for (Proc p : ent.procs) {
                    Boolean readable = p.targetForRead(ent);
                    if (readable == Boolean.FALSE) {
                        notReadable = true;
                    } else if (readable == Boolean.TRUE) {
                        isReadable = true;
                    }
                }
                if (isReadable && !notReadable) {
                    readableEnts.add(ent);
                }
            }

            if (readableEnts.size() > 0) {
                box.addHeader("*** " + cat.getName() + " ***");
            }
            for (Entity ent : readableEnts) {
                box.addItem(ent.getVisibleNameIndefiniteOrSpecific(), ent);
            }
        }
        GameLoop.dialogueBoxModule.openDialogueBox(box, Inventory::handleRead);
    }

    public static void handleRead(Object chosenEntity) {
        Entity e = (Entity)chosenEntity;
        if (e != null) {
            Game.getPlayerEntity().readItem(e);
            Game.turn();
        }
    }



    public static void openInventoryToInspect() {
        Collection<Entity> inventory = Game.getPlayerEntity().getInventoryEntities();
        DialogueBox box = new DialogueBox()
                .withFooterClosable()
                .withTitle("Inventory")
                .withAllowLetters(true)
                .withMargins(60, 60);
        for (ItemCategory cat : ItemCategory.categories) {
            List<Entity> ents = inventory.stream().filter(e -> Itempedia.get(e.itemTypeKey).category == cat).collect(Collectors.toList());
            if (ents.size() > 0) {
                box.addHeader("*** " + cat.getName() + " ***");
            }
            for (Entity ent : ents) {
                box.addItem(ent.getVisibleNameIndefiniteOrSpecific(), ent);
            }
        }
        GameLoop.dialogueBoxModule.openDialogueBox(box, Inventory::handleInventoryInspectResponse);
    }

    public static void handleInventoryInspectResponse(Object chosenEntity) {
        if (chosenEntity == null) {
            return;
        }
        Entity ent = (Entity)chosenEntity;
        String description = GameEntities.describeItem(ent);

        ChatBox chatBox = new ChatBox()
                .withMargins(60, 60)
                .withTitle("` " + ent.getVisibleNameIndefiniteOrSpecific(), EntityGlyph.getGlyph(ent))
                .withText(description);

        ArrayList<ChatLink> links = new ArrayList<>();
        ChatLink linkOk = new ChatLink();
        linkOk.text = "OK";
        links.add(linkOk);

        GameLoop.chatModule.openArbitrary(chatBox, links);
    }

    public static void promptQuantity(String command, Entity entity, BiConsumer<Entity, Integer> handler) {
        promptQuantityEntity = entity;
        promptQuantityMin = 0;
        promptQuantityMax = entity.getItem().quantity;
        promptQuantityDefault = promptQuantityMax;
        promptQuantityHandler = handler;
        TextEntryBox box = new TextEntryBox()
                .withMaxLength(6)
                .withTitle(command + " how many? (default " + promptQuantityDefault + ")")
                .withMargins(60, 60)
                .autoHeight()
                .autoWidth();

        GameLoop.textEntryModule.openTextEntryBox(box, Inventory::promptQuantityResponse);
    }

    public static void promptQuantityResponse(String response) {
        int intResponse = 0;
        try {
            intResponse = Integer.parseInt(response);
        } catch (NumberFormatException e) {
            intResponse = promptQuantityDefault;
        }

        promptQuantityHandler.accept(promptQuantityEntity, Math.max(Math.min(intResponse, promptQuantityMax), promptQuantityMin));
    }

}
