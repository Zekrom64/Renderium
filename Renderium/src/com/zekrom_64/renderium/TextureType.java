package com.zekrom_64.renderium;

import org.lwjgl.opengl.GL45;

public enum TextureType {
	TEX2D(GL45.GL_TEXTURE_2D, 2);
	
	public final int glTarget;
	public final int dimensions;
	
	private TextureType(int target, int dimensions) {
		this.glTarget = target;
		this.dimensions = dimensions;
	}
}
