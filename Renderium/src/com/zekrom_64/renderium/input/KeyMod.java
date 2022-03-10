package com.zekrom_64.renderium.input;

import org.eclipse.jdt.annotation.NonNull;

/** Record of modifier key states.
 * 
 * @author Zekrom_64
 *
 */
public record KeyMod(boolean shift, boolean ctrl, boolean alt) {

	// Value cache
	private static KeyMod[] cache = new KeyMod[8];
	
	/** Gets a cached value of a modifier key state for the given combination of modifiers.
	 * 
	 * @param shift Shift key state
	 * @param ctrl Control key state
	 * @param alt Alt key state
	 * @return Modifier key state
	 */
	public static @NonNull KeyMod valueOf(boolean shift, boolean ctrl, boolean alt) {
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
