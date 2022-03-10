package com.zekrom_64.renderium.render;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.joml.Vector2ic;
import org.joml.Vector3i;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL45;
import org.lwjgl.opengl.GLDebugMessageCallbackI;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.event.Level;

import com.zekrom_64.renderium.render.Sampler.SamplerInfo;
import com.zekrom_64.renderium.render.Texture.TextureInfo;
import com.zekrom_64.renderium.render.info.BlendEquation;
import com.zekrom_64.renderium.render.info.CullMode;
import com.zekrom_64.renderium.render.info.DrawMode;
import com.zekrom_64.renderium.render.info.Filter;
import com.zekrom_64.renderium.render.info.Format;
import com.zekrom_64.renderium.render.info.FrontFace;
import com.zekrom_64.renderium.render.info.MapMode;
import com.zekrom_64.renderium.render.info.TextureType;
import com.zekrom_64.renderium.render.info.UniformBinding;
import com.zekrom_64.renderium.render.info.WrapMode;
import com.zekrom_64.renderium.resource.ResourceLocation;
import com.zekrom_64.renderium.util.HighResolutionTime;
import com.zekrom_64.renderium.util.ISafeCloseable;
import com.zekrom_64.renderium.util.LogUtil;
import com.zekrom_64.renderium.util.TypeUtils;
import com.zekrom_64.renderium.util.collections.RectStack;

public class Renderer implements ISafeCloseable {

	// The number of frame times to use to compute the average
	private static final int FRAMETIME_AVG_COUNT = 16;
	
	// Offset to apply to wait periods for target frame times to account for delays
	private static final int FRAMETIME_TARGET_OFFSET = -1000000;
	
	
	/** The display the renderer is linked to. */
	public final @NonNull Display display;
	
	/** The default framebuffer which will output to the display. */
	public final @NonNull Framebuffer defaultFramebuffer;
	
	/** The shader compiler to load shaders with. */
	public final @NonNull ShaderCompiler shaderCompiler = new ShaderCompiler(this);
	
	/** A "plain" shader program that draws textured and colored polygons with no additional effects.
	 * This is useful for rendering objects such as UI elements that aren't part of the world.
	 */
	public final @NonNull ShaderProgram shaderPlain;
	public final @NonNull UniformBinding uniformPlainGlobals;
	public final @NonNull UniformBinding uniformPlainTexture;
	
	/** A "missing" texture, of a 2x2 tiling of black and magenta. */
	public final @NonNull Texture missingTexture;
	/** An "identity" texture, with a 1x1 opaque white pixel. */
	public final @NonNull Texture identityTexture;
	/** A "null" texture, with a 1x1 transparent black pixel. */
	public final @NonNull Texture nullTexture;
	
	/** An "identity" sampler with nearest sampling, clamp-to-edge addressing, and a transparent border. */
	public final @NonNull Sampler identitySampler;
	
	// The held OpenGL debug callback reference, so it doesn't get GC'd away
	private GLDebugMessageCallbackI debugCallback;
	
	/** The logger used by the renderer. */
	public final @Nullable Logger logger;
	
	/** Renderer creation information.
	 * 
	 * @author Zekrom_64
	 *
	 */
	public static class RendererInfo {
		
		/** The display this renderer will output to. */
		public final @NonNull Display display;
		/** An optional logger to print errors and warnings to. */
		public @Nullable Logger logger = null;
		/** An optional logging level to override error messages with. */
		public @Nullable Level errorLevel = null;
		
		public RendererInfo(@NonNull Display display) {
			this.display = display;
		}
		
	}
	
