package com.churchofcoyote.hero.roguelike.game;

import com.churchofcoyote.hero.GameLoop;
import com.churchofcoyote.hero.dialogue.DialogueBox;
import com.churchofcoyote.hero.roguelike.spells.Spell;
import com.churchofcoyote.hero.roguelike.spells.SpellFirebeam;
import com.churchofcoyote.hero.roguelike.spells.SpellMagicMissile;

import java.util.ArrayList;
import java.util.List;

public class Spellbook {

    public List<Spell> getSpells() {
        ArrayList list = new ArrayList();
        list.add(new SpellMagicMissile());
        list.add(new SpellFirebeam());
        return list;
    }

    public void openSpellbookToCast() {
        DialogueBox box = new DialogueBox()
                .withFooterClosable()
                .withTitle("Select spell to cast")
                .withMargins(60, 60);
        String format = "%-20s %-6s %-5s %-5s";
        box.addHeader(String.format(format, "Name", "Type", "Range", "Cost"));
        for (Spell spell : getSpells()) {
            box.addItem(String.format(
                    spell.getName(), spell.getTypeDescription(),
                    spell.getRange(Game.getPlayerEntity()), spell.getCost(Game.getPlayerEntity()))
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
