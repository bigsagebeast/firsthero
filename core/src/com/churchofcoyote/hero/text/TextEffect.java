package com.churchofcoyote.hero.text;

import com.badlogic.gdx.graphics.Color;
import com.churchofcoyote.hero.GameLogic;
import com.churchofcoyote.hero.GameState;
import com.churchofcoyote.hero.Graphics;
import com.churchofcoyote.hero.GraphicsState;

public abstract class TextEffect implements GameLogic, Cloneable {
	public void update(GameState state) {}
	public void render(Graphics g, GraphicsState gState) {}
	public float getX() { return 0; }
	public float getY() { return 0; }
	public Color getFade() { return Color.WHITE; }
	public boolean isClosed(GameState state) { return false; }
}
