package com.zekrom_64.renderium;

import org.lwjgl.opengl.GL45;

public enum BlendFactor {
	ZERO(GL45.GL_ZERO),
	ONE(GL45.GL_ONE),
	SRC_ALPHA(GL45.GL_SRC_ALPHA),
	SRC_RGB(GL45.GL_SRC_COLOR),
	DST_ALPHA(GL45.GL_DST_ALPHA),
	DST_RGB(GL45.GL_DST_COLOR),
	ONE_MINUS_SRC_ALPHA(GL45.GL_ONE_MINUS_SRC_ALPHA),
	ONE_MINUS_SRC_COLOR(GL45.GL_ONE_MINUS_SRC_COLOR);
	
	public final int glFactor;
	
	private BlendFactor(int factor) {
		this.glFactor = factor;
	}
	
}
