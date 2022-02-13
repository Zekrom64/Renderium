package com.zekrom_64.renderium;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.joml.Vector3fc;
import org.lwjgl.opengl.GL45;

import com.zekrom_64.renderium.util.ISafeCloseable;

public class VertexArray implements ISafeCloseable {

	final int vertexArrayID;
	private final int vertexBufferID;
	private final int indexBufferID;
	
	private int vertexSize, indexSize;
	
	private VertexWriter writer = null;
	
	public class VertexWriter implements ISafeCloseable {
		
		// The mapped vertex buffer
		private final FloatBuffer vertexBuffer;
		// The mapped index buffer
		private final IntBuffer indexBuffer;
		
		// The offset into the vertex buffer
		private int vertexOffset = 0;
		// The offset into the index buffer
		private int indexOffset = 0;
		
		/** The "current" index value. */
		public int currentIndex = 0;
		
		/** The "current" vertex value. */
		public final Vertex currentVertex = new Vertex();
		
		private VertexWriter() {
			// Map vertex buffer
			vertexBuffer = GL45.glMapNamedBufferRange(
				vertexBufferID,
				0,
				vertexSize * Vertex.SIZEOF,
				GL45.GL_MAP_WRITE_BIT | GL45.GL_MAP_INVALIDATE_BUFFER_BIT
			).asFloatBuffer();
			// Map index buffer if non-null
			if (indexBufferID != 0)
				indexBuffer = GL45.glMapNamedBufferRange(
					indexBufferID,
					0,
					indexSize * Integer.BYTES,
					GL45.GL_MAP_WRITE_BIT | GL45.GL_MAP_INVALIDATE_BUFFER_BIT
				).asIntBuffer();
			else indexBuffer = null;
		}
		
		/** Sets the position of the current vertex.
		 * 
		 * @param x X coordinate
		 * @param y Y coordinate
		 * @param z Z coordinate
		 * @return This vertex writer
		 */
		public VertexWriter position(float x, float y, float z) {
			currentVertex.position.set(x, y, z);
			return this;
		}
		
		/** Sets the position of the current vertex.
		 * 
		 * @param v Position vector
		 * @return This vertex writer
		 */
		public VertexWriter position(Vector3fc v) {
			return position(v.x(), v.y(), v.z());
		}
		
		/** Sets the color of the current vertex.
		 * 
		 * @param r Red component
		 * @param g Green component
		 * @param b Blue component
		 * @return This vertex writer
		 */
		public VertexWriter color(float r, float g, float b) {
			currentVertex.color.set(r, g, b, 1.0f);
			return this;
		}
		
		/** Sets the texture coordinate of the current vertex.
		 * 
		 * @param x X coordinate
		 * @param y Y coordinate
		 * @return This vertex writer
		 */
		public VertexWriter texcoord(float x, float y) {
			currentVertex.texcoord.set(x, y);
			return this;
		}
		
		public VertexWriter quad2d(float x, float y, float w, float h, float tx, float ty, float tw, float th) {
			float x2 = x + w, y2 = y + h;
			float tx2 = tx + tw, ty2 = ty + th;
			if(isIndexed()) {
				// Only need 4 vertices
				currentVertex.position.x = x;
				currentVertex.position.y = y2;
				currentVertex.texcoord.set(tx, ty2);
				emitVertex();
				currentVertex.position.y = y;
				currentVertex.texcoord.y = ty;
				emitVertex();
				currentVertex.position.x = x2;
				currentVertex.texcoord.x = tx2;
				emitVertex();
				currentVertex.position.y = y2;
				currentVertex.texcoord.y = ty2;
				emitVertex();
				
				// Emit 6 indices for the quad
				emitIndex(currentIndex);
				emitIndex(currentIndex + 1);
				emitIndex(currentIndex + 2);
				emitIndex(currentIndex);
				emitIndex(currentIndex + 2);
				emitIndex(currentIndex + 3);
				currentIndex += 4;
			} else {
				
			}
			return this;
		}
		
		/** Emits the current vertex ({@link currentVertex}) to the vertex array.
		 * 
		 * @return This vertex writer
		 */
		public VertexWriter emitVertex() {
			currentVertex.get(vertexOffset, vertexBuffer);
			vertexOffset += Vertex.SIZEOF_FLOATS;
			return this;
		}
		
		/** Emits the current index ({@link currentIndex}) to the vertex array.
		 * 
		 * @return This vertex writer
		 */
		public VertexWriter emitIndex() {
			return emitIndex(currentIndex++);
		}
		
		/** Emits the given index to the vertex array.
		 * 
		 * @param index Index to emit
		 * @return This vertex writer
		 */
		public VertexWriter emitIndex(int index) {
			if (indexBuffer != null)
				indexBuffer.put(indexOffset++, index);
			return this;
		}
		
		/** Emits both the current vertex and current index ({@link currentVertex} and {@link currentIndex})
		 * to the vertex array.
		 * 
		 * @return This vertex writer
		 */
		public VertexWriter emitVertexAndIndex() {
			return emitVertex().emitIndex();
		}
		
		/** Gets the current byte offset into the vertex buffer.
		 * 
		 * @return Current vertex offset
		 */
		public long getVertexOffset() {
			return vertexBuffer.position() * Float.BYTES;
		}
		
