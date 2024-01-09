package com.bigsagebeast.hero.roguelike.game;

import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.proc.item.ProcCorpse;
import com.bigsagebeast.hero.roguelike.world.proc.item.ProcItem;
import com.bigsagebeast.hero.roguelike.world.Bestiary;
import com.bigsagebeast.hero.roguelike.world.Phenotype;
import com.bigsagebeast.hero.roguelike.world.dungeon.Level;

public class MoverLogic {
    public static void createCorpse(Level level, Entity entity) {
        if (entity.summoned) {
            return;
        }
        Phenotype phenotype = Bestiary.get(entity.phenotypeName);
        if (Game.random.nextInt(100) >= phenotype.corpseSpawnPercent) {
            return;
        }
        Entity newCorpse = Game.itempedia.create("misc.corpse");
        newCorpse.name = phenotype.name + " corpse";
        newCorpse.palette = phenotype.corpseSpawnColors;
        ProcCorpse procCorpse = new ProcCorpse(phenotype.corpseMessage, phenotype.corpseMethod, phenotype.corpseMethodPre);
        procCorpse.satiation = phenotype.size.corpseSatiation;
        newCorpse.addProc(procCorpse);
        newCorpse.addProc(new ProcItem());
        level.addEntityWithStacking(newCorpse, entity.pos);
    }
}
