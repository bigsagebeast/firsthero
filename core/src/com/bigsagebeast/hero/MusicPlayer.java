package com.bigsagebeast.hero;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

import java.util.ArrayList;
import java.util.List;

public class MusicPlayer {
    private static Music introMusic;
    private static Music bossMusic;
    private static Music aurexMusic;
    private static final List<Music> loopTracks = new ArrayList<>();
    private static int loopIndex = -1; // starts at -1 because we increment each time
    private static boolean boss = false;
    private static boolean aurex = false;
    private static boolean looping = false;
    private static boolean enabled = true;

    private static Music currentSong;

    public static void initialize() {
        introMusic = Gdx.audio.newMusic(Gdx.files.internal("music/level 1 sketch v1.1.mp3"));
        bossMusic = Gdx.audio.newMusic(Gdx.files.internal("music/forgotten facility sketch v3.mp3"));
        aurexMusic = Gdx.audio.newMusic(Gdx.files.internal("music/01 Vignette Dreams.mp3"));
        loopTracks.add(Gdx.audio.newMusic(Gdx.files.internal("music/not a perfect rainstorm.mp3")));
        loopTracks.add(Gdx.audio.newMusic(Gdx.files.internal("music/empty streets v3.mp3")));
        loopTracks.add(Gdx.audio.newMusic(Gdx.files.internal("music/flooded citadel sketch2.mp3")));
        loopTracks.add(Gdx.audio.newMusic(Gdx.files.internal("music/Sewers of Hope V2.mp3")));
        loopTracks.add(Gdx.audio.newMusic(Gdx.files.internal("music/11 Drowned City.mp3")));
    }

    public static boolean enabled() {
        return enabled;
    }

    public static void disable() {
        if (!enabled) {
            return;
        }
        enabled = false;
        if (currentSong != null) {
            currentSong.stop();
        }
    }

    public static void enable() {
        if (enabled) {
            return;
        }
        enabled = true;
        if (boss) {
            boss = false;
            playBoss();
        } else if (aurex) {
            aurex = false;
            playAurex();
        } else if (looping) {
            looping = false;
            playLoop();
        }
    }

    public static void stop() {
        boss = false;
        looping = false;
        aurex = false;
        if (currentSong != null) {
            currentSong.stop();
            currentSong = null;
        }
    }

    public static void playIntro() {
        looping = false;
        boss = false;
        aurex = false;
        if (!enabled) {
            return;
        }
        play(introMusic);
        currentSong.setLooping(true);
    }

    public static void playLoop() {
        if (looping) {
            return;
        }
        looping = true;
        boss = false;
        aurex = false;
        if (!enabled) {
            return;
        }
        nextTrack();
    }

    public static void playBoss() {
        if (boss) {
            return;
        }
        looping = false;
        boss = true;
        aurex = false;
        if (!enabled) {
            return;
        }
        play(bossMusic);
        currentSong.setLooping(true);
    }

    public static void playAurex() {
        if (aurex) {
            return;
        }
        looping = false;
        boss = false;
        aurex = true;
        if (!enabled) {
            return;
        }
        play(aurexMusic);
        currentSong.setLooping(true);
    }

    private static void play(Music music) {
        if (currentSong != null) {
            currentSong.stop();
        }
        currentSong = music;
        currentSong.setOnCompletionListener(m -> nextTrack());
        music.setVolume(0.15f);
        music.play();
    }

    public static void skipTo(float position) {
        currentSong.setPosition(position);
    }

    private static void nextTrack() {
        if (boss) {
            play(bossMusic);
        } else if (looping) {
            loopIndex = (loopIndex + 1) % loopTracks.size();
            play(loopTracks.get(loopIndex));
        }
    }
}
