package com.zekrom_64.renderium.render.info;

import org.lwjgl.opengl.GL45;

/** Enumeration of uniform types.
 * 
 * @author Zekrom_64
 *
 */
public enum UniformType {
	/** A uniform buffer object. */
	UNIFORM_BUFFER(GL45.GL_UNIFORM_BUFFER),
	/** A combined texture & sampler. */
	TEXTURE(-1);
	
	/** The indexed buffer target used by the uniform type, or -1 if it is not a buffer type. */
	public final int glIndexedTarget;
	
	private UniformType(int indexedTarget) {
		this.glIndexedTarget = indexedTarget;
	}
}
