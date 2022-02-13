package com.zekrom_64.renderium.util;

public record KeyMod(boolean shift, boolean ctrl, boolean alt) {

	private static KeyMod[] cache = new KeyMod[8];
	
	public static KeyMod valueOf(boolean shift, boolean ctrl, boolean alt) {
		int index = shift ? 1 : 0;
		index += ctrl ? 2 : 0;
		index += alt ? 4 : 0;
		KeyMod km = cache[index];
		if (km == null) {
			km = new KeyMod(shift, ctrl, alt);
			cache[index] = km;
		}
		return km;
	}
	
}
