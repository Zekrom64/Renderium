package com.zekrom_64.renderium.util.threading;

import com.zekrom_64.renderium.util.ISafeCloseable;

/** A closeable lock is a type of closeable object that manages locking semantics.
 * Acquiring the closeable lock implies the lock being acquired and closing
 * the lock (disposing of it) implies releasing the lock. This also allows locking
 * to use the try-with-resources pattern that makes locks safer to use around
 * exceptions.
 * 
 * @author Zekrom_64
 *
 */
public interface ICloseableLock extends ISafeCloseable { }