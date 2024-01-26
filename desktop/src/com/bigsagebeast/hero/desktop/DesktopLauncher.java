package com.bigsagebeast.hero.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.bigsagebeast.hero.Graphics;
import com.bigsagebeast.hero.HeroGame;
import com.bigsagebeast.hero.StartupHelper;
import com.bigsagebeast.hero.ui.UIManager;

public class DesktopLauncher {
	public static void main (String[] arg) {
		if (StartupHelper.startNewJvmIfRequired()) return; // This handles macOS support and helps on Windows.
		createApplication();
	}

	private static Lwjgl3Application createApplication() {
		return new Lwjgl3Application(new HeroGame(), getDefaultConfiguration());
	}

	private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.useVsync(true);
		config.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate);
		config.setWindowedMode(Graphics.width, Graphics.height);
		config.setWindowSizeLimits(UIManager.MIN_RESIZE_X, UIManager.MIN_RESIZE_Y, -1, -1);
		config.setMaximized(true);
		return config;
	}
}
