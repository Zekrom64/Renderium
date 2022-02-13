package com.zekrom_64.renderium;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.lwjgl.opengl.GL45;

import com.zekrom_64.renderium.util.Resources;

public class ShaderCompiler {
	
	public static final String SHADER_RESOURCE_DIR = "/assets/ne0x10c/shaders/";
	
	@SuppressWarnings("serial")
	public static class ShaderException extends RuntimeException {
		
		public String log;
		
		public ShaderException(String msg, String log) {
			super(msg);
			this.log = log;
		}
		
		public ShaderException(String msg, Exception cause) {
			super(msg, cause);
		}
		
	}
	
	private static final String VERSION_DEF = "#version 450";

	private class ShaderSource {
		
		public final ShaderType type;
		
		public final StringBuilder source = new StringBuilder();
		
		public ShaderSource(ShaderType type) {
			this.type = type;
			source.append(VERSION_DEF); // Source MUST start with a preprocessor version, we always use GLSL 4.5
		}
		
		public void append(String code) {
			source.append(code);
		}
		
		public int compileShader() {
			int shaderID = GL45.glCreateShader(type.glType);
			// Set shader source and compile
			GL45.glShaderSource(shaderID, source);
			GL45.glCompileShader(shaderID);
			// If compilation failed, delete shader and throw exception
			if (GL45.glGetShaderi(shaderID, GL45.GL_COMPILE_STATUS) != GL45.GL_TRUE) {
				String log = GL45.glGetShaderInfoLog(shaderID);
				GL45.glDeleteShader(shaderID);
				throw new ShaderException("Failed to compile " + type + " shader", log);
			}
			return shaderID;
		}
		
	}
	
	public ShaderProgram loadShader(String name) {
		int[] shaders = null;
		int programID = 0;
		try {
			Map<String, String> loadedSources = new HashMap<>();
			
			JSONObject json = new JSONObject(Resources.readResourceText(SHADER_RESOURCE_DIR + name + ".json"));
			
			// For each shader
			JSONArray jshaders = json.getJSONArray("shaders");
			shaders = new int[jshaders.length()];
			for(int i = 0; i < jshaders.length(); i++) {
				JSONObject jshader = jshaders.getJSONObject(i);
				// Parse the shader type
				ShaderType stype = ShaderType.valueOf(jshader.getString("type").toUpperCase());
				ShaderSource source = new ShaderSource(stype);
				// For each source of the shader, append the source code
				JSONArray jsources = jshader.getJSONArray("sources");
				for(int j = 0; j < jsources.length(); j++) {
					String sourcename = SHADER_RESOURCE_DIR + jsources.getString(j);
					String sourcetext = loadedSources.get(sourcename);
					if (sourcetext == null) {
						sourcetext = Resources.readResourceText(sourcename);
						loadedSources.put(sourcename, sourcetext);
					}
					source.append(sourcetext);
				}
				// Compile the complete shader
				shaders[i] = source.compileShader();
			}
			
			// Create the shader program
			programID = GL45.glCreateProgram();
			// Attach each shader and link
			for(int shader : shaders) GL45.glAttachShader(programID, shader);
			GL45.glLinkProgram(programID);
			// If not linked, throw exception
			if (GL45.glGetProgrami(programID, GL45.GL_LINK_STATUS) != GL45.GL_TRUE)
				throw new ShaderException("Failed to link shader program", GL45.glGetProgramInfoLog(programID));
			
			// Parse uniform bindings from JSON
			Map<String, UniformBinding> uniforms = new HashMap<>();
			JSONArray juniforms = json.optJSONArray("uniforms");
			if (juniforms != null) {
				for(int i = 0; i < juniforms.length(); i++) {
					JSONObject juniform = juniforms.getJSONObject(i);
					String uname = juniform.getString("name");
					UniformBinding binding = new UniformBinding(
						uname,
						UniformType.valueOf(juniform.getString("type").toUpperCase()),
						juniform.getInt("binding")
					);
					switch(binding.type()) {
					case TEXTURE:
						GL45.glProgramUniform1i(programID, GL45.glGetUniformLocation(programID, uname), binding.binding());
					default:
						break;
					}
					uniforms.put(uname, binding);
				}
			}
			
			return new ShaderProgram(programID, shaders, uniforms);
		} catch (Exception e) {
			if (shaders != null)
				for(int shader : shaders) GL45.glDeleteShader(shader);
			if (programID  != 0) GL45.glDeleteProgram(programID);
			throw new ShaderException("Caught exception while loading shader", e);
		}
	}
	
}
