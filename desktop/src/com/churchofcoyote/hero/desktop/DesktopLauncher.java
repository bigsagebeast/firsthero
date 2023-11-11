package com.churchofcoyote.hero.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.churchofcoyote.hero.Graphics;
import com.churchofcoyote.hero.HeroGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = Graphics.WIDTH;
		config.height = Graphics.HEIGHT;
		new LwjglApplication(new HeroGame(), config);
	}
}
