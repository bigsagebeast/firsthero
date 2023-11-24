package com.churchofcoyote.hero.roguelike.world.dungeon;
import com.churchofcoyote.hero.roguelike.world.EntityTracker;
import org.eclipse.collections.impl.map.mutable.primitive.IntObjectHashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.game.Visibility;
import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.roguelike.world.LevelTransition;
import com.churchofcoyote.hero.roguelike.world.Terrain;
import com.churchofcoyote.hero.roguelike.world.proc.Proc;
import com.churchofcoyote.hero.util.Fov;
import com.churchofcoyote.hero.util.Point;

public class Level {
	String name;
	private int width, height;
	// list of entities on the floor
	private Collection<Integer> entityIds = new HashSet<>();

	private LevelCell[][] cell;
	private LevelCell[] allCells;
	private List<LevelTransition> transitions;
	private int lastEntityId = 0;
	
	public Level(String name, int width, int height) {
		this.name = name;
		this.width = width;
		this.height = height;

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

	public void reinitialize() {
		allCells = new LevelCell[width * height];
		for (int x=0; x<width; x++) {
			for (int y=0; y<height; y++) {
				cell[x][y].explored = false;
				allCells[(x*height) + y] = cell[x][y];
			}
		}
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
	
	public int getWidth() {
		return width;
	}
	public int getHeight() {
		return height;
	}
	public boolean contains(Point p) {
		return contains(p.x, p.y);
	}
	public boolean contains(int x, int y) {
		return (x >= 0 && y >= 0 && x < width && y < height);
	}

	public Stream<Entity> getEntityStream() {
		return entityIds.stream().map(EntityTracker::get);
	}

	public Collection<Entity> getEntities() {
		// this seems slow.
		// TODO avoid all uses of this when we can.  Can we replace calls to this with lookups by cell position?
		return getEntityStream().collect(Collectors.toList());
	}

	public List<Entity> getNonMovers() {
		return getEntityStream().filter(e -> e.getMover() == null).collect(Collectors.toList());
	}

	public List<Entity> getMovers() {
		return getEntityStream().filter(e -> e.getMover() != null).collect(Collectors.toList());
	}

	public List<Entity> getEntitiesOnTile(Point p) {
		return getEntityStream().filter(e -> e.pos.equals(p)).collect(Collectors.toList());
	}

	public List<Entity> getItemsOnTile(Point p) {
		// TODO should we exclude entities that are also movers?
		return getEntityStream().filter(e -> e.pos.equals(p) && e.getMover() == null).collect(Collectors.toList());
	}

	public List<Entity> getMoversOnTile(Point p) {
		return getEntityStream().filter(e -> e.pos.equals(p) && e.getMover() != null).collect(Collectors.toList());
	}

	public List<Proc> getProcEntities() {
		List<Proc> procEntities = new ArrayList<Proc>();
		for (Entity e : getEntities()) {
			procEntities.addAll(e.procs);
		}
		return procEntities;
	}
	public void addEntity(Entity entity) {
		entityIds.add(entity.entityId);
		for (Proc p : entity.procs) {
			if (p.hasAction() && p.nextAction < 0) {
				p.setDelay(0);
			}
		}
	}
	public void removeEntity(Entity creature) {
		entityIds.remove(creature.entityId);
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
	public void putCell(int x, int y, LevelCell putCell) { cell[x][y] = putCell; }

	// TODO this assumes only one mover per tile, which probably isn't good.  Replace with moversAt?
	public Entity moverAt(int x, int y) {
		for (Entity e : getMovers()) {
			if (e.pos.x == x && e.pos.y == y) {
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
		for (LevelCell cell : allCells) {
			cell.light = 0f;
		}
		Entity pc = Game.getPlayerEntity();
		Fov.calculateFOV(this, pc.pos.x, pc.pos.y, 15f);
	}
	
	public boolean isOpaque(int x, int y) {
		if (!cell(x, y).terrain.isPassable()) {
			return true;
		}
		for (Entity e : Game.getLevel().getEntitiesOnTile(new Point(x, y))) {
			if (e.isObstructiveToVision()) {
				return true;
			}
		}
		return false;
	}
	
	public void clearTemp() {
		for (LevelCell cell : allCells) {
			cell.temp = null;
		}
	}

	public Boolean withinBounds(int x, int y) {
		return (x >= 0 && x < width && y >= 0 && y < height);
	}

	public Point findOpenTile() {
		for (int i=0; i<width; i++) {
			for (int j=0; j<height; j++) {
				if (cell[i][j].terrain.isPassable()) {
					return new Point(i, j);
				}
			}
		}
		throw new RuntimeException("Failed to find any open tile in the level");
	}

}
