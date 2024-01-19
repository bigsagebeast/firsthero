package com.bigsagebeast.hero.util;

import com.bigsagebeast.hero.HeroGame;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.dungeon.Level;
import com.bigsagebeast.hero.roguelike.world.dungeon.LevelCell;

import java.util.ArrayList;
import java.util.List;

public class Fov {

	/**
	* Calculates the Field Of View for the provided map from the given x, y
	* coordinates. Returns a lightmap for a result where the values represent a
	* percentage of fully lit.
	*
	* A value equal to or below 0 means that cell is not in the
	* field of view, whereas a value equal to or above 1 means that cell is
	* in the field of view.
	*
	* @param radius the maximum distance to draw the FOV
	* @return the computed light grid
	*/
	public static void calculateFOV(Level level, float radius, Entity entity) {
		Fov.level = level;
	    Fov.startx = entity.pos.x;
	    Fov.starty = entity.pos.y;
	    Fov.radius = radius;

		long milliStart = System.currentTimeMillis();

		level.clearTemp();
		boolean isPlayer = entity == Game.getPlayerEntity();
		if (isPlayer) {
			for (LevelCell cell : level.getCellStream()) {
				cell.light = 0f;
			}
			level.cell(startx, starty).light = 10f;
			level.cell(startx, starty).explored = true;
		}

	    for (Compass d : Compass.diagonals()) {
	        castLight(1, 1.0f, 0.0f, 0, d.getX(), d.getY(), 0, isPlayer);
	        castLight(1, 1.0f, 0.0f, d.getX(), 0, 0, d.getY(), isPlayer);
	    }

		entity.visibleEntities.clear();
		// TODO: For some entities, only test against movers?
		for (Entity test : level.getEntities()) {
			if (level.cell(test.pos).temp == Boolean.TRUE) {
				entity.visibleEntities.add(test);
			}
		}
		HeroGame.addTimer("fov", System.currentTimeMillis() - milliStart);
	}

	private static Level level;
	private static int startx;
	private static int starty;
	private static float radius;
	
	private static void castLight(int row, float start, float end, int xx, int xy, int yx, int yy, boolean isPlayer) {
	    float newStart = 0.0f;
	    if (start < end) {
	        return;
	    }
	    boolean blocked = false;
	    for (int distance = row; distance <= radius && !blocked; distance++) {
	        int deltaY = -distance;
	        for (int deltaX = -distance; deltaX <= 0; deltaX++) {
	            int currentX = startx + deltaX * xx + deltaY * xy;
	            int currentY = starty + deltaX * yx + deltaY * yy;
	            float leftSlope = (deltaX - 0.5f) / (deltaY + 0.5f);
	            float rightSlope = (deltaX + 0.5f) / (deltaY - 0.5f);
	 
	            if (!(currentX >= 0 && currentY >= 0 && currentX < level.getWidth() && currentY < level.getHeight()) || start < rightSlope) {
	                continue;
	            } else if (end > leftSlope) {
	                break;
	            }
	 
	            //check if it's within the lightable area and light if needed
	            if (radius(deltaX, deltaY) <= radius) {
	                float bright = (float) (1 - (radius(deltaX, deltaY) / radius));
					LevelCell cell = level.cell(currentX, currentY);
					cell.temp = Boolean.TRUE;
					if (isPlayer) {
						level.cell(currentX, currentY).light = bright;
						level.cell(currentX, currentY).explored = true;
					}
	            }
	 
	            if (blocked) { //previous cell was a blocking one
	                if (level.isOpaque(currentX, currentY)) {//hit a wall
	                    newStart = rightSlope;
	                } else {
	                    blocked = false;
	                    start = newStart;
	                }
	            } else {
	                if (level.isOpaque(currentX, currentY) && distance < radius) {//hit a wall within sight line
	                    blocked = true;
	                    castLight(distance + 1, start, leftSlope, xx, xy, yx, yy, isPlayer);
	                    newStart = rightSlope;
	                }
	            }
	        }
	    }
	}
	
	private static float radius(int dx, int dy) {
		return (float)Math.sqrt(dx * dx + dy * dy) - 0.5f;
	}

