package com.zekrom_64.renderium.render.info;

import org.lwjgl.opengl.GL45;

/** Enumeration of texture types.
 * 
 * @author Zekrom_64
 *
 */
public enum TextureType {
	/** A regular 2D texture. */
	TEX2D(GL45.GL_TEXTURE_2D, 2);
	
	/** The OpenGL texture target used by this type of texture. */
	public final int glTarget;
	/** The number of dimensions the texture has. */
	public final int dimensions;
	
	private TextureType(int target, int dimensions) {
		this.glTarget = target;
		this.dimensions = dimensions;
	}
}
