package com.churchofcoyote.hero.roguelike.world;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.game.Visibility;
import com.churchofcoyote.hero.roguelike.world.proc.ProcEntity;
import com.churchofcoyote.hero.util.Fov;
import com.churchofcoyote.hero.util.Point;

public class Level {
	private long width, height;
	private List<Entity> entities;
	private LevelCell[][] cell;
	private LevelCell[] allCells;
	private List<LevelTransition> transitions;
	
	public Level(int width, int height) {
		this.width = width;
		this.height = height;
		this.entities = new ArrayList<Entity>();

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
	public List<Entity> getEntities() {
		return entities;
	}

	public List<Entity> getNonMovers() {
		return entities.stream().filter(e -> e.getMover() == null).collect(Collectors.toList());
	}

	public List<Entity> getMovers() {
		return entities.stream().filter(e -> e.getMover() != null).collect(Collectors.toList());
	}

	public List<Entity> getItemsOnTile(Point p) {
		//return entities.stream().filter(e -> e.pos == p && e.getItem() != null).collect(Collectors.toList());
		return entities.stream().filter(e -> e.pos.x == p.x && e.pos.y == p.y && e.getItem() != null).collect(Collectors.toList());
	}

	public List<ProcEntity> getProcEntities() {
		List<ProcEntity> procEntities = new ArrayList<ProcEntity>();
		for (Entity e : entities) {
			procEntities.addAll(e.procs);
		}
		return procEntities;
	}
	public void addEntity(Entity entity) {
		entities.add(entity);
		for (ProcEntity pe : entity.procs) {
			if (pe.hasAction() && pe.nextAction < 0) {
				pe.setDelay(0);
			}
		}
	}
	public void removeEntity(Entity creature) {
		entities.remove(creature);
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
	
	public Entity moverAt(int x, int y) {
		for (Entity e : entities) {
			if (e.getMover() != null && e.pos.x == x && e.pos.y == y) {
				return e;
			}
		}
		return null;
	}
	
	public Visibility checkVis(Entity player, Entity actor, Entity target) {
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
		Entity pc = Game.getPlayerEntity();
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
