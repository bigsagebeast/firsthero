package com.churchofcoyote.hero.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.churchofcoyote.hero.Graphics;
import com.churchofcoyote.hero.HeroGame;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration(Graphics.WIDTH, Graphics.HEIGHT);
        }

        @Override
        public ApplicationListener createApplicationListener () {
                return new HeroGame();
        }
}