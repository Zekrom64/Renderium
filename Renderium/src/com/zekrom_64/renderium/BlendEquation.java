package com.zekrom_64.renderium;

public record BlendEquation(
		BlendFactor srcRGB, BlendFactor dstRGB, BlendFunction rgbFunc,
		BlendFactor srcAlpha, BlendFactor dstAlpha, BlendFunction alphaFunc) {

	public static final BlendEquation PASSTHROUGH = new BlendEquation(
		BlendFactor.ONE, BlendFactor.ZERO, BlendFunction.ADD,
		BlendFactor.ONE, BlendFactor.ZERO, BlendFunction.ADD
	);
	
	public static final BlendEquation ALPHA_BLEND = new BlendEquation(
		BlendFactor.SRC_ALPHA, BlendFactor.ONE_MINUS_SRC_ALPHA, BlendFunction.ADD,
		BlendFactor.SRC_ALPHA, BlendFactor.ONE_MINUS_SRC_ALPHA, BlendFunction.ADD
	);
	
}
