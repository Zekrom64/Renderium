package com.zekrom_64.renderium.render.info;

/** A uniform binding stores a location of a uniform in a shader program.
 * 
 * @param name The name of the uniform
 * @param type The type of the uniform
 * @param binding The binding index of the uniform
 * 
 * @author Zekrom_64
 *
 */
public record UniformBinding(String name, UniformType type, int binding) {

}
