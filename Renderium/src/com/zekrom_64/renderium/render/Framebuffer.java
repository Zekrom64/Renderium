package com.zekrom_64.renderium.render;

import java.nio.FloatBuffer;

import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.joml.Vector4fc;
import org.lwjgl.opengl.GL45;
import org.lwjgl.system.MemoryStack;

import com.zekrom_64.renderium.render.info.Filter;
import com.zekrom_64.renderium.render.info.Format;
import com.zekrom_64.renderium.util.ISafeCloseable;
import com.zekrom_64.renderium.util.geometry.Rectangle;

public class Framebuffer implements ISafeCloseable {
	
	public enum AttachmentType {
		TEXTURE,
		RENDERBUFFER
	}
	
	public interface IFramebufferAttachment {
		
		public int getGLID();
		
		public AttachmentType getType();
		
		public Format getFormat();
		
	}
	
	private final Vector2ic size;
	final int framebufferID;
	
	Framebuffer(Display d) {
		size = d.getSize();
		framebufferID = 0;
	}
	
	public Framebuffer(Vector2ic size, IFramebufferAttachment ... attachments) {
		this.size = new Vector2i(size);
		framebufferID = GL45.glCreateFramebuffers();
		
		int nextColorAttachment = GL45.GL_COLOR_ATTACHMENT0;
		for(IFramebufferAttachment attachment : attachments) {
			int fbattachment = -1;
			switch(attachment.getFormat().components) {
			case COLOR:
				fbattachment = nextColorAttachment++;
				break;
			case DEPTH:
				fbattachment = GL45.GL_DEPTH_ATTACHMENT;
				break;
			case STENCIL:
				fbattachment = GL45.GL_STENCIL_ATTACHMENT;
				break;
			case DEPTH_STENCIL:
				fbattachment = GL45.GL_DEPTH_STENCIL_ATTACHMENT;
				break;
			}
			
			switch(attachment.getType()) {
			case TEXTURE:
				GL45.glNamedFramebufferTexture(framebufferID, fbattachment, attachment.getGLID(), 0);
				break;
			case RENDERBUFFER:
				GL45.glNamedFramebufferRenderbuffer(framebufferID, fbattachment, GL45.GL_RENDERBUFFER, attachment.getGLID());
				break;
			}
		}
	}
	
	public Vector2ic getSize() {
		return size;
	}
	
	public void copy(Framebuffer src, Format.Components components) {
		int sizex = Math.min(size.x(), src.size.x()), sizey = Math.min(size.y(), src.size.y());
		GL45.glBlitNamedFramebuffer(src.framebufferID, framebufferID, 0, 0, sizex, sizey, 0, 0, sizex, sizey, components.glBufferMask, GL45.GL_NEAREST);
	}
	
	public void copy(int dx, int dy, Framebuffer src, int sx, int sy, Format.Components components) {
		int sizex = Math.min(size.x(), src.size.x()), sizey = Math.min(size.y(), src.size.y());
		GL45.glBlitNamedFramebuffer(src.framebufferID, framebufferID, sx, sy, sx + sizex, sy + sizey, dx, dy, dx + sizex, dy + sizey, components.glBufferMask, GL45.GL_NEAREST);
	}
	
	public void copy(int dx, int dy, Framebuffer src, int sx, int sy, int width, int height, Format.Components components) {
		GL45.glBlitNamedFramebuffer(src.framebufferID, framebufferID, sx, sy, sx + width, sy + height, dx, dy, dx + width, dy + height, components.glBufferMask, GL45.GL_NEAREST);
	}
	
	public void blit(Rectangle dstArea, Framebuffer src, Rectangle srcArea, Format.Components components, Filter filter) {
		Vector2ic smin = srcArea.getMin(), smax = srcArea.getMax();
		Vector2ic dmin = dstArea.getMin(), dmax = dstArea.getMax();
		GL45.glBlitNamedFramebuffer(src.framebufferID, framebufferID, smin.x(), smin.y(), smax.x(), smax.y(), dmin.x(), dmin.y(), dmax.x(), dmax.y(), components.glBufferMask, filter.glFilter);
	}
	
	public void clearColor(int attachment, float x, float y, float z, float w) {
		try(MemoryStack sp = MemoryStack.stackPush()) {
			FloatBuffer fv = sp.floats(x, y, z, w);
			GL45.glClearNamedFramebufferfv(framebufferID, GL45.GL_COLOR, attachment, fv);
		}
	}
	
	public void clearColor(int attachment, Vector4fc v) {
		try(MemoryStack sp = MemoryStack.stackPush()) {
			FloatBuffer fv = sp.mallocFloat(4);
			v.get(0, fv);
			GL45.glClearNamedFramebufferfv(framebufferID, GL45.GL_COLOR, attachment, fv);
		}
	}
	
	public void clearDepth(float x) {
		try(MemoryStack sp = MemoryStack.stackPush()) {
			GL45.glClearNamedFramebufferfv(framebufferID, GL45.GL_DEPTH, 0, sp.floats(x));
		}
	}
	
	public void clearStencil(int x) {
		try(MemoryStack sp = MemoryStack.stackPush()) {
			GL45.glClearNamedFramebufferiv(framebufferID, GL45.GL_STENCIL, 0, sp.ints(x));
		}
	}

	@Override
	public void close() {
		if (framebufferID != 0) GL45.glDeleteFramebuffers(framebufferID);
	}
	
}
