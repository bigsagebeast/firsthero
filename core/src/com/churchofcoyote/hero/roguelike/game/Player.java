package com.churchofcoyote.hero.roguelike.game;

import com.churchofcoyote.hero.roguelike.world.Element;
import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.roguelike.world.EntityTracker;

import java.util.HashMap;

public class Player {
	private int entityId;

	public HashMap<Element, Integer> currentElementCharges = new HashMap<>();
	public HashMap<Element, Integer> maxElementCharges = new HashMap<>();

	public Player() {
		maxElementCharges.put(Element.WATER, 8);
		maxElementCharges.put(Element.LIGHTNING, 8);
		maxElementCharges.put(Element.FIRE, 8);
		maxElementCharges.put(Element.PLANT, 8);
		currentElementCharges.put(Element.WATER, 4);
		currentElementCharges.put(Element.LIGHTNING, 4);
		currentElementCharges.put(Element.FIRE, 4);
		currentElementCharges.put(Element.PLANT, 4);
	}

	public boolean areElementsFull() {
		for (Element key : maxElementCharges.keySet()) {
			if (!isElementFull(key)) {
				return false;
			}
		}
		return true;
	}

	public boolean isElementFull(Element element) {
		if (!maxElementCharges.containsKey(element)) {
			return true;
		}
		return elementMissing(element) == 0;
	}

	public int elementMissing(Element element) {
		if (!maxElementCharges.containsKey(element)) {
			return 0;
		}
		return maxElementCharges.get(element) - currentElementCharges.get(element);
	}

	public void changeCharges(Element element, int change) {
		currentElementCharges.put(element, currentElementCharges.get(element) + change);
	}

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
