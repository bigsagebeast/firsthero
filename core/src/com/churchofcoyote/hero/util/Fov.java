package com.churchofcoyote.hero.util;

import com.churchofcoyote.hero.roguelike.world.dungeon.Level;

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
	* @param startx the horizontal component of the starting location
	* @param starty the vertical component of the starting location
	* @param radius the maximum distance to draw the FOV
	* @return the computed light grid
	*/
	public static void calculateFOV(Level level, int startx, int starty, float radius) {
		Fov.level = level;
	    Fov.startx = startx;
	    Fov.starty = starty;
	    Fov.radius = radius;
	    
	    level.cell(startx, starty).light = 10f;
		level.cell(startx, starty).explored = true;

	    for (Compass d : Compass.diagonals()) {
	        castLight(1, 1.0f, 0.0f, 0, d.getX(), d.getY(), 0);
	        castLight(1, 1.0f, 0.0f, d.getX(), 0, 0, d.getY());
	    }
	 
	    //return lightMap;
	}
	
	private static Level level;
	private static int startx;
	private static int starty;
	private static float radius;
	
	private static void castLight(int row, float start, float end, int xx, int xy, int yx, int yy) {
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
	                level.cell(currentX, currentY).light = bright;
	                level.cell(currentX, currentY).explored = true;
	            }
	 
	            if (blocked) { //previous cell was a blocking one
	                if (level.isOpaque(currentX, currentY)) {//hit a wall
	                    newStart = rightSlope;
	                    continue;
	                } else {
	                    blocked = false;
	                    start = newStart;
	                }
	            } else {
	                if (level.isOpaque(currentX, currentY) && distance < radius) {//hit a wall within sight line
	                    blocked = true;
	                    castLight(distance + 1, start, leftSlope, xx, xy, yx, yy);
	                    newStart = rightSlope;
	                }
	            }
	        }
	    }
	}
	
	private static float radius(int dx, int dy) {
		return (float)Math.sqrt(dx * dx + dy * dy) - 0.5f;
	}
/*
	// TODO only check valid diagonal
	public static boolean canSee(Level level, Point from, Point to, int distance, float minLight) {
		Fov.level = level;
		Fov.startx = from.x;
		Fov.starty = from.y;
		Fov.radius = distance;

		for (Compass d : Compass.diagonals()) {
			if (checkPov(1, 1.0f, 0.0f, 0, d.getX(), d.getY(), 0, to)) return true;
			if (checkPov(1, 1.0f, 0.0f, d.getX(), 0, 0, d.getY(), to)) return true;
		}
		return false;
	}

	// also not working right
	public static boolean checkPov(int row, float start, float end, int xx, int xy, int yx, int yy, Point target) {
		float newStart = 0.0f;
		if (start < end) {
			return false;
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

				if (currentX == target.x && currentY == target.y) {
					return true;
				}

				if (blocked) { //previous cell was a blocking one
					if (level.isOpaque(currentX, currentY)) {//hit a wall
						newStart = rightSlope;
						continue;
					} else {
						blocked = false;
						start = newStart;
					}
				} else {
					if (level.isOpaque(currentX, currentY) && distance < radius) {//hit a wall within sight line
						blocked = true;
						castLight(distance + 1, start, leftSlope, xx, xy, yx, yy);
						newStart = rightSlope;
					}
				}
			}
		}
		return false;
	}
*/

	/*
	// TODO this is broken when seeing around corners at long angles
	public static boolean canSee (Level level, Point from, Point to, float distance, float minLight) {
		int delta_x, delta_y, move_x, move_y, error;

		Point track = new Point(from.x, from.y);

		if (level.cell(to).light < minLight) {
			//System.out.println("Can't see - too dark (" + level.cell(to).light + ")");
			//return false;
		}

		// Calculate deltas.
		delta_x = Math.abs(track.x - to.x) << 1;
		delta_y = Math.abs(track.y - to.y) << 1;

		// Calculate signs.
		move_x = to.x >= track.x ? 1 : -1;
		move_y = to.y >= track.y ? 1 : -1;

		// There is an automatic line of sight, of course, between a
		// location and the same location or directly adjacent
		// locations./
		if (Math.abs(to.x - track.x) < 2 && Math.abs(to.y - track.y) < 2) {
			//System.out.println("Can see - adjacent");
			return true;
		}

		// Ensure that the line will not extend too long.
		if (((to.x - track.x) * (to.x - track.x))
				+ ((to.y - track.y) * (to.y -
				track.y)) >
				distance * distance) {
			//System.out.println("Can't see - distance'");
			return false;
		}

		// "Draw" the line, checking for obstructions.
		if (delta_x >= delta_y) {
			// Calculate the error factor, which may go below zero.
			error = delta_y - (delta_x >> 1);


			// Search the line.
			while (track.x != to.x) {
				if (!level.cell(track).terrain.isPassable()) {
					//System.out.println("Can't see - obstructed X axis");
					return false;
				}

				if (error > 0) {
					if (error > 0 || (move_x > 0)) {
						track.y += move_y;
						error -= delta_x;
					}
				}
				track.x += move_x;
				error += delta_y;
			}
		}
		else {
			// Calculate the error factor, which may go below zero.
			error = delta_x - (delta_y >> 1);

			// Search the line.
			while (track.y != to.y) {
				if (!level.cell(track).terrain.isPassable()) {
					//System.out.println("Can't see - obstructed Y axis");
					return false;
				}
				if (error > 0) {
					if (move_y > 0) {
						track.x += move_x;
						error -= delta_y;
					}
				}
				track.y += move_y;
				error += delta_x;
			}
		}

		//System.out.println("Can see");
		return true;
	}
	*/

	/*
	public static List<Point> findRay(Level level, Point origin, Point target) {
		boolean hasInsertedFailedSentinel = false;
		List<Point> ray = new ArrayList<>();
		List<Point> failedCanonicalRay = null;
		int signX = (origin.x < target.x) ? 1 : (origin.x == target.x) ? 0 : -1;
		int signY = (origin.y < target.y) ? 1 : (origin.y == target.y) ? 0 : -1;
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
		}
		return failedCanonicalRay;
	}
	*/

	// "permissive" means we think we should be able to see it - find some way, even if it's a little unreasonable
	// not permissive means we think we shouldn't OR we have no opinion, and it cuts corners
	// I don't presently have a LOS calculator that agrees with my FOV calculator, so this is a little cheat.
	public static List<Point> findRay(Level level, Point origin, Point target, boolean permissive) {
		boolean hasInsertedFailedSentinel = false;
		List<Point> ray = new ArrayList<>();
		List<Point> failedCanonicalRay = null;
		int signX = (origin.x < target.x) ? 1 : (origin.x == target.x) ? 0 : -1;
		int signY = (origin.y < target.y) ? 1 : (origin.y == target.y) ? 0 : -1;
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
