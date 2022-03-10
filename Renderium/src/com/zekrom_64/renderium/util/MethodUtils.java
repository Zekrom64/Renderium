package com.zekrom_64.renderium.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Collection;
import java.util.Iterator;

public class MethodUtils {

	private static final MethodHandles.Lookup lookup = MethodHandles.publicLookup();
	
	public static MethodHandle iterator(Collection<?> c) throws NoSuchMethodException, IllegalAccessException {
		MethodHandle mhiterator = lookup.findVirtual(c.getClass(), "iterator", MethodType.methodType(Iterator.class));
		return MethodHandles.insertArguments(mhiterator, 0, c);
	}
	
}
