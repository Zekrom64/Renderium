package com.zekrom_64.renderium.render.structs;

import java.nio.FloatBuffer;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import com.zekrom_64.renderium.util.IStruct;

/** A vertex stores position, normal, texture coordinate, and color components that are
 * passed to shaders during rendering. The position and normal are 3D vectors identifying
 * a position and normal vector in 3D-space, respectively. The texture coordinate is a 2D
 * vector identifying a location on a texture mapped to this vertex. The color is a 4-component
 * float storing RGBA color values.
 * 
 * @author Zekrom_64
 *
 */
public class Vertex implements IStruct<FloatBuffer> {
	
	//===========//
	// Constants //
	//===========//
	
	/** The size of a vertex, in floats. */
	public static final int SIZEOF_FLOATS = 12;
	
	/** The size of a vertex, in bytes. */
	public static final int SIZEOF = SIZEOF_FLOATS * Float.BYTES;
	
	
	/** The byte offset of the 'position' component. */
	public static final int OFFSETOF_POSITION = 0;
	
	/** The byte offset of the 'normal' component. */
	public static final int OFFSETOF_NORMAL = OFFSETOF_POSITION + (3 * Float.BYTES);
	
	/** The byte offset of the 'texcoord' component. */
	public static final int OFFSETOF_TEXCOORD = OFFSETOF_NORMAL + (3 * Float.BYTES);
	
	/** The byte offset of the 'color' component. */
	public static final int OFFSETOF_COLOR = OFFSETOF_TEXCOORD + (2 * Float.BYTES);
	
	
	//===========//
	// Variables //
	//===========//

	/** The 3D position component of the vertex. */
	public final Vector3f position = new Vector3f();
	
	/** The 3D normal component of the vertex. */
	public final Vector3f normal = new Vector3f();
	
	/** The 2D texture coordinate component of the vertex. */
	public final Vector2f texcoord = new Vector2f();
	
	/** The RGBA color component of the vertex. */
	public final Vector4f color = new Vector4f();
	
	//====================//
	// Read/Write Methods //
	//====================//
	
	/** Stores this vertex into a {@link FloatBuffer} at the given position.
	 * 
	 * @param index Position to store at
	 * @param fb Buffer to store into
	 */
	@Override
	public void get(int index, FloatBuffer fb) {
		position.get(index, fb);
		normal.get(index + 3, fb);
		texcoord.get(index + 6, fb);
		color.get(index + 8, fb);
	}
	
	/** Loads this vertex from a {@link FloatBuffer} at the given position.
	 * 
	 * @param index Position to load from
	 * @param fb Buffer to load from
	 */
	@Override
	public void set(int index, FloatBuffer fb) {
		position.set(index, fb);
		normal.set(index + 3, fb);
		texcoord.set(index + 6, fb);
		color.set(index + 8, fb);
	}
	
}
