package com.zekrom_64.renderium;

import org.lwjgl.opengl.GL45;

public enum DrawMode {
	TRIANGLES(GL45.GL_TRIANGLES);
	
	public final int glMode;
	
	private DrawMode(int mode) {
		this.glMode = mode;
	}
}
