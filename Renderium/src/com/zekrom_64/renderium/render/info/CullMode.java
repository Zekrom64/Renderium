package com.zekrom_64.renderium.render.info;

import org.lwjgl.opengl.GL11;

/** Enumeration of face culling modes.
 * 
 * @author Zekrom_64
 *
 */
public enum CullMode {
	/** No culling is done. */
	NONE(GL11.GL_NONE),
	/** Front-faces are culled. */
	FRONT(GL11.GL_FRONT),
	/** Back-faces are culled. */
	BACK(GL11.GL_BACK);
	
	/** The OpenGL culling mode. */
	public final int glMode;
	
	private CullMode(int mode) {
		this.glMode = mode;
	}
}
