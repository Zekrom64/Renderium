package com.zekrom_64.renderium.render;

import java.util.Collection;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.lwjgl.opengl.GL45;

import com.zekrom_64.renderium.render.info.UniformBinding;
import com.zekrom_64.renderium.util.ISafeCloseable;
import com.zekrom_64.renderium.util.TypeUtils;

public class ShaderProgram implements ISafeCloseable {

	final int programID;
	private final int[] shaderIDs;
	private final Map<String, UniformBinding> uniforms;
	
	ShaderProgram(int programID, int[] shaderIDs, Map<String, UniformBinding> uniforms) {
		this.programID = programID;
		this.shaderIDs = shaderIDs;
		this.uniforms = uniforms;
	}

	@Override
	public void close() {
		GL45.glDeleteProgram(programID);
		for(int shader : shaderIDs) GL45.glDeleteShader(shader);
	}
	
	public @NonNull Collection<UniformBinding> getAllUniforms() {
		return TypeUtils.nonNull(uniforms.values());
	}
	
	public @Nullable UniformBinding getUniform(String name) {
		return uniforms.get(name);
	}
	
}
