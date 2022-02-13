package com.zekrom_64.renderium;

import org.lwjgl.opengl.GL45;

public enum UniformType {
	UNIFORM_BUFFER(GL45.GL_UNIFORM_BUFFER),
	TEXTURE(-1);
	
	public final int glIndexedTarget;
	
	private UniformType(int indexedTarget) {
		this.glIndexedTarget = indexedTarget;
	}
}
