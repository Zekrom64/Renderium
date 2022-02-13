package com.zekrom_64.renderium;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

import org.eclipse.jdt.annotation.NonNull;

import com.zekrom_64.renderium.util.IStruct;

public class UniformBuffer<T extends IStruct<ByteBuffer>> extends BufferObject {
	
	private final T instance;
	private boolean dirty;
	
	public UniformBuffer(@NonNull T instance) {
		super(IStruct.getSizeOf(instance.getClass()), MapMode.WRITE_INVALIDATE);
		this.instance = instance;
	}
	
	public UniformBuffer<T> modify(Consumer<T> modifier) {
		modifier.accept(instance);
		dirty = true;
		return this;
	}
	
	public UniformBuffer<T> update() {
		if (dirty) {
			dirty = false;
			update(instance);
		}
		return this;
	}

}
