package com.bigsagebeast.hero.roguelike.world.proc;

import com.bigsagebeast.hero.roguelike.world.Entity;

import java.util.concurrent.atomic.AtomicReference;

public class ProcEveryTurn extends Proc {

    private float hpAccumulator;
    private float spAccumulator;
    private float dpAccumulator;

    public ProcEveryTurn() { super(); }
    @Override
    public void turnPassed(Entity entity) {
        if (entity.getMover() != null) {
            AtomicReference<Float> hpMultiplierAccumulator = new AtomicReference<>(1.0f);
            entity.forEachProcIncludingEquipment((e, p) -> hpMultiplierAccumulator.updateAndGet(val -> val *= p.getRegenHpMultiplier(e, entity)));
            hpAccumulator += entity.maxHitPoints * hpMultiplierAccumulator.get() / 400.0f;
            while (hpAccumulator >= 1.0f) {
                hpAccumulator--;
                entity.hitPoints = Math.min(entity.hitPoints + 1, entity.maxHitPoints);
            }

            AtomicReference<Float> spMultiplierAccumulator = new AtomicReference<>(1.0f);
            entity.forEachProcIncludingEquipment((e, p) -> spMultiplierAccumulator.updateAndGet(val -> val *= p.getRegenSpMultiplier(e, entity)));
            spAccumulator += entity.maxSpellPoints * spMultiplierAccumulator.get() / 300.0f;
            while (spAccumulator >= 1.0f) {
                spAccumulator--;
                entity.spellPoints = Math.min(entity.spellPoints + 1, entity.maxSpellPoints);
            }

            AtomicReference<Float> dpMultiplierAccumulator = new AtomicReference<>(1.0f);
            entity.forEachProcIncludingEquipment((e, p) -> dpMultiplierAccumulator.updateAndGet(val -> val *= p.getRegenDpMultiplier(e, entity)));
            dpAccumulator += entity.maxDivinePoints * dpMultiplierAccumulator.get() / 500.0f;
            while (dpAccumulator >= 1.0f) {
                dpAccumulator--;
                entity.divinePoints = Math.min(entity.divinePoints + 1, entity.maxDivinePoints);
            }
        }
    }
}
