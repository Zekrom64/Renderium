package com.zekrom_64.renderium.input;

import org.eclipse.jdt.annotation.NonNull;

/** A key state holds the state of a single key press or release, including the key
 * pressed and any modifiers (modifiers are only stored for presses).
 * 
 * @author Zekrom_64
 *
 */
public record KeyState(@NonNull Key key, @NonNull KeyMod mods, boolean pressed) {

	/** Naively converts this key state to a character. This should not be used
	 * for general text input as it is just a simple mapping from key and mod state
	 * to an ASCII character.
	 * 
	 * @return Simple character conversion of this key state
	 */
	public char toChar() {
		return key.toChar(mods);
	}
	
}
