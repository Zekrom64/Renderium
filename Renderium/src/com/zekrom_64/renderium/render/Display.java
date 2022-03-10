package com.zekrom_64.renderium.render;

import java.nio.IntBuffer;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import com.zekrom_64.renderium.render.info.DisplayMode;
import com.zekrom_64.renderium.util.ISafeCloseable;

/** A display presents graphics to the user and captures user input. A display
 * can be a window on the desktop or 
 * 
 * @author Zekrom_64
 *
 */
public class Display implements ISafeCloseable {
	
	private static final GLFWErrorCallbackI errorCallback = new GLFWErrorCallbackI() {

		@Override
		public void invoke(int error, long description) {
			throw new RuntimeException("GLFW Error: " + MemoryUtil.memUTF8(description));
		}
		
	};
	
	private static boolean glfwInit = false;
	
	public static void ensureGLFWInit() {
		if (!glfwInit) {
			GLFW.glfwSetErrorCallback(errorCallback);
			GLFW.glfwInit();
			Runtime.getRuntime().addShutdownHook(new Thread(GLFW::glfwTerminate));
			glfwInit = true;
		}
	}
	
	public static class DisplayInfo {
		
		public final @NonNull String title;
		public final @Nullable Vector2ic size;
		public final @Nullable Monitor monitor;
		public final @Nullable DisplayMode displayMode;
		public boolean isDebug = false;
		public boolean isInitiallyVisible = false;
		
		public DisplayInfo(@NonNull String title, @NonNull Vector2ic size) {
			this.title = title;
			this.size = size;
			this.monitor = null;
			this.displayMode = null;
		}
		
		public DisplayInfo(@NonNull String title, @NonNull Monitor monitor, @NonNull DisplayMode displayMode) {
			this.title = title;
			this.size = new Vector2i(displayMode.width(), displayMode.height());
			this.monitor = null;
			this.displayMode = displayMode;
		}
		
		public DisplayInfo setDebug() {
			isDebug = true;
			return this;
		}
		
		public DisplayInfo setInitiallyVisible() {
			isInitiallyVisible = true;
			return this;
		}
		
	}
	
	final long window;

	private final Vector2i size;
	
	public Display(@NonNull DisplayInfo info) {
		ensureGLFWInit();
		
		this.size = new Vector2i(info.size);
		
		GLFW.glfwInit();
		GLFW.glfwDefaultWindowHints();
		// Use core OpenGL profile
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
		// Requires OpenGL 4.5+
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 4);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 5);
		// Enable debug if requested
		if (info.isDebug) GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_DEBUG, GLFW.GLFW_TRUE);
		// Hide window initially if requested
		if (!info.isInitiallyVisible) GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
		
		// Create the window
		window = GLFW.glfwCreateWindow(size.x, size.y, info.title, 0, 0);
	}
	
	/** Gets if the display is closing.
	 * 
	 * @return If the display is closing
	 */
	public boolean isClosing() {
		return GLFW.glfwWindowShouldClose(window);
	}
	
	/** Sets the display to be closing.
	 * 
	 */
	public void setClosing() {
		GLFW.glfwSetWindowShouldClose(window, true);
	}
	
	/** Sets if the display is visible to the user.
	 * 
	 * @param visible If the display is visible
	 */
	public void setVisible(boolean visible) {
		if (visible) GLFW.glfwShowWindow(window);
		else GLFW.glfwHideWindow(window);
	}
	
	/** Sets the title of the display's window.
	 * 
	 * @param title Display title
	 */
	public void setTitle(String title) {
		GLFW.glfwSetWindowTitle(window, title);
	}
	
	/** Polls for input for this display.
	 * 
	 */
	public void pollInput() {
		GLFW.glfwPollEvents();
		try(MemoryStack sp = MemoryStack.stackPush()) {
			IntBuffer pX = sp.mallocInt(1), pY = sp.mallocInt(1);
			GLFW.glfwGetFramebufferSize(window, pX, pY);
			size.x = pX.get(0);
			size.y = pY.get(0);
		}
	}
	
	/** Gets a vector holding the current size of the display.
	 * 
	 * @return Display size vector
	 */
	public Vector2ic getSize() {
		return size;
	}
	
	@Override
	public void close() {
		GLFW.glfwDestroyWindow(window);
		GLFW.glfwTerminate();
	}
	
}