	/** Creates a new renderer using the given renderer information.
	 * 
	 * @param info Renderer information
	 */
	public Renderer(@NonNull RendererInfo info) {
		// Create the OpenGL context from the display
		this.display = info.display;
		this.logger = info.logger;
		GLFW.glfwMakeContextCurrent(display.window);
		GL.createCapabilities();
		
		// If logging is enabled and this is a debug context
		if (info.logger != null && GLFW.glfwGetWindowAttrib(display.window, GLFW.GLFW_CONTEXT_DEBUG) == GLFW.GLFW_TRUE) {
			final Level errorLevel = info.errorLevel != null ? info.errorLevel : Level.ERROR;
			
			// Enable debug messaging
			debugCallback = new GLDebugMessageCallbackI() {

				@Override
				public void invoke(int source, int type, int id, int severity, int length, long message, long userParam) {
					String mstr = MemoryUtil.memUTF8(message);
					
					Level lvl;
					switch(type) {
					// Errors should be reported as whatever the error level is
					case GL45.GL_DEBUG_TYPE_ERROR:
						lvl = errorLevel;
						break;
					// All these message types are not necessarily errors, but should be warned about
					case GL45.GL_DEBUG_TYPE_DEPRECATED_BEHAVIOR:
					case GL45.GL_DEBUG_TYPE_PORTABILITY:
					case GL45.GL_DEBUG_TYPE_UNDEFINED_BEHAVIOR:
						lvl = Level.WARN;
						break;
					// Performance messages are really only debug information
					case GL45.GL_DEBUG_TYPE_PERFORMANCE:
						lvl = Level.DEBUG;
						break;
					// Everything else gets categorized as info
					default:
						lvl = Level.INFO;
						break;
					}
					
					LogUtil.log(info.logger, lvl, mstr);
				}
				
			};
			GL45.glDebugMessageCallback(debugCallback, 0);
			GL45.glEnable(GL45.GL_DEBUG_OUTPUT);
		}
		
		// Create the default framebuffer
		defaultFramebuffer = new Framebuffer(display);
		
		// Load the plain shader
		shaderPlain = shaderCompiler.loadShader("renderium:shaders/plain.json");
		uniformPlainGlobals = TypeUtils.requireNonNull(shaderPlain.getUniform("uGlobals"));
		uniformPlainTexture = TypeUtils.requireNonNull(shaderPlain.getUniform("uTexture"));
		
		// Create the built-in textures
		missingTexture = new Texture(2, 2, new int[] {
			0xFFFF00FF, 0,
			0, 0xFFFF00FF
		});
		identityTexture = new Texture(1, 1, new int[] { 0xFFFFFFFF });
		nullTexture = new Texture(1, 1, new int[] { 0x00000000 });
		
		// Create the built-in samplers
		identitySampler = new Sampler(new SamplerInfo(
			Filter.NEAREST,
			Filter.NEAREST,
			Filter.NEAREST,
			WrapMode.CLAMP_TO_EDGE,
			WrapMode.CLAMP_TO_EDGE,
			WrapMode.CLAMP_TO_EDGE,
			null
		));

		// Setup the basic OpenGL state
		GL45.glEnable(GL45.GL_BLEND);
	}
	
	//==================//
	// Resource Loading //
	//==================//
	
	// Buffer object used for uploads
	private BufferObject uploadBuffer = null;
	
