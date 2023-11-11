package com.churchofcoyote.hero;

import com.badlogic.gdx.graphics.Color;

public class GraphicsState {
	private float x;
	private float y;
	private Color filter;
	public GraphicsState(float x, float y, Color filter) {
		this.x = x;
		this.y = y;
		this.filter = filter;
	}
	
	public GraphicsState() {
		this.x = 0f;
		this.y = 0f;
		this.filter = Color.WHITE;
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
	
	public Color getFilter() {
		return filter;
	}
}
