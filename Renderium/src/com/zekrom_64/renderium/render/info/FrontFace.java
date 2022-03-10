package com.zekrom_64.renderium.render.info;

import org.lwjgl.opengl.GL45;

/** Enumeration of front-face winding orders.
 * 
 * @author Zekrom_64
 *
 */
public enum FrontFace {
	/** Front-facing fragments have vertices in a clockwise order. */
	CLOCKWISE(GL45.GL_CW),
	/** Front-facing fragments have vertices in a counter-clockwise order. */
	COUNTER_CLOCKSIZE(GL45.GL_CCW);
	
	public final int glFace;
	
	private FrontFace(int face) {
		this.glFace = face;
	}
}
