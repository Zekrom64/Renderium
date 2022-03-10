package com.zekrom_64.renderium.render.info;

import org.lwjgl.opengl.GL45;

/** Enumeration of blend equation functions.
 * 
 * @author Zekrom_64
 *
 */
public enum BlendFunction {
	/** Addition. */
	ADD(GL45.GL_FUNC_ADD),
	/** Maximum. */
	MAX(GL45.GL_MAX),
	/** Minimum. */
	MIN(GL45.GL_MIN);
	
	/** The OpenGL blend function value. */
	public final int glFunc;
	
	private BlendFunction(int func) {
		this.glFunc = func;
	}
}
