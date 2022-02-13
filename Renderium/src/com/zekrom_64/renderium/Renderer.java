package com.zekrom_64.renderium;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.joml.Vector2ic;
import org.joml.Vector3i;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL45;
import org.lwjgl.opengl.GLDebugMessageCallbackI;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import com.zekrom_64.renderium.Sampler.SamplerInfo;
import com.zekrom_64.renderium.Texture.TextureInfo;
import com.zekrom_64.renderium.util.HighResolutionTime;
import com.zekrom_64.renderium.util.ISafeCloseable;
import com.zekrom_64.renderium.util.RectStack;
import com.zekrom_64.renderium.util.Resources;

public class Renderer implements ISafeCloseable {
	
	public static final String TEXTURE_RESOURCE_DIR = "/assets/ne0x10c/textures/";

	// The number of frametimes to use to compute the average
	private static final int FRAMETIME_AVG_COUNT = 16;
	
	// Offset to apply to wait periods for target frametimes to account for delays
	private static final int FRAMETIME_TARGET_OFFSET = -1000000;
	
	
	/** The display the renderer is linked to. */
	public final Display display;
	
	/** The default framebuffer which will output to the display. */
	public final Framebuffer defaultFramebuffer;
	
	/** The shader compiler to load shaders with. */
	public final ShaderCompiler shaderCompiler = new ShaderCompiler();
	
	public final ShaderProgram shaderPlain;
	public final UniformBinding uniformPlainGlobals;
	public final UniformBinding uniformPlainTexture;
	
	/** A "missing" texture, of a 2x2 tiling of black and magenta. */
	public final Texture missingTexture;
	
	/** An "identity" texture, with a 1x1 opaque white pixel. */
	public final Texture identityTexture;
	
	/** An "identity" sampler with nearest sampling, clamp-to-edge addressing, and a transparent border. */
	public final Sampler identitySampler;

	
	private final GLDebugMessageCallbackI debugCallback = new GLDebugMessageCallbackI() {

		@Override
		public void invoke(int source, int type, int id, int severity, int length, long message, long userParam) {
			switch(type) {
			case GL45.GL_DEBUG_TYPE_ERROR:
				throw new RuntimeException("OpenGL Error: " + MemoryUtil.memUTF8(message));
			}
		}
		
	};
	
	// The nanosecond timestamp of when the last frame started
	private long nsLastFrameStart = HighResolutionTime.getNanoseconds();

	// The target time for a frame to take in nanoseconds
	private int nsFrameTarget = -1;
	
	// Buffer of sampled frametimes
	private long[] nsFrametimeBuffer = new long[FRAMETIME_AVG_COUNT];
	// The next index to store to in the frametime buffer
	private int nextFrametimeIndex = 0;
	private long avgFrametime;
	
	// The floating-point time delta between frames
	private float fdelta = 0.0f;
	
	// The currently bound vertex array ID
	private int currentVertexArrayID = 0;
	// The currently bound shader program ID
	private int currentProgramID = 0;
	// The currently bound framebuffer
	private int currentFramebufferID = 0;
	// The current blend equation
	private BlendEquation blending = null;
	
	// Buffer object used for uploads
	private BufferObject uploadBuffer = null;
	
	
	// Viewport/scissor stack
	private final RectStack viewportStack = new RectStack(1000);
	private final RectStack scissorStack = new RectStack(1000);
	
	public Renderer(Display display) {
		this.display = display;
		GL.createCapabilities();
		
		GL45.glDebugMessageCallback(debugCallback, 0);
		GL45.glEnable(GL45.GL_DEBUG_OUTPUT);
		GL45.glEnable(GL45.GL_BLEND);
		
		defaultFramebuffer = new Framebuffer(display);
		
		shaderPlain = shaderCompiler.loadShader("plain");
		uniformPlainGlobals = shaderPlain.getUniform("uGlobals");
		uniformPlainTexture = shaderPlain.getUniform("uTexture");
		
		missingTexture = new Texture(2, 2, new int[] {
			0xFFFF00FF, 0,
			0, 0xFFFF00FF
		});
		identityTexture = new Texture(1, 1, new int[] { 0xFFFFFFFF });
		identitySampler = new Sampler(new SamplerInfo(
			Filter.NEAREST,
			Filter.NEAREST,
			Filter.NEAREST,
			WrapMode.CLAMP_TO_EDGE,
			WrapMode.CLAMP_TO_EDGE,
			WrapMode.CLAMP_TO_EDGE,
			null
		));
	}

