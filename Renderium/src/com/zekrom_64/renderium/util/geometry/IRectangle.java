package com.zekrom_64.renderium.util.geometry;

import org.joml.Vector2ic;

/** A rectangle is an axis-aligned 2-dimensional shape defined by two integer points.
 * 
 * @author Zekrom_64
 *
 */
public interface IRectangle {
	
	/** Gets a vector describing the minimum of the points.
	 * 
	 * @return Minimum point
	 */
	public Vector2ic getMin();
	
	/** Gets a vector describing the maximum of the points.
	 * 
	 * @return Maximum point
	 */
	public Vector2ic getMax();
	
	/** Gets the width of the rectangle.
	 * 
	 * @return Width
	 */
	public int getWidth();
	
	/** Gets the height of the rectangle.
	 * 
	 * @return Height
	 */
	public int getHeight();
	
}