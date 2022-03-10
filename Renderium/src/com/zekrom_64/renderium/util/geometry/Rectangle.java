package com.zekrom_64.renderium.util.geometry;

import java.nio.ByteBuffer;

import org.joml.Vector2i;
import org.joml.Vector2ic;

import com.zekrom_64.renderium.util.IStruct;

/** Implementation of the {@link IRectangle} interface.
 * 
 * @author Zekrom_64
 *
 */
public class Rectangle implements IStruct<ByteBuffer>, IRectangle {

	/** The size of the struct in bytes. */
	public static final int SIZEOF = 4 * Integer.BYTES;
	
	private final Vector2i pmin = new Vector2i();
	private final Vector2i pmax = new Vector2i();
	
	/** Creates a new rectangle at (0,0) with a size of (0,0).
	 * 
	 */
	public Rectangle() { }
	
	/** Creates a new rectangle of the given width and height at (0,0).
	 * 
	 * @param width Rectangle width
	 * @param height Rectangle height
	 */
	public Rectangle(int width, int height) {
		pmax.x = width;
		pmax.y = height;
	}
	
	/** Creates a new rectangle defined by two points.
	 * 
	 * @param x0 First X coordinate
	 * @param y0 First Y coordinate
	 * @param x1 Second X coordinate
	 * @param y1 Second Y coordinate
	 */
	public Rectangle(int x0, int y0, int x1, int y1) {
		pmin.x = Math.min(x0, x1);
		pmin.y = Math.min(y0, y1);
		pmax.x = Math.max(x0, x1);
		pmax.y = Math.max(y0, y1);
	}
	
	public Vector2ic getMin() {
		return pmin;
	}
	
	public Vector2ic getMax() {
		return pmax;
	}
	
	public int getWidth() {
		return pmax.x - pmin.x;
	}
	
	public int getHeight() {
		return pmax.y - pmin.y;
	}
	
	/** Offsets this rectangle by the given amount. 
	 * 
	 * @param x X offset
	 * @param y Y offset
	 * @return This rectangle
	 */
	public Rectangle offset(int x, int y) {
		pmin.x += x;
		pmin.y += y;
		pmax.x += x;
		pmax.y += y;
		return this;
	}
	
	/** Offsets this rectangle by the given amount.
	 * 
	 * @param v Vector offset
	 * @return This rectangle
	 */
	public Rectangle offset(Vector2ic v) {
		return offset(v.x(), v.y());
	}
	
	/** Sets this rectangle to another rectangle.
	 * 
	 * @param r Rectange to copy
	 * @return This rectangle
	 */
	public Rectangle set(Rectangle r) {
		pmin.set(r.pmin);
		pmax.set(r.pmax);
		return this;
	}
	
	/** Sets this rectangle to a rectangle of the given size at (0,0).
	 * 
	 * @param width Rectangle width
	 * @param height Rectangle height
	 * @return This rectangle
	 */
	public Rectangle set(int width, int height) {
		pmin.set(0, 0);
		pmax.set(width, height);
		return this;
	}
	
	/** Sets this rectangle to a rectangle defined by two points.
	 * 
	 * @param x0 First X coordinate
	 * @param y0 First Y coordinate
	 * @param x1 Second X coordinate
	 * @param y1 Second Y coordinate
	 * @return This rectangle
	 */
	public Rectangle set(int x0, int y0, int x1, int y1) {
		pmin.x = Math.min(x0, x1);
		pmin.y = Math.min(y0, y1);
		pmax.x = Math.max(x0, x1);
		pmax.y = Math.max(y0, y1);
		return this;
	}
	
	public void get(int position, ByteBuffer buf) {
		buf.putInt(position, pmin.x);
		buf.putInt(position + 4, pmin.y);
		buf.putInt(position + 8, pmax.x);
		buf.putInt(position + 12, pmax.y);
	}
	
	public void set(int position, ByteBuffer buf) {
		pmin.x = buf.getInt(position);
		pmin.y = buf.getInt(position + 4);
		pmax.x = buf.getInt(position + 8);
		pmax.y = buf.getInt(position + 12);
	}
	
}