	@Override
	public void close() {
		missingTexture.close();
		identitySampler.close();
		identityTexture.close();
		viewportStack.close();
		scissorStack.close();
	}
	
	//==================//
	// Resource Loading //
	//==================//
	
	public Texture loadTexture(String name) {
		ByteBuffer databuf = null;
		try (MemoryStack sp = MemoryStack.stackPush()) {
			byte[] pngdata = Resources.readResourceBytes(TEXTURE_RESOURCE_DIR + name + ".png");
			databuf = MemoryUtil.memAlloc(pngdata.length);
			databuf.put(0, pngdata);
			
			IntBuffer x = sp.mallocInt(1), y = sp.mallocInt(1), channels = sp.mallocInt(1);
			ByteBuffer pixels = STBImage.stbi_load_from_memory(databuf, x, y, channels, STBImage.STBI_default);
			Format format = null;
			switch(channels.get(0)) {
			case STBImage.STBI_rgb_alpha:
				format = Format.R8G8B8A8_UNORM;
				break;
			case STBImage.STBI_rgb:
				format = Format.R8G8B8_UNORM;
				break;
			}
			
			try {
				if (uploadBuffer == null || uploadBuffer.getSize() < pixels.capacity()) {
					if (uploadBuffer != null) uploadBuffer.close();
					uploadBuffer = new BufferObject(pixels.capacity(), MapMode.WRITE_INVALIDATE);
				}
				uploadBuffer.map(MapMode.WRITE_INVALIDATE).put(pixels);
				uploadBuffer.unmap();
				
				Texture texture = new Texture(new TextureInfo(
					TextureType.TEX2D,
					format,
					new Vector3i(x.get(0), y.get(0), 1),
					1
				));
				texture.upload(uploadBuffer);
				return texture;
			} finally {
				pixels.rewind();
				STBImage.stbi_image_free(pixels);
			}
		} catch (Exception e) {
			return new Texture(missingTexture);
		} finally {
			if (databuf != null) MemoryUtil.memFree(databuf);
		}
	}
	
	//===================//
	// Renderer Settings //
	//===================//
	
	public Renderer setTargetFramerate(int framerate) {
		if (framerate <= 0) nsFrameTarget = -1;
		else nsFrameTarget = 1000000000 / framerate;
		
		return this;
	}
	
	public Renderer beginFrame() {
		// Get the current time
		long now = HighResolutionTime.getNanoseconds();
		// Find the change in time from the last frame
		long delta = now - nsLastFrameStart;
		// If vsync is not already enabled and there is a framerate target
		if (!display.isVSyncEnabled() && nsFrameTarget > 0) {
			// Compute the amount of time needed to wait to meet the target
			long wait = (nsFrameTarget - delta) + FRAMETIME_TARGET_OFFSET;
			if (wait > 0) {
				// If non-zero, wait for the time period
				HighResolutionTime.waitNanoseconds(wait);
				// Update the time delta based on how long we actually waited
				now = HighResolutionTime.getNanoseconds();
				delta = now - nsLastFrameStart;
			}
		}
		// Update the last frame start with the current time
		nsLastFrameStart = now;
		
		// Store frametime in buffer
		nsFrametimeBuffer[nextFrametimeIndex++] = delta;
		if (nextFrametimeIndex >= FRAMETIME_AVG_COUNT) nextFrametimeIndex = 0;
		// Compute average frametime
		avgFrametime = 0;
		for(long frametime : nsFrametimeBuffer) avgFrametime += frametime;
		avgFrametime /= FRAMETIME_AVG_COUNT;
		
		// Compute frametime as a floating-point number of seconds
		fdelta = 0.000000001f * delta;
		
		return this;
	}
	
	public long getAverageFrametimeNanos() {
		return avgFrametime;
	}
	
	public float getAverageFramerate() {
		return 1000000000.0f / avgFrametime;
	}
	
	public float getTimeDelta() {
		return fdelta;
	}
	
	public Renderer endFrame() {
		// Swap buffers with the default framebuffer
		display.swapBuffers();
		
		return this;
	}
	
	//==================//
	// Resource Binding //
	//==================//
	
	public Renderer useVertexArray(VertexArray array) {
		int id = array.vertexArrayID;
		if (id != currentVertexArrayID) {
			GL45.glBindVertexArray(id);
			currentVertexArrayID = id;
		}
		return this;
	}
	
	public Renderer useShaderProgram(ShaderProgram program) {
		int id = program.programID;
		if (id != currentProgramID) {
			GL45.glUseProgram(id);
			currentProgramID = id;
		}
		return this;
	}
	
