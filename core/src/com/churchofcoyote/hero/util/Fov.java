package com.churchofcoyote.hero.util;

import com.churchofcoyote.hero.roguelike.world.Level;

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
}
