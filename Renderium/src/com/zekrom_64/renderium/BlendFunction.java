package com.zekrom_64.renderium;

import org.lwjgl.opengl.GL45;

public enum BlendFunction {
	ADD(GL45.GL_FUNC_ADD),
	MAX(GL45.GL_MAX),
	MIN(GL45.GL_MIN);
	
	public final int glFunc;
	
	private BlendFunction(int func) {
		this.glFunc = func;
	}
}
