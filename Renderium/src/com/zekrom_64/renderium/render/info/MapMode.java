package com.zekrom_64.renderium.render.info;

import org.lwjgl.opengl.GL45;

/** Enumeration of memory mapping modes.
 * 
 * @author Zekrom_64
 *
 */
public enum MapMode {
	/** The mapped memory will only be written to by the host. */
	WRITE_ONLY(GL45.GL_WRITE_ONLY, GL45.GL_MAP_WRITE_BIT),
	/** The mapped memory will only be read by the host. */
	READ_ONLY(GL45.GL_READ_ONLY, GL45.GL_MAP_READ_BIT),
	/** The mapped memory may be read or written by the host. */
	READ_WRITE(GL45.GL_READ_WRITE, GL45.GL_MAP_READ_BIT | GL45.GL_MAP_WRITE_BIT),
	
	/** Similar to {@link WRITE_ONLY} but hints that the memory range can be invalidated before writing. */
	WRITE_INVALIDATE(GL45.GL_WRITE_ONLY, GL45.GL_MAP_WRITE_BIT | GL45.GL_MAP_INVALIDATE_RANGE_BIT);
	
	/** The OpenGL mapping mode. */
	public final int glMapMode;
	/** Bitmask of OpenGL access flags. */
	public final int glMapAccess;
	
	private MapMode(int mapMode, int mapAccess) {
		this.glMapMode = mapMode;
		this.glMapAccess = mapAccess;
	}
}
