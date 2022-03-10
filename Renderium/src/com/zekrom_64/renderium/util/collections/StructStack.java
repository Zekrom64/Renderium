package com.zekrom_64.renderium.util.collections;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

import org.eclipse.jdt.annotation.NonNull;
import org.lwjgl.system.MemoryUtil;

import com.zekrom_64.renderium.util.ISafeCloseable;
import com.zekrom_64.renderium.util.IStructAccessor;
import com.zekrom_64.renderium.util.TypeUtils;

/** A struct stack implements a fixed-size stack of structures.
 * 
 * @author Zekrom_64
 *
 * @param <S> Struct type
 */
public class StructStack<@NonNull S> implements ISafeCloseable {

	// The accessor of struct value
	protected final @NonNull IStructAccessor<S> accessor;
	// The buffer storing stack values
	protected final @NonNull ByteBuffer stackBuffer;
	// The maximum number of values the stack can hold
	protected final int maxSize;
	// The descending stack pointer
	protected int stackPointer;
	
	/** Cache of the topmost value. */
	protected final S topCache;
	/** If the top cache value is dirty (modified but not written back). */
	protected boolean cacheDirty = false;
	
	/** Creates a new struct stack of the given size, using an accessor.
	 * 
	 * @param size Stack size
	 * @param accessor Accessor for struct values
	 */
	public StructStack(int size, @NonNull IStructAccessor<S> accessor) {
		this.accessor = accessor;
		stackBuffer = TypeUtils.nonNull(MemoryUtil.memAlloc(accessor.getSizeOf() * size));
		maxSize = size;
		stackPointer = size;
		topCache = accessor.create();
	}
	
	@Override
	public void close() {
		MemoryUtil.memFree(stackBuffer);
	}
	
	
	//=============================//
	// Getters & Simple Operations //
	//=============================//
	
	/** Clear the stack.
	 * 
	 */
	public void clear() {
		stackPointer = maxSize;
	}
	
	/** Gets the maximum number of values the stack can hold.
	 * 
	 * @return Maximum stack size
	 */
	public int getMaxSize() {
		return maxSize;
	}
	
	/** Gets the number of values currently on the stack.
	 * 
	 * @return Current stack size
	 */
	public int getSize() {
		return maxSize - stackPointer;
	}
	
	/** Gets if the stack is empty.
	 * 
	 * @return If the stack is empty
	 */
	public boolean isEmpty() {
		return stackPointer >= maxSize;
	}
	
	/** Gets if the stack is full.
	 * 
	 * @return If the stack is full
	 */
	public boolean isFull() {
		return stackPointer <= 0;
	}
	
	
	//==================//
	// Stack Operations //
	//==================//
	
	// Check that the stack is not full
	protected void checkNotFull() {
		if (isFull()) throw new BufferOverflowException();
	}
	
	// Check that the stack is not empty
	protected void checkNotEmpty() {
		if (isEmpty()) throw new BufferUnderflowException();
	}
	
	// Check that the top cache is up-to-date
	protected void checkTopCache() {
		if (cacheDirty) {
			accessor.read(stackPointer * accessor.getSizeOf(), stackBuffer, topCache);
			cacheDirty = false;
		}
	}
	
	// Write-back the top cache to memory
	protected void writebackCache() {
		accessor.write(stackPointer * accessor.getSizeOf(), stackBuffer, topCache);
		cacheDirty = false; // It cannot be dirty if it has been written back
	}
	
	/** Pops a value from this matrix stack.
	 * 
	 * @return This matrix stack
	 * @throws BufferUnderflowException If the stack is empty
	 */
	public StructStack<S> pop() {
		checkNotEmpty();
		stackPointer++;
		cacheDirty = true;
		return this;
	}
	
	/** Pops zero or more values from the stack.
	 * 
	 * @param n Number of values to pop
	 * @return This stack
	 * @throws IllegalArgumentException If a negative number is passed
	 * @throws BufferUnderflowException If more values are requested to be popped than are on the stack
	 */
	public StructStack<S> pop(int n) {
		if (n < 0) throw new IllegalArgumentException("Cannot pop a negative number of values");
		if (n > getSize()) throw new BufferUnderflowException();
		stackPointer += n;
		cacheDirty = true;
		return this;
	}
	
	/** Peeks at the topmost value on the stack.
	 * 
	 * @throws BufferUnderflowException If the stack is empty
	 */
	public S peek() {
		checkNotEmpty();
		S s = accessor.create();
		accessor.read(stackPointer * accessor.getSizeOf(), stackBuffer, s);
		return s;
	}
	
	/** Pushes a matrix onto the stack.
	 * 
	 * @param m Matrix to push on the stack
	 * @return This matrix stack
	 * @throws BufferOverflowException If the stack is full
	 */
	public StructStack<S> push(S s) {
		checkNotFull();
		cacheDirty = true;
		stackPointer--;
		accessor.write(stackPointer * accessor.getSizeOf(), stackBuffer, s);
		return this;
	}
	
	/** Duplicates the topmost value on the stack.
	 * 
	 * @return This matrix stack
	 */
	public StructStack<S> dup() {
		checkNotFull();
		long pbuf = MemoryUtil.memAddress(stackBuffer);
		long psrc = pbuf + (stackPointer * accessor.getSizeOf());
		long pdst = psrc - accessor.getSizeOf();
		MemoryUtil.memCopy(psrc, pdst, accessor.getSizeOf());
		stackPointer--;
		// Don't neet to dirty cache, topmost value will be the same so dirtiness is unchanged
		return this;
	}
	
	
}
