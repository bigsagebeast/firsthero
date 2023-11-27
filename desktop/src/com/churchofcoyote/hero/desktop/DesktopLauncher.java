package com.churchofcoyote.hero.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.churchofcoyote.hero.Graphics;
import com.churchofcoyote.hero.HeroGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setWindowedMode(Graphics.width, Graphics.height);
		new Lwjgl3Application(new HeroGame(), config);
	}
}
