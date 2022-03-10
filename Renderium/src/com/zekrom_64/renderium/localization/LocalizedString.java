package com.zekrom_64.renderium.localization;

import org.eclipse.jdt.annotation.NonNull;

/** A localized string is an object whose {@link #toString()} method will return
 * the localized form of a string key in the current localization.
 * 
 * @author Zekrom_64
 *
 */
public class LocalizedString {

	/** The key to use for the string. */
	public final @NonNull String key;
	
	// Cached string value
	private String stringCache = null;
	// Mod count of cached string
	private int cacheModCount;
	
	/** Creates a new localized string from a key.
	 * 
	 * @param key Localization key
	 */
	public LocalizedString(@NonNull String key) {
		this.key = key;
	}
	
	@Override
	public int hashCode() {
		return key.hashCode();
	}
	
	/** Tests if this localized string is equal to another. The
	 * strings will be equal if their keys are equal.
	 * 
	 * @param str String to compare
	 * @return If the strings are equal
	 */
	public boolean equals(LocalizedString str) {
		return key.equals(str.key);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (o == null) return false;
		if (o instanceof LocalizedString) return equals((LocalizedString)o);
		else return false;
	}

	@Override
	public String toString() {
		int mod = Localization.getModCount();
		if (stringCache == null || cacheModCount != mod) {
			stringCache = Localization.getFromCurrent(key);
			cacheModCount = mod;
		}
		return stringCache;
	}
	
}
