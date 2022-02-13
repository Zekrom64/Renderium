package com.zekrom_64.renderium;

import org.lwjgl.opengl.GL45;

import com.zekrom_64.renderium.Framebuffer.AttachmentType;
import com.zekrom_64.renderium.Framebuffer.IFramebufferAttachment;
import com.zekrom_64.renderium.util.ISafeCloseable;

public class Renderbuffer implements ISafeCloseable, IFramebufferAttachment {

	private final int renderbufferID;
	private final Format format;
	
	public Renderbuffer(int width, int height, Format format) {
		renderbufferID = GL45.glCreateRenderbuffers();
		this.format = format;
		GL45.glNamedRenderbufferStorage(renderbufferID, format.glInternalFormat, width, height);
	}
	
	@Override
	public int getGLID() {
		return renderbufferID;
	}

	@Override
	public AttachmentType getType() {
		return AttachmentType.RENDERBUFFER;
	}

	@Override
	public Format getFormat() {
		return format;
	}

	@Override
	public void close() {
		GL45.glDeleteRenderbuffers(renderbufferID);
	}

}
