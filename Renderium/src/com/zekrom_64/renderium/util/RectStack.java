package com.zekrom_64.renderium.util;

import java.util.function.Consumer;

import org.eclipse.jdt.annotation.NonNull;

import com.zekrom_64.renderium.util.IStructAccessor.DefaultStructAccessor;

public class RectStack extends StructStack<@NonNull Rectangle> {

	public static final IStructAccessor<@NonNull Rectangle> ACCESSOR = new DefaultStructAccessor<@NonNull Rectangle>(Rectangle.class);
	
	public RectStack(int size) {
		super(size, ACCESSOR, new Rectangle());
	}
	
	public RectStack peek(Rectangle r) {
		checkTopCache();
		r.set(topCache);
		return this;
	}
	
	public RectStack peek(Consumer<IRectangle> c) {
		checkTopCache();
		c.accept(topCache);
		return this;
	}
	
	public RectStack push(int x0, int y0, int x1, int y1) {
		dup();
		topCache.set(x0, y0, x1, y1);
		writebackCache();
		return this;
	}
	
}
