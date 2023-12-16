package com.churchofcoyote.hero.roguelike.game;

import com.churchofcoyote.hero.GameLoop;
import com.churchofcoyote.hero.dialogue.DialogueBox;
import com.churchofcoyote.hero.roguelike.spells.SpellFirebeam;
import com.churchofcoyote.hero.roguelike.spells.SpellMagicMissile;
import com.churchofcoyote.hero.roguelike.world.BodyPart;
import com.churchofcoyote.hero.roguelike.world.Entity;

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
        pcEntity = Game.bestiary.create("player");
        setStats();
        setEquipment();

        handler.accept(pcEntity);
    }

    private void setStats() {
        switch (race) {
            case "human":
                pcEntity.statblock.str = 10;
                pcEntity.statblock.tou = 10;
                pcEntity.statblock.dex = 10;
                pcEntity.statblock.agi = 10;
                pcEntity.statblock.per = 10;
                pcEntity.statblock.wil = 10;
                pcEntity.statblock.arc = 10;
                pcEntity.statblock.ava = 10;
                break;
        }

        switch (archetype) {
            case "warrior":
                pcEntity.statblock.str += 6;
                pcEntity.statblock.tou += 6;
                pcEntity.statblock.dex += 4;
                pcEntity.statblock.agi += 4;
                pcEntity.statblock.wil += 4;
                pcEntity.statblock.arc -= 2;
                break;
            case "archer":
                pcEntity.statblock.tou += 2;
                pcEntity.statblock.dex += 6;
                pcEntity.statblock.agi += 6;
                pcEntity.statblock.per += 6;
                break;
            case "wizard":
                pcEntity.statblock.str -= 2;
                pcEntity.statblock.agi -= 2;
                pcEntity.statblock.wil += 4;
                pcEntity.statblock.arc += 6;
        }
    }

    private void setEquipment() {
        Entity temp;
        switch (archetype) {
            case "warrior":
                equip("armor.body.chain", BodyPart.TORSO);
                equip("armor.head.leatherhat", BodyPart.HEAD);
                equip("longsword", BodyPart.PRIMARY_HAND);
                break;
            case "archer":
                equip("armor.body.leather", BodyPart.TORSO);
                equip("short sword", BodyPart.PRIMARY_HAND);
                equip("shortbow", BodyPart.RANGED_WEAPON);
                equip("weapon.ammo.arrow", BodyPart.RANGED_AMMO, 30);
                break;
            case "wizard":
                equip("armor.body.whiterobe", BodyPart.TORSO);
                equip("dagger", BodyPart.PRIMARY_HAND);
                GameLoop.roguelikeModule.game.spellbook.addSpell("magic missile");
                GameLoop.roguelikeModule.game.spellbook.addSpell("firebeam");
                break;
        }
        Entity magicmap = Game.itempedia.create("scroll.magic.map", 100);
        pcEntity.receiveItem(magicmap);
    }

    private void equip(String key, BodyPart bodyPart) {
        equip(key, bodyPart, 1);
    }

    private void equip(String key, BodyPart bodyPart, int quantity) {
        pcEntity.equip(Game.itempedia.create(key, quantity), bodyPart);
    }


}