	// "permissive" means we think we should be able to see it - find some way, even if it's a little unreasonable
	// not permissive means we think we shouldn't OR we have no opinion, and it cuts corners
	// I don't presently have a LOS calculator that agrees with my FOV calculator, so this is a little cheat.
	public static List<Point> findRay(Level level, Point origin, Point target, boolean permissive) {
		boolean hasInsertedFailedSentinel = false;
		List<Point> ray = new ArrayList<>();
		List<Point> failedCanonicalRay = null;
		int signX = Integer.compare(target.x, origin.x);
		int signY = Integer.compare(target.y, origin.y);
		if (signX == 0 && signY == 0) {
			ray.add(new Point(origin));
			return ray;
		}
		// straight lines
		if (signY == 0) {
			int y = origin.y;
			for (int x = origin.x; x != target.x; x += signX) {
				if (level.isOpaque(x, y) && !hasInsertedFailedSentinel) {
					hasInsertedFailedSentinel = true;
					ray.add(null);
				}
				ray.add(new Point(x, y));
			}
			if (level.isOpaque(target.x, target.y) && !hasInsertedFailedSentinel) {
				ray.add(null);
			}
			ray.add(new Point(target));
			return ray;
		}
		if (signX == 0) {
			int x = origin.x;
			for (int y = origin.y; y != target.y; y += signY) {
				if (level.isOpaque(x, y) && !hasInsertedFailedSentinel) {
					hasInsertedFailedSentinel = true;
					ray.add(null);
				}
				ray.add(new Point(x, y));
			}
			if (level.isOpaque(target.x, target.y) && !hasInsertedFailedSentinel) {
				ray.add(null);
			}
			ray.add(new Point(target));
			return ray;
		}
		float distX = Math.abs(target.x - origin.x);
		float distY = Math.abs(target.y - origin.y);
		float slope = distX / distY;
		// x more than y: iterate over x
		if (slope >= 1.0f) {
			for (int xStep = 0; xStep <= distX; xStep++) {
				int x = origin.x + (xStep * signX);
				int y = origin.y + (Math.round(xStep / slope * signY));
				if (level.isOpaque(x, y) && !hasInsertedFailedSentinel) {
					// YES, we WILL keep adding to the failed canonical ray after this point, don't clone it
					ray.add(null);
					hasInsertedFailedSentinel = true;
					failedCanonicalRay = ray;
				}
				ray.add(new Point(x, y));
			}
			if (failedCanonicalRay == null) {
				return ray;
			}

			// permissive: we THINK we should be able to see it
			if (permissive) {
				boolean failedAttempt2 = false;
				ray = new ArrayList<>();
				for (int xStep = 0; xStep <= distX; xStep++) {
					int x = origin.x + (xStep * signX);
					int y = origin.y + (int) (Math.floor(xStep / slope * signY));
					if (level.isOpaque(x, y)) {
						failedAttempt2 = true;
						break;
					}
					ray.add(new Point(x, y));
				}
				if (!failedAttempt2) {
					return ray;
				}

				boolean failedAttempt3 = false;
				ray = new ArrayList<>();
				for (int xStep = 0; xStep <= distX; xStep++) {
					int x = origin.x + (xStep * signX);
					int y = origin.y + (int) (Math.ceil(xStep / slope * signY));
					if (level.isOpaque(x, y)) {
						failedAttempt3 = true;
						break;
					}
					ray.add(new Point(x, y));
				}
				if (!failedAttempt3) {
					return ray;
				}
			}
		}

		// y more than x: iterate over y
		if (slope < 1.0f) {
			for (int yStep = 0; yStep <= distY; yStep++) {
				int y = origin.y + (yStep * signY);
				int x = origin.x + (Math.round(yStep * slope * signX));
				if (level.isOpaque(x, y) && !hasInsertedFailedSentinel) {
					ray.add(null);
					hasInsertedFailedSentinel = true;
					failedCanonicalRay = ray;
				}
				ray.add(new Point(x, y));
			}
			if (failedCanonicalRay == null) {
				return ray;
			}

			if (permissive) {
				boolean failedAttempt2 = false;
				ray = new ArrayList<>();
				for (int yStep = 0; yStep <= distY; yStep++) {
					int y = origin.y + (yStep * signY);
					int x = origin.x + (int) (Math.floor(yStep * slope * signX));
					if (level.isOpaque(x, y)) {
						failedAttempt2 = true;
						break;
					}
					ray.add(new Point(x, y));
				}
				if (!failedAttempt2) {
					return ray;
				}

				boolean failedAttempt3 = false;
				ray = new ArrayList<>();
				for (int yStep = 0; yStep <= distY; yStep++) {
					int y = origin.y + (yStep * signY);
					int x = origin.x + (int) (Math.ceil(yStep * slope * signX));
					if (level.isOpaque(x, y)) {
						failedAttempt3 = true;
						break;
					}
					ray.add(new Point(x, y));
				}
				if (!failedAttempt3) {
					return ray;
				}
			}
		}

		return failedCanonicalRay;
	}

	// TODO this is a little slower than it needs to be, re-implement it without fucking with lists
	public static boolean canSee(Level level, Point origin, Point target, boolean permissive, float distance) {
		List<Point> ray = findRay(level, origin, target, permissive);
		return !ray.contains(null) && ray.size() <= distance;
	}

	public static boolean canSee(Level level, Point origin, Point target, float distance) {
		return canSee(level, origin, target, true, distance);
	}
}
