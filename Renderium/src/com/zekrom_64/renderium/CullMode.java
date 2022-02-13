package com.zekrom_64.renderium;

import org.lwjgl.opengl.GL11;

public enum CullMode {
	NONE(GL11.GL_NONE),
	FRONT(GL11.GL_FRONT),
	BACK(GL11.GL_BACK);
	
	public final int glMode;
	
	private CullMode(int mode) {
		this.glMode = mode;
	}
}
