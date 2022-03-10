package com.zekrom_64.renderium.util.collections;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

import org.eclipse.jdt.annotation.NonNull;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;
import org.joml.Vector3fc;

import com.zekrom_64.renderium.util.IStructAccessor;
import com.zekrom_64.renderium.util.MatrixUtil;

/** A matrix stack is a specialization of a stack designed to store 4x4 float transformation matrices.
 * 
 * @author Zekrom_64
 *
 */
public class MatrixStack extends StructStack<@NonNull Matrix4f> {
	
	/** Struct accessor for the {@link Matrix4f} class.
	 * 
	 * @author Zekrom_64
	 *
	 */
	public static class Matrix4fAccessor implements IStructAccessor<@NonNull Matrix4f> {

		@Override
		public @NonNull Matrix4f create() {
			return new Matrix4f();
		}

		@Override
		public int getSizeOf() {
			return 16 * Float.BYTES;
		}

		@Override
		public void read(int position, @NonNull ByteBuffer buffer, @NonNull Matrix4f struct) {
			struct.set(position, buffer);
		}

		@Override
		public void write(int position, @NonNull ByteBuffer buffer, @NonNull Matrix4f struct) {
			struct.get(position, buffer);
		}
		
	}
	
	/** The accessor used by the matrix stack.
	 * 
	 */
	public static final @NonNull Matrix4fAccessor ACCESSOR = new Matrix4fAccessor();
	
	//==============//
	// Constructors //
	//==============//
	
	/** Create a new matrix stack of the given size.
	 * 
	 * @param size The maximum number of matrices the stack will store
	 */
	public MatrixStack(int size) {
		super(size, ACCESSOR);
	}
	
	
	//==================//
	// Stack Operations //
	//==================//
	
	/** Variant of {@link #push(Matrix4f)} that will push a constant matrix.
	 * 
	 * @param m Matrix to push
	 * @return This matrix stack
	 */
	public MatrixStack push(Matrix4fc m) {
		checkNotFull();
		stackPointer--;
		m.get(stackPointer * accessor.getSizeOf(), stackBuffer);
		return this;
	}
	
	/** Variant of {@link #push(Matrix4fc)} that will push an identity matrix.
	 * 
	 * @return This matrix stack
	 */
	public MatrixStack pushIdentity() {
		return push(MatrixUtil.IDENTITY);
	}
	
	/** Variant of {@link #peek()} that gets the topmost value into a matrix.
	 * 
	 * @param m Matrix to get topmost value into
	 * @return This matrix stack
	 */
	public MatrixStack peek(Matrix4f m) {
		checkNotEmpty();
		m.set(stackPointer * accessor.getSizeOf(), stackBuffer);
		return this;
	}
	
	/** Variant of {@link #peek()} that passes the topmost value to a consumer.
	 * 
	 * @param c Top value consumer
	 * @return This matrix stack
	 */
	public MatrixStack peek(Consumer<Matrix4fc> c) {
		checkTopCache();
		c.accept(topCache);
		return this;
	}
	
	//===========================//
	// Transformation Operations //
	//===========================//
	
	/** Sets the topmost value to the identity matrix.
	 * 
	 * @return This matrix stack
	 */
	public MatrixStack setIdentity() {
		checkNotEmpty();
		topCache.identity();
		writebackCache();
		return this;
	}
	
	/** Applies a translation transformation to the topmost matrix.
	 * 
	 * @param x X translation
	 * @param y Y translation
	 * @param z Z translation
	 * @return This matrix stack
	 */
	public MatrixStack translate(float x, float y, float z) {
		checkNotEmpty();
		checkTopCache();
		topCache.translate(x, y, z);
		writebackCache();
		return this;
	}
	
	/** Variant of {@link #translate(float, float, float)} that takes a constant vector as the translation.
	 * 
	 * @param v Translation vector
	 * @return This matrix stack
	 */
	public MatrixStack translate(Vector3fc v) {
		return translate(v.x(), v.y(), v.z());
	}
	
	/** Applies a scaling transformation to the topmost matrix.
	 * 
	 * @param x X scaling
	 * @param y Y scaling
	 * @param z Z scaling
	 * @return This matrix stack
	 */
	public MatrixStack scale(float x, float y, float z) {
		checkNotEmpty();
		checkTopCache();
		topCache.scale(x, y, z);
		writebackCache();
		return this;
	}
	
	/** Variant of {@link #scale(float, float, float)} that takes a constant vector as the scaling factors.
	 * 
	 * @param v Scaling vector
	 * @return This matrix stack
	 */
	public MatrixStack scale(Vector3fc v) {
		return scale(v.x(), v.y(), v.z());
	}
	
	/** Applies a rotation transformation to the topmost matrix.
	 * 
	 * @param angle Rotation angle (in radians)
	 * @param x X axis rotation factor
	 * @param y Y axis rotation factor
	 * @param z Z axis rotation factor
	 * @return This matrix stack
	 */
	public MatrixStack rotate(float angle, float x, float y, float z) {
		checkNotEmpty();
		checkTopCache();
		topCache.rotate(angle, x, y, z);
		writebackCache();
		return this;
	}
	
	/** Variant of {@link #rotate(float, float, float, float)} that takes a constant vector as the
	 * rotation vector.
	 * 
	 * @param angle Rotation angle (in radians)
	 * @param v Rotation vector
	 * @return This matrix stack
	 */
	public MatrixStack rotate(float angle, Vector3fc v) {
		return rotate(angle, v.x(), v.y(), v.z());
	}
	
	/** Applies a rotation transformation to the topmost matrix defined by a quaternion.
	 * 
	 * @param q Quaternion rotation
	 * @return This matrix stack
	 */
	public MatrixStack rotate(Quaternionfc q) {
		checkNotEmpty();
		checkTopCache();
		topCache.rotate(q);
		writebackCache();
		return this;
	}
	
	/** Applies an orthographic projection to the topmost matrix.
	 * 
	 * @param left Left projection edge
	 * @param right Right projection edge
	 * @param top Top projection edge
	 * @param bottom Bottom projection edge
	 * @param near Near clipping plane distance
	 * @param far Far clipping plane distance
	 * @return This matrix stack
	 */
	public MatrixStack ortho(float left, float right, float top, float bottom, float near, float far) {
		checkNotEmpty();
		checkTopCache();
		topCache.ortho(left, right, bottom, top, near, far);
		writebackCache();
		return this;
	}
	
}
