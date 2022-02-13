package com.zekrom_64.renderium;

import java.nio.IntBuffer;

import com.zekrom_64.renderium.util.IStruct;

public class DrawArray {

	/** Structure describing a non-indexed draw call.
	 * 
	 * @author Zekrom_64
	 *
	 */
	public static class Draw implements IStruct<IntBuffer> {
		
		/** The size of the structure in integers. */
		public static final int SIZEOF_INTS = 4;
		
		/** The size of the structure in bytes. */
		public static final int SIZEOF = SIZEOF_INTS * Integer.BYTES;
		
		/** The number of vertices to draw. */
		public int vertexCount;
		
		/** The number of instances to draw. */
		public int instanceCount;
		
		/** The offset of the first vertex to start drawing at. */
		public int firstVertex;
		
		/** The offset to use for the first instance. */
		public int firstInstance;

		@Override
		public void get(int position, IntBuffer ib) {
			ib.put(position, vertexCount);
			ib.put(position + 1, instanceCount);
			ib.put(position + 2, firstVertex);
			ib.put(position + 3, firstInstance);
		}

		@Override
		public void set(int position, IntBuffer ib) {
			vertexCount = ib.get(position);
			instanceCount = ib.get(position + 1);
			firstVertex = ib.get(position + 2);
			firstInstance = ib.get(position + 3);
		}
		
	}

	/** Structure describing an indexed draw call.
	 * 
	 * @author Zekrom_64
	 *
	 */
	public static class DrawIndexed implements IStruct<IntBuffer> {
		
		/** The size of the structure in integers. */
		public static final int SIZEOF_INTS = 5;
		
		/** The size of the structure in bytes. */
		public static final int SIZEOF = SIZEOF_INTS * Integer.BYTES;
		
		/** The number of indices to draw. */
		public int indexCount;
		
		/** The number of instances to draw. */
		public int instanceCount;
		
		/** The offset of the first index to start drawing at. */
		public int firstIndex;
		
		/** An offset to apply to each index read (effectively offsetting the start of where vertices are fetched). */
		public int vertexOffset;
		
		/** The offset to use for the first instance. */
		public int firstInstance;
		
		@Override
		public void get(int position, IntBuffer ib) {
			ib.put(position, indexCount);
			ib.put(position + 1, instanceCount);
			ib.put(position + 2, firstIndex);
			ib.put(position + 3, vertexOffset);
			ib.put(position + 4, firstInstance);
		}

		@Override
		public void set(int position, IntBuffer ib) {
			indexCount = ib.get(position);
			instanceCount = ib.get(position + 1);
			firstIndex = ib.get(position + 2);
			vertexOffset = ib.get(position + 3);
			firstInstance = ib.get(position + 4);
		}
		
	}
	
}
