package com.zekrom_64.renderium.render;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

import org.eclipse.jdt.annotation.NonNull;

import com.zekrom_64.renderium.render.info.MapMode;
import com.zekrom_64.renderium.util.IStruct;

/** A uniform buffer is a specialization of a {@link BufferObject} that stores a
 * single struct value that is updated before rendering. The uniform buffer
 * class will keep a CPU-side instance of this struct and track if it needs
 * to be updated before rendering.
 * 
 * @author Zekrom_64
 *
 * @param <T>
 */
public class UniformBuffer<T extends IStruct<ByteBuffer>> extends BufferObject {
	
	private final T instance;
	private boolean dirty;
	
	/** Creates a new uniform buffer using the given struct instance.
	 * 
	 * @param instance Uniform struct instance
	 */
	public UniformBuffer(@NonNull T instance) {
		super(IStruct.getSizeOf(instance.getClass()), MapMode.WRITE_INVALIDATE);
		this.instance = instance;
	}
	
	/** Modifies this uniform buffer, notifying the buffer that it needs to be updated.
	 * 
	 * @param modifier Modification function
	 * @return This uniform buffer
	 */
	public UniformBuffer<T> modify(Consumer<T> modifier) {
		modifier.accept(instance);
		dirty = true;
		return this;
	}
	
	/** Updates this uniform buffer if needed.
	 * 
	 * @return This uniform buffer
	 */
	public UniformBuffer<T> update() {
		if (dirty) {
			dirty = false;
			update(instance);
		}
		return this;
	}

}
