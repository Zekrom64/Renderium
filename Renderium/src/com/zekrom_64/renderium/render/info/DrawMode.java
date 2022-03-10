package com.zekrom_64.renderium.render.info;

import org.lwjgl.opengl.GL45;

/** Enumeration of vertex drawing modes.
 * 
 * @author Zekrom_64
 *
 */
public enum DrawMode {
	TRIANGLES(GL45.GL_TRIANGLES);
	
	/** The OpenGL draw mode. */
	public final int glMode;
	
	private DrawMode(int mode) {
		this.glMode = mode;
	}
}