	/** Loads an image as a texture. If the texture fails to load, a "missing texture"
	 * texture is returned.
	 * 
	 * @param resource Image resource
	 * @return Loaded texture
	 */
	public @NonNull Texture loadTextureImage(@NonNull ResourceLocation resource) {
		ByteBuffer databuf = null;
		try (MemoryStack sp = MemoryStack.stackPush()) {
			// Read resource bytes and transfer to byte buffer
			byte[] imgdata = resource.readBytes();
			databuf = MemoryUtil.memAlloc(imgdata.length);
			databuf.put(0, imgdata);
			
			// Load from memory
			IntBuffer x = sp.mallocInt(1), y = sp.mallocInt(1), channels = sp.mallocInt(1);
			ByteBuffer pixels = STBImage.stbi_load_from_memory(databuf, x, y, channels, STBImage.STBI_default);
			// Initialize format based on what STB returns
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
				// If missing the upload buffer or it is too small, recreate it
				if (uploadBuffer == null || uploadBuffer.getSize() < pixels.capacity()) {
					if (uploadBuffer != null) uploadBuffer.close();
					uploadBuffer = new BufferObject(pixels.capacity(), MapMode.WRITE_INVALIDATE);
				}
				// Map the upload buffer and copy the pixel data, then unmap
				uploadBuffer.map(MapMode.WRITE_INVALIDATE).put(pixels);
				uploadBuffer.unmap();
				
				// Create a new texture and upload the pixel data, then return the created texture
				Texture texture = new Texture(new TextureInfo(
					TextureType.TEX2D,
					format,
					new Vector3i(x.get(0), y.get(0), 1),
					1
				));
				texture.upload(uploadBuffer);
				return texture;
			} finally {
				// Free the pixel data STB returned
				pixels.rewind();
				STBImage.stbi_image_free(pixels);
			}
		} catch (Exception e) {
			// If there was an error loading the texture, return the "missing texture" texture
			return missingTexture;
		} finally {
			// Free the image data
			if (databuf != null) MemoryUtil.memFree(databuf);
		}
	}
	
	//===================//
	// Renderer Settings //
	//===================//
	
	// The nanosecond timestamp of when the last frame started
	private long nsLastFrameStart = HighResolutionTime.getNanoseconds();
	// The target time for a frame to take in nanoseconds
	private int nsFrameTarget = -1;
	// Buffer of sampled frame times
	private long[] nsFrametimeBuffer = new long[FRAMETIME_AVG_COUNT];
	// The next index to store to in the frame time buffer
	private int nextFrametimeIndex = 0;
	// The average frame time
	private long avgFrametime;
	// The floating-point time delta between frames
	private float fdelta = 0.0f;
	// If V-sync is enabled
	private boolean vsync = false;

	/** Gets if V-sync is enabled.
	 * 
	 * @return If V-sync is enabled
	 */
	public boolean isVSyncEnabled() {
		return vsync;
	}
	
	/** Sets if V-sync is enabled.
	 * 
	 * @param enable If V-sync is enabled
	 */
	public void setVSyncEnabled(boolean enable) {
		GLFW.glfwSwapInterval(enable ? 1 : 0);
		vsync = enable;
	}
	
	/** Sets the target framerate to use.
	 * 
	 * @param framerate Target framerate in frames per second
	 * @return This renderer
	 */
	public @NonNull Renderer setTargetFramerate(int framerate) {
		if (framerate <= 0) nsFrameTarget = -1;
		else nsFrameTarget = 1000000000 / framerate;
		
		return this;
	}
	
	/** Begins rendering a new frame.
	 * 
	 * @return This renderer
	 */
	public @NonNull Renderer beginFrame() {
		// Get the current time
		long now = HighResolutionTime.getNanoseconds();
		// Find the change in time from the last frame
		long delta = now - nsLastFrameStart;
		// If vsync is not already enabled and there is a framerate target
		if (!vsync && nsFrameTarget > 0) {
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
	
	/** Gets the average frame time in nanoseconds.
	 * 
	 * @return Average frame time
	 */
	public long getAverageFrametimeNanos() {
		return avgFrametime;
	}
	
	/** Gets the average fram erate in frames per second.
	 * 
	 * @return Average frame rate
	 */
	public float getAverageFramerate() {
		return 1000000000.0f / avgFrametime;
	}
	
	/** Gets the time delta (difference in time) between this frame and the last.
	 * 
	 * @return Frame time delta
	 */
	public float getTimeDelta() {
		return fdelta;
	}
	
	/** Ends the current frame.
	 * 
	 * @return This renderer
	 */
	public @NonNull Renderer endFrame() {
		// Swap buffers with the default framebuffer
		GLFW.glfwSwapBuffers(display.window);
		
		return this;
	}
	
	//==================//
	// Resource Binding //
	//==================//
	
	// The currently bound vertex array ID
	private int currentVertexArrayID = 0;
	// The currently bound shader program ID
	private int currentProgramID = 0;
	// The currently bound framebuffer
	private int currentFramebufferID = 0;
	
	/** Binds the given vertex array for rendering.
	 * 
	 * @param array Vertex array to use
	 * @return This renderer
	 */
	public @NonNull Renderer useVertexArray(@NonNull VertexArray array) {
		int id = array.vertexArrayID;
		if (id != currentVertexArrayID) {
			GL45.glBindVertexArray(id);
			currentVertexArrayID = id;
		}
		return this;
	}
	
	/** Binds the given shader program for rendering.
	 * 
	 * @param program Shader program to use
	 * @return This renderer
	 */
	public @NonNull Renderer useShaderProgram(@NonNull ShaderProgram program) {
		int id = program.programID;
		if (id != currentProgramID) {
			GL45.glUseProgram(id);
			currentProgramID = id;
		}
		return this;
	}
	
	/** Binds a buffer object range to a shader program uniform.
	 * 
	 * @param binding Uniform binding
	 * @param buffer Buffer object to bind
	 * @param offset The offset into the buffer to bind at
	 * @param size The length of the binding in bytes
	 * @return This renderer
	 */
	public @NonNull Renderer bindUniform(@NonNull UniformBinding binding, @NonNull BufferObject buffer, int offset, int size) {
		switch(binding.type()) {
		case UNIFORM_BUFFER:
			GL45.glBindBufferRange(GL45.GL_UNIFORM_BUFFER, binding.binding(), buffer.bufferID, offset, size);
			break;
		default:
			throw new IllegalArgumentException("Cannot bind buffer object to non-buffer binding");
		}
		return this;
	}
	
	/** Binds a whole buffer object to a shader program uniform.
	 * 
	 * @param binding Uniform binding
	 * @param buffer Buffer object to bind
	 * @return This renderer
	 */
	public @NonNull Renderer bindUniform(@NonNull UniformBinding binding, @NonNull BufferObject buffer) {
		return bindUniform(binding, buffer, 0, buffer.getSize());
	}
	
	/** Binds a combined texture and sampler to a shader program uniform.
	 * 
	 * @param binding Uniform binding
	 * @param texture Texture to bind
	 * @param sampler Sampler to bind
	 * @return This renderer
	 */
	public @NonNull Renderer bindUniform(@NonNull UniformBinding binding, @NonNull Texture texture, @NonNull Sampler sampler) {
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
	
	/** Binds the given framebuffer to use for rendering.
	 * 
	 * @param fb Framebuffer to use
	 * @return This renderer
	 */
	public @NonNull Renderer useFramebuffer(@NonNull Framebuffer fb) {
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
	
	/** Draws vertices using the current set of bound resources.
	 * 
	 * @param mode Draw mode
	 * @param vertexCount Number of vertices to draw
	 * @param instanceCount Number of instances to draw
	 * @param firstVertex Offset of the first vertex to draw
	 * @param firstInstance Offset of the first instance to draw
	 * @return This renderer
	 */
	public @NonNull Renderer draw(@NonNull DrawMode mode, int vertexCount, int instanceCount, int firstVertex, int firstInstance) {
		GL45.glDrawArraysInstancedBaseInstance(mode.glMode, firstVertex, vertexCount, instanceCount, firstInstance);
		return this;
	}
	
	/** Draws indexed vertices using the current set of bound resources.
	 * 
	 * @param mode Draw mode
	 * @param indexCount Number of indexed vertices to draw
	 * @param instanceCount Number of instances to draw
	 * @param firstIndex Offset of the first index to draw
	 * @param vertexOffset Offset to apply to each index
	 * @param firstInstance Offset of the first instance to draw
	 * @return This renderer
	 */
	public @NonNull Renderer drawIndexed(@NonNull DrawMode mode, int indexCount, int instanceCount, int firstIndex, int vertexOffset, int firstInstance) {
		GL45.glDrawElementsInstancedBaseVertexBaseInstance(mode.glMode, indexCount, GL45.GL_UNSIGNED_INT, 4 * firstIndex, instanceCount, vertexOffset, firstInstance);
		return this;
	}
	
	//========================//
	// Viewport/Scissor State //
	//========================//
	
	// Viewport/scissor stack
	private final RectStack viewportStack = new RectStack(1000);
	private final RectStack scissorStack = new RectStack(1000);
	
	/** Resets the viewport stack and initializes it with the given viewport parameters.
	 * 
	 * @param x X offset
	 * @param y Y offset
	 * @param width Viewport width
	 * @param height Viewport height
	 * @return This renderer
	 */
	public @NonNull Renderer resetViewport(int x, int y, int width, int height) {
		viewportStack.clear();
		return pushViewport(x, y, width, height);
	}
	
	/** Pushes a new viewport to the viewport stack and sets the current viewport.
	 * 
	 * @param x X offset
	 * @param y Y offset
	 * @param width Viewport width
	 * @param height Viewport height
	 * @return This renderer
	 */
	public @NonNull Renderer pushViewport(int x, int y, int width, int height) {
		y = -y;
		viewportStack.push(x, y, width, height);
		GL45.glViewport(x, y, width, height);
		return this;
	}
	
	/** Pops a viewport from the viewport stack and sets the current viewport to the new top.
	 * 
	 * @return This renderer
	 */
	public @NonNull Renderer popViewport() {
		if (viewportStack.getSize() > 1) {
			viewportStack.pop();
			viewportStack.peek(r -> {
				Vector2ic xy = r.getMin();
				GL45.glViewport(xy.x(), xy.y(), r.getWidth(), r.getHeight());
			});
		}
		return this;
	}

	/** Resets the scissor stack and initializes it with the given scissor parameters.
	 * 
	 * @param x X offset
	 * @param y Y offset
	 * @param width Scissor width
	 * @param height Scissor height
	 * @return This renderer
	 */
	public @NonNull Renderer resetScissor(int x, int y, int width, int height) {
		scissorStack.clear();
		return pushScissor(x, y, width, height);
	}

	/** Pushes a new scissor to the scissor stack and sets the current scissor.
	 * 
	 * @param x X offset
	 * @param y Y offset
	 * @param width Scissor width
	 * @param height Scissor height
	 * @return This renderer
	 */
	public @NonNull Renderer pushScissor(int x, int y, int width, int height) {
		y = -y;
		scissorStack.push(x, y, width, height);
		GL45.glScissor(x, y, width, height);
		return this;
	}

	/** Pops a scissor from the scissor stack and sets the current scissor to the new top.
	 * 
	 * @return This renderer
	 */
	public @NonNull Renderer popScissor() {
		if (scissorStack.getSize() > 1) {
			scissorStack.pop();
			scissorStack.peek(r -> {
				Vector2ic xy = r.getMin();
				GL45.glScissor(xy.x(), xy.y(), r.getWidth(), r.getHeight());
			});
		}
		return this;
	}
	
	/** Resets both the viewport and scissor via {@link #resetViewport(int, int, int, int)} and
	 * {@link #resetScissor(int, int, int, int)}.
	 * 
	 * @param x X coordinate
	 * @param y Y coordiante
	 * @param width Viewport/scissor width
	 * @param height Viewport/scissor height
	 * @return This renderer
	 */
	public @NonNull Renderer resetViewportScissor(int x, int y, int width, int height) {
		return resetViewport(x, y, width, height).resetScissor(x, y, width, height);
	}
	
	//======================//
	// Misc. Pipeline State //
	//======================//
	
	// The current front face
	private FrontFace frontFace = null;
	// The current culling mode
	private CullMode cullMode = null;
	// The current blend equation
	private BlendEquation blending = null;
	
	/** Set which winding order is used to determine the front face for face culling.
	 * 
	 * @param face Front face type
	 * @return This renderer
	 */
	public @NonNull Renderer setFrontFace(@NonNull FrontFace face) {
		if (face != frontFace) {
			frontFace = face;
			GL45.glFrontFace(face.glFace);
		}
		return this;
	}
	
	/** Sets the face culling mode to use.
	 * 
	 * @param mode Face culling mode
	 * @return This renderer
	 */
	public @NonNull Renderer setCullMode(@NonNull CullMode mode) {
		if (mode != cullMode) {
			cullMode = mode;
			switch(mode) {
			case NONE:
				GL45.glDisable(GL45.GL_CULL_FACE);
				break;
			default:
				GL45.glEnable(GL45.GL_CULL_FACE);
				GL45.glCullFace(mode.glMode);
				break;
			}
		}
		return this;
	}
	
	/** Sets the blend equation to use.
	 * 
	 * @param eq Blend equation
	 * @return This renderer
	 */
	public @NonNull Renderer setBlendEquation(@NonNull BlendEquation eq) {
		if (eq != blending) {
			blending = eq;
			GL45.glBlendEquationSeparate(eq.rgbFunc().glFunc, eq.alphaFunc().glFunc);
			GL45.glBlendFuncSeparate(eq.srcRGB().glFactor, eq.dstRGB().glFactor, eq.srcAlpha().glFactor, eq.dstAlpha().glFactor);
		}
		return this;
	}
	
	//=====================//
	// Closeable Interface //
	//=====================//

	@Override
	public void close() {
		shaderPlain.close();
		
		missingTexture.close();
		identityTexture.close();
		nullTexture.close();
		identitySampler.close();
		
		if (uploadBuffer != null) uploadBuffer.close();
		
		viewportStack.close();
		scissorStack.close();
	}
	
}
