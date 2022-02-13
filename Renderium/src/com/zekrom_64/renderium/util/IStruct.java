package com.zekrom_64.renderium.util;

import java.nio.Buffer;

/** Interface implemented by classes that are plain structures, providing
 * methods to store or load them from the appropriate buffer type.
 * 
 * @author Zekrom_64
 *
 * @param <TBuffer> The buffer type the structure uses
 */
public interface IStruct<TBuffer extends Buffer> {

	/** Stores this structure to a buffer at the given position.
	 * 
	 * @param position Buffer position to store at
	 * @param buffer Buffer to store to
	 */
	public void get(int position, TBuffer buffer);
	
	/** Loads this structure from a buffer at the given position.
	 * 
	 * @param position Buffer position to load from
	 * @param buffer Buffer to load from
	 */
	public void set(int position, TBuffer buffer);
	
	/** Reflects the size of a structure type from the static int field 'SIZEOF'.
	 * 
	 * @param clazz Struct type
	 * @return Size of the type
	 */
	public static int getSizeOf(Class<?> clazz) {
		try {
			return clazz.getField("SIZEOF").getInt(null);
		} catch (Exception e) {
			throw new RuntimeException("Failed to get structure size of type " + clazz.getTypeName(), e);
		}
	}
	
}
