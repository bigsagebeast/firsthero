package com.bigsagebeast.hero.roguelike.game;

public enum Rank {

	E       (0, "E"),
	D_MINUS (1, "D-"),
	D       (2, "D"),
	D_PLUS  (3, "D+"),
	C_MINUS (4, "D-"),
	C       (5, "C"),
	C_PLUS  (6, "C+"),
	B_MINUS (7, "B-"),
	B       (8, "B"),
	B_PLUS  (9, "B+"),
	A_MINUS (10, "A-"),
	A       (11, "A"),
	A_PLUS  (12, "A+"),
	S       (13, "S");
	/*
	public static final Rank E = 		new Rank(0);
	public static final Rank D_MINUS = 	new Rank(1);
	public static final Rank D = 		new Rank(2);
	public static final Rank D_PLUS = 	new Rank(3);
	public static final Rank C_MINUS = 	new Rank(4);
	public static final Rank C = 		new Rank(5);
	public static final Rank C_PLUS = 	new Rank(6);
	public static final Rank B_MINUS = 	new Rank(7);
	public static final Rank B = 		new Rank(8);
	public static final Rank B_PLUS = 	new Rank(9);
	public static final Rank A_MINUS = 	new Rank(10);
	public static final Rank A = 		new Rank(11);
	public static final Rank A_PLUS = 	new Rank(12);
	public static final Rank S = 		new Rank(13);
	
	*/
	public static final Rank[] chart = new Rank[] {
		E,
		D_MINUS, D, D_PLUS,
		C_MINUS, C, C_PLUS,
		B_MINUS, B, B_PLUS,
		A_MINUS, A, A_PLUS,
		S
	};
	
	public static Rank major(Rank minor) {
		if (minor.num < D_MINUS.num) {
			return E;
		} else if (minor.num < C_MINUS.num) {
			return D;
		} else if (minor.num < B_MINUS.num) {
			return C;
		} else if (minor.num < A_MINUS.num) {
			return B;
		} else if (minor.num < S.num) {
			return A;
		}
		return S;
	}
	
	public final int num;
	public final String str;
	
	private Rank(int num, String str) {
		this.num = num;
		this.str = str;
	}
	
	public Rank by(int delta) {
		if (num + delta < 0) {
			return chart[0];
		} else if (num + delta > 13) {
			return chart[13];
		} else {
			return chart[num + delta];
		}
	}
	
	@Override
	public String toString() {
		return str;
	}
}
