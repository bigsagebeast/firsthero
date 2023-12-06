package com.churchofcoyote.hero.roguelike.game;

import com.churchofcoyote.hero.roguelike.world.Bestiary;
import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.roguelike.world.EntityTracker;
import com.churchofcoyote.hero.roguelike.world.Phenotype;
import com.churchofcoyote.hero.roguelike.world.dungeon.Level;
import com.churchofcoyote.hero.roguelike.world.proc.item.ProcCorpse;
import com.churchofcoyote.hero.roguelike.world.proc.item.ProcItem;

public class MoverLogic {
    public static void createCorpse(Level level, Entity entity) {
        Phenotype phenotype = Bestiary.get(entity.phenotypeName);
        if (Game.random.nextInt(100) >= phenotype.corpseSpawnPercent) {
            return;
        }
        Entity newCorpse = Game.itempedia.create("misc.corpse");
        newCorpse.name = phenotype.name + " corpse";
        newCorpse.palette = phenotype.corpseSpawnColors;
        newCorpse.addProc(new ProcCorpse());
        newCorpse.addProc(new ProcItem());
        level.addEntityWithStacking(newCorpse, entity.pos);
    }
}
