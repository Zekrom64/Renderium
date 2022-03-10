package com.zekrom_64.renderium.render.structs;

import java.nio.ByteBuffer;

import org.joml.Matrix4f;

import com.zekrom_64.renderium.util.IStruct;

public class UGlobals implements IStruct<ByteBuffer> {

	public static final int SIZEOF = 16 * Float.BYTES;
	
	public final Matrix4f mTransform = new Matrix4f();
	
	@Override
	public void get(int position, ByteBuffer buffer) {
		mTransform.get(buffer);
	}

	@Override
	public void set(int position, ByteBuffer buffer) {
		mTransform.set(buffer);
	}

}
