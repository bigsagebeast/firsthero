package com.bigsagebeast.hero.module;

import com.bigsagebeast.hero.GameLoop;
import com.bigsagebeast.hero.GameState;
import com.bigsagebeast.hero.Graphics;
import com.bigsagebeast.hero.GraphicsState;
import com.bigsagebeast.hero.dialogue.DialogueBox;
import com.bigsagebeast.hero.dialogue.TextEntryBox;
import com.bigsagebeast.hero.roguelike.game.CharacterBuilder;
import com.bigsagebeast.hero.roguelike.game.CombatLogic;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.game.GameSpecials;
import com.bigsagebeast.hero.roguelike.world.Bestiary;
import com.bigsagebeast.hero.roguelike.world.BodyPart;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.Phenotype;

public class DuelModule extends Module {
    private Entity duelistOne;
    private Entity duelistTwo;

    @Override
    public void start() {
        super.start();
        Game.loadFiles();
        DialogueBox box = new DialogueBox()
                .withCancelable(true)
                .withMargins(60, 60)
                .withAllowLetters(true)
                .withTitle("Select a player archetype, space to stop testing");
        box.addItem("Warrior   High strength, armored", "warrior");
        box.addItem("Archer    Quick, armed with ranged weapons", "archer");
        box.addItem("Wizard    Weak, has starting spells", "wizard");
        box.autoHeight();
        GameLoop.dialogueBoxModule.openDialogueBox(box, this::handleChooseDuelistOne);
    }

    private void handleChooseDuelistOne(Object chosenObject) {
        if (chosenObject == null) {
            end();
            GameLoop.titleModule.start();
            return;
        }
        String archetype = (String)chosenObject;
        if (duelistOne == null) {
            duelistOne = Bestiary.create("pc.avatar");
            CharacterBuilder.setStats(duelistOne, "human", archetype);
            CharacterBuilder.setEquipment(duelistOne, "human", archetype);
        }

        TextEntryBox box = new TextEntryBox()
                .withTitle("Player level?")
                .withMargins(60, 60)
                .withMaxLength(40)
                .autoHeight();
        GameLoop.textEntryModule.openTextEntryBox(box, this::handleChoosePlayerLevel);
    }

    private void handleChoosePlayerLevel(Object chosenObject) {
        String intString = (String)chosenObject;
        int level = 1;
        try {
            level = Integer.valueOf(intString);
        } catch (NumberFormatException e) {}
        duelistOne.level = level;
        duelistOne.recalculateSecondaryStats();

        TextEntryBox box = new TextEntryBox()
                .withTitle("Choose an enemy")
                .withMargins(60, 60)
                .withMaxLength(40)
                .autoHeight();
        GameLoop.textEntryModule.openTextEntryBox(box, this::handleChooseDuelistTwo);
    }

    private void handleChooseDuelistTwo(String chosenKey) {
        if (Bestiary.map.containsKey(chosenKey)) {
            duelistTwo = Bestiary.create(chosenKey);
        } else {
            for (String key : Bestiary.map.keySet()) {
                Phenotype p = Bestiary.get(key);
                if (p.name != null && p.name.equalsIgnoreCase(chosenKey)) {
                    duelistTwo = Bestiary.create(key);
                }
            }
        }
        if (duelistTwo == null) {
            System.out.println("I don't know that entity.");
            handleChooseDuelistOne(null);
            return;
        }
        startDuel();
    }

    private void startDuel() {
        System.out.println(Bestiary.get(duelistOne.phenotypeName).name + " vs " + Bestiary.get(duelistTwo.phenotypeName).name + ": Fight!");
        int[] bucket = new int[12];
        for (int i=0; i<1000; i++) {
            int result = runOneDuel();
            bucket[result]++;
        }

        DialogueBox box = new DialogueBox()
                .withCancelable(true)
                .withMargins(60, 60)
                .withTitle("Duel results for PC");
        box.addHeader("Versus " + Bestiary.get(duelistTwo.phenotypeName).name);
        box.addHeader("");
        box.addItem("Dead    " + bucket[0], null);
        for (int i=1; i<10; i++) {
            box.addItem("<" + (i * 10) + "%    " + bucket[i], null);
        }
        box.addItem("<100%   " + bucket[10], null);
        box.addItem(" 100%   " + bucket[11], null);
        box.autoHeight();
        GameLoop.dialogueBoxModule.openDialogueBox(box, this::handleRestart);
    }

    private void handleRestart(Object dummy) {
        duelistOne = null;
        start();
    }

    private int runOneDuel() {
        duelistOne.hitPoints = duelistOne.maxHitPoints;
        duelistTwo.hitPoints = duelistTwo.maxHitPoints;

        if (duelistOne.body.getEquipment(BodyPart.RANGED_WEAPON) != null) {
            for (int i=0; i<4; i++) {
                CombatLogic.shoot(duelistOne, duelistTwo,
                        duelistOne.body.getEquipment(BodyPart.RANGED_WEAPON),
                        duelistOne.body.getEquipment(BodyPart.RANGED_AMMO));
            }
        }

        while (duelistOne.hitPoints > 0 && duelistTwo.hitPoints > 0) {
            Game.attack(duelistOne, duelistTwo);
            if (duelistTwo.hitPoints > 0) {
                Game.attack(duelistTwo, duelistOne);
            }
        }
        if (duelistOne.hitPoints == 0) {
            return 0;
        } else {
            return 1 + (duelistOne.hitPoints * 10 / duelistOne.maxHitPoints);
        }
    }

    @Override
    public void update(GameState state) {
    }

    @Override
    public void render(Graphics g, GraphicsState gState) {

    }
}
