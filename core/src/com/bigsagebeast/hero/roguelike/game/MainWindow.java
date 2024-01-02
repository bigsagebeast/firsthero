package com.bigsagebeast.hero.roguelike.game;

public class MainWindow {
	private int width, height;
	private int cameraX, cameraY;
	
	public MainWindow() {
		width = 60;
		height = 60;
		cameraX = 0;
		cameraY = 0;
	}
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getCameraX() {
		return cameraX;
	}

	public int getCameraY() {
		return cameraY;
	}
}
