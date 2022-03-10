package com.zekrom_64.renderium.render.info;

import org.lwjgl.opengl.GL45;

/** Enumeration of sampler wrap modes.
 * 
 * @author Zekrom_64
 *
 */
public enum WrapMode {
	/** Sampled values are clamped to the edge of the texture. */
	CLAMP_TO_EDGE(GL45.GL_CLAMP_TO_EDGE),
	/** Sampled values are repeated outside of the normalized range, mirrored every <i>n</i> times. */
	MIRRORED_REPEAT(GL45.GL_MIRRORED_REPEAT),
	/** Sampled values are repeated outside of the normalized range. */
	REPEAT(GL45.GL_REPEAT),
	/** Sampled values are clamped to the edge of the texture, mirrored every <i>n</i> times. */
	MIRROR_CLAMP_TO_EDGE(GL45.GL_MIRROR_CLAMP_TO_EDGE),
	/** Sampled values are clamped to a constant border color. */
	CLAMP_TO_BORDER(GL45.GL_CLAMP_TO_BORDER);
	
	/** The OpenGL wrap mode. */
	public final int glMode;
	
	private WrapMode(int mode) {
		this.glMode = mode;
	}
	
}
