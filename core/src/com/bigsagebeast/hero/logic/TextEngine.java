package com.bigsagebeast.hero.logic;

import java.util.HashSet;
import java.util.Set;

import com.bigsagebeast.hero.GameLogic;
import com.bigsagebeast.hero.GameState;
import com.bigsagebeast.hero.Graphics;
import com.bigsagebeast.hero.GraphicsState;
import com.bigsagebeast.hero.text.TextBlock;

public class TextEngine implements GameLogic {
	
	private Set<TextBlock> activeBlocks;
	private HashSet<TextBlock> closingBlocks = new HashSet<TextBlock>();
	
	public TextEngine() {
		activeBlocks = new HashSet<TextBlock>();
		
		/*
		TextEffectJitter jitter = new TextEffectJitter(0.05f, 4f);
		TextBlock block = new TextBlock("Block jitter", 16f, 0f, 0f, Color.WHITE, 0f, 0.1f,
				jitter, null);
		activeBlocks.add(block);

		TextEffectJitter jitter2 = new TextEffectJitter(0.05f, 4f);
		TextBlock block2 = new TextBlock("Word jitter", 16f, 0f, 1f, Color.YELLOW, 0.5f, 0.1f,
				jitter2, null, TextEffectGranularity.WORD);
		activeBlocks.add(block2);

		TextEffectJitter jitter3 = new TextEffectJitter(0.1f, 2f);
		TextBlock block3 = new TextBlock("Heavy jitter", 16f, 0f, 2f, Color.RED, 1f, 0.1f,
				jitter3, null, TextEffectGranularity.LETTER);
		activeBlocks.add(block3);

		TextEffectJitter jitter4 = new TextEffectJitter(0.2f, 2f);
		TextBlock block4 = new TextBlock("Chaos jitter", 16f, 0f, 3f, Color.MAGENTA, 1.5f, 0.1f,
				jitter4, null, TextEffectGranularity.LETTER);
		activeBlocks.add(block4);

		TextEffectSwap swap1 = new TextEffectSwap(0.1f, 0.8f, 0.05f, 0.2f, true);
		TextEffectJitter jitter5 = new TextEffectJitter(0.2f, 4f);
		TextBlock block5 = new TextBlock(null, 16f, 0f, 6f, Color.ORANGE, 2f, 0.1f,
				null, null, TextEffectGranularity.BLOCK);
		TextBlock block5_1 = new TextBlock("Glitch", 16f, 0f, 0f, Color.ORANGE, 2f, 0.1f,
				null, null, TextEffectGranularity.BLOCK);
		TextBlock block5_2 = new TextBlock("text ", 16f, 7f, 0f, Color.ORANGE, 2.7f, 0.3f,
				null, swap1, TextEffectGranularity.BLOCK);
		TextBlock block5_2a = new TextBlock("kill", 16f, 0.3f, -0.2f, Color.SCARLET, 0f, 0f,
				jitter5, null, TextEffectGranularity.BLOCK);
		TextBlock block5_2b = new TextBlock("destroy", 16f, 0.5f, 0.5f, Color.SALMON, 0f, 0f,
				jitter5, null, TextEffectGranularity.BLOCK);
		TextBlock block5_2c = new TextBlock("no mercy", 16f, -2f, -0.8f, Color.TAN, 0f, 0f,
				jitter5, null, TextEffectGranularity.BLOCK);
		TextBlock block5_2d = new TextBlock("power", 16f, -0.5f, 0.3f, Color.ROYAL, 0f, 0f,
				jitter5, null, TextEffectGranularity.BLOCK);
		block5.addChild(block5_1);
		block5.addChild(block5_2);
		swap1.addAlternate(block5_2a);
		swap1.addAlternate(block5_2b);
		swap1.addAlternate(block5_2c);
		swap1.addAlternate(block5_2d);
		activeBlocks.add(block5);
		*/
	}

	public void recompile() {
		for (TextBlock block : activeBlocks) {
			block.compile();
		}
	}

	public void addBlock(TextBlock block) {
		activeBlocks.add(block);
	}

	@Override
	public void update(GameState state) {
		for (TextBlock block : activeBlocks) {
			block.update(state);
			if (block.isClosed()) {
				closingBlocks.add(block);
			}
		}
		for (TextBlock block : closingBlocks) {
			activeBlocks.remove(block);
		}
		
		closingBlocks.clear();
	}

	public void purge() {
		activeBlocks.clear();
	}
	
	@Override
	public void render(Graphics g, GraphicsState gState) {
		//g.startBatch();
		for (TextBlock block : activeBlocks) {
			block.render(g, gState);
		}
		//g.endBatch();
	}

}
