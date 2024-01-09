package com.bigsagebeast.hero.roguelike.game;

import com.bigsagebeast.hero.GameLoop;
import com.bigsagebeast.hero.roguelike.spells.Spell;
import com.bigsagebeast.hero.roguelike.world.*;
import com.bigsagebeast.hero.dialogue.DialogueBox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Spellbook {
    public List<String> spells = new ArrayList<>();

    public void addSpell(String key) {
        if (spells.contains(key)) {
            return;
        }
        spells.add(key);
        Collections.sort(spells);

        // ensure that the spellbook is identified, even if the player learned the spell some other way
        for (ItemType it : Itempedia.map.values()) {
            LoadProc procLoader = it.procLoaders.stream().filter(pl -> pl.procName.equals("item.book.ProcBookSpell")).findAny().orElse(null);
            if (procLoader != null) {
                String spell = procLoader.fields.get("spell");
                if (spell.equals(key)) {
                    it.identified = true;
                }
            }
        }
    }

    public boolean hasSpell(String key) {
        return spells.contains(key);
    }

    public List<Spell> getSpells() {
        return spells.stream().map(k -> Spellpedia.get(k)).collect(Collectors.toList());
    }

    public void openSpellbookToCast() {
        if (spells.isEmpty()) {
            Game.announce("You don't know any spells.");
            return;
        }
        DialogueBox box = new DialogueBox()
                .withFooterClosable()
                .withTitle("Select spell to cast")
                .withMargins(60, 60);
        String format = "%-15s %-8s %-5s %-5s";
        box.addHeader(String.format("  " + format, "Name", "Type", "Range", "Cost"));
        for (Spell.SpellType type : Spell.SpellType.values()) {
            List<Spell> spellsOfType = getSpells().stream().filter(s -> s.getSpellType() == type).collect(Collectors.toList());
            if (!spellsOfType.isEmpty()) {
                box.addHeader(type.name());
                for (Spell spell : spellsOfType) {
                    Map<Element, Integer> elementCost = spell.getElementCost(Game.getPlayerEntity());
                    StringBuilder elementString = new StringBuilder();
                    for (Element element : elementCost.keySet()) {
                        for (int i = 0; i < elementCost.get(element); i++) {
                            elementString.append(element.symbol);
                        }
                    }
                    box.addItem(String.format(format,
                                    spell.getName(), spell.getTypeDescription(),
                                    spell.getRange(Game.getPlayerEntity()), spell.getCost(Game.getPlayerEntity()) + " " + elementString)
                            , spell);
                }
            }
        }
        GameLoop.dialogueBoxModule.openDialogueBox(box, this::handleSpellbookToCastResponse);
    }

    public void handleSpellbookToCastResponse(Object chosenSpell) {
        Spell spell = (Spell)chosenSpell;
        if (spell != null) {
            spell.playerStartSpell();
        }
    }

}
