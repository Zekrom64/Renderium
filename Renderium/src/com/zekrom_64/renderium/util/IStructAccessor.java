package com.zekrom_64.renderium.util;

import java.nio.ByteBuffer;
import java.util.function.Supplier;

import org.eclipse.jdt.annotation.NonNull;

public interface IStructAccessor<@NonNull S> {
	
	public S create();
	
	public int getSizeOf();

	public void read(int position, ByteBuffer buffer, S struct);

	public void write(int position, ByteBuffer buffer, S struct);
	
	public static class DefaultStructAccessor<@NonNull S extends IStruct<ByteBuffer>> implements IStructAccessor<S> {
		
		private final Supplier<S> creator;
		private final int sizeof;
		
		public DefaultStructAccessor(Class<S> clazz) {
			sizeof = IStruct.getSizeOf(clazz);
			creator = TypeUtils.getDefaultConstructor(clazz);
		}
		
		@Override
		public int getSizeOf() {
			return sizeof;
		}
		
		@Override
		public void read(int position, ByteBuffer buffer, S struct) {
			struct.set(position, buffer);
		}

		@Override
		public void write(int position, ByteBuffer buffer, S struct) {
			struct.get(position, buffer);
		}

		@Override
		public S create() {
			return creator.get();
		}
		
	}
	
}
