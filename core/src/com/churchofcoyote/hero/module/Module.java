package com.churchofcoyote.hero.module;

import com.churchofcoyote.hero.GameLogic;
import com.churchofcoyote.hero.logic.EffectEngine;
import com.churchofcoyote.hero.logic.TextEngine;

public abstract class Module implements GameLogic {

	protected static TextEngine textEngine;
	protected static TextEngine uiEngine;
	protected static EffectEngine effectEngine;

	public static void setEngines(TextEngine textEngine, TextEngine uiEngine, EffectEngine effectEngine) {
		Module.textEngine = textEngine;
		Module.uiEngine = uiEngine;
		Module.effectEngine = effectEngine;
	}
	
	private boolean running = false;
	
	public boolean isRunning() { return running; }
	
	public void start() {running = true;}
	public void end() {running = false;}
	
	// false = ignored, true = captured
	public boolean keyDown(int keycode, boolean shift, boolean ctrl, boolean alt) {
		return false;
	}
	public boolean keyTyped(char key, boolean ctrl, boolean alt) {
		return false;
	}
}
