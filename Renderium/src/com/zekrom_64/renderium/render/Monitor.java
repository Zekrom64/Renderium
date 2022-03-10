package com.zekrom_64.renderium.render;

import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWMonitorCallbackI;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;

import com.zekrom_64.renderium.render.info.DisplayMode;
import com.zekrom_64.renderium.util.Event;
import com.zekrom_64.renderium.util.TypeUtils;
import com.zekrom_64.renderium.util.geometry.IRectangle;
import com.zekrom_64.renderium.util.geometry.Rectangle;

/** <p>A monitor represents a physical display connected to the system. Systems may have multiple
 * monitors connected and stitched together to form the desktop area. Even if there are multiple
 * monitors connected, there is a "primary" monitor which is the preferred monitor for displaying
 * windows on.</p>
 * 
 * <p>Note that monitors can be connected and disconnected at any time, and disconnected monitors
 * become invalid an no longer usable. This can be checked by {@link #isValid()} and an event will
 * be fired when a monitor is disconnected.</p>
 * 
 * @author Zekrom_64
 *
 */
public class Monitor {
	
	public static interface MonitorDisconnectEvent {
		
		public void onMonitorDisconnect(Monitor m);
		
	}
	
	private static Map<Long, Monitor> monitorCache = new HashMap<>();
	
	private static final GLFWMonitorCallbackI monitorCallback = new GLFWMonitorCallbackI() {

		@Override
		public void invoke(long monitor, int event) {
			synchronized(monitorCache) {
				switch(event) {
				case GLFW.GLFW_CONNECTED:
					monitorCache.put(monitor, new Monitor(monitor));
					break;
				case GLFW.GLFW_DISCONNECTED:
					Monitor m = monitorCache.remove(monitor);
					if (m != null) m.disconnect();
					break;
				}
			}
		}
		
	};
	
	private static boolean isInit = false;
	
	private static void ensureInit() {
		if (!isInit) {
			Display.ensureGLFWInit();
			GLFW.glfwSetMonitorCallback(monitorCallback);
			isInit = true;
		}
	}

	// The GLFWmonitor pointer
	private final int hashcode;
	private long glfwmonitor;
	
	public final Event<@NonNull MonitorDisconnectEvent> onDisconnect = new Event<>(MonitorDisconnectEvent.class);
	
	private Monitor(long glfwmonitor) {
		this.hashcode = (int)glfwmonitor;
		this.glfwmonitor = glfwmonitor;
	}
	
	/** Gets if this monitor is valid.
	 * 
	 * @return If this monitor is valid
	 */
	public boolean isValid() {
		return glfwmonitor != 0;
	}
	
	private void disconnect() {
		glfwmonitor = 0;
		onDisconnect.sender.onMonitorDisconnect(this);
	}
	
	private static @NonNull Monitor getMonitor(long glfwmonitor) {
		synchronized(monitorCache) {
			Long key = glfwmonitor;
			Monitor m = monitorCache.get(key);
			if (m == null) {
				m = new Monitor(glfwmonitor);
				monitorCache.put(key, m);
			}
			return m;
		}
	}
	
	private static @NonNull DisplayMode toDisplayMode(GLFWVidMode vm) {
		return new DisplayMode(vm.width(), vm.height(), vm.redBits(), vm.greenBits(), vm.blueBits(), vm.refreshRate());
	}
	
	/** Gets the primary monitor 
	 * 
	 * @return
	 */
	public static @NonNull Monitor getPrimaryMonitor() {
		ensureInit();
		return new Monitor(GLFW.glfwGetPrimaryMonitor());
	}
	
	/** Gets all of the monitors currently connected to the system.
	 * 
	 * @return All connected monitors
	 */
	public static @NonNull Monitor @NonNull[] getMonitors() {
		ensureInit();
		PointerBuffer pMonitors = GLFW.glfwGetMonitors();
		@NonNull Monitor @NonNull[] monitors = TypeUtils.nonNull(new Monitor[pMonitors.capacity()]);
		for(int i = 0; i < monitors.length; i++) monitors[i] = getMonitor(pMonitors.get());
		return monitors;
	}
	
	/** Gets the current display mode of the monitor.
	 * 
	 * @return Current display mode
	 */
	public @NonNull DisplayMode getCurrentDisplayMode() {
		return toDisplayMode(GLFW.glfwGetVideoMode(glfwmonitor));
	}
	
	/** Gets all of the available display modes for this monitor.
	 * 
	 * @return Available display modes
	 */
	public @NonNull DisplayMode[] getAvailableDisplayModes() {
		var pModes = GLFW.glfwGetVideoModes(glfwmonitor);
		@NonNull DisplayMode @NonNull[] modes = TypeUtils.nonNull(new DisplayMode[pModes.capacity()]);
		for(int i = 0; i < modes.length; i++) modes[i] = toDisplayMode(pModes.get());
		return modes;
	}
	
	/** Gets the area of the desktop this monitor displays.
	 * 
	 * @return Desktop area on monitor
	 */
	public @NonNull IRectangle getDesktopArea() {
		try(MemoryStack sp = MemoryStack.stackPush()) {
			IntBuffer pX = sp.mallocInt(1), pY = sp.mallocInt(1), pW = sp.mallocInt(1), pH = sp.mallocInt(1);
			GLFW.glfwGetMonitorWorkarea(glfwmonitor, pX, pY, pW, pH);
			int x = pX.get(0), y = pY.get(0);
			return new Rectangle(x, y, x + pW.get(0), y + pH.get(0));
		}
	}
	
	/** Tests if this monitor is the same as another monitor.
	 * 
	 * @param m Monitor to test against
	 * @return If the monitors are the same
	 */
	public boolean equals(Monitor m) {
		if (m == this) return true;
		if (m == null) return false;
		return glfwmonitor == m.glfwmonitor;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (o == null) return false;
		if (o instanceof Monitor) return equals((Monitor)o);
		else return false;
	}
	
	@Override
	public int hashCode() {
		return hashcode;
	}
	
	@Override
	public String toString() {
		if (glfwmonitor == 0) return null;
		return GLFW.glfwGetMonitorName(glfwmonitor);
	}
	
}
