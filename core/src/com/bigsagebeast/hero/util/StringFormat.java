package com.bigsagebeast.hero.util;

import com.google.gwt.regexp.shared.*;


public class StringFormat {
	private static final RegExp INTER = RegExp.compile("%(-?)([0-9]*)(.[0-9]+)?([sd])", "g");
	
	public static String format(String formatString, Object... arg) {
		StringBuffer out = new StringBuffer();
		MatchResult m = INTER.exec(formatString);
		int argIndex = 0;
		int lastMatchIndex = 0;
		
		while (m != null) {
			out.append(formatString.substring(lastMatchIndex, m.getIndex()));
			lastMatchIndex = m.getIndex() + m.getGroup(0).length();
			boolean flagLeftJustify = m.getGroup(1) != null && m.getGroup(1).contains("-");
			boolean hasWidth = m.getGroup(2) != null && !m.getGroup(2).isEmpty();
			boolean hasPrecision = m.getGroup(3) != null && !m.getGroup(3).isEmpty();
			int width = 0;
			int precision;
			if (hasWidth) {
				width = Integer.parseInt(m.getGroup(2));
			}
			if (hasPrecision) {
				precision = Integer.parseInt(m.getGroup(3).substring(1));
			}
			String type = m.getGroup(4);
			String argAsString;
			switch(type) {
			case "s":
				argAsString = arg[argIndex++].toString();
				if (hasWidth) {
					while (argAsString.length() < width) {
						if (flagLeftJustify) {
							argAsString = argAsString + " ";
						} else {
							argAsString = " " + argAsString;
						}
					}
				}
				out.append(argAsString);
				break;
			case "d":
				int argAsInt = Integer.valueOf(arg[argIndex++].toString());
				argAsString = Integer.toString(argAsInt);
				if (hasWidth) {
					while (argAsString.length() < width) {
						if (flagLeftJustify) {
							argAsString = argAsString + " ";
						} else {
							argAsString = " " + argAsString;
						}
					}
				}
				out.append(argAsString);
			}
			m = INTER.exec(formatString);
		}
		out.append(formatString.substring(lastMatchIndex));
		return out.toString();
	}
}
