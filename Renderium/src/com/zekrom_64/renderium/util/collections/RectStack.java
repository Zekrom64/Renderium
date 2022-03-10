package com.zekrom_64.renderium.util.collections;

import java.util.function.Consumer;

import org.eclipse.jdt.annotation.NonNull;

import com.zekrom_64.renderium.util.IStructAccessor;
import com.zekrom_64.renderium.util.IStructAccessor.DefaultStructAccessor;
import com.zekrom_64.renderium.util.geometry.IRectangle;
import com.zekrom_64.renderium.util.geometry.Rectangle;

/** A rectangle stack is a specialization of a struct stack for {@link Rectangle} objects.
 * 
 * @author Zekrom_64
 *
 */
public class RectStack extends StructStack<@NonNull Rectangle> {

	public static final @NonNull IStructAccessor<@NonNull Rectangle> ACCESSOR = new DefaultStructAccessor<@NonNull Rectangle>(Rectangle.class);
	
	public RectStack(int size) {
		super(size, ACCESSOR);
	}
	
	/** Pushes a rectangle to the stack.
	 * 
	 * @param r Rectangle to push
	 * @return This rectangle stack
	 */
	public @NonNull RectStack peek(Rectangle r) {
		checkTopCache();
		r.set(topCache);
		return this;
	}
	
	/** Peeks at the rectangle at the top of the stack.
	 * 
	 * @param c Peek value consumer
	 * @return This rectangle stack
	 */
	public @NonNull RectStack peek(Consumer<IRectangle> c) {
		checkTopCache();
		c.accept(topCache);
		return this;
	}
	
	/** Pushes a rectangle to the stack.
	 * 
	 * @param x0 First X coordinate
	 * @param y0 First Y coordinate
	 * @param x1 Second X coordinate
	 * @param y1 Second Y coordinate
	 * @return This rectangle stack
	 */
	public @NonNull RectStack push(int x0, int y0, int x1, int y1) {
		dup();
		topCache.set(x0, y0, x1, y1);
		writebackCache();
		return this;
	}
	
}
