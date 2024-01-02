package com.bigsagebeast.hero.text.effect;

import com.bigsagebeast.hero.GameState;
import com.bigsagebeast.hero.text.TextBlock;

public class TextEffectSwapLinear extends TextEffectSwap {
	
	public TextEffectSwapLinear(float fMin, float fMax, float start) {
		super(fMin, fMax, 0, 0);
		lastStart = start;
		currentWait = 0f;
		lastChosen = -1;
	}
	
	@Override
	public TextBlock getAlternate() {
		if (lastChosen < 0) {
			return null;
		} else if (lastChosen >= alternates.size()) {
			return alternates.get(alternates.size() - 1);
		}
		return alternates.get(lastChosen);
	}
	
	@Override
	public void update(GameState state) {
		for (TextBlock alternate : alternates) {
			alternate.update(state);
		}
		if (state.getSeconds() > lastStart + currentWait) {
			currentWait = state.randFloat(frequencyMin, frequencyMax);
			lastStart = state.getSeconds();
			lastChosen++;
		}
	}
	
	@Override
	public boolean isClosed(GameState state) {
		return (lastChosen >= alternates.size());
	}

	@Override
	public float getX() {
		return 0;
	}

	@Override
	public float getY() {
		return 0;
	}
}
