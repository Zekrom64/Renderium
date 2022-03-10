package com.zekrom_64.renderium.render;

import java.nio.FloatBuffer;

import org.eclipse.jdt.annotation.Nullable;
import org.joml.Vector4fc;
import org.lwjgl.opengl.GL45;
import org.lwjgl.system.MemoryStack;

import com.zekrom_64.renderium.render.info.Filter;
import com.zekrom_64.renderium.render.info.WrapMode;
import com.zekrom_64.renderium.util.ISafeCloseable;
import com.zekrom_64.renderium.util.TypeUtils;

/** A sampler controls how texels are read from a texture.
 * 
 * @author Zekrom_64
 *
 */
public class Sampler implements ISafeCloseable {

	final int samplerID;
	
	/** Record type holding information about a sampler.
	 * 
	 * @param magFilter The magnification filter to use
	 * @param minFilter The minification filter to use
	 * @param mipFilter The filter to apply between mipmap levels
	 * @param wrapX The texture wrapping to apply to the X axis
	 * @param wrapY The texture wrapping to apply to the Y axis
	 * @param wrapZ The texture wrapping to apply to the Z axis
	 * @param borderColor An optional border color to use
	 * 
	 * @author Zekrom_64
	 *
	 */
	public static record SamplerInfo (
		Filter magFilter,
		Filter minFilter,
		Filter mipFilter,
		WrapMode wrapX,
		WrapMode wrapY,
		WrapMode wrapZ,
		@Nullable Vector4fc borderColor
	) { }
	
	/** Creates a new sampler using the given information.
	 * 
	 * @param info Sampler information
	 */
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
			if (info.borderColor != null) TypeUtils.nonNull(info.borderColor).get(bc);
			GL45.glSamplerParameterfv(samplerID, GL45.GL_TEXTURE_BORDER_COLOR, bc);
		}
	}

	@Override
	public void close() {
		GL45.glDeleteSamplers(samplerID);
	}
	
}
