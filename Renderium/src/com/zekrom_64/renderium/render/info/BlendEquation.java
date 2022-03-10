package com.zekrom_64.renderium.render.info;

/** A blend equation defines how color and alpha values are blended into the framebuffer.
 * 
 * @param srcRGB The source RGB blend factor
 * @param dstRGB The destination RGB blend factor
 * @param rgbFunc The RGB blending function
 * @param srcAlpha The source alpha blend factor
 * @param dstAlpha The destination alpha blend factor
 * @param alphaFunc The alpha blending function
 * 
 * @author Zekrom_64
 * 
 */
public record BlendEquation(
		BlendFactor srcRGB, BlendFactor dstRGB, BlendFunction rgbFunc,
		BlendFactor srcAlpha, BlendFactor dstAlpha, BlendFunction alphaFunc) {

	/** A "passthrough" blend equation that will overwrite the the destination with the source. */
	public static final BlendEquation PASSTHROUGH = new BlendEquation(
		BlendFactor.ONE, BlendFactor.ZERO, BlendFunction.ADD,
		BlendFactor.ONE, BlendFactor.ZERO, BlendFunction.ADD
	);
	
	/** A standard alpha blend equation that blends using the source alpha as a factor. */
	public static final BlendEquation ALPHA_BLEND = new BlendEquation(
		BlendFactor.SRC_ALPHA, BlendFactor.ONE_MINUS_SRC_ALPHA, BlendFunction.ADD,
		BlendFactor.SRC_ALPHA, BlendFactor.ONE_MINUS_SRC_ALPHA, BlendFunction.ADD
	);
	
}
