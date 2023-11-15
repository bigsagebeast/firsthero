package com.churchofcoyote.hero.module;

import com.churchofcoyote.hero.GameLogic;
import com.churchofcoyote.hero.engine.asciitile.AsciiTileEngine;
import com.churchofcoyote.hero.logic.EffectEngine;
import com.churchofcoyote.hero.logic.TextEngine;

public abstract class Module implements GameLogic {

	protected static TextEngine textEngine;
	protected static TextEngine uiEngine;
	protected static EffectEngine effectEngine;
	protected static AsciiTileEngine asciiTileEngine;
	
	public static void setEngines(TextEngine textEngine, TextEngine uiEngine, EffectEngine effectEngine, AsciiTileEngine asciiTileEngine) {
		Module.textEngine = textEngine;
		Module.uiEngine = uiEngine;
		Module.effectEngine = effectEngine;
		Module.asciiTileEngine = asciiTileEngine;
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
