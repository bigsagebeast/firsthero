package com.churchofcoyote.hero.roguelike.world.dungeon;
import com.churchofcoyote.hero.roguelike.game.EntityProc;
import com.churchofcoyote.hero.roguelike.world.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.game.Visibility;
import com.churchofcoyote.hero.roguelike.world.proc.Proc;
import com.churchofcoyote.hero.util.Fov;
import com.churchofcoyote.hero.util.Point;

public class Level {
	String name;
	int width, height;
	// list of entities on the floor
	private Collection<Integer> entityIds = new HashSet<>();

	private LevelCell[][] cell;
	private LevelCell[] allCells;
	private List<LevelTransition> transitions;
	private int lastEntityId = 0;
	public int threat = -1;
	private long lastWander = 0;
	public long wanderRate = 50000;
	public int maxForWander = DungeonGenerator.NUM_MONSTERS;
	public List<Room> rooms = new ArrayList<>();
	
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
		
		transitions = new ArrayList<>();
	}

	public void timePassed(long time) {
		if (lastWander + wanderRate < time) {
			lastWander = time;
			if (getMovers().size() > maxForWander) {
				return;
			}
			String monsterKey = DungeonGenerator.getAllowedMonster(this);
			Point pos = findSpawnTile(10);
			if (pos == null || monsterKey == null) {
				return;
			}
			Entity e = Game.bestiary.create(monsterKey, null);
			addEntityWithStacking(e, pos);
		}
	}

	public String getName() {
		return name;
	}

	public Iterable<LevelCell> getCellStream() {
		// prevent returning something modifiable
		// it's dumb
		return Arrays.asList(allCells);
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
		// TODO should we exclude entities that are also movers?  Is that even a thing?
		// TODO This sortOrder should become a field on Entity
		return getEntityStream().filter(e -> e.pos.equals(p) && e.getMover() == null)
				.sorted(Comparator.comparing(e -> -e.getItemType().sortOrder)).collect(Collectors.toList());
	}

	public List<Entity> getMoversOnTile(Point p) {
		return getEntityStream().filter(e -> e.pos.equals(p) && e.getMover() != null).collect(Collectors.toList());
	}

	public List<EntityProc> getProcEntities() {
		List<EntityProc> procEntities = new ArrayList<>();
		for (Entity e : getEntities()) {
			for (Proc p : e.procs) {
				procEntities.add(new EntityProc(e, p));
			}
		}
		return procEntities;
	}

	public Entity addEntityWithStacking(Entity entity, Point pos) {
		entity.containingLevel = this.name;
		entity.containingEntity = -1;
		entity.pos = pos;
		entity.roomId = cell(pos).roomId;
		if (entity.roomId >= 0 && entity.getMover() != null) {
			entity.changeRoom(null, rooms.get(entity.roomId));
		}
		Entity stackedInto = null;
		for (Entity mergeTarget : getItemsOnTile(entity.pos)) {
			if (entity.canStackWith(mergeTarget)) {
				stackedInto = mergeTarget;
				mergeTarget.beStackedWith(entity);
				entity.destroy();
			}
		}

		if (stackedInto == null) {
			entityIds.add(entity.entityId);
			for (Proc p : entity.procs) {
				if (p.hasAction() && p.nextAction < 0) {
					p.clearDelay();
				}
			}
		}
		return stackedInto == null ? entity : stackedInto;
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

	public Boolean withinBounds(Point p) {
		return withinBounds(p.x, p.y);
	}

	public boolean obstructiveBesidesMovers(Point p) {
		if (!cell[p.x][p.y].terrain.isPassable()) {
			return true;
		}
		for (Entity e : getEntitiesOnTile(p)) {
			if (e.getMover() == null && e.isObstructive()) {
				return true;
			}
		}
		return false;
	}

	public Point findOpenTile() {
		for (int i=0; i<10000; i++) {
			int x = Game.random.nextInt(width);
			int y = Game.random.nextInt(height);
			if (cell[x][y].terrain.isPassable()) {
				boolean blockingEntity = false;
				for (Entity e : getEntitiesOnTile(new Point(x, y))) {
					if (e.getMover() != null || e.isObstructive()) {
						blockingEntity = true;
						break;
					}
				}
				if (blockingEntity) {
					continue;
				}
				return new Point(x, y);
			}
		}
		return null;
	}

	public Point findSpawnTile(int distance) {
		int playerPosX = Game.getPlayerEntity().pos.x;
		int playerPosY = Game.getPlayerEntity().pos.y;
		int excludedXMin = playerPosX - distance;
		int excludedXMax = playerPosX + distance;
		int excludedYMin = playerPosY - distance;
		int excludedYMax = playerPosY + distance;

		for (int i=0; i<10000; i++) {
			int x = Game.random.nextInt(width);
			int y = Game.random.nextInt(height);
			if (x > excludedXMin && x < excludedXMax &&	y > excludedYMin && y < excludedYMax) {
				continue;
			}
			if (cell[x][y].terrain.isPassable() && cell[x][y].visible() == false) {
				for (Entity e : getEntitiesOnTile(new Point(x, y))) {
					if (e.getMover() != null) {
						continue;
					}
				}
				return new Point(x, y);
			}
		}
		return null;
	}

	public Point findPackSpawnTile(Point origin, int area) {
		for (int i = 0; i<100; i++) {
			int xRand = origin.x - (area/2) + Game.random.nextInt(area);
			int yRand = origin.y - (area/2) + Game.random.nextInt(area);
			if (!withinBounds(xRand, yRand)) continue;
			if (cell[xRand][yRand].terrain.isPassable() && cell[xRand][yRand].visible() == false) {
				for (Entity e : getEntitiesOnTile(new Point(xRand, yRand))) {
					if (e.getMover() != null) {
						continue;
					}
				}
				return new Point(xRand, yRand);
			}
		}
		return null;
	}


}
