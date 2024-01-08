package com.bigsagebeast.hero.roguelike.world.proc.effect;

import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.proc.Proc;
import com.bigsagebeast.hero.roguelike.world.proc.intrinsic.ProcTelepathy;

import java.util.Arrays;
import java.util.List;

public class ProcEffectTimedTelepathy extends ProcTimedEffect {
    @Override
    public void initialize(Entity entity) {
        Entity user = entity.getTopLevelContainer();
        if (user == null) {
            return;
        }
        List<Proc> telepathyProcs = user.getProcByTypeIncludingEquipment(
                Arrays.asList(ProcEffectTimedTelepathy.class, ProcTelepathy.class)
        );
        telepathyProcs.remove(this);
        if (telepathyProcs.isEmpty()) {
            Game.announceVis(user, null, "Your mind expands.", null, null, null);
        }
    }

    @Override
    public void expire(Entity entity) {
        Entity user = entity.getTopLevelContainer();
        if (user == null) {
            return;
        }
        List<Proc> telepathyProcs = user.getProcByTypeIncludingEquipment(
                Arrays.asList(ProcEffectTimedTelepathy.class, ProcTelepathy.class)
        );
        telepathyProcs.remove(this);
        if (telepathyProcs.isEmpty()) {
            Game.announceVis(user, null, "Your mind no longer feels so expanded.", null, null, null);
        }
    }
}