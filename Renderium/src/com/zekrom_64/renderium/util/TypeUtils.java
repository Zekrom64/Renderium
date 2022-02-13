package com.zekrom_64.renderium.util;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;

public class TypeUtils {
	
	public static <T> T[] createGenericArray(int size, Class<?> elemt) {
		@SuppressWarnings("unchecked")
		T[] arr = (T[]) Array.newInstance(elemt, size);
		return arr;
	}

	public static <T> Supplier<T> getDefaultConstructor(Class<T> clazz) {
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
	
}
