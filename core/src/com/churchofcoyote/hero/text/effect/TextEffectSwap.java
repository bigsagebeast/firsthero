package com.churchofcoyote.hero.text.effect;

import java.util.ArrayList;
import java.util.List;

import com.churchofcoyote.hero.GameState;
import com.churchofcoyote.hero.text.TextBlock;
import com.churchofcoyote.hero.text.TextEffect;

public class TextEffectSwap extends TextEffect {
	protected float frequencyMin = 0.4f;
	protected float frequencyMax = 1.6f;
	protected float durationMin = 0.1f;
	protected float durationMax = 0.25f;
	
	protected float currentWait;
	protected float currentDuration;
	
	protected Float lastStart = null; 
	protected Float lastEnd = null; 
	
	protected List<TextBlock> alternates;
	protected TextBlock current;
	protected int lastChosen;
	
	protected boolean waitForWord;
	
	public TextEffectSwap(float fMin, float fMax, float dMin, float dMax) {
		this(fMin, fMax, dMin, dMax, true);
	}
	
	public TextEffectSwap(float fMin, float fMax, float dMin, float dMax, boolean waitForWord) {
		this.waitForWord = waitForWord;
		this.frequencyMin = fMin;
		this.frequencyMax = fMax;
		this.durationMin = dMin;
		this.durationMax = dMax;

		alternates = new ArrayList<TextBlock>();
		current = null;
	}
	
	public boolean isWaitForWord() {
		return waitForWord;
	}
	
	public void addAlternate(TextBlock alternate) {
		alternates.add(alternate);
	}
	
	public TextBlock getAlternate() {
		return current;
	}
	
	@Override
	public void update(GameState state) {
		for (TextBlock alternate : alternates) {
			alternate.update(state);
		}
		if (lastStart == null && lastEnd == null) {
			currentWait = state.randFloat(frequencyMin, frequencyMax);
			lastEnd = state.getSeconds() - (float)(Math.random() * currentWait);
		}
		if (lastEnd != null && state.getSeconds() - lastEnd > currentWait) {
			currentDuration = state.randFloat(durationMin, durationMax);
			int newChosen;
			newChosen = state.randInt(alternates.size());
			if (alternates.size() > 1) {
				while (newChosen == lastChosen) {
					newChosen = state.randInt(alternates.size());
				}
			}
			lastChosen = newChosen;
			current = alternates.get(newChosen);
			lastEnd = null;
			lastStart = state.getSeconds();
		} else if (lastStart != null && state.getSeconds() - lastStart > currentDuration) {
			currentWait = state.randFloat(frequencyMin, frequencyMax);
			lastStart = null;
			lastEnd = state.getSeconds();
			current = null;
		}
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
