package com.zekrom_64.renderium.util.threading;

import org.eclipse.jdt.annotation.NonNull;

/**A write lock is a type of closeable lock that belongs to
 * a reader-writer lock object. Write locks can be downgraded
 * to read locks.
 * 
 * @author Zekrom_64
 *
 */
public interface IWriteLock extends ICloseableLock {

	/** Downgrades this write lock to a read lock.
	 * 
	 * @return Acquired read lock
	 */
	public @NonNull IReadLock downgrade();
	
}
