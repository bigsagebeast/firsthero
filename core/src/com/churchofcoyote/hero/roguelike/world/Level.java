package com.churchofcoyote.hero.roguelike.world;
import java.util.ArrayList;
import java.util.List;

import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.game.Visibility;
import com.churchofcoyote.hero.util.Fov;
import com.churchofcoyote.hero.util.Point;

public class Level {
	private long width, height;
	private List<Creature> creatures;
	private LevelCell[][] cell;
	private LevelCell[] allCells;
	private List<LevelTransition> transitions;
	
	public Level(int width, int height) {
		this.width = width;
		this.height = height;
		this.creatures = new ArrayList<Creature>();

		allCells = new LevelCell[width * height];
		cell = new LevelCell[width][];
		for (int x=0; x<width; x++) {
			cell[x] = new LevelCell[height];
			for (int y=0; y<height; y++) {
				cell[x][y] = new LevelCell();
				cell[x][y].terrain = Terrain.BLANK;
				cell[x][y].explored = false;
				allCells[(x*height) + y] = cell[x][y];
			}
		}
		
		transitions = new ArrayList<LevelTransition>();
	}
	
	public void addTransition(LevelTransition transition) {
		transitions.add(transition);
	}
	
	public LevelTransition findTransition(String direction, Point loc) {
		for (LevelTransition transition : transitions) {
			if (transition.direction.equals(direction) && transition.loc.equals(loc)) {
				return transition;
			}
		}
		return null;
	}
	
	public long getWidth() {
		return width;
	}
	public long getHeight() {
		return height;
	}
	public boolean contains(Point p) {
		return contains(p.x, p.y);
	}
	public boolean contains(int x, int y) {
		return (x >= 0 && y >= 0 && x < width && y < height);
	}
	public List<Creature> getCreatures() {
		return creatures;
	}
	public void addCreature(Creature creature) {
		creatures.add(creature);
	}
	public void removeCreature(Creature creature) {
		creatures.remove(creature);
	}
	public LevelCell cell(int x, int y) {
		if (x < 0 || y < 0 || x >= width | y >= height) {
			return LevelCell.NONE;
		}
		return cell[x][y];
	}
	public LevelCell cell(Point p) {
		return cell(p.x, p.y);
	}
	
	public Creature creatureAt(int x, int y) {
		for (Creature c : creatures) {
			if (c.pos.x == x && c.pos.y == y) {
				return c;
			}
		}
		return null;
	}
	
	public Visibility checkVis(Creature player, Creature actor, Creature target) {
		if (player == actor) {
			return Visibility.ACTOR;
		} else if (player == target) {
			return Visibility.TARGET;
		} else {
			// todo: LOS...
			return Visibility.VISIBLE;
		}
	}
	
	public void updateVis() {
		Creature pc = Game.getPlayerCreature();
		for (LevelCell cell : allCells) {
			cell.light = 0f;
		}
		Fov.calculateFOV(this, pc.pos.x, pc.pos.y, 10f);
	}
	
	public boolean isOpaque(int x, int y) {
		return !cell(x, y).terrain.isPassable();
	}
	
	public void clearTemp() {
		for (LevelCell cell : allCells) {
			cell.temp = null;
		}
	}
}
