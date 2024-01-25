package com.bigsagebeast.hero.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.bigsagebeast.hero.Graphics;
import com.bigsagebeast.hero.HeroGame;
import com.bigsagebeast.hero.ui.UIManager;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setWindowedMode(Graphics.width, Graphics.height);
		config.setWindowSizeLimits(UIManager.MIN_RESIZE_X, UIManager.MIN_RESIZE_Y, -1, -1);
		config.setMaximized(true);
		new Lwjgl3Application(new HeroGame(), config);
	}
}
