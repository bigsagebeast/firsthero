package com.churchofcoyote.hero.roguelike.world.ai;

import java.util.List;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import com.churchofcoyote.hero.roguelike.world.Creature;

public abstract class Strategy {

	protected Strategy parent;
	protected List<Strategy> children;
	protected Creature creature;
	
	protected String lastPurpose = "";
	protected Tactic currentTactic;

	private String debugString = "unset";
	
	public Tactic getTactic() {
		if (parent != null) {
			return getTactic(parent.getTactic());
		}
		return getSoloTactic();
	}
	
	public Tactic getTactic(Tactic parentTactic) {
		throw new RuntimeException("No chain tactic for strategy " + getName());
	}
	
	public Tactic getSoloTactic() {
		throw new RuntimeException("No solo tactic for strategy " + getName());
	}
	
	public abstract String getName();
	
	protected void setDebug(String debug) {
		debugString = debug;
	}
	
	public String getDebug() {
		return debugString;
	}
	

}
