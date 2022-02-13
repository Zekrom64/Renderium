package com.zekrom_64.renderium;

import java.nio.ByteBuffer;

import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL45;

import com.zekrom_64.renderium.Framebuffer.AttachmentType;
import com.zekrom_64.renderium.Framebuffer.IFramebufferAttachment;
import com.zekrom_64.renderium.util.ISafeCloseable;

public class Texture implements ISafeCloseable, IFramebufferAttachment {
	
	public static record TextureInfo(TextureType type, Format format, Vector3ic size, int mipLevels) { }

	public final TextureInfo info;
	
	private final int textureID;
	
	Texture(int width, int height, int[] data) {
		info = new TextureInfo(
			TextureType.TEX2D,
			Format.R8G8B8A8_UNORM,
			new Vector3i(width, height, 1),
			1
		);
		textureID = GL45.glCreateTextures(GL45.GL_TEXTURE_2D);
		GL45.glTextureStorage2D(textureID, 1, GL45.GL_RGBA8, width, height);
		GL45.glBindBuffer(GL45.GL_PIXEL_UNPACK_BUFFER, 0);
		GL45.glTextureSubImage2D(textureID, 0, 0, 0, width, height, GL45.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data);
	}
	
	public Texture(TextureInfo info) {
		this.info = info;
		textureID = GL45.glCreateTextures(info.type.glTarget);
		switch(info.type.dimensions) {
		case 1:
			GL45.glTextureStorage1D(textureID, info.mipLevels, info.format.glInternalFormat, info.size.x());
			break;
		case 2:
			GL45.glTextureStorage2D(textureID, info.mipLevels, info.format.glInternalFormat, info.size.x(), info.size.y());
			break;
		case 3:
			GL45.glTextureStorage3D(textureID, info.mipLevels, info.format.glInternalFormat, info.size.x(), info.size.y(), info.size.z());
			break;
		}
	}
	
	public Texture(Texture parent) {
		info = parent.info;
		textureID = GL45.glGenTextures();
		GL45.glTextureView(textureID, info.type.glTarget, parent.textureID, info.format.glInternalFormat, 0, info.mipLevels, 0, 1);
	}
	
	@Override
	public int getGLID() {
		return textureID;
	}

	@Override
	public AttachmentType getType() {
		return AttachmentType.TEXTURE;
	}

	@Override
	public Format getFormat() {
		return info.format;
	}
	
	public Texture upload(ByteBuffer buffer, int x, int y, int width, int height, int mipLevel, int offset) {
		if (offset != 0) {
			buffer = buffer.slice();
			buffer.position(offset);
		}
		GL45.glTextureSubImage2D(textureID, mipLevel, x, y, width, height, info.format.glFormat, info.format.glType, buffer);
		return this;
	}

	public Texture upload(ByteBuffer buffer, int x, int y, int width, int height, int mipLevel) {
		return upload(buffer, x, y, width, height, mipLevel, 0);
	}

	public Texture upload(ByteBuffer buffer, int x, int y, int width, int height) {
		return upload(buffer, x, y, width, height, 0);
	}

	public Texture upload(ByteBuffer buffer) {
		return upload(buffer, 0, 0, info.size.x(), info.size.y());
	}
	
	public Texture upload(BufferObject buffer, int x, int y, int width, int height, int mipLevel, int offset) {
		GL45.glBindBuffer(GL45.GL_PIXEL_UNPACK_BUFFER, buffer.bufferID);
		GL45.glTextureSubImage2D(textureID, mipLevel, x, y, width, height, info.format.glFormat, info.format.glType, offset);
		return this;
	}

	public Texture upload(BufferObject buffer, int x, int y, int width, int height, int mipLevel) {
		return upload(buffer, x, y, width, height, mipLevel, 0);
	}

	public Texture upload(BufferObject buffer, int x, int y, int width, int height) {
		return upload(buffer, x, y, width, height, 0);
	}

	public Texture upload(BufferObject buffer) {
		return upload(buffer, 0, 0, info.size.x(), info.size.y());
	}

	@Override
	public void close() {
		GL45.glDeleteTextures(textureID);
	}

}
