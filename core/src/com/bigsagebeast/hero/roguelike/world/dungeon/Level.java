package com.bigsagebeast.hero.roguelike.world.dungeon;
import com.bigsagebeast.hero.roguelike.game.EntityProc;
import com.bigsagebeast.hero.roguelike.world.*;
import com.bigsagebeast.hero.roguelike.world.dungeon.generation.SpecialSpawner;
import com.bigsagebeast.hero.roguelike.world.proc.Proc;
import com.bigsagebeast.hero.util.Compass;
import com.bigsagebeast.hero.util.Fov;
import com.bigsagebeast.hero.util.Point;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.game.Visibility;
import com.bigsagebeast.hero.util.Util;

public class Level {
	String key;
	String friendlyName;
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
	public Map<Integer, List<Point>> roomMap = new HashMap<>();
	public Map<Point, Float> jitters = new HashMap<>();
	private int lastTurnUpdate = 0;
	
	public Level(String key, int width, int height) {
		this.key = key;
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
		if (time < lastTurnUpdate + Game.ONE_TURN) {
			return;
		}
		lastTurnUpdate += Game.ONE_TURN;
		for (Room r : rooms) {
			for (SpecialSpawner s : r.spawners) {
				s.spawnInRoomPostGen(this, r.roomId);
			}
		}
		if (lastWander + wanderRate < time) {
			lastWander = time;
			if (getMovers().stream().filter(e -> e.wanderer).count() > maxForWander) {
				return;
			}

			String monsterKey = DungeonGenerator.getAllowedMonster(Arrays.asList("generic-fantasy"), getMinThreat(), getMaxThreat(), this, true);
			Point pos = findSpawnTile(10);
			if (pos == null || monsterKey == null) {
				return;
			}
			Entity e = Game.bestiary.create(monsterKey);
			e.wanderer = true;
			addEntityWithStacking(e, pos);
		}
	}

