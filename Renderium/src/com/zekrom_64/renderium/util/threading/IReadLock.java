package com.zekrom_64.renderium.util.threading;

import org.eclipse.jdt.annotation.NonNull;

/** A read lock is a type of closeable lock that belongs to
 * a reader-writer lock object. Read locks can be upgraded
 * to write locks
 * 
 * @author Zekrom_64
 *
 */
public interface IReadLock extends ICloseableLock {

	/** Upgrades this read lock to a write lock.
	 * 
	 * @return Acquired write lock
	 */
	public @NonNull IWriteLock upgrade();
	
}
