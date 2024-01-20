package com.bigsagebeast.hero;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

import java.util.ArrayList;
import java.util.List;

public class MusicPlayer {
    private static Music introMusic;
    private static Music bossMusic;
    private static final List<Music> loopTracks = new ArrayList<>();
    private static int loopIndex = 0;
    private static boolean boss = false;
    private static boolean looping = false;

    private static Music currentSong;

    public static void initialize() {
        introMusic = Gdx.audio.newMusic(Gdx.files.internal("music/level 1 sketch v1.mp3"));
        bossMusic = Gdx.audio.newMusic(Gdx.files.internal("music/forgotten facility sketch v3.mp3"));
        //loopTracks.add(Gdx.audio.newMusic(Gdx.files.internal("music/not a perfect rainstorm.mp3")));
        //loopTracks.add(Gdx.audio.newMusic(Gdx.files.internal("music/empty streets v3.mp3")));
        //loopTracks.add(Gdx.audio.newMusic(Gdx.files.internal("music/flooded citadel sketch2 (coalesced).mp3")));
        loopTracks.add(Gdx.audio.newMusic(Gdx.files.internal("music/Sewers of Hope V2.mp3")));
    }

    public static void stop() {
        boss = false;
        looping = false;
        if (currentSong != null) {
            currentSong.stop();
            currentSong = null;
        }
    }

    public static void playIntro() {
        looping = false;
        boss = false;
        play(introMusic);
        currentSong.setLooping(true);
    }

    public static void playLoop() {
        if (looping) {
            return;
        }
        looping = true;
        boss = false;
        loopIndex = 0;
        play(loopTracks.get(loopIndex));
    }

    public static void playBoss() {
        looping = false;
        boss = true;
        play(bossMusic);
        currentSong.setLooping(true);
    }

    private static void play(Music music) {
        if (currentSong != null) {
            currentSong.stop();
        }
        currentSong = music;
        currentSong.setOnCompletionListener(m -> nextTrack());
        music.setVolume(0.25f);
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
