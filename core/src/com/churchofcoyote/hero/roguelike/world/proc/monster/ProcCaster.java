package com.churchofcoyote.hero.roguelike.world.proc.monster;

import com.churchofcoyote.hero.roguelike.spells.Spell;
import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.roguelike.world.Spellpedia;
import com.churchofcoyote.hero.roguelike.world.proc.Proc;

import java.util.ArrayList;
import java.util.List;

public class ProcCaster extends Proc {
    public String[] spellNames;
    public ArrayList<Spell> spells;

    public ProcCaster(String[] spellNames) {
        this.spellNames = spellNames;
    }

    public List<Spell> getSpells(Entity entity) {
        if (spells == null) {
            if (spellNames == null) {
                throw new RuntimeException("Spells not initialized for " + entity.name);
            }
            spells = new ArrayList<>();
            for (String name : spellNames) {
                spells.add(Spellpedia.get(name));
            }
        }
        return spells;
    }
}
