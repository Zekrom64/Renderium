package com.zekrom_64.renderium.render;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL45;

import com.zekrom_64.renderium.render.info.MapMode;
import com.zekrom_64.renderium.util.ISafeCloseable;
import com.zekrom_64.renderium.util.IStruct;

public class BufferObject implements ISafeCloseable {

	private static final int STORAGE_FLAG_MASK =
			GL45.GL_MAP_READ_BIT |
			GL45.GL_MAP_WRITE_BIT |
			GL45.GL_MAP_PERSISTENT_BIT |
			GL45.GL_MAP_COHERENT_BIT;
	
	final int bufferID;

	private final int size;
	
	public BufferObject(int size, MapMode requiredMode) {
		bufferID = GL45.glCreateBuffers();
		this.size = size;
		GL45.glNamedBufferStorage(bufferID, size, requiredMode.glMapAccess & STORAGE_FLAG_MASK);
	}
	
	public int getSize() {
		return size;
	}
	
	public ByteBuffer map(MapMode mode, int offset, int length) {
		return GL45.glMapNamedBufferRange(bufferID, offset, length, mode.glMapAccess);
	}
	
	public ByteBuffer map(MapMode mode) {
		return map(mode, 0, size);
	}
	
	public BufferObject unmap() {
		GL45.glUnmapNamedBuffer(bufferID);
		return this;
	}
	
	public BufferObject update(IStruct<ByteBuffer> structure) {
		structure.get(0, map(MapMode.WRITE_INVALIDATE));
		unmap();
		return this;
	}
	
	@Override
	public void close() {
		GL45.glDeleteBuffers(bufferID);
	}
	
}
