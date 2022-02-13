package com.zekrom_64.renderium;

import org.lwjgl.opengl.GL45;

public enum ShaderType {
	VERTEX(GL45.GL_VERTEX_SHADER),
	FRAGMENT(GL45.GL_FRAGMENT_SHADER);
	
	public final int glType;
	
	private ShaderType(int type) {
		glType = type;
	}
	
}
