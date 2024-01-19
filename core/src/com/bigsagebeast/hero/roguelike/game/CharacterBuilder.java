package com.bigsagebeast.hero.roguelike.game;

import com.bigsagebeast.hero.GameLoop;
import com.bigsagebeast.hero.dialogue.DialogueBox;
import com.bigsagebeast.hero.enums.Stat;
import com.bigsagebeast.hero.roguelike.world.*;

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
        Game.spellbook.spells.clear();
        Game.resetPlayer();
        selectRace();
    }

    private void selectRace() {
        DialogueBox box = new DialogueBox()
                .withCancelable(false)
                .withMargins(60, 60)
                .withAllowLetters(true)
                .withTitle("Select a race");
        box.addItem("Human     Well-rounded", "human");
        box.addItem("Dwarf     Strong but slow", "dwarf");
        box.addItem("Elf       Agile but weaker", "elf");
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
                .withAllowLetters(true)
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
        EntityTracker.cleanUp();
        pcEntity = Bestiary.create("pc.avatar");
        setStats(pcEntity, race, archetype);
        setEquipment(pcEntity, race, archetype);

        handler.accept(pcEntity);
    }

    public static void setStats(Entity entity, String race, String archetype) {
        switch (race) {
            case "human":
                entity.statblock.set(Stat.STRENGTH, 10);
                entity.statblock.set(Stat.TOUGHNESS, 10);
                entity.statblock.set(Stat.DEXTERITY, 10);
                entity.statblock.set(Stat.AGILITY, 10);
                entity.statblock.set(Stat.PERCEPTION, 10);
                entity.statblock.set(Stat.WILLPOWER, 10);
                entity.statblock.set(Stat.ARCANUM, 10);
                entity.statblock.set(Stat.AVATAR, 10);
                break;
            case "dwarf":
                entity.statblock.set(Stat.STRENGTH, 12);
                entity.statblock.set(Stat.TOUGHNESS, 12);
                entity.statblock.set(Stat.DEXTERITY, 8);
                entity.statblock.set(Stat.AGILITY, 6);
                entity.statblock.set(Stat.PERCEPTION, 10);
                entity.statblock.set(Stat.WILLPOWER, 12);
                entity.statblock.set(Stat.ARCANUM, 10);
                entity.statblock.set(Stat.AVATAR, 10);
                break;
            case "elf":
                entity.statblock.set(Stat.STRENGTH, 8);
                entity.statblock.set(Stat.TOUGHNESS, 6);
                entity.statblock.set(Stat.DEXTERITY, 12);
                entity.statblock.set(Stat.AGILITY, 12);
                entity.statblock.set(Stat.PERCEPTION, 10);
                entity.statblock.set(Stat.WILLPOWER, 10);
                entity.statblock.set(Stat.ARCANUM, 12);
                entity.statblock.set(Stat.AVATAR, 10);
                break;
        }

        switch (archetype) {
            case "warrior":
                entity.statblock.change(Stat.STRENGTH, 4, true);
                entity.statblock.change(Stat.TOUGHNESS, 6, true);
                //entity.statblock.change(Stat.DEXTERITY, 2);
                //entity.statblock.change(Stat.AGILITY, 4);
                //entity.statblock.change(Stat.WILLPOWER, 4);
                entity.statblock.change(Stat.ARCANUM, -2, true);
                break;
            case "archer":
                entity.statblock.change(Stat.TOUGHNESS, 2, true);
                entity.statblock.change(Stat.AGILITY, 2, true);
                entity.statblock.change(Stat.DEXTERITY, 4, true);
                entity.statblock.change(Stat.WILLPOWER, -2, true);
                entity.statblock.change(Stat.ARCANUM, -2, true);
                entity.statblock.change(Stat.PERCEPTION, 4, true);
                break;
            case "wizard":
                entity.statblock.change(Stat.STRENGTH, -2, true);
                entity.statblock.change(Stat.AGILITY, -2, true);
                entity.statblock.change(Stat.WILLPOWER, 6, true);
                entity.statblock.change(Stat.ARCANUM, 6, true);
        }
        entity.recalculateSecondaryStats();
    }

    public static void setEquipment(Entity entity, String race, String archetype) {
        switch (archetype) {
            case "warrior":
                equip(entity, "armor.body.chain", BodyPart.TORSO);
                equip(entity, "armor.head.leatherhat", BodyPart.HEAD);
                equip(entity, "weapon.melee.shortsword", BodyPart.PRIMARY_HAND);
                break;
            case "archer":
                equip(entity, "armor.body.leather", BodyPart.TORSO);
                equip(entity, "weapon.melee.shortsword", BodyPart.PRIMARY_HAND);
                equip(entity, "weapon.ranged.shortbow", BodyPart.RANGED_WEAPON);
                equip(entity, "weapon.ammo.arrow", BodyPart.RANGED_AMMO, 30);
                break;
            case "wizard":
                equip(entity, "armor.body.whiterobe", BodyPart.TORSO);
                equip(entity, "weapon.melee.dagger", BodyPart.PRIMARY_HAND);
                equip(entity, "ring.charge.lightning", BodyPart.RIGHT_RING);
                Game.spellbook.addSpell("magic missile");
                Game.spellbook.addSpell("firebeam");
                Game.spellbook.addSpell("water blast");
                Game.spellbook.addSpell("root spear");
                Game.spellbook.addSpell("shocking grasp");
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
        equipment.silentIdentifyItemFully();
    }
}
