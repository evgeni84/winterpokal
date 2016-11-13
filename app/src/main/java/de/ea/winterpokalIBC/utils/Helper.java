package de.ea.winterpokalIBC.utils;

public class Helper {
	public static String getDurationAsHours(double dduration) {
		int duration = (int) dduration;
		int hours = duration / 60;
		int min = duration % 60;
		return String.format("%02d:%02dh", hours, min);
	}
}
