package com.bigsagebeast.hero.roguelike.game;

import com.bigsagebeast.hero.GameLoop;
import com.bigsagebeast.hero.dialogue.DialogueBox;
import com.bigsagebeast.hero.enums.Stat;
import com.bigsagebeast.hero.roguelike.world.Element;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.EntityTracker;
import com.bigsagebeast.hero.enums.Satiation;
import com.bigsagebeast.hero.roguelike.world.proc.ProcEffectHunger;
import com.bigsagebeast.hero.util.Util;

import java.util.HashMap;

public class Player {
	public int entityId = -1;
	public int statPoints = 0;
	public Statblock upgradedStats = new Statblock(0);

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
			if (delta > 0) {
				Game.announce(after.messageUp);
				// TODO update stat window
				// TODO update a proc on the player
			} else {
				if (after == Satiation.STARVING || after == Satiation.HUNGRY) {
					Game.interruptAndBreak(after.messageDown);
				} else {
					Game.announce(after.messageDown);
				}
			}
		}
		((ProcEffectHunger)getEntity().getProcByType(ProcEffectHunger.class)).setSatiation(Satiation.getStatus(satiation));
		if (after == Satiation.DEAD) {
			Game.die("starvation");
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

	public void registerExperienceForKill(Entity target) {
		Entity pc = getEntity();
		pc.experience += target.experienceAwarded;
		if (pc.experience >= pc.experienceToNext) {
			Game.getPlayer().levelUp();
		}
	}

	public void levelUp() {
		Entity entity = getEntity();
		statPoints += 2;
		entity.level++;
		entity.experience -= entity.experienceToNext;
		entity.experienceToNext *= 2;
		entity.recalculateSecondaryStats();

		Game.interruptAndBreak("You have advanced to level " + entity.level + "!", this::levelUpDialogue);
	}


	public void levelUpDialogue() {
		DialogueBox box = new DialogueBox()
				.withMargins(60, 60)
				.withFooterSelectable()
				.withCancelable(false)
				.withTitle("Select stat to level up");
		if (statPoints == 1) {
			box.addHeader("You have " + statPoints + " stat point to spend.");
		} else {
			box.addHeader("You have " + statPoints + " stat points to spend.");
		}
		box.addHeader("              Current   Cost");
		for (Stat stat : Stat.values()) {
			String current = "" + getEntity().statblock.get(stat);
			box.addItem(Util.capitalize(stat.description()) + Util.repeat(" ", 12 - stat.description().length()) + current + Util.repeat(" ", 10 - current.length()) + getCostForStat(stat), stat.name());
		}
		box.addItem("Save points for next level", "SAVE");
		box.autoHeight();
		GameLoop.dialogueBoxModule.openDialogueBox(box, this::handleLevelUp);
	}

	private void handleLevelUp(Object result) {
		String resultString = (String)result;
		if (!result.equals("SAVE")) {
			Stat stat = Stat.valueOf(resultString);
			if (statPoints >= getCostForStat(stat)) {
				statPoints -= getCostForStat(stat);
				upgradedStats.change(stat, 1);
				getEntity().statblock.change(stat, 1, true);
				getEntity().recalculateSecondaryStats();
				if (statPoints > 0) {
					levelUpDialogue();
				}
			} else {
				levelUpDialogue();
			}
		}
	}

	private int getCostForStat(Stat stat) {
		return 1 + (upgradedStats.get(stat) / 2);
	}

}
