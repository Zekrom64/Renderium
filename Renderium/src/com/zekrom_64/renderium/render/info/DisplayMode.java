package com.zekrom_64.renderium.render.info;

/** A display mode defines how video will be output to a display.
 * 
 * @author Zekrom_64
 *
 */
public record DisplayMode(int width, int height, int redBits, int greenBits, int blueBits, int refreshRate) {
	
	public String getDescription() {
		return width + "x" + height + "@" + refreshRate + "Hz, " + (redBits + greenBits + blueBits) + " bpp";
	}
	
}
