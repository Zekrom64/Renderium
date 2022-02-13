package com.zekrom_64.renderium;

import org.lwjgl.opengl.GL45;

public enum WrapMode {
	CLAMP_TO_EDGE(GL45.GL_CLAMP_TO_EDGE),
	MIRRORED_REPEAT(GL45.GL_MIRRORED_REPEAT),
	REPEAT(GL45.GL_REPEAT),
	MIRROR_CLAMP_TO_EDGE(GL45.GL_MIRROR_CLAMP_TO_EDGE),
	CLAMP_TO_BORDER(GL45.GL_CLAMP_TO_BORDER);
	
	public final int glMode;
	
	private WrapMode(int mode) {
		this.glMode = mode;
	}
	
}
