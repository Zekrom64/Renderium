package com.zekrom_64.renderium.util;

public record KeyState(Key key, KeyMod mods) {

	public char toChar() {
		return key.toChar(mods);
	}
	
}
