package com.zekrom_64.renderium.util.threading;

import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

/** A reader-writer lock implements a locking pattern where certain locks
 * are "reader" locks (which generally have concurrent access to a resource)
 * and others are "writer" locks (which have exclusive access to a resource).
 * 
 * @author Zekrom_64
 *
 */
public interface IRWLock {
	
	/** Acquires a new reader lock.
	 * 
	 * @return Reader lock
	 */
	public @NonNull IReadLock read();
	
	/** Interruptibly acquires a reader lock.
	 * 
	 * @return Reader lock
	 * @throws InterruptedException If the thread was interrupted while waiting for a lock
	 */
	public @NonNull IReadLock readInterruptibly() throws InterruptedException;
	
	/** Attempts to acquire a reader lock immediately, returning <b>null</b> if
	 * one could not be immediately acquired.
	 * 
	 * @return Reader lock, or <b>null</b>
	 */
	public @Nullable IReadLock tryRead();
	
	/** Attempts to acquire a reader lock within the given time span, returning <b>null</b>
	 * if one could not be required.
	 * 
	 * @param time Amount of time to wait
	 * @param units Units of wait time
	 * @return Reader lock, or <b>null</b>
	 * @throws InterruptedException If the thread was interrupted while waiting for a lock
	 */
	public @Nullable IReadLock tryRead(long time, TimeUnit units) throws InterruptedException;
	
	/** Acquires a new writer lock.
	 * 
	 * @return Writer lock
	 */
	public @NonNull IWriteLock write();
	
	/** Interruptibly acquires a writer lock
	 * 
	 * @return Writer lock
	 * @throws InterruptedException If the thread was interrupted while waiting for a lock
	 */
	public @NonNull IWriteLock writeInterruptibly() throws InterruptedException;

	/** Attempts to acquire a writer lock immediately, returning <b>null</b> if
	 * one could not be immediately acquired.
	 * 
	 * @return Writer lock, or <b>null</b>
	 */
	public @Nullable IWriteLock tryWrite();

	/** Attempts to acquire a writer lock within the given time span, returning <b>null</b>
	 * if one could not be required.
	 * 
	 * @param time Amount of time to wait
	 * @param units Units of wait time
	 * @return Writer lock, or <b>null</b>
	 * @throws InterruptedException If the thread was interrupted while waiting for a lock
	 */
	public @Nullable IWriteLock tryWrite(long time, TimeUnit units) throws InterruptedException;

}