	public Renderer bindUniform(UniformBinding binding, BufferObject buffer, int offset, int size) {
		switch(binding.type()) {
		case UNIFORM_BUFFER:
			GL45.glBindBufferRange(GL45.GL_UNIFORM_BUFFER, binding.binding(), buffer.bufferID, offset, size);
			break;
		default:
			throw new IllegalArgumentException("Cannot bind buffer object to non-buffer binding");
		}
		return this;
	}
	
	public Renderer bindUniform(UniformBinding binding, BufferObject buffer) {
		return bindUniform(binding, buffer, 0, buffer.getSize());
	}
	
	public Renderer bindUniform(UniformBinding binding, Texture texture, Sampler sampler) {
		switch(binding.type()) {
		case TEXTURE:
			GL45.glBindSampler(binding.binding(), sampler.samplerID);
			GL45.glBindTextureUnit(binding.binding(), texture.getGLID());
			break;
		default:
			throw new IllegalArgumentException("Cannot bind texture to non-texture binding");
		}
		return this;
	}
	
	public Renderer useFramebuffer(Framebuffer fb) {
		int id = fb.framebufferID;
		if (id != currentFramebufferID) {
			GL45.glBindFramebuffer(GL45.GL_FRAMEBUFFER, id);
			currentFramebufferID = id;
		}
		return this;
	}
	
	//============//
	// Draw Calls //
	//============//
	
	public Renderer draw(DrawMode mode, int vertexCount, int instanceCount, int firstVertex, int firstInstance) {
		GL45.glDrawArraysInstancedBaseInstance(mode.glMode, firstVertex, vertexCount, instanceCount, firstInstance);
		return this;
	}
	
	public Renderer drawIndexed(DrawMode mode, int indexCount, int instanceCount, int firstIndex, int vertexOffset, int firstInstance) {
		GL45.glDrawElementsInstancedBaseVertexBaseInstance(mode.glMode, indexCount, GL45.GL_UNSIGNED_INT, 4 * firstIndex, instanceCount, vertexOffset, firstInstance);
		return this;
	}
	
	//========================//
	// Viewport/Scissor State //
	//========================//
	
	public Renderer resetViewport(int x, int y, int width, int height) {
		viewportStack.clear();
		return pushViewport(x, y, width, height);
	}
	
	public Renderer pushViewport(int x, int y, int width, int height) {
		y = -y;
		viewportStack.push(x, y, width, height);
		GL45.glViewport(x, y, width, height);
		return this;
	}
	
	public Renderer popViewport() {
		if (viewportStack.getSize() > 1) {
			viewportStack.pop();
			viewportStack.peek(r -> {
				Vector2ic xy = r.getMin();
				GL45.glViewport(xy.x(), xy.y(), r.getWidth(), r.getHeight());
			});
		}
		return this;
	}
	
	public Renderer resetScissor(int x, int y, int width, int height) {
		scissorStack.clear();
		return pushScissor(x, y, width, height);
	}
	
	public Renderer pushScissor(int x, int y, int width, int height) {
		y = -y;
		scissorStack.push(x, y, width, height);
		GL45.glScissor(x, y, width, height);
		return this;
	}
	
	public Renderer popScissor() {
		if (scissorStack.getSize() > 1) {
			scissorStack.pop();
			scissorStack.peek(r -> {
				Vector2ic xy = r.getMin();
				GL45.glScissor(xy.x(), xy.y(), r.getWidth(), r.getHeight());
			});
		}
		return this;
	}
	
	public Renderer resetViewportScissor(int x, int y, int width, int height) {
		return resetViewport(x, y, width, height).resetScissor(x, y, width, height);
	}
	
	//======================//
	// Misc. Pipeline State //
	//======================//
	
	public Renderer setFrontFace(FrontFace face) {
		GL45.glFrontFace(face.glFace);
		return this;
	}
	
	public Renderer setCullMode(CullMode mode) {
		switch(mode) {
		case NONE:
			GL45.glDisable(GL45.GL_CULL_FACE);
			break;
		default:
			GL45.glEnable(GL45.GL_CULL_FACE);
			GL45.glCullFace(mode.glMode);
			break;
		}
		return this;
	}
	
	public void setBlendEquation(BlendEquation eq) {
		if (eq != blending) {
			blending = eq;
			GL45.glBlendEquationSeparate(eq.rgbFunc().glFunc, eq.alphaFunc().glFunc);
			GL45.glBlendFuncSeparate(eq.srcRGB().glFactor, eq.dstRGB().glFactor, eq.srcAlpha().glFactor, eq.dstAlpha().glFactor);
		}
	}
	
}
