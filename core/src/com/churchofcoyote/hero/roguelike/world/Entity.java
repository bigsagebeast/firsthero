package com.churchofcoyote.hero.roguelike.world;

import com.churchofcoyote.hero.engine.asciitile.Glyph;
import com.churchofcoyote.hero.roguelike.game.Player;
import com.churchofcoyote.hero.roguelike.game.Rank;
import com.churchofcoyote.hero.roguelike.world.ai.Strategy;
import com.churchofcoyote.hero.roguelike.world.proc.ProcEntity;
import com.churchofcoyote.hero.roguelike.world.proc.ProcMover;
import com.churchofcoyote.hero.util.Point;

import java.util.ArrayList;
import java.util.List;

public class Entity {

    public String name;
    public Glyph glyph;
    public Point pos;

    public Strategy strategy;

    public List<ProcEntity> procs = new ArrayList<>();

    // schedule for removal?
    public boolean dead;

    // combat stats
    public int hitPoints;
    public int spellPoints;
    public int divinePoints;
    public int maxHitPoints;
    public int maxSpellPoints;
    public int maxDivinePoints;

    public Phenotype phenotype;

    public Rank stats = Rank.C;

    public void addProc(ProcEntity proc)
    {
        procs.add(proc);
    }

    public String getVisibleName(Player p) {
        return name;
    }

    public ProcMover getMover() {
        for (ProcEntity pe : procs) {
           if (pe instanceof ProcMover) {
               return (ProcMover)pe;
           }
        }
        return null;
    }

}