		public void close() {
			// Unmap buffers
			GL45.glUnmapNamedBuffer(vertexBufferID);
			if (indexBuffer != null) GL45.glUnmapNamedBuffer(indexBufferID);
			// Clear writer
			writer = null;
		}
		
	}
	
	public static VertexArray ofIndexedQuads(int nquads) {
		return new VertexArray(nquads * 4, nquads * 6);
	}
	
	/** Creates a new vertex array of a fixed size. Vertex arrays may be indexed or non-indexed.
	 * 
	 * @param vertices The number of vertices to allocate
	 * @param indices The number of indices to allocate
	 */
	public VertexArray(int vertices, int indices) {
		this.vertexSize = vertices;
		vertexArrayID = GL45.glCreateVertexArrays();
		
		// Initialize vertex buffer
		vertexBufferID = GL45.glCreateBuffers();
		GL45.glNamedBufferStorage(vertexBufferID, vertices * Vertex.SIZEOF, GL45.GL_MAP_WRITE_BIT);
		GL45.glVertexArrayVertexBuffer(vertexArrayID, 0, vertexBufferID, 0, Vertex.SIZEOF);
		
		// Initialize vertex array format
		GL45.glEnableVertexArrayAttrib(vertexArrayID, 0);
		GL45.glVertexArrayAttribBinding(vertexArrayID, 0, 0);
		GL45.glVertexArrayAttribFormat(vertexArrayID, 0, 3, GL45.GL_FLOAT, false, Vertex.OFFSETOF_POSITION);
		GL45.glEnableVertexArrayAttrib(vertexArrayID, 1);
		GL45.glVertexArrayAttribBinding(vertexArrayID, 1, 0);
		GL45.glVertexArrayAttribFormat(vertexArrayID, 1, 3, GL45.GL_FLOAT, false, Vertex.OFFSETOF_NORMAL);
		GL45.glEnableVertexArrayAttrib(vertexArrayID, 2);
		GL45.glVertexArrayAttribBinding(vertexArrayID, 2, 0);
		GL45.glVertexArrayAttribFormat(vertexArrayID, 2, 2, GL45.GL_FLOAT, false, Vertex.OFFSETOF_TEXCOORD);
		GL45.glEnableVertexArrayAttrib(vertexArrayID, 3);
		GL45.glVertexArrayAttribBinding(vertexArrayID, 3, 0);
		GL45.glVertexArrayAttribFormat(vertexArrayID, 3, 4, GL45.GL_FLOAT, false, Vertex.OFFSETOF_COLOR);
		
		// Initialize index buffer
		if (indices > 0) {
			this.indexSize = indices;
			indexBufferID = GL45.glCreateBuffers();
			GL45.glNamedBufferStorage(indexBufferID, indices * Integer.BYTES, GL45.GL_MAP_WRITE_BIT);
			GL45.glVertexArrayElementBuffer(vertexArrayID, indexBufferID);
		} else {
			this.indexSize = 0;
			indexBufferID = 0;
		}
	}
	
	/** Gets the size of this vertex array in vertices.
	 * 
	 * @return Vertex array size
	 */
	public int getVertexSize() {
		return vertexSize;
	}
	
	/** Gets the size of this vertex array in indices.
	 * 
	 * @return Index array size
	 */
	public int getIndexSize() {
		return indexSize;
	}
	
	/** Gets if this vertex array uses indexed vertices.
	 * 
	 * @return If vertex indexing is used
	 */
	public boolean isIndexed() {
		return indexBufferID != 0;
	}
	
	/** Starts writing data to this vertex array. Writing must end before the vertices are drawn.
	 * 
	 * @return Vertex writer for this array
	 */
	public VertexWriter beginWriting() {
		if (writer != null) throw new IllegalStateException("Already writing vertices");
		writer = new VertexWriter();
		return writer;
	}
	
	/** Copies vertices to this vertex array. The range of vertices copied is defined by two offsets;
	 * the minimum one defines the start and the maximum is used to compute the length of the copy.
	 * Both vertex arrays must be non-indexed for vertex-only copies to be done.
	 * 
	 * @param dstOffset Destination offset
	 * @param array Source vertex array
	 * @param srcOffset1 First source offset
	 * @param srcOffset2 Second source offset
	 */
	public void copyVertices(long dstOffset, VertexArray array, long srcOffset1, long srcOffset2) {
		if (isIndexed()) throw new IllegalStateException("Cannot copy only vertices on indexed vertex array");
		if (array.isIndexed()) throw new IllegalArgumentException("Cannot copy only vertices from indexed vertex array");
		long length = srcOffset2 - srcOffset1;
		if (length < 0) length = -length;
		GL45.glCopyNamedBufferSubData(array.vertexBufferID, vertexBufferID, Math.min(srcOffset1, srcOffset2), dstOffset, length);
	}

	@Override
	public void close() {
		GL45.glDeleteVertexArrays(vertexArrayID);
		GL45.glDeleteBuffers(vertexBufferID);
		if (indexBufferID != 0) GL45.glDeleteBuffers(indexBufferID);
	}
	
}
