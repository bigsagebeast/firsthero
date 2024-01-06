package com.bigsagebeast.hero.roguelike.game;

import com.bigsagebeast.hero.GameLoop;
import com.bigsagebeast.hero.dialogue.DialogueBox;
import com.bigsagebeast.hero.roguelike.world.BodyPart;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.Itempedia;

import java.util.function.Consumer;

public class CharacterBuilder {
    public String race;
    public String archetype;
    public Consumer<Entity> handler;
    public Entity pcEntity;

    public CharacterBuilder(Consumer<Entity> handler) {
        this.handler = handler;
    }

    public void begin() {
        selectRace();
    }

    private void selectRace() {
        DialogueBox box = new DialogueBox()
                .withCancelable(false)
                .withMargins(60, 60)
                .withTitle("Select a race");
        box.addItem("Human     Well-rounded", "human");
        box.autoHeight();
        GameLoop.dialogueBoxModule.openDialogueBox(box, this::handleSelectRace);
    }

    private void handleSelectRace(Object val) {
        race = (String)val;
        selectArchetype();
    }

    private void selectArchetype() {
        DialogueBox box = new DialogueBox()
                .withCancelable(false)
                .withMargins(60, 60)
                .withTitle("Select an archetype");
        box.addItem("Warrior   High strength, armored", "warrior");
        box.addItem("Archer    Quick, armed with ranged weapons", "archer");
        box.addItem("Wizard    Weak, has starting spells", "wizard");
        box.autoHeight();
        GameLoop.dialogueBoxModule.openDialogueBox(box, this::handleSelectArchetype);
    }

    private void handleSelectArchetype(Object val) {
        archetype = (String)val;
        finish();
    }

    private void finish() {
        pcEntity = Game.bestiary.create("pc.avatar");
        setStats(pcEntity, race, archetype);
        setEquipment(pcEntity, race, archetype);

        handler.accept(pcEntity);
    }

    public static void setStats(Entity entity, String race, String archetype) {
        switch (race) {
            case "human":
                entity.statblock.str = 10;
                entity.statblock.tou = 10;
                entity.statblock.dex = 10;
                entity.statblock.agi = 10;
                entity.statblock.per = 10;
                entity.statblock.wil = 10;
                entity.statblock.arc = 10;
                entity.statblock.ava = 10;
                break;
        }

        switch (archetype) {
            case "warrior":
                entity.statblock.str += 6;
                entity.statblock.tou += 6;
                entity.statblock.dex += 4;
                entity.statblock.agi += 4;
                entity.statblock.wil += 4;
                entity.statblock.arc -= 2;
                break;
            case "archer":
                entity.statblock.tou += 2;
                entity.statblock.dex += 6;
                entity.statblock.agi += 6;
                entity.statblock.per += 6;
                break;
            case "wizard":
                entity.statblock.str -= 2;
                entity.statblock.agi -= 2;
                entity.statblock.wil += 4;
                entity.statblock.arc += 6;
        }
        entity.recalculateSecondaryStats();
    }

    public static void setEquipment(Entity entity, String race, String archetype) {
        switch (archetype) {
            case "warrior":
                equip(entity, "armor.body.chain", BodyPart.TORSO);
                equip(entity, "armor.head.leatherhat", BodyPart.HEAD);
                equip(entity, "longsword", BodyPart.PRIMARY_HAND);
                break;
            case "archer":
                equip(entity, "armor.body.leather", BodyPart.TORSO);
                equip(entity, "short sword", BodyPart.PRIMARY_HAND);
                equip(entity, "shortbow", BodyPart.RANGED_WEAPON);
                equip(entity, "weapon.ammo.arrow", BodyPart.RANGED_AMMO, 30);
                break;
            case "wizard":
                equip(entity, "armor.body.whiterobe", BodyPart.TORSO);
                equip(entity, "dagger", BodyPart.PRIMARY_HAND);
                equip(entity, "ring.charge.lightning", BodyPart.RIGHT_RING);
                Game.spellbook.addSpell("magic missile");
                Game.spellbook.addSpell("firebeam");
                Game.spellbook.addSpell("water blast");
                Game.spellbook.addSpell("root spear");
                break;
        }
        Game.spellbook.addSpell("divine banish");
        Game.spellbook.addSpell("divine healing");
        Game.spellbook.addSpell("divine time stop");
    }

    private static void equip(Entity entity, String key, BodyPart bodyPart) {
        equip(entity, key, bodyPart, 1);
    }

    private static void equip(Entity entity, String key, BodyPart bodyPart, int quantity) {
        Entity equipment = Itempedia.create(key, quantity);
        entity.equip(equipment, bodyPart);
        equipment.identifyItemFully();
    }
}
