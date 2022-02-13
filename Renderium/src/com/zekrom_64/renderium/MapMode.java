package com.zekrom_64.renderium;

import org.lwjgl.opengl.GL45;

public enum MapMode {
	WRITE_ONLY(GL45.GL_WRITE_ONLY, GL45.GL_MAP_WRITE_BIT),
	READ_ONLY(GL45.GL_READ_ONLY, GL45.GL_MAP_READ_BIT),
	READ_WRITE(GL45.GL_READ_WRITE, GL45.GL_MAP_READ_BIT | GL45.GL_MAP_WRITE_BIT),
	
	WRITE_INVALIDATE(GL45.GL_WRITE_ONLY, GL45.GL_MAP_WRITE_BIT | GL45.GL_MAP_INVALIDATE_RANGE_BIT);
	
	public final int glMapMode;
	public final int glMapAccess;
	
	private MapMode(int mapMode, int mapAccess) {
		this.glMapMode = mapMode;
		this.glMapAccess = mapAccess;
	}
}
