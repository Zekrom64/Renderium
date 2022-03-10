package com.zekrom_64.renderium.render.info;

import org.lwjgl.opengl.GL45;

/** Enumeration of blend equation factors.
 * 
 * @author Zekrom_64
 *
 */
public enum BlendFactor {
	/** Constant zero. */
	ZERO(GL45.GL_ZERO),
	/** Constant one. */
	ONE(GL45.GL_ONE),
	/** The source alpha. */
	SRC_ALPHA(GL45.GL_SRC_ALPHA),
	/** The source color. */
	SRC_RGB(GL45.GL_SRC_COLOR),
	/** The destination alpha. */
	DST_ALPHA(GL45.GL_DST_ALPHA),
	/** The destination color. */
	DST_RGB(GL45.GL_DST_COLOR),
	/** One minus the source alpha. */
	ONE_MINUS_SRC_ALPHA(GL45.GL_ONE_MINUS_SRC_ALPHA),
	/** One minus the source color. */
	ONE_MINUS_SRC_RGB(GL45.GL_ONE_MINUS_SRC_COLOR),
	/** One minus the source alpha. */
	ONE_MINUS_DST_ALPHA(GL45.GL_ONE_MINUS_DST_ALPHA),
	/** One minus the source color. */
	ONE_MINUS_DST_RGB(GL45.GL_ONE_MINUS_DST_COLOR);
	
	/** The OpenGL factor value. */
	public final int glFactor;
	
	private BlendFactor(int factor) {
		this.glFactor = factor;
	}
	
}
