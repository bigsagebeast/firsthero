package com.churchofcoyote.hero.roguelike.game;

import com.churchofcoyote.hero.GameLoop;
import com.churchofcoyote.hero.dialogue.DialogueBox;
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
        Entity pc = Game.bestiary.create("player");
        Entity dagger = Game.itempedia.create("dagger");
        pc.equip(dagger, BodyPart.PRIMARY_HAND);
        Entity shortbow = Game.itempedia.create("shortbow");
        pc.equip(shortbow, BodyPart.RANGED_WEAPON);
        Entity arrow = Game.itempedia.create("arrow", 100);
        pc.equip(arrow, BodyPart.RANGED_AMMO);
        Entity magicmap = Game.itempedia.create("scroll.magic.map", 100);
        pc.receiveItem(magicmap);

        handler.accept(pc);
    }



}
