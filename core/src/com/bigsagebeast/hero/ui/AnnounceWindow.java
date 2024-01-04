package com.bigsagebeast.hero.ui;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.bigsagebeast.hero.engine.WindowEngine;
import com.bigsagebeast.hero.module.RoguelikeModule;
import com.bigsagebeast.hero.text.TextBlock;

public class AnnounceWindow extends UIWindow {
	private int windowWidth;
	private int windowHeight;
	private int paraWidth;
	private int allowedSpace = 11;
	private int slack = 0;

	List<String> lines = new ArrayList<>();
	List<TextBlock> lineBlocks = new ArrayList<>();
	TextBlock parent;
	
	public AnnounceWindow() {
		windowWidth = 46;
		windowHeight = 20;
		paraWidth = 44;
		parent = new TextBlock("", UIManager.NAME_ANNOUNCEMENTS, RoguelikeModule.FONT_SIZE, 0, 0, Color.WHITE);
	}
	
	public TextBlock getTextBlockParent() {
		return parent;
	}

	@Override
	public void update() {
		parent.compile();
	}

	@Override
	public void close() {
		if (parent != null) {
			parent.close();
		}
	}

	public void addLine(String line) {
		WindowEngine.setDirty(UIManager.NAME_ANNOUNCEMENTS);
		if (line.isEmpty()) {
			return;
		}
		line = addLine(line, windowWidth);
		
		while (!line.isEmpty()) {
			line = addLine("  " + line, paraWidth);
		}
		
		while (lines.size() > windowHeight) {
			lines.remove(0);
			lineBlocks.get(0).close();
			lineBlocks.remove(0);
		}
		parent.compile();
	}

	public void unannounce() {
		lines.remove(lines.size() - 1);
		lineBlocks.get(lineBlocks.size() - 1).close();
		lineBlocks.remove(lineBlocks.size() - 1);
		slack++;
		parent.compile();
	}
	
	private String addLine(String line, int width) {
		/*
		// remove prepended spaces
		while (!line.isEmpty() && line.indexOf(0) == ' ') {
			line = line.substring(1);
		}
		*/
		if (line.isEmpty()) {
			return "";
		}
		
		String thisLine;
		String nextLine;
		int lastSpace = line.lastIndexOf(' ', width);

		// find space to cut on
		if (line.length() <= width) {
			// No cut required
			thisLine = line;
			nextLine = "";
		} else if (line.charAt(width) == ' ') {
			// Perfect spacing: character after width is a space
			thisLine = line.substring(0, width);
			nextLine = line.substring(width+1); // TODO width + 2?
		} else if (lastSpace > width - allowedSpace) {
			// can cut within our range
			thisLine = line.substring(0, lastSpace);
			nextLine = line.substring(lastSpace + 1);
		} else {
			// can't cut at a space, take maximum length and break
			thisLine = line.substring(0, width);
			nextLine = line.substring(width);
		}
		
		lines.add(thisLine);
		
		TextBlock lineBlock = new TextBlock("", null, RoguelikeModule.FONT_SIZE, 0, windowHeight-1, Color.WHITE);
		TextBlock partialBlock = new TextBlock(thisLine, null, RoguelikeModule.FONT_SIZE, 0, 0, Color.WHITE);
		lineBlock.addChild(partialBlock);

		if (slack > 0) {
			slack--;
		} else {
			for (TextBlock block : lineBlocks) {
				block.y -= 1.0f;
			}
		}
		lineBlocks.add(lineBlock);
		parent.addChild(lineBlock);
		parent.compile();
		return nextLine;
	}
	

}
