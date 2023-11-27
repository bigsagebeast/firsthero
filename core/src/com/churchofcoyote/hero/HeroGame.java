package com.churchofcoyote.hero;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.churchofcoyote.hero.module.RoguelikeModule;
import com.churchofcoyote.hero.ui.UIManager;

public class HeroGame extends ApplicationAdapter {
	
	GameLoop loop;
	
	long startTimeMillis;
	float tickRate = 30;
	long tick = 0;

	private static HeroGame instance;

	public static float getSeconds() {
		long curMillis = System.currentTimeMillis();
		float seconds = (curMillis - instance.startTimeMillis) / 1000f;
		return seconds;
	}

	private static Map<String, Long> timer;
	
	static {
		timer = new HashMap<String, Long>();
	}
	
	Graphics g;
	
	@Override
	public void create () {
		instance = this;
		loop = new GameLoop();
		startTimeMillis = System.currentTimeMillis();
		g = new Graphics();
		Gdx.input.setInputProcessor(loop);
	}

	long minRenderDuration = 50;
	long lastRenderTime = 0;
	
	@Override
	public void render () {
		float seconds = getSeconds();
		long desiredTick = (long)(seconds * tickRate);
		while (tick < desiredTick) {
			tick++;
			GameState state = new GameState(tick, seconds);
			loop.update(state);
			//updated = true;
		}
		//if (!updated) return;
		
		long pre = System.currentTimeMillis();
		lastRenderTime = pre;
		if (pre - lastRenderTime < minRenderDuration) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		loop.render(g, new GraphicsState());
		renderTime *= (renderScale - 1) / renderScale;
		renderTime += (System.currentTimeMillis() - pre);
		//Gdx.graphics.setTitle("The First Hero - Render time: " + (long)(renderTime / renderScale) + "ms");
		StringBuilder timerOutput = new StringBuilder();
		for (String key : timer.keySet()) {
			timerOutput.append(" ");
			timerOutput.append(key);
			timerOutput.append(":");
			if (timer.get(key) < 10) {
				timerOutput.append(" ");
			}
			timerOutput.append(timer.get(key));
			timerOutput.append("ms");
		}
		Gdx.graphics.setTitle("The First Hero -" + timerOutput.toString());
		if (RoguelikeModule.topBorder != null) {
			StringBuilder border = new StringBuilder();
			border.append("# ");
			border.append(timerOutput.toString());
			border.append(" ");
			while (border.length() < 120) {
				border.append("#");
			}
			RoguelikeModule.topBorder.text = border.toString();
		}
	}

	float renderTime = 0f;
	float renderScale = 30f;
	
	
	@Override
	public void resize(int width, int height)
	{
		g.resize(width, height);
		UIManager.resize(width, height);
	}
	
	public static void updateTimer(String name, Long millis) {
		Long last = timer.get(name);
		if (!timer.containsKey(name)) {
			last = 0l;
		}
		last /= 10;
		last += millis;
		timer.put(name, last);
	}
}
