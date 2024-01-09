package com.bigsagebeast.hero.roguelike.world.ai;

import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.proc.ProcMover;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.EntityTracker;

public class TacticRangedAmmoThenMelee extends Tactic {

	int ammo;

	public TacticRangedAmmoThenMelee(int ammo) {
		this.ammo = ammo;
	}

	@Override
	public boolean execute(Entity e, ProcMover pm) {
		Entity target = null;
		if (ammo > 0 && pm.targetEntityId != EntityTracker.NONE) {
			target = EntityTracker.get(pm.targetEntityId);
		}
		// TODO range for equipped weapons, too
		float distance = -1;
		if (target != null) {
			distance = e.pos.distance(target.pos);
		}
		if (target != null && e.canSee(target) && distance < e.naturalRangedWeaponRange) {
			// maybe stand there instead of shooting
			if (Math.random() < 0.25) {
				pm.setDelay(e, e.getMoveCost());
				return true;
			}
			ammo--;
			Game.npcShoot(e, target.pos);
			pm.setDelay(e, e.getMoveCost());
			return true;
		}

		// fall back to chase and melee
		if (pm.targetEntityId == EntityTracker.NONE) {
			if (lastSeen != null) {
				if (!huntLastSeen(e, pm)) {
					return idle(e, pm);
				}
			}
			else {
				return idle(e, pm);
			}
		} else {
			return chaseSeenPlayer(e, pm);
		}
		return false;
	}
}
