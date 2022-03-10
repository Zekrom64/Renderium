package com.zekrom_64.renderium.util;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.function.Supplier;

import org.eclipse.jdt.annotation.NonNull;

/** Type utilities.
 * 
 * @author Zekrom_64
 *
 */
public class TypeUtils {
	
	/** Creates an array of generic objects of the given type.
	 * 
	 * @param <T> Element type
	 * @param size Array size
	 * @param elemt Element class
	 * @return Generic array
	 */
	public static <T> T[] createGenericArray(int size, Class<?> elemt) {
		@SuppressWarnings("unchecked")
		T[] arr = (T[]) Array.newInstance(elemt, size);
		return arr;
	}

	/** Gets a supplier for the "default" constructor for a class (ie. a public
	 * parameterless constructor).
	 * 
	 * @param <T> Type to reflect
	 * @param clazz Class to get constructor for
	 * @return Supplier for the default constructor
	 */
	@SuppressWarnings("null")
	public static <T> Supplier<@NonNull T> getDefaultConstructor(Class<T> clazz) {
		try {
			Constructor<T> ctor = clazz.getConstructor();
			return () -> {
				try {
					return ctor.newInstance();
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					throw new RuntimeException("Failed to construct class " + clazz);
				}
			};
		} catch (NoSuchMethodException | SecurityException e) {
			throw new RuntimeException("Failed to find default constructor for class" + clazz);
		}
	}
	
	/** Performs runtime checks against null values and satisfies static analysis.
	 * 
	 * @param <T> Type to check
	 * @param t Value to check
	 * @return Non-null value
	 */
	public static <T> @NonNull T requireNonNull(T t) {
		Objects.requireNonNull(t);
		return t;
	}

	/** Performs runtime checks against null values and satisfies static analysis.
	 * 
	 * @param <T> Type to check
	 * @param t Value to check
	 * @return Non-null value
	 */
	@SuppressWarnings("null")
	public static <T> @NonNull T @NonNull[] requireNonNull(T[] t) {
		Objects.requireNonNull(t);
		for(T tv : t) Objects.requireNonNull(tv);
		return t;
	}
	
	/** Indicates that a value is non-null to static analysis.
	 * 
	 * @param <T> Type to check
	 * @param t Value to check
	 * @return Non-null value
	 */
	@SuppressWarnings("null")
	public static <T> @NonNull T nonNull(T t) {
		return t;
	}

	/** Indicates that a value is non-null to static analysis.
	 * 
	 * @param <T> Type to check
	 * @param t Value to check
	 * @return Non-null value
	 */
	@SuppressWarnings("null")
	public static <T> @NonNull T @NonNull[] nonNull(T[] t) {
		return t;
	}
	
}
