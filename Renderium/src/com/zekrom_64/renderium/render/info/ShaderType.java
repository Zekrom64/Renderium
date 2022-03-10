package com.zekrom_64.renderium.render.info;

import org.lwjgl.opengl.GL45;

/** Enumeration of shader types.
 * 
 * @author Zekrom_64
 *
 */
public enum ShaderType {
	/** Vertex shader. */
	VERTEX(GL45.GL_VERTEX_SHADER),
	/** Fragment shader. */
	FRAGMENT(GL45.GL_FRAGMENT_SHADER);
	
	/** The OpenGL shader type. */
	public final int glType;
	
	private ShaderType(int type) {
		glType = type;
	}
	
}
