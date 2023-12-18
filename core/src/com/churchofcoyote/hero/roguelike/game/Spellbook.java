package com.churchofcoyote.hero.roguelike.game;

import com.churchofcoyote.hero.GameLoop;
import com.churchofcoyote.hero.dialogue.DialogueBox;
import com.churchofcoyote.hero.roguelike.spells.Spell;
import com.churchofcoyote.hero.roguelike.spells.SpellFirebeam;
import com.churchofcoyote.hero.roguelike.spells.SpellMagicMissile;
import com.churchofcoyote.hero.roguelike.world.Element;
import com.churchofcoyote.hero.roguelike.world.Spellpedia;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Spellbook {

    public List<String> spells = new ArrayList<>();

    public void addSpell(String key) {
        spells.add(key);
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
        String format = "%-20s %-6s %-5s %-5s";
        box.addHeader(String.format("  " + format, "Name", "Type", "Range", "Cost"));
        for (Spell spell : getSpells()) {
            Map<Element, Integer> elementCost = spell.getElementCost(Game.getPlayerEntity());
            StringBuilder elementString = new StringBuilder();
            for (Element element : elementCost.keySet()) {
                for (int i=0; i<elementCost.get(element); i++) {
                    elementString.append(element.symbol);
                }
            }
            box.addItem(String.format(format,
                    spell.getName(), spell.getTypeDescription(),
                    spell.getRange(Game.getPlayerEntity()), spell.getCost(Game.getPlayerEntity()) + " " + elementString)
                , spell);
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
