open module com.zekrom_64.renderium {
	
	requires org.lwjgl;
	requires org.lwjgl.glfw;
	requires org.lwjgl.opengl;
	requires org.lwjgl.stb;

	requires transitive org.joml;
	requires org.json;
	
	requires transitive org.slf4j;
	
	requires transitive org.eclipse.jdt.annotation;
	
	exports com.zekrom_64.renderium.input;
	exports com.zekrom_64.renderium.localization;
	exports com.zekrom_64.renderium.render;
	exports com.zekrom_64.renderium.render.info;
	exports com.zekrom_64.renderium.render.structs;
	exports com.zekrom_64.renderium.resource;
	exports com.zekrom_64.renderium.util;
	exports com.zekrom_64.renderium.util.collections;
	exports com.zekrom_64.renderium.util.geometry;
	exports com.zekrom_64.renderium.util.threading;
	
}