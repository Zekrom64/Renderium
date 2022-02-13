package com.zekrom_64.renderium;

import java.nio.FloatBuffer;

import org.joml.Vector4fc;
import org.lwjgl.opengl.GL45;
import org.lwjgl.system.MemoryStack;

import com.zekrom_64.renderium.util.ISafeCloseable;

public class Sampler implements ISafeCloseable {

	final int samplerID;
	
	public static record SamplerInfo (
		Filter magFilter,
		Filter minFilter,
		Filter mipFilter,
		WrapMode wrapX,
		WrapMode wrapY,
		WrapMode wrapZ,
		Vector4fc borderColor
	) { }
	
	public Sampler(SamplerInfo info) {
		samplerID = GL45.glCreateSamplers();
		GL45.glSamplerParameteri(samplerID, GL45.GL_TEXTURE_MAG_FILTER, info.magFilter.glFilter);
		int minFilter = -1;
		switch(info.minFilter) {
		case NEAREST:
			switch(info.mipFilter) {
			case NEAREST:
				minFilter = GL45.GL_NEAREST_MIPMAP_NEAREST;
				break;
			case LINEAR:
				minFilter = GL45.GL_NEAREST_MIPMAP_LINEAR;
				break;
			}
		case LINEAR:
			switch(info.mipFilter) {
			case NEAREST:
				minFilter = GL45.GL_LINEAR_MIPMAP_NEAREST;
				break;
			case LINEAR:
				minFilter = GL45.GL_LINEAR_MIPMAP_LINEAR;
				break;
			}
		}
		GL45.glSamplerParameteri(samplerID, GL45.GL_TEXTURE_MIN_FILTER, minFilter);
		GL45.glSamplerParameteri(samplerID, GL45.GL_TEXTURE_WRAP_S, info.wrapX.glMode);
		GL45.glSamplerParameteri(samplerID, GL45.GL_TEXTURE_WRAP_T, info.wrapY.glMode);
		GL45.glSamplerParameteri(samplerID, GL45.GL_TEXTURE_WRAP_R, info.wrapZ.glMode);
		try(MemoryStack sp = MemoryStack.stackPush()) {
			FloatBuffer bc = sp.callocFloat(4);
			if (info.borderColor != null) info.borderColor.get(bc);
			GL45.glSamplerParameterfv(samplerID, GL45.GL_TEXTURE_BORDER_COLOR, bc);
		}
	}

	@Override
	public void close() {
		GL45.glDeleteSamplers(samplerID);
	}
	
}
