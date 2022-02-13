package com.zekrom_64.renderium;

import org.lwjgl.opengl.GL45;

/** A format describes how each element of a texture or renderbuffer is stored.
 * 
 * @author Zekrom_64
 *
 */
public enum Format {
	// Color formats
	R8G8B8A8_UNORM(GL45.GL_RGBA8, GL45.GL_RGBA, GL45.GL_UNSIGNED_BYTE, 4, 4, true, Components.COLOR, 4),
	R8G8B8_UNORM(GL45.GL_RGB8, GL45.GL_RGB, GL45.GL_UNSIGNED_BYTE, 3, 3, true, Components.COLOR, 3),
	R5G6B5_UNORM(GL45.GL_RGB565, GL45.GL_RGB, GL45.GL_UNSIGNED_SHORT_5_6_5, 3, 2, true, Components.COLOR, 1),
	// Depth/stencil formats
	D32_SFLOAT(GL45.GL_DEPTH_COMPONENT32F, GL45.GL_R, GL45.GL_FLOAT, 1, Float.BYTES, false, Components.DEPTH, -1),
	D24_UNORM_S8_SINT(GL45.GL_DEPTH24_STENCIL8, -1, -1, -1, -1, false, Components.DEPTH_STENCIL, -1),
	S8_SINT(GL45.GL_STENCIL_INDEX8, GL45.GL_R, GL45.GL_BYTE, 1, 1, false, Components.STENCIL, 1);
	
	/** The 'internal format' specification of this format, as used by OpenGL. */
	public final int glInternalFormat;
	/** The 'format' specification of this format, as used by OpenGL, or -1 if unavailable. */
	public final int glFormat;
	/** The 'type' specification of this format, as used by OpenGL, or -1 if unavailable. */
	public final int glType;
	/** The number of components in this format, or -1 if unavailable. */
	public final int count;
	/** The size of a pixel of this format, or -1 if unavailable. */
	public final int sizeof;
	/** If the components of this format are normalized. */
	public final boolean normalized;
	/** The components that compose the format. */
	public final Components components;
	/** The 'count' specification of this format, as used by OpenGL, or -1 if unavailable. */
	public final int glCount;
	
	/** Enumeration of format component groupings.
	 * 
	 * @author Zekrom_64
	 *
	 */
	public static enum Components {
		/** A color component. */
		COLOR(GL45.GL_COLOR_BUFFER_BIT),
		/** A depth component. */
		DEPTH(GL45.GL_DEPTH_BUFFER_BIT),
		/** A stencil component. */
		STENCIL(GL45.GL_STENCIL_BUFFER_BIT),
		/** A combined depth & stencil component. */
		DEPTH_STENCIL(GL45.GL_DEPTH_BUFFER_BIT | GL45.GL_STENCIL_BUFFER_BIT);
		
		/** Bitmask of OpenGL buffer fields. */
		public final int glBufferMask;
		
		private Components(int bufferMask) {
			this.glBufferMask = bufferMask;
		}
	}
	
	private Format(int internalFormat, int format, int type, int count, int sizeof, boolean normalized, Components components, int glcount) {
		this.glInternalFormat = internalFormat;
		this.glFormat = format;
		this.glType = type;
		this.count = count;
		this.sizeof = sizeof;
		this.normalized = normalized;
		this.components = components;
		this.glCount = glcount;
	}
}
