package com.bigsagebeast.hero.roguelike.game;

import com.bigsagebeast.hero.roguelike.world.Element;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.EntityTracker;
import com.bigsagebeast.hero.enums.Satiation;

import java.util.HashMap;

public class Player {
	private int entityId = -1;

	public HashMap<Element, Integer> currentElementCharges = new HashMap<>();
	public HashMap<Element, Integer> maxElementCharges = new HashMap<>();
	public float satiation = Satiation.startingSatiation;

	public Player() {
		maxElementCharges.put(Element.WATER, 4);
		maxElementCharges.put(Element.LIGHTNING, 4);
		maxElementCharges.put(Element.FIRE, 4);
		maxElementCharges.put(Element.NATURAE, 4);
		currentElementCharges.put(Element.WATER, 2);
		currentElementCharges.put(Element.LIGHTNING, 2);
		currentElementCharges.put(Element.FIRE, 2);
		currentElementCharges.put(Element.NATURAE, 2);
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
		currentElementCharges.put(element, Math.min(currentElementCharges.get(element) + change, maxElementCharges.get(element)));
	}

	public void fillCharges(Element element) {
		currentElementCharges.put(element, maxElementCharges.get(element));
	}

	public Satiation getSatiationStatus() {
		return Satiation.getStatus(satiation);
	}

	public void changeSatiation(float delta) {
		// TODO death
		Satiation before = getSatiationStatus();
		satiation += delta;
		if (satiation < Satiation.DEAD.topThreshold) {
			satiation = Satiation.DEAD.topThreshold;
		}
		Satiation after = getSatiationStatus();
		if (before != after) {
			Game.announce(after.message);
			// TODO update stat window
			// TODO update a proc on the player
		}
	}

	public void setEntityId(int entityId) {
		this.entityId = entityId;
	}

	public Entity getEntity() {
		if (entityId >= 0) {
			return EntityTracker.get(entityId);
		} else {
			return null;
		}
	}

	public boolean isEntity(Entity e) {
		return entityId == e.entityId;
	}



	public void gainStatElement(Element element, int num, int max) {
		int maxCharges = maxElementCharges.get(element);
		if (maxCharges < max) {
			int newMax = Math.min(max, maxCharges + num);
			maxElementCharges.put(element, newMax);
			switch (element) {
				case FIRE:
					Game.announce("You feel hot! Your fire charges increase.");
					break;
				case WATER:
					Game.announce("Your mind flows! Your water charges increase.");
					break;
				case LIGHTNING:
					Game.announce("You feel tingly! Your lightning charges increase.");
					break;
				case NATURAE:
					Game.announce("You feel rooted! Your naturae charges increase.");
					break;
			}
		} else {
			switch (element) {
				case FIRE:
					Game.announce("You feel warm.");
					break;
				case WATER:
					Game.announce("You feel a gentle flow.");
					break;
				case LIGHTNING:
					Game.announce("You feel a mild tingle.");
					break;
				case NATURAE:
					Game.announce("Your feet feel steady.");
					break;
			}
		}
		fillCharges(element);

	}
}