	public String getKey() {
		return key;
	}
	public String getFriendlyName() {
		if (friendlyName != null) {
			return friendlyName;
		}
		return getKey();
	}
	public void setFriendlyName(String friendlyName) {
		this.friendlyName = friendlyName;
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

	// call after the level is all set up
	public void prepare() {
		for (Entity ent : getEntities()) {
			ent.postLoad();
		}

		recalculateJitter();
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

	public LevelTransition findTransitionTo(String toKey) {
		for (LevelTransition transition : transitions) {
			if (transition.toMap.equals(toKey)) {
				return transition;
			}
		}
		throw new RuntimeException("Failed to find 'to' transition when moving levels from " + toKey + " to " + key);
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

	public List<EntityProc> getEntityProcs() {
		List<EntityProc> procEntities = new ArrayList<>();
		for (Entity e : getEntities()) {
			for (Proc p : e.procs) {
				procEntities.add(new EntityProc(e, p));
			}
		}
		return procEntities;
	}

	public Entity addEntityWithStacking(Entity entity, Point pos) {
		return addEntityWithStacking(entity, pos, true);
	}

	public Entity addEntityWithStacking(Entity entity, Point pos, boolean runPostLoad) {
		entity.containingLevel = this.key;
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
		if (runPostLoad) {
			entity.postLoad();
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
		if (cell(x, y).terrain.isOpaque()) {
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

	public Collection<Point> surroundingTiles(Point p) {
		return Compass.points().stream().map(dir -> dir.from(p))
				.filter(this::withinBounds).collect(Collectors.toList());
	}

	public Collection<Point> surroundingAndCurrentTiles(Point p) {
		Collection<Point> points = surroundingTiles(p);
		points.add(p);
		return points;
	}

	public Point findOpenTileWithinRange(Point center, int range, boolean includeCenter) {
		int minX = Math.max(center.x - range, 0);
		int maxX = Math.min(center.x + range, width - 1);
		int minY = Math.max(center.y - range, 0);
		int maxY = Math.min(center.y + range, height - 1);
		for (int i=0; i<10000; i++) {
			// TODO not stochastic?
			int x = Util.randomBetween(minX, maxX);
			int y = Util.randomBetween(minY, maxY);
			if (!includeCenter && x == center.x && y == center.y) {
				continue;
			}
			if (cell[x][y].terrain.isPassable() && cell[x][y].terrain.isSpawnable()) {
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

	public Point findOpenTile() {
		for (int i=0; i<10000; i++) {
			// TODO not stochastic?
			int x = Game.random.nextInt(width);
			int y = Game.random.nextInt(height);
			if (cell[x][y].terrain.isPassable() && cell[x][y].terrain.isSpawnable()) {
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

	// no movers on the tile
	public Point findEmptyTileInRoom(int roomId) {
		List<Point> points = roomMap.get(roomId);
		if (points == null || points.isEmpty()) {
			return null;
		}

		Collections.shuffle(points);
		for (Point p : points) {
			if (cell(p).terrain.isPassable()) { // should always be true...
				if (getEntitiesOnTile(p).isEmpty()) {
					return p;
				}
			}
		}
		return null;
	}

	// no movers on the tile
	public List<Point> findEmptyTilesInRoom(int roomId) {
		List<Point> points = roomMap.get(roomId);
		if (points == null || points.isEmpty()) {
			return null;
		}

		ArrayList<Point> found = new ArrayList<>();
		Collections.shuffle(points);
		for (Point p : points) {
			if (cell(p).terrain.isPassable()) { // should always be true...
				if (getEntitiesOnTile(p).isEmpty()) {
					found.add(p);
				}
			}
		}
		return found;
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
			boolean foundMover = false;
			if (cell[x][y].terrain.isPassable() && cell[x][y].visible() == false) {
				for (Entity e : getEntitiesOnTile(new Point(x, y))) {
					if (e.getMover() != null) {
						foundMover = true;
					}
				}
				if (foundMover) {
					continue;
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
			if (cell[xRand][yRand].terrain.isPassable() && !cell[xRand][yRand].visible()) {
				if (!getMoversOnTile(new Point(xRand, yRand)).isEmpty()) {
					continue;
				}
				return new Point(xRand, yRand);
			}
		}
		return null;
	}

	public List<Point> getEmptyRoomMapOpenFloor(int roomId) {
		ArrayList<Point> foundPoints = new ArrayList<>();
		for (Point p : findEmptyTilesInRoom(roomId)) {
			boolean isValid = true;
			for (Point surrounding : surroundingTiles(p)) {
				if (!cell(surrounding).terrain.isPassable()) {
					isValid = false;
				}
			}
			if (isValid) {
				foundPoints.add(p);
			}
		}
		return foundPoints;
	}

	public List<Point> getEmptyRoomMapAlongWall(int roomId) {
		// TODO find a better way of hating doorways
		Terrain doorway = Terrain.get("doorway");
		ArrayList<Point> foundPoints = new ArrayList<>();
		for (Point p : findEmptyTilesInRoom(roomId)) {
			boolean isValid = false;
			for (Point surrounding : surroundingTiles(p)) {
				if (cell(surrounding).terrain == doorway) {
					isValid = false;
					break;
				}
				if (!cell(surrounding).terrain.isPassable()) {
					isValid = true;
				}
			}
			if (isValid) {
				foundPoints.add(p);
			}
		}
		return foundPoints;
	}

	public Point getRoomUpperLeft(int roomId) {
		int x = Integer.MAX_VALUE;
		int y = Integer.MAX_VALUE;
		for (Point p : roomMap.get(roomId)) {
			if (p.x < x) {
				x = p.x;
			}
			if (p.y < y) {
				y = p.y;
			}
		}
		if (x < Integer.MAX_VALUE) {
			return new Point(x, y);
		}
		return null;
	}

	public Point getRoomLowerRight(int roomId) {
		int x = -1;
		int y = -1;
		for (Point p : roomMap.get(roomId)) {
			if (p.x > x) {
				x = p.x;
			}
			if (p.y > y) {
				y = p.y;
			}
		}
		if (x > -1) {
			return new Point(x, y);
		}
		return null;
	}

	public float getJitterAt(Point p) {
		if (!withinBounds(p)) {
			return 0f;
		}
		float diminishPower = 0.7f;
		float floor = 0.3f;
		float totalJitter = 0f;
		for (Point jitterPoint : jitters.keySet()) {
			float distance = p.distance(jitterPoint);
			totalJitter += jitters.get(jitterPoint) * Math.pow(diminishPower, distance);
		}
		if (totalJitter > floor) {
			return totalJitter;
		}
		return 0f;
	}

	public void recalculateJitter() {
		jitters.clear();
		for (EntityProc ep : getEntityProcs()) {
			Float jitter = ep.proc.getJitter(ep.entity);
			if (jitter != null) {
				jitters.put(ep.entity.pos, jitter);
			}
		}
	}

	public int getMinThreat() {
		return getMaxThreat() / 2;
	}

	public int getMaxThreat() {
		return (Game.getPlayerEntity().level / 2) + Game.getLevel().threat + 1;
	}
}
