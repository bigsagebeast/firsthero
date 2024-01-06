package com.bigsagebeast.hero.roguelike.world.proc.intrinsic;

import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.BodyPart;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.proc.Proc;
import com.bigsagebeast.hero.roguelike.world.proc.effect.ProcEffectTimedTelepathy;

import java.util.Arrays;
import java.util.List;

public class ProcTelepathy extends Proc {
    @Override
    public void postBeEquipped(Entity entity, BodyPart bp, Entity actor) {
        List<Proc> telepathyProcs = actor.getProcByTypeIncludingEquipment(
                Arrays.asList(ProcEffectTimedTelepathy.class, ProcTelepathy.class)
        );
        telepathyProcs.remove(this);
        if (telepathyProcs.isEmpty()) {
            Game.announceVis(actor, null, "Your mind expands.", null, null, null);
        }
    }

    @Override
    public void postBeUnequipped(Entity entity, BodyPart bp, Entity actor) {
        List<Proc> telepathyProcs = actor.getProcByTypeIncludingEquipment(
                Arrays.asList(ProcEffectTimedTelepathy.class, ProcTelepathy.class)
        );
        telepathyProcs.remove(this);
        if (telepathyProcs.isEmpty()) {
            Game.announceVis(actor, null, "Your mind no longer feels so expanded.", null, null, null);
        }
    }

}
