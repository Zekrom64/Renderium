package com.zekrom_64.renderium.localization;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import com.zekrom_64.renderium.resource.ResourceLocation;
import com.zekrom_64.renderium.util.TypeUtils;

/** <p>A localization determines how strings are localized for different languages. Localizations store
 * a mapping of key strings to the localized text, and are associated with a particular {@link Locale}.
 * Localizations also follow a hierarchy; the <tt>en</tt> localization is a parent of the <tt>en-US</tt>
 * locale and will be used as a fallback if no such key exists in the more specific localization, the
 * <tt>en-US</tt> locale is the parent of the <tt>en-US-...</tt> localization and so on. A globally set
 * localization is also managed that defines how text will be localized for the entire application.</p>
 * 
 * <p>Localizations can have mappings dynamically added to them by {@link #addLocalization(Map)} and its
 * variants, but they cannot have mappings removed. If mappings need to be changed at runtime they should
 * be overwritten instead.</p>
 * 
 * @author Zekrom_64
 *
 */
public class Localization {
	
	private static volatile int modcount = 0;

	private static Map<Locale, Localization> allLocalizations = new HashMap<>();
	
	/** Gets a localization by its string name, eg. <tt>en-US</tt>.
	 * 
	 * @param name The localization name
	 * @return The localization mapped to that name
	 */
	public static @NonNull Localization getLocalization(@NonNull String name) {
		return getLocalization(TypeUtils.nonNull(Locale.forLanguageTag(name)));
	}
	
	private static Localization getParent(Locale current) {
		if (current.getCountry().isEmpty()) return null;
		String str = current.toString();
		@NonNull Locale parentLocale = TypeUtils.nonNull(Locale.forLanguageTag(str.substring(0, str.lastIndexOf('-'))));
		Localization parent = allLocalizations.get(parentLocale);
		if (parent == null) {
			parent = new Localization(parentLocale, getParent(parentLocale));
			allLocalizations.put(parentLocale, parent);
		}
		return parent;
	}
	
	/** Gets a localization by its locale.
	 * 
	 * @param locale Locale mapped to localization
	 * @return The localization for the locale
	 */
	public static @NonNull Localization getLocalization(@NonNull Locale locale) {
		synchronized(allLocalizations) {
			Localization loc = allLocalizations.get(locale);
			if (loc == null) {
				loc = new Localization(locale, getParent(locale));
				allLocalizations.put(locale, loc);
			}
			return loc;
		}
	}
	
	private static @NonNull Localization currentLocalization = getLocalization(TypeUtils.nonNull(Locale.getDefault()));
	
	/** Gets the current global localization.
	 * 
	 * @return Global localization
	 */
	public static @NonNull Localization getCurrentLocalization() {
		return currentLocalization;
	}
	
	/** Sets the current global localization.
	 * 
	 * @param loc Global localization
	 */
	public static void setCurrentLocalization(@NonNull Localization loc) {
		currentLocalization = loc;
	}
	
	/** Sets the current global localization to the given locale.
	 * 
	 * @param locale Global locale
	 */
	public static void setCurrentLocalization(@NonNull Locale locale) {
		setCurrentLocalization(getLocalization(locale));
	}
	
	/** Sets the current global localization to the localization with the given name.
	 * 
	 * @param name Global localization name
	 */
	public static void setCurrentLocalization(@NonNull String name) {
		setCurrentLocalization(getLocalization(name));
	}
	
	/** Gets a localized string from the current global localization.
	 * 
	 * @param key Key string
	 * @return Localized string
	 */
	public static @NonNull String getFromCurrent(@NonNull String key) {
		return getCurrentLocalization().get(key);
	}
	
	static {
		try {
			Localization en = getLocalization("en");
			en.addLocalization(new ResourceLocation("renderium:localization/en.properties"));
		} catch (Exception e) { }
	}
	
	/** The locale this localization belongs to. */
	public final @NonNull Locale locale;
	/** The parent localization, or <b>null</b>. */
	public final @Nullable Localization parent;
	private final HashMap<String, String> stringMap = new HashMap<>();
	
	private Localization(@NonNull Locale locale, Localization parent) {
		this.locale = locale;
		this.parent = parent;
		modcount++;
	}
	
	/** Adds localization mappings from a {@link Properties} object.
	 * 
	 * @param props Mappings to add
	 */
	public void addLocalization(@NonNull Properties props) {
		if (!props.isEmpty()) {
			props.forEach((key, value) -> stringMap.put((String)key, (String)value));
			modcount++;
		}
	}
	
	/** Adds localization mappings from a string map.
	 * 
	 * @param localization Mappings to add
	 */
	public void addLocalization(@NonNull Map<String, String> localization) {
		if (!localization.isEmpty()) {
			stringMap.putAll(localization);
			modcount++;
		}
	}
	
	/** Adds localization mappings from a properties resource.
	 * 
	 * @param locFile Localization file
	 * @throws IOException If an exception occurs loading the localization file
	 */
	public void addLocalization(@NonNull ResourceLocation locFile) throws IOException {
		Properties props = new Properties();
		try(InputStream in = locFile.open()) {
			props.load(in);
		}
		addLocalization(props);
	}
	
	/** Gets the 'modification count' of the localization system, incremented whenever the
	 * localizations are modified. This can be used as a watch value to determine when
	 * localized strings should be refreshed.
	 * 
	 * @return Modification count
	 */
	public static int getModCount() {
		return modcount;
	}
	
	/** Gets the localized form of the given key string. If no localization is found the
	 * key string itself is returned.
	 * 
	 * @param key Key string
	 * @return Localized string
	 */
	public @NonNull String get(@NonNull String key) {
		String value = stringMap.get(key);
		if (value == null) {
			if (parent != null) value = TypeUtils.nonNull(parent).get(key);
			else value = key;
		}
		return value;
	}
	
}
