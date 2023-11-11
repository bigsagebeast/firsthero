package com.churchofcoyote.hero;

public interface GameLogic {
	public void update(GameState state);
	public void render(Graphics g, GraphicsState gState);
}
