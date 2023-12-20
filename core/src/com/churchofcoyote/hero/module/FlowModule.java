package com.churchofcoyote.hero.module;

import com.churchofcoyote.hero.GameLoop;
import com.churchofcoyote.hero.GameState;
import com.churchofcoyote.hero.Graphics;
import com.churchofcoyote.hero.GraphicsState;
import com.churchofcoyote.hero.roguelike.game.Profile;

public class FlowModule extends Module {
    @Override
    public void update(GameState state) {
        switch (Profile.getString("mode")) {
            case "newGameCutscene1":
                Profile.setString("mode", "newGameIntroQuest");
                GameLoop.cutsceneModule.loadIntro1();
                GameLoop.cutsceneModule.start();
                break;
            case "newGameIntroQuest":
                GameLoop.roguelikeModule.initialize();
                GameLoop.roguelikeModule.start();
                GameLoop.roguelikeModule.game.startIntro();
                break;
            case "newGameCutscene2":
                Profile.setString("mode", "newGameAurexFirstVisit");
                GameLoop.cutsceneModule.loadIntro2();
                GameLoop.cutsceneModule.start();
                break;
            case "newGameAurexFirstVisit":
                GameLoop.roguelikeModule.start();
                GameLoop.roguelikeModule.game.startAurex();
                break;
        }
        end();
    }

    @Override
    public void render(Graphics g, GraphicsState gState) {

    }
}
