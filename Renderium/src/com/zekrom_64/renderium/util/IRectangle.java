package com.zekrom_64.renderium.util;

import org.joml.Vector2ic;

public interface IRectangle {
	
	public Vector2ic getMin();
	
	public Vector2ic getMax();
	
	public int getWidth();
	
	public int getHeight();
	
}