package com.bigsagebeast.hero.roguelike.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.bigsagebeast.hero.GameLoop;
import com.bigsagebeast.hero.HeroGame;
import com.bigsagebeast.hero.MusicPlayer;
import com.bigsagebeast.hero.SetupException;
import com.bigsagebeast.hero.chat.ChatLink;
import com.bigsagebeast.hero.dialogue.ChatBox;
import com.bigsagebeast.hero.enums.Gender;
import com.bigsagebeast.hero.enums.Stat;
import com.bigsagebeast.hero.module.TargetingModule;
import com.bigsagebeast.hero.persistence.Persistence;
import com.bigsagebeast.hero.persistence.PersistentProfile;
import com.bigsagebeast.hero.roguelike.world.*;
import com.bigsagebeast.hero.roguelike.world.dungeon.DungeonPlan;
import com.bigsagebeast.hero.roguelike.world.dungeon.Level;
import com.bigsagebeast.hero.roguelike.world.dungeon.Room;
import com.bigsagebeast.hero.enums.Satiation;
import com.bigsagebeast.hero.roguelike.world.dungeon.generation.Themepedia;
import com.bigsagebeast.hero.roguelike.world.proc.Proc;
import com.bigsagebeast.hero.roguelike.world.proc.ProcMover;
import com.bigsagebeast.hero.roguelike.world.proc.environment.ProcDoor;
import com.bigsagebeast.hero.roguelike.world.proc.item.ProcItem;
import com.bigsagebeast.hero.roguelike.world.proc.item.ProcWeaponAmmo;
import com.bigsagebeast.hero.roguelike.world.proc.item.ProcWeaponMelee;
import com.bigsagebeast.hero.roguelike.world.proc.item.ProcWeaponRanged;
import com.bigsagebeast.hero.roguelike.world.proc.monster.ProcShooter;
import com.bigsagebeast.hero.roguelike.world.proc.unique.ProcFirstQuestFinalBoss;
import com.bigsagebeast.hero.util.Compass;
import com.bigsagebeast.hero.util.Fov;
import com.bigsagebeast.hero.util.Point;
import com.bigsagebeast.hero.roguelike.world.BodyPart;
import com.bigsagebeast.hero.util.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Game {
	// current level
	private static Level level;
	public static HashMap<String, DungeonPlan> dungeonPlans = new HashMap<String, DungeonPlan>();
	private static Player player = new Player();
	public static final DungeonGenerator dungeon = new DungeonGenerator();
	public static final UnidMapping unidMapping = new UnidMapping();
	public static long time = 0;
	public static long lastTurnProc = 0;
	public static Random random = new Random();
	public static final int ONE_TURN = 1000;
	public static boolean initialized = false;

	public static boolean interrupted;
	public static boolean paused;
	public static String pauseMessage;
	public static int restTurns; // if > 0, we're waiting in place
	public static Compass longWalkDir = null; // if non-null, we're long-walking

	public static Spellbook spellbook = new Spellbook();

	public static Entity lastSelectedInventory; // for use with throwing, maybe others, but not default
	public static String deathMessage;

	public static void initialize() {
		time = 0;
		lastTurnProc = 0;
		if (!initialized) {
			initialized = true;
			try {
				BodyPlanpedia.initialize();
				// TODO: Rescan these when starting a new run
			} catch (SetupException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public static void loadFiles() {
		try {
			ArrayList<FileHandle> files = new ArrayList<>();
			ArrayList<FileHandle> dirs = new ArrayList<>();
			dirs.add(Gdx.files.internal("defs"));

			Itempedia.map.clear();
			Bestiary.map.clear();
			Themepedia.map.clear();
			LoadingTips.tips.clear();

			FileHandle assetDefsHandle = Gdx.files.internal("assets-defs.txt");
			BufferedReader reader = new BufferedReader(new InputStreamReader(assetDefsHandle.read()));
			List<String> defFilePaths = new ArrayList<>();
			while (true) {
				String line;
				try {
					line = reader.readLine();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				if (line == null) break;
				if (line.length() > 0) {
					defFilePaths.add(line);
				}
			}

			while (!dirs.isEmpty()) {
				FileHandle[] dirFiles = dirs.remove(0).list();
				for (FileHandle dirFile : dirFiles) {
					if (dirFile.isDirectory()) {
						dirs.add(dirFile);
					} else {
						files.add(dirFile);
					}
				}
			}
			if (files.size() > 0 && files.size() != defFilePaths.size()) {
				throw new RuntimeException("Incorrect assets-defs manifest! " + files.size() + " vs " + defFilePaths.size());
			}
			for (String defFilePath : defFilePaths) {
				FileHandle defFileHandle = Gdx.files.internal(defFilePath);
				DefinitionLoader.loadFile(defFileHandle);
			}

			initializeItems();
		} catch (SetupException e) {
			throw new RuntimeException(e);
		}
	}

	public static void initializeItems() throws SetupException {
		unidMapping.scan();
		unidMapping.randomize();
		unidMapping.apply();
	}

	public static void startIntro() {
		MusicPlayer.playLoop();
		dungeon.levels.clear();
		loadFiles();
		resetPlayer();
		time = 0;
		Entity pc = Bestiary.create("pc.farmboy");
		pc.statblock.set(Stat.ARCANUM, 5);
		pc.statblock.set(Stat.AVATAR, 0);
		pc.recalculateSecondaryStats();
		player.setEntityId(pc.entityId);
		Entity pitchfork = Itempedia.create("pitchfork");
		dungeon.generateFromFile("start", "start.fhm");
		dungeon.getLevel("start").setFriendlyName("Besieged Farm");
		dungeon.getLevel("start").ambientLight = 15;
		dungeon.getLevel("start").tags.add("tutorial");
		dungeon.generateFromFile("cave-entry", "cave-entry.fhm");
		dungeon.getLevel("cave-entry").setFriendlyName("Goblin cave entrance");
		dungeon.getLevel("cave-entry").ambientLight = 15;
		dungeon.getLevel("cave-entry").tags.add("tutorial");
		dungeon.generateFromFile("cave", "cave.fhm");
		dungeon.getLevel("cave").setFriendlyName("Goblin caves");
		dungeon.getLevel("cave").tags.add("tutorial");
		changeLevel(dungeon.getLevel("start"), new Point(26, 27));
		level.addEntityWithStacking(pitchfork, new Point(29, 24));
		level = dungeon.getLevel("start");
		Level cave = dungeon.getLevel("cave");
		level.prepare();

		ChatBox chatBox = new ChatBox()
				.withMargins(60, 60)
				.withTitle("Tutorial", null)
				.withText("The First Hero is a turn-based game. To move or attack, use all eight directions of the numpad. If you don't have a full numpad, alt-left and right move in the northern diagonals, and ctrl-left and right move in the southern diagonals. Watch the 'Announcements' window for more information.");

		ArrayList<ChatLink> links = new ArrayList<>();
		ChatLink linkOk = new ChatLink();
		linkOk.text = "OK";
		links.add(linkOk);

		GameLoop.chatModule.openArbitrary(chatBox, links);
	}

	public static void startAurex() {
		MusicPlayer.playAurex();
		dungeon.levels.clear();
		loadFiles();
		resetPlayer();
		time = 0;
		Entity pc = Bestiary.create("pc.deity");
		pc.name = Profile.getString("godName");
		pc.recalculateSecondaryStats();
		player.setEntityId(pc.entityId);
		dungeon.generateFromFile("aurex", "aurex.fhm");
		dungeon.getLevel("aurex").setFriendlyName("Aurex, Realm of the Gods");
		dungeon.getLevel("aurex").ambientLight = 15;
		changeLevel(dungeon.getLevel("aurex"), new Point(106, 30));
		level.addEntityWithStacking(Itempedia.create("feature.worldportal"), new Point(88, 25));
		level.prepare();
		if (GameLoop.roguelikeModule.isRunning()) {
			GameLoop.roguelikeModule.end();
		}
		GameLoop.roguelikeModule.start();
	}


	public static void handleStartCaves(Entity pc) {
		MusicPlayer.playLoop();
		dungeon.levels.clear();
		pc.name = Profile.getString("godName") + "'s avatar";
		time = 0;
		player.setEntityId(pc.entityId);

		dungeonPlans.clear();
		dungeonPlans.put("dungeon", new DungeonPlan(8));

		dungeon.generateClassic("dungeon.1");
		changeLevel("dungeon.1", "out");
		level.prepare();
		if (GameLoop.roguelikeModule.isRunning()) {
			GameLoop.roguelikeModule.end();
		}
		GameLoop.roguelikeModule.start();
	}

	public static void startCaves() {
		loadFiles();
		time = 0;
		resetPlayer(); // redundant with cb
		CharacterBuilder cb = new CharacterBuilder(Game::handleStartCaves);
		cb.begin();
	}

	public static void changeLevel(Level nextLevel, Point playerPos) {
		changeLevelGeneral();

		level = nextLevel;
		if (!level.tags.contains("tutorial")) {
			LoadingTips.showNextTip();
		}
		level.addEntityWithStacking(player.getEntity(), playerPos, false);

		GameLoop.glyphEngine.initializeLevel(level);
		level.prepare();
		interrupted = false;
		passTime(0);
	}

	public static void changeLevel(String toKey, String fromKey) {
		changeLevelGeneral();

		// TODO hack
		if (toKey.equals("dungeon.8") && !ProcFirstQuestFinalBoss.dead) {
			MusicPlayer.playBoss();
		} else if (toKey.startsWith("dungeon")) {
			MusicPlayer.playLoop();
		}

		Level nextLevel = dungeon.getLevel(toKey);
		level = nextLevel;
		if (!level.tags.contains("tutorial")) {
			LoadingTips.showNextTip();
		}
		Point playerPos = nextLevel.findTransitionTo(fromKey).pos;
		level.addEntityWithStacking(player.getEntity(), playerPos, false);
		level.prepare();
		interrupted = false;

		GameLoop.glyphEngine.initializeLevel(level);
		/*
		for (Entity e : level.getEntities()) {
			System.out.println(e + " " + e.roomId);
		}
		for (Room r : level.rooms) {
			System.out.println(r.roomId + " " + r);
		}
		 */
		passTime(0);
	}

	public static void changeLevel(Level nextLevel) {
		changeLevelGeneral();

		level = nextLevel;
		if (!level.tags.contains("tutorial")) {
			LoadingTips.showNextTip();
		}
		GameLoop.glyphEngine.initializeLevel(level);
		level.prepare();
		interrupted = false;
		passTime(0);
	}

	private static void changeLevelGeneral() {
		player.getEntity().roomId = -1;
		if (level != null) {
			for (EntityProc tuple : level.getEntityProcs()) {
				if (tuple.proc.hasAction()) {
					tuple.proc.nextAction = tuple.proc.nextAction - Game.time;
				}
				if (tuple.proc.getClass().isAssignableFrom(ProcMover.class)) {
					((ProcMover)tuple.proc).lastAttackedByPlayer -= Game.time;
				}
			}
			level.removeEntity(player.getEntity());
		}
		Game.time = 0;
		Game.lastTurnProc = 0;
	}

	// TODO should specify a profile name or slot or something
	public static void load() {
		PersistentProfile profile = Persistence.loadProfile();
		profile.load();
		Level loadedlevel = Persistence.loadLevel(profile.levelName);
		changeLevel(loadedlevel);
	}
	
	public static Level getLevel() {
		return level;
	}
	
	public static Player getPlayer() {
		return player;
	}

	public static void resetPlayer() { player = new Player(); }

	public static Entity getPlayerEntity() {
		return player.getEntity();
	}

	public static void feelMsg(Entity entity, String message) {
		if (player.isEntity(entity)) {
			//emitMessage(message);
		}
	}
	
	public static void turn() {
		if (getPlayerEntity() == null) {
			return;
		}
		HeroGame.resetTimer("astar");
		HeroGame.resetTimer("fov");
		// at the start of the turn, the player has just acted
		for (Proc p : getPlayerEntity().procs) {
			p.onAction(getPlayerEntity());
		}
		GameLoop.roguelikeModule.redraw();

		// TODO maybe check state here? like death etc

		while (true) {
			GameLoop.roguelikeModule.setDirty();

			level.timePassed(time);

			// TODO getProcEntities needs a variant that gets ALL procs including on inventory,
			// maybe filtered by ones that have an 'act' or a 'turnPassed'
			while (lastTurnProc + ONE_TURN < time) {
				lastTurnProc += ONE_TURN;
				for (EntityProc tuple : level.getEntityProcs()) {
					tuple.proc.turnPassed(tuple.entity);
				}
			}

			long lowestTurn = -1;
			long secondLowestTurn = -1;
			EntityProc lowestProc = null;
			for (EntityProc tuple : level.getEntityProcs()) {
				if (tuple.proc.nextAction != -1 && (lowestTurn == -1 || tuple.proc.nextAction < lowestTurn)) {
					lowestTurn = tuple.proc.nextAction;
					lowestProc = tuple;
				} else if (tuple.proc.nextAction != -1 && (secondLowestTurn == -1 || tuple.proc.nextAction < secondLowestTurn)) {
					secondLowestTurn = tuple.proc.nextAction;
				}
			}
			time = lowestTurn;
			if (lowestProc.proc == player.getEntity().getMover()) {
				tryLongTaskAction();
				if (paused) {
					// This isn't in PauseModule, to make messages go in the right order
					if (pauseMessage != null) {
						Game.announce(pauseMessage);
					}
					Game.announceLoud("-- Press ENTER to continue --");
					paused = false;
				}
				break;
			}
			//lowestProc.entity.visionRange
			int vision = lowestProc.entity.incorporeal ? 99 : level.ambientLight;
			Fov.calculateFOV(level, vision, lowestProc.entity);
			lowestProc.proc.act(lowestProc.entity);
			for (Proc onActProc : lowestProc.entity.procs) {
				onActProc.onAction(lowestProc.entity);
			}
		}
	}

	public static void passTime(int delay) {
		getPlayerEntity().getMover().setDelay(getPlayerEntity(), delay);
		turn();
	}

	public static void interrupt() {
		interrupted = true;
	}

	public static void interruptAndBreak(String pauseMessage) {
		interrupt();
		paused = true;
		Game.pauseMessage = pauseMessage;
		GameLoop.pauseModule.begin(null);
	}

	public static void interruptAndBreak(String pauseMessage, Runnable runnable) {
		interrupt();
		paused = true;
		Game.pauseMessage = pauseMessage;
		GameLoop.pauseModule.begin(runnable);
	}
	public static boolean hasLongTask() {
		return restTurns > 0 || longWalkDir != null;
	}

	// return true if still resting, false if not resting / interrupted
	private static boolean tryLongTaskAction() {
		if (interrupted && (restTurns > 0 || longWalkDir != null)) {
			announce("You are interrupted.");
			interrupted = false;
			restTurns = 0;
			longWalkDir = null;
			return false;
		}
		interrupted = false;

		if (restTurns-- > 0) {
			if (hasInterruption()) {
				announce("You are interrupted.");
				restTurns = 0;
				return false;
			} else if (restTurns == 0) {
				announce("You finish resting.");
				return false;
			} else {
				// TODO: This is putting a ton on the stack!
				Game.passTime(Game.ONE_TURN);
			}
			return true;
		} else if (longWalkDir != null) {
			if (hasInterruption()) {
				announce("You are interrupted.");
				longWalkDir = null;
				return false;
			}
			if (!canMoveBy(getPlayerEntity(), longWalkDir)) {
				//announce("You finish walking.");
				longWalkDir = null;
				return false;
			}
			if (!level.cell(longWalkDir.from(getPlayerEntity().pos)).terrain.isSafe()) {
				longWalkDir = null;
				return false;
			}

			else {
				playerCmdMoveBy(longWalkDir);
				return true;
			}
		}
		return false;
	}

	public static void cmdMoveLeft() {
		playerCmdMoveBy(-1, 0);
	}
	
	public static void cmdMoveRight() {
		playerCmdMoveBy(+1, 0);
	}
	
	public static void cmdMoveUp() {
		playerCmdMoveBy(0, -1);
	}
	
	public static void cmdMoveDown() {
		playerCmdMoveBy(0, +1);
	}
	
	public static void cmdMoveUpLeft() {
		playerCmdMoveBy(-1, -1);
	}
	
	public static void cmdMoveDownLeft() {
		playerCmdMoveBy(-1, +1);
	}
	
	public static void cmdMoveDownRight() {
		playerCmdMoveBy(+1, +1);
	}
	
	public static void cmdMoveUpRight() {
		playerCmdMoveBy(+1, -1);
	}
	
	public static void cmdStairsUp() {
		for (Entity e : level.getEntitiesOnTile(getPlayerEntity().pos)) {
			for (Proc p : e.procs) {
				Boolean result = p.stairsUp(e, getPlayerEntity());
				if (result != null) {
					return;
				}
			}
		}
		announce("You can't go up here.");
	}

	public static void cmdStairsDown() {
		for (Entity e : level.getEntitiesOnTile(getPlayerEntity().pos)) {
			for (Proc p : e.procs) {
				Boolean result = p.stairsDown(e, getPlayerEntity());
				if (result != null) {
					return;
				}
			}
		}
		announce("You can't go down here.");
	}

	public static void cmdPickup() {
		List<Entity> itemsHere = level.getItemsOnTile(player.getEntity().pos);
		itemsHere = itemsHere.stream().filter(i -> !i.getItemType().isFeature).collect(Collectors.toList());
		if (itemsHere.isEmpty()) {
			announce("There's nothing here to pick up.");
			return;
		}
		if (itemsHere.size() == 1) {
			if (itemsHere.get(0).getItem().quantity == 1) {
				pickupWithQuantity(itemsHere.get(0), 1);
			} else if (itemsHere.get(0).getItemType().keyName == "gold") {
				// TODO are there other items that we want to pick up all automatically?
				pickupWithQuantity(itemsHere.get(0), itemsHere.get(0).getItem().quantity);
			} else {
				Inventory.handleFloorToGetResponse(itemsHere.get(0));
			}
		} else {
			Inventory.openFloorToGet();
		}
	}

	public static void pickupWithQuantity(Entity entity, int quantity) {
		if (quantity == 0) {
			return;
		}
		if (!player.getEntity().pickupItemWithQuantity(entity, quantity)) {
			// TODO: This message will be wrong if we attempt to pick up a small number of them
			// Maybe this message needs to happen elsewhere?
			announce("You can't pick up " + entity.getVisibleNameDefinite() + ".");
		} else {
			passTime(Game.ONE_TURN);
		}
	}

	public static void cmdWait() {
		if (player.getEntity().isConfused()) {
			playerCmdMoveBy(0, 0);
		} else {
			player.getEntity().getMover().setDelay(getPlayerEntity(), ONE_TURN);
		}
	}

	public static void cmdRest() {
		if (hasInterruption()) {
			announce("You can't rest right now.");
		} else {
			restTurns = 50;
			player.getEntity().getMover().setDelay(getPlayerEntity(), ONE_TURN);
		}
	}

	public static void cmdLongWalk(Compass dir) {
		if (hasInterruption()) {
			announce("You are interrupted.");
			return; // TODO does this skip a turn?
		}
		longWalkDir = dir;
		playerCmdMoveBy(dir);
	}

	public static void cmdWield() {
		Inventory.doWield();
	}

	public static void cmdQuaff() {
		Inventory.doQuaff();
	}

	public static void cmdRead() {
		Inventory.doRead();
	}

	public static void cmdInventory() {
		Inventory.openInventoryToInspect();
	}

	public static void cmdDrop() {
		Inventory.openInventoryToDrop();
	}

	public static void cmdEat() {
		if (player.getSatiationStatus() == Satiation.STUFFED) {
			announce("You are too stuffed to eat!");
			return;
		}
		Inventory.openInventoryToEat();
	}

	public static void cmdRegenerate() { startCaves(); }

	public static void cmdOpen() {
		GameLoop.directionModule.begin("Open or close a door in what direction?", Game::cmdOpenHandle);
	}

	public static void cmdOpenHandle(Compass dir) {
		if (dir == Compass.OTHER) {
			Game.announce("Canceled.");
			return;
		}
		boolean confused = getPlayerEntity().isConfused();
		if (confused) {
			dir = Compass.randomDirection();
		}
		boolean somethingToHandle = false;
		Point targetPoint = dir.from(player.getEntity().pos);
		for (Entity target : level.getEntitiesOnTile(targetPoint)) {
			if (target.tryOpen(player.getEntity())) {
				somethingToHandle = true;
			}
		}
		if (somethingToHandle) {
			passTime(player.getEntity().moveCost);
		} else if (!confused) {
			announce("There's nothing there to open.");
		} else {
			announce("You fail to grasp a door!");
			passTime(player.getEntity().moveCost);
		}
	}

	public static void cmdChat() {
		GameLoop.directionModule.begin("Chat in what direction?", Game::cmdChatHandle);
	}

	public static void cmdChatHandle(Compass dir) {
		if (dir == Compass.OTHER) {
			Game.announce("Canceled.");
			return;
		}
		Point targetPoint = dir.from(player.getEntity().pos);
		for (Entity target : level.getEntitiesOnTile(targetPoint)) {
			// TODO
			if (target.getMover() != null) {
				String chatPage = Bestiary.get(target.phenotypeName).chatPage;
				if (chatPage == null) {
					Game.announce(target.getVisibleNameDefinite() + " won't talk to you.");
				} else {
					GameLoop.chatModule.openStory(target);
				}
				passTime(player.getEntity().moveCost);
				return;
			}
		}
		Game.announce("There's nobody there.");
	}


	public static void cmdSave() {
		Persistence.saveLevel(level);
		Persistence.saveProfile();
	}

	public static void cmdLoad() {

	}

	public static void cmdMagic() {
		spellbook.openSpellbookToCast();
	}

	public static void cmdTarget() {
		Entity rangedWeapon = getPlayerEntity().body.getEquipment(BodyPart.RANGED_WEAPON);
		Entity rangedAmmo = getPlayerEntity().body.getEquipment(BodyPart.RANGED_AMMO);
		ProcWeaponRanged pwr = (rangedWeapon != null) ? (ProcWeaponRanged)rangedWeapon.getProcByType(ProcWeaponRanged.class) : null;
		ProcWeaponAmmo pwa = (rangedAmmo != null) ? (ProcWeaponAmmo)rangedAmmo.getProcByType(ProcWeaponAmmo.class) : null;

		if (rangedWeapon != null && pwr == null) {
			announce("Invalid ranged weapon.");
			GameLoop.error("Invalid ranged weapon.");
			return;
		}
		if (rangedAmmo != null && pwa == null) {
			announce("Invalid ranged ammo.");
			GameLoop.error("Invalid ranged ammo.");
			return;
		}

		if (pwr == null && pwa == null) {
			announce("You need a ranged weapon and ammo equipped, or throwable ammo!");
			return;
		} else if (pwa == null) {
			announce("Out of ammo.");
			return;
		} else if (pwr == null && !pwa.canThrow) {
			announce("You need a ranged weapon to use that ammo.");
			return;
		} else if (pwr != null && pwa.ammoType != pwr.ammoType) {
			announce("Wrong ammo type for that ranged weapon.");
			return;
		}

		int range;
		if (pwr != null) {
			range = pwr.range;
		} else {
			range = pwa.throwRange;
		}
		TargetingModule.TargetMode tm = GameLoop.targetingModule.new TargetMode(false, true, true, true, range);
		GameLoop.targetingModule.begin(tm, Game::handleTarget);
	}

	public static void cmdThrow() {
		Inventory.openInventoryToThrow();
	}

	public static void handleThrowInventory(Object selectedObject) {
		if (selectedObject != null) {
			lastSelectedInventory = (Entity)selectedObject;
			int range = 6;
			TargetingModule.TargetMode tm = GameLoop.targetingModule.new TargetMode(false, true, true, true, range);
			GameLoop.targetingModule.begin(tm, range, Game::handleThrowTarget);
		}
	}

	public static void handleThrowTarget(Point target) {
		Entity thrownEntity;
		ProcItem pi = lastSelectedInventory.getItem();
		if (pi.quantity == 1) {
			thrownEntity = lastSelectedInventory;
		} else {
			thrownEntity = lastSelectedInventory.split(1);
		}

		if (target != null) {
			Entity targetMover = level.moverAt(target);
			if (thrownEntity.getItemType().category == ItemCategory.CATEGORY_POTION) {
				announce(thrownEntity.getVisibleNameDefinite() + " shatters!");
				if (targetMover != null) {
					ProcMover pm = targetMover.getMover();
					// TODO: Only for harmful effects
					pm.logRecentlyAttacked();
					// skip preBeQuaffed
					for (Proc p : thrownEntity.procs) {
						p.postBeQuaffed(thrownEntity, targetMover);
					}
				}
				thrownEntity.destroy();
			} else {
				if (targetMover != null) {
					// TODO hit things with weapons or whatever
					announce(thrownEntity.getVisibleNameDefinite() + " misses " + targetMover.getVisibleNameDefinite() + ".");
				}
				level.addEntityWithStacking(thrownEntity, target);
			}
		}
		passTime(ONE_TURN);
	}

	public static void cmdPray() {
		boolean prayingAtProc = false;
		for (Entity e : getLevel().getEntitiesOnTile(getPlayerEntity().pos)) {
			for (Proc p : e.procs) {
				Boolean canPray = p.canPrayAt(e, getPlayerEntity());
				if (canPray == Boolean.TRUE) {
					prayingAtProc = true;
					p.prayAt(e, getPlayerEntity());
					break;
				}
			}
			if (prayingAtProc) {
				// only pray at the first prayable proc we find
				break;
			}
		}
		if (!prayingAtProc) {
			Game.announce("You pray.  Nothing happens yet.");
		}
	}

	public static void handleTarget(Point targetPoint) {
		if (targetPoint == null) {
			announce("Cancelled.");
			return;
		}
		Entity target = level.moverAt(targetPoint.x, targetPoint.y);
		// Targeting yourself is like canceling your shot
		if (target == getPlayerEntity()) {
			announce("No target.");
			return;
		}

		Entity rangedWeapon = getPlayerEntity().body.getEquipment(BodyPart.RANGED_WEAPON);
		Entity rangedAmmo = getPlayerEntity().body.getEquipment(BodyPart.RANGED_AMMO);

		ProcItem pia = rangedAmmo.getItem();
		Entity shotEntity;
		boolean lastShot = false;
		if (pia.quantity == 1) {
			shotEntity = rangedAmmo;
			getPlayerEntity().body.putEquipment(BodyPart.RANGED_AMMO, null);
			lastShot = true;
		} else {
			shotEntity = rangedAmmo.split(1);
		}

		if (target != null) {
			CombatLogic.shoot(player.getEntity(), target, rangedWeapon, shotEntity);
			target.getMover().logRecentlyAttacked();
		}
		if (lastShot) {
			announce("You're out of " + shotEntity.getVisiblePluralName() + ".");
		}

		//TODO break chance per ammo
		if (Game.random.nextInt(100) < 75) {
			level.addEntityWithStacking(shotEntity, targetPoint);
		}
		GameLoop.targetingModule.animate(getPlayerEntity().pos, targetPoint);
		passTime(player.getEntity().moveCost);
	}

	public static void npcShoot(Entity actor, Point targetPoint) {
		Proc procShooter = actor.getProcByType(ProcShooter.class);
		String itemKeyAmmo = procShooter.provideProjectile();
		if (itemKeyAmmo == null) {
			// TODO debug message and abort safely?
			throw new RuntimeException("No ammo type for shooter: " + actor.name);
		}
		Entity oneAmmo = Itempedia.create(itemKeyAmmo, 1);
		GameLoop.targetingModule.animate(actor.pos, targetPoint);
		npcShootConsequence(actor, targetPoint, oneAmmo);
	}

	// later support for passing this method to 'animate', so it gets called afterwards
	public static void npcShootConsequence(Entity actor, Point targetPoint, Entity ammo) {
		Entity rangedWeapon = actor.body.getEquipment(BodyPart.RANGED_WEAPON);
		Entity targetEntity = level.moverAt(targetPoint.x, targetPoint.y);

		if (targetPoint != null) {
			CombatLogic.shoot(actor, targetEntity, rangedWeapon, ammo);
		}
		actor.getMover().setDelay(actor, actor.moveCost);
		// TODO break chance for ammo
		if (Game.random.nextInt(100) < 50) {
			level.addEntityWithStacking(ammo, targetPoint);
		}
	}

	public static void cmdLook() {
		TargetingModule.TargetMode tm = GameLoop.targetingModule.new TargetMode(true, false, false, false, -1);
		GameLoop.targetingModule.begin(tm, null);
	}

	public static void playerCmdMoveBy(Compass dir) {
		playerCmdMoveBy(dir.getX(), dir.getY());
	}

	public static void playerCmdMoveBy(int dx, int dy) {
		if (getPlayerEntity().isConfused()) {
			Compass dir = Compass.randomDirection();
			dx = dir.getX();
			dy = dir.getY();
			Game.announce("You are confused!");
		}
		int tx = getPlayerEntity().pos.x + dx;
		int ty = getPlayerEntity().pos.y + dy;

		Entity targetCreature = level.moverAt(tx, ty);
		if (targetCreature != null) {
			ProcMover targetMover = targetCreature.getMover();
			if (targetMover.isPeacefulToPlayer(targetCreature)) {
				announce("You bump into " + targetCreature.getVisibleNameWithQuantity() + ". (Press 'c' to chat.)");
			} else {
				attack(player.getEntity(), targetCreature);
				player.getEntity().getMover().setDelay(getPlayerEntity(), player.getEntity().moveCost);
			}
			return;
		}

		if (!level.cell(tx, ty).terrain.isPassable()) {
			if (level.cell(tx, ty).terrain.isBumpInto()) {
				announce("You bump into " + level.cell(tx, ty).terrain.getDescription() + ".");
			} else {
				announce("You can't go that way.");
			}
			if (player.getEntity().isConfused()) {
				player.getEntity().getMover().setDelay(getPlayerEntity(), player.getEntity().moveCost);
			}
			return;
		}

		for (Entity e : level.getEntitiesOnTile(new Point(tx, ty))) {
			if (e.isObstructive()) {
				if (!player.getEntity().isConfused() && e.containsProc(ProcDoor.class)) {
					e.tryOpen(player.getEntity());
					passTime(player.getEntity().moveCost);
				} else {
					announce("You bump into " + e.getVisibleNameDefinite() + ".");
				}
				if (player.getEntity().isConfused()) {
					player.getEntity().getMover().setDelay(getPlayerEntity(), player.getEntity().moveCost);
				}
				return;
			}
		}

		movePlayer(tx, ty);

		if (level.cell(player.getEntity().pos).terrain.getName().equals("water")) {
			announce("You flail around in the water.");
			player.getEntity().getMover().setDelay(getPlayerEntity(), player.getEntity().moveCost * 2L);
		} else {
			player.getEntity().getMover().setDelay(getPlayerEntity(), player.getEntity().moveCost);
		}
	}

	public static boolean pushBy(Entity actor, int dx, int dy) {
		int tx = actor.pos.x + dx;
		int ty = actor.pos.y + dy;
		if (!level.cell(tx, ty).terrain.isPassable()) {
			return false;
		}
		for (Entity e : level.getEntitiesOnTile(new Point(tx, ty))) {
			if (e.isObstructive()) {
				return false;
			}
		}
		moveNpc(actor, tx, ty);
		return true;
	}

	public static void npcMoveBy(Entity actor, ProcMover pm, Compass dir) {
		npcMoveBy(actor, pm, dir.getX(), dir.getY());
	}

	public static void npcMoveBy(Entity actor, ProcMover pm, int dx, int dy) {
		int tx = actor.pos.x + dx;
		int ty = actor.pos.y + dy;
		
		pm.setDelay(actor, actor.moveCost);
		
		//Entity targetCreature = level.moverAt(tx, ty);

		Point t = new Point(tx, ty);
		for (Entity target : level.getEntitiesOnTile(t)) {
			ProcDoor door = (ProcDoor)target.getProcByType(ProcDoor.class);
			if (door != null && !door.isOpen) {
				// TODO some creatures can destroy doors?
				if (actor.isManipulator) {
					target.tryOpen(actor);
				} else {
					/*
					// TODO should depend on creature type; floating eyes shouldn't scratch at doors
					Game.announceVis(actor, actor, null, null,
							actor.getVisibleName() + " scratches at " + door.entity.getVisibleName() + ".",
							"You hear something scratching at a door.");
					 */
				}
				return;
			}
		}

		if (Game.tryMoveTo(actor, t)) {
			moveNpc(actor, tx, ty);
		}
	}

	public static void npcAttack(Entity actor, ProcMover pm, int dx, int dy) {
		int tx = actor.pos.x + dx;
		int ty = actor.pos.y + dy;

		Entity targetCreature = level.moverAt(tx, ty);
		if (targetCreature != null) {
			if (targetCreature.getMover().isPeacefulToPlayer(targetCreature)) {
				announce("Was moved into by a " +
						(targetCreature.getMover().isPeacefulToPlayer(targetCreature) ? "peaceful" : "hostile") +
						" creature (" + actor.getVisibleNameWithQuantity() + ").");
			} else {
				attack(actor, targetCreature);
			}
		}
	}

	public static void attack(Entity actor, Entity target) {
		if (actor == getPlayerEntity()) {
			target.getMover().logRecentlyAttacked();
		}
		// If the first swing is cancelled, don't call anything else.
		// If the first swing hits and penetrates, no second swing.
		// If the first swing hits and doesn't penetrate, use second swing only if it hits.
		// If the first swing misses, use second swing if it doesn't miss.
		Entity weaponPrimary = actor.body.getEquipment(BodyPart.PRIMARY_HAND);
		Entity weaponSecondary = actor.body.getEquipment(BodyPart.OFF_HAND);
		boolean isDualWielding = weaponSecondary != null && weaponSecondary.containsProc(ProcWeaponMelee.class);
		// TODO 2-weapon fighting: split into trySwing, doHit, doMiss
		SwingResult resultSwingOne = CombatLogic.swing(actor, target, weaponPrimary);
		SwingResult resultSwingTwo = null;
		if (resultSwingOne.cancelled) {
			return;
		}
		if (isDualWielding && (!resultSwingOne.hit || !resultSwingOne.penetrationFailed)) {
			resultSwingTwo = CombatLogic.swing(actor, target, weaponSecondary);
		}
		if (resultSwingTwo != null && resultSwingTwo.hit && (!resultSwingTwo.penetrationFailed || !resultSwingOne.hit)) {
			CombatLogic.doHit(actor, target, resultSwingTwo);
		} else {
			if (!resultSwingOne.hit) {
				CombatLogic.doMiss(actor, target, resultSwingOne);
			} else if (resultSwingOne.penetrationFailed) {
				CombatLogic.doPenetrationFailed(actor, target, resultSwingOne);
			} else {
				CombatLogic.doHit(actor, target, resultSwingOne);
			}
		}
	}

	private static void movePlayer(int tx, int ty) {
		Point newPos = new Point(tx, ty);
		player.getEntity().pos = newPos;

		int oldRoomId = player.getEntity().roomId;
		int newRoomId = level.cell(newPos).roomId;
		if (player.getEntity().roomId != newRoomId) {
			Room oldRoom = oldRoomId < 0 ? null : level.rooms.get(oldRoomId);
			Room newRoom = newRoomId < 0 ? null : level.rooms.get(newRoomId);
			player.getEntity().changeRoom(oldRoom, newRoom);
		}

		List<Entity> items = level.getItemsOnTile(newPos);
		List<Entity> itemsToMention = new ArrayList<>(items);
		itemsToMention.removeIf(e -> e.getItemType().hideWalkOver || e.hide);

		if (!items.isEmpty()) {
			longWalkDir = null; // stop when walking over items
			if (!itemsToMention.isEmpty()) {
				StringBuilder listString = new StringBuilder();
				if (itemsToMention.size() == 1 && items.get(0).getItem().quantity == 1) {
					listString.append("There is ");
				} else {
					listString.append("There are ");
				}
				for (int i = 0; i < itemsToMention.size(); i++) {
					listString.append(itemsToMention.get(i).getVisibleNameIndefiniteOrSpecific());
					if (itemsToMention.size() > 1 && i < itemsToMention.size() - 2) {
						listString.append(", ");
					}
					if (i == itemsToMention.size() - 2) {
						listString.append(" and ");
					}
				}
				listString.append(" here.");
				announce(listString.toString());
			}

			for (Entity item : items) {
				for (Proc p : item.procs) {
					p.postBeSteppedOn(item, getPlayerEntity());
				}
				Entity currentAmmo = getPlayerEntity().body.getEquipment(BodyPart.RANGED_AMMO);
				if (currentAmmo != null) {
					if (currentAmmo.canStackWith(item)) {
						announce("You add " + item.getVisibleNameIndefiniteOrSpecific() + " to your ammunition.");
						currentAmmo.beStackedWith(item);
					}
				}
			}
		}

		for (Point adjacent : level.surroundingTiles(getPlayerEntity().pos)) {
			for (Entity e : level.getEntitiesOnTile(adjacent)) {
				e.entityProcs().forEach(ep -> ep.proc.onPlayerMovesAdjacentTo(e));
			}
		}
	}
	
	private static void moveNpc(Entity actor, int tx, int ty) {
		Point newPos = new Point(tx, ty);
		actor.pos = newPos;
		int oldRoomId = actor.roomId;
		int newRoomId = level.cell(newPos).roomId;
		if (actor.roomId != newRoomId) {
			Room oldRoom = oldRoomId < 0 ? null : level.rooms.get(oldRoomId);
			Room newRoom = newRoomId < 0 ? null : level.rooms.get(newRoomId);
			actor.changeRoom(oldRoom, newRoom);
		}
		for (Entity item : level.getItemsOnTile(actor.pos)) {
			for (Proc p : item.procs) {
				p.postBeSteppedOn(item, actor);
			}
		}
	}

	public static boolean canMoveBy(Entity actor, Compass dir) {
		return !level.isBlockedByTerrain(actor, new Point(actor.pos.x + dir.getX(), actor.pos.y + dir.getY())) &&
				!isBlockedByEntity(actor, actor.pos.x + dir.getX(), actor.pos.y + dir.getY());
	}

	public static boolean isBlockedByNonManipulable(Entity actor, int tx, int ty) {
		if (actor.isManipulator) {
			return false;
		}

		for (Entity target : level.getEntitiesOnTile(new Point(tx, ty))) {
			if ((actor.isManipulator && target.isObstructiveToManipulators()) ||
					(!actor.isManipulator && target.isObstructive())) {
				return true;
			}
		}

		return false;
	}

	public static boolean isBlockedByEntity(Entity actor, int tx, int ty) {
		if (level.moverAt(tx, ty) != null) {
			return true;
		}

		for (Entity target : level.getEntitiesOnTile(new Point(tx, ty))) {
			if (target != player.getEntity() && target.isManipulator) {
				if (target.isObstructiveToManipulators()) {
					return true;
				}
			}
			else if (target.isObstructive()) {
				return true;
			}
		}

		return false;
	}

	public static boolean tryMoveTo(Entity e, Point t) {
		if (level.moverAt(t) != null) {
			return false;
		}
		if (level.isBlockedByTerrain(e, t)) {
			return false;
		}

		for (Entity target : level.getEntitiesOnTile(t)) {
			if (target.isObstructive()) {
				announceVis(e, target, "You bump into " + target.getVisibleNameDefinite() + ".",
						e.getVisibleNameDefinite() + " bumps into you.",
						e.getVisibleNameDefinite() + " bumps into " + target.getVisibleNameDefinite() + ".",
						null);
				return false;
			}
		}

		return true;
	}

	public static void announce(String s) {
		if (s == null || !GameLoop.roguelikeModule.isRunning()) {
			return;
		}
		GameLoop.roguelikeModule.announce(Util.capitalize(s));
	}

	public static void announceLoud(String s) {
		if (s == null || !GameLoop.roguelikeModule.isRunning()) {
			return;
		}
		GameLoop.roguelikeModule.announceLoud(Util.capitalize(s));
	}

	public static void unannounce() {
		GameLoop.roguelikeModule.unannounce();
	}
	
	public static void announceVis(Visibility vis, String actor, String target, String visible, String audible) {
		switch (vis) {
		case ACTOR:
			announce(actor);
			return;
		case TARGET:
			announce(target);
			return;
		case VISIBLE:
			announce(visible);
			return;
		case AUDIBLE:
			announce(audible);
			return;
		case NONE:
			return;
		}
	}

	public static void announceSeen(Entity entity, String message) {
		announceVis(entity, null, null, null, message, null);
	}

	public static void announceVis(Entity actorEntity, Entity targetEntity, String actor, String target, String visible, String audible) {
		if (!GameLoop.roguelikeModule.isRunning()) {
			// hack to avoid messages during test duel
			return;
		}
		// TODO: Move vision range somewhere else
		Fov.calculateFOV(level, level.ambientLight, getPlayerEntity());

		if (actorEntity != null && actorEntity.containingEntity < 0) {
			actorEntity = actorEntity.getTopLevelContainer();
		}
		if (targetEntity != null && targetEntity.containingEntity < 0) {
			targetEntity = targetEntity.getTopLevelContainer();
		}
		Gender actorGender = actorEntity == null ? null : actorEntity.gender;
		Gender targetGender = targetEntity == null ? null : targetEntity.gender;
		Entity playerEntity = player.getEntity();
		if (actorEntity != null && actorEntity.containingLevel == null) {
			GameLoop.error("Invalid vis for actor");
			return;
		}
		if (targetEntity != null && targetEntity.containingLevel == null) {
			GameLoop.error("Invalid vis for target");
			return;
		}
		if (playerEntity == actorEntity) {
			announce(Util.substitute(actor, actorGender, targetGender));
		} else if (playerEntity == targetEntity) {
			announce(Util.substitute(target, actorGender, targetGender));
		} else if ((actorEntity != null && playerEntity.canSee(actorEntity)) || (targetEntity != null && playerEntity.canSee(targetEntity))) {
			// TODO this might be a problem if you can't see the other actor or target?
			announce(Util.substitute(visible, actorGender, targetGender));
		} else if (actorEntity != null && playerEntity.canHear(actorEntity)){
			announce(Util.substitute(audible, actorGender, targetGender));
		}
	}

	public static boolean hasInterruption() {
		// TODO: Have a flag that can be set during a turn by things like starvation.
		if (getPlayerEntity().isConfused()) {
			return true;
		}
		for (Entity e : level.getMovers()) {
			if (e != getPlayerEntity() && !e.peaceful && getPlayerEntity().canSee(e)) {
				return true;
			}
		}
		return false;
	}

	public static void die(String deathMessage) {
		Game.deathMessage = deathMessage;
		interruptAndBreak("You have died...", Game::playerDeath);
		turn();
	}

	public static void playerDeath() {
		if (level.tags.contains("tutorial")) {
			announceLoud("A glowing light revives you! You still have a purpose here!");
			getPlayerEntity().dead = false;
			getPlayerEntity().hitPoints = getPlayerEntity().maxHitPoints;
			getPlayer().satiation = Satiation.FULL.topThreshold;
			getPlayer().changeSatiation(Satiation.FULL.topThreshold - getPlayer().satiation);
			turn();
			return;
		}

		ChatBox chatBox = new ChatBox()
				.withMargins(60, 60)
				.withTitle("You have died", null)
				.withText("Goodbye, " + getPlayerEntity().name + ". You were killed by " + deathMessage + ". You achieved level " + getPlayerEntity().level + ".");

		ArrayList<ChatLink> links = new ArrayList<>();
		ChatLink linkOk = new ChatLink();
		linkOk.text = "Return to Aurex";
		linkOk.runnable = Game::handlePlayerDeath;
		linkOk.terminal = true;

		links.add(linkOk);

		GameLoop.chatModule.openArbitrary(chatBox, links);
	}

	public static void handlePlayerDeath() {
		startAurex();
	}
}
