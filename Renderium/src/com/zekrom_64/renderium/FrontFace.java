package com.zekrom_64.renderium;

import org.lwjgl.opengl.GL45;

public enum FrontFace {
	CLOCKWISE(GL45.GL_CW),
	COUNTER_CLOCKSIZE(GL45.GL_CCW);
	
	public final int glFace;
	
	private FrontFace(int face) {
		this.glFace = face;
	}
}
