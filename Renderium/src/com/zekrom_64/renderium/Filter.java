package com.zekrom_64.renderium;

import org.lwjgl.opengl.GL11;

/** A filter determines how pixels are blended or selected when scaled.
 * 
 * @author Zekrom_64
 *
 */
public enum Filter {
	NEAREST(GL11.GL_NEAREST),
	LINEAR(GL11.GL_LINEAR);
	
	/** The filter value for OpenGL. */
	public final int glFilter;
	
	private Filter(int filter) {
		this.glFilter = filter;
	}
}
