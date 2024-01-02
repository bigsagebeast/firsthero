package com.bigsagebeast.hero.util;

public class FVector {
	public float x;
	public float y;
	public FVector() {
		this(0f, 0f);
	}
	public FVector(float x, float y) {
		this.x = y;
		this.y = y;
	}
	public float magnitude() {
		return (float)Math.sqrt((double)(x*x + y*y)); 
	}
}
