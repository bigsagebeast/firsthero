package com.churchofcoyote.hero.roguelike.game;

import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.roguelike.world.EntityTracker;

public class Player {
	private int entityId;

	public void setEntityId(int entityId) {
		this.entityId = entityId;
	}

	public Entity getEntity() {
		return EntityTracker.get(entityId);
	}

	public boolean isEntity(Entity e) {
		return entityId == e.entityId;
	}
}
