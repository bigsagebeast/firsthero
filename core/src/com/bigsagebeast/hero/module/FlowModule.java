package com.bigsagebeast.hero.module;

import com.bigsagebeast.hero.GameLoop;
import com.bigsagebeast.hero.GameState;
import com.bigsagebeast.hero.Graphics;
import com.bigsagebeast.hero.GraphicsState;
import com.bigsagebeast.hero.roguelike.game.Profile;

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
                GameLoop.roguelikeModule.initialize();
                GameLoop.roguelikeModule.start();
                GameLoop.roguelikeModule.game.startAurex();
                break;
            case "enterWorld":
                GameLoop.roguelikeModule.initialize();
                GameLoop.roguelikeModule.start();
                GameLoop.roguelikeModule.game.startCaves();
                break;
        }
        end();
    }

    @Override
    public void render(Graphics g, GraphicsState gState) {

    }
}
