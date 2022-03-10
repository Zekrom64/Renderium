package com.zekrom_64.renderium.util;

import java.nio.ByteBuffer;
import java.util.function.Supplier;

import org.eclipse.jdt.annotation.NonNull;

/** A struct accessor provides a binary interface for structural types with
 * a native representation. Classes that implement {@link IStruct} can use
 * the {@link DefeaultStructAccessor} class, while external struct classes
 * must define custom accessors.
 * 
 * @author Zekrom_64
 *
 * @param <S> Struct type
 */
public interface IStructAccessor<@NonNull S> {
	
	/** Creates a new struct instance.
	 * 
	 * @return New struct
	 */
	public S create();
	
	/** Gets the size of the structure in bytes.
	 * 
	 * @return Struct size
	 */
	public int getSizeOf();

	/** Reads a struct from memory.
	 * 
	 * @param position Byte offset to read at
	 * @param buffer Buffer to read from
	 * @param struct Struct to read into
	 */
	public void read(int position, @NonNull ByteBuffer buffer, S struct);

	/** Writes a struct to memory.
	 * 
	 * @param position Byte offset to write at
	 * @param buffer Buffer to write to
	 * @param struct Struct to write
	 */
	public void write(int position, @NonNull ByteBuffer buffer, S struct);
	
	/** A default struct accessor for classes implementing the {@link IStruct} interface.
	 * 
	 * @author Zekrom_64
	 *
	 * @param <S> Struct type
	 */
	public static class DefaultStructAccessor<@NonNull S extends IStruct<ByteBuffer>> implements IStructAccessor<S> {
		
		private final Supplier<@NonNull S> creator;
		private final int sizeof;
		
		/** Creates a new default struct accessor for the given class.
		 * 
		 * @param clazz Struct class
		 */
		public DefaultStructAccessor(@NonNull Class<S> clazz) {
			sizeof = IStruct.getSizeOf(clazz);
			creator = TypeUtils.getDefaultConstructor(clazz);
		}
		
		@Override
		public int getSizeOf() {
			return sizeof;
		}
		
		@Override
		public void read(int position, @NonNull ByteBuffer buffer, S struct) {
			struct.set(position, buffer);
		}

		@Override
		public void write(int position, @NonNull ByteBuffer buffer, S struct) {
			struct.get(position, buffer);
		}

		@SuppressWarnings("null")
		@Override
		public S create() {
			return creator.get();
		}
		
	}
	
}
