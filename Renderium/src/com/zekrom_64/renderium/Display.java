package com.zekrom_64.renderium;

import java.nio.IntBuffer;

import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;

import com.zekrom_64.renderium.util.ISafeCloseable;

public class Display implements ISafeCloseable {
	
	private final long window;

	private final Vector2i size = new Vector2i(640, 480);
	private boolean vsync = false;
	
	public Display() {
		GLFW.glfwInit();
		GLFW.glfwDefaultWindowHints();
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 4);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 5);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_DEBUG, GLFW.GLFW_TRUE);
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
		window = GLFW.glfwCreateWindow(size.x, size.y, "Ne0x10c", 0, 0);
		GLFW.glfwMakeContextCurrent(window);
	}
	
	public boolean isClosing() {
		return GLFW.glfwWindowShouldClose(window);
	}
	
	public void setClosing() {
		GLFW.glfwSetWindowShouldClose(window, true);
	}
	
	public boolean isVSyncEnabled() {
		return vsync;
	}
	
	public void setVisible(boolean visible) {
		if (visible) GLFW.glfwShowWindow(window);
		else GLFW.glfwHideWindow(window);
	}
	
	public void setVSyncEnabled(boolean enable) {
		GLFW.glfwSwapInterval(enable ? 1 : 0);
		vsync = enable;
	}
	
	public void setTitle(String title) {
		GLFW.glfwSetWindowTitle(window, title);
	}
	
	public void pollInput() {
		GLFW.glfwPollEvents();
		try(MemoryStack sp = MemoryStack.stackPush()) {
			IntBuffer pX = sp.mallocInt(1), pY = sp.mallocInt(1);
			GLFW.glfwGetFramebufferSize(window, pX, pY);
			size.x = pX.get(0);
			size.y = pY.get(0);
		}
	}
	
	public Vector2ic getSize() {
		return size;
	}
	
	public void swapBuffers() {
		GLFW.glfwSwapBuffers(window);
	}
	
	@Override
	public void close() {
		GLFW.glfwDestroyWindow(window);
		GLFW.glfwTerminate();
	}
	
}
