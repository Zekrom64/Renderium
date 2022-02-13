package com.zekrom_64.renderium.util;

import java.io.Closeable;

/** Identical to {@link Closeable}, but does not throw any checked exceptions.
 * 
 * @author Zekrom_64
 *
 */
public interface ISafeCloseable extends Closeable {

	@Override
	public void close();
	
}
