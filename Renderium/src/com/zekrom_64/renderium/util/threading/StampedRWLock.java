package com.zekrom_64.renderium.util.threading;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.StampedLock;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

/** An implementation of the {@link IRWLock} interface using the {@link StampedLock} class.
 * 
 * @author Zekrom_64
 *
 */
public class StampedRWLock implements IRWLock {
	
	private final StampedLock slock = new StampedLock();
	
	private class StampedReadLock implements IReadLock {

		private volatile long rlock;
		
		public StampedReadLock(long rlock) {
			this.rlock = rlock;
		}
		
		@Override
		public void close() {
			if (rlock != 0) {
				slock.unlockRead(rlock);
				rlock = 0;
			}
		}

		@Override
		public @NonNull IWriteLock upgrade() {
			long wlock = slock.tryConvertToWriteLock(rlock);
			if (wlock == 0) throw new IllegalStateException("Failed to upgrade to write lock");
			else {
				rlock = 0;
				return new StampedWriteLock(wlock);
			}
		}
		
	}
	
	public @NonNull IReadLock read() {
		return new StampedReadLock(slock.readLock());
	}
	
	public @NonNull IReadLock readInterruptibly() throws InterruptedException {
		return new StampedReadLock(slock.readLockInterruptibly());
	}
	
	public @Nullable IReadLock tryRead() {
		long rlock = slock.tryReadLock();
		if (rlock == 0) return null;
		return new StampedReadLock(rlock);
	}
	
	public @Nullable IReadLock tryRead(long time, TimeUnit units) throws InterruptedException {
		long rlock = slock.tryReadLock(time, units);
		if (rlock == 0) return null;
		return new StampedReadLock(rlock);
	}
	
	private class StampedWriteLock implements IWriteLock {

		private volatile long wlock;
		
		public StampedWriteLock(long wlock) {
			this.wlock = wlock;
		}
		
		@Override
		public void close() {
			if (wlock != 0) {
				slock.unlockWrite(wlock);
				wlock = 0;
			}
		}

		@Override
		public @NonNull IReadLock downgrade() {
			long rlock = slock.tryConvertToWriteLock(wlock);
			if (rlock == 0) throw new IllegalStateException("Failed to upgrade to write lock");
			else {
				wlock = 0;
				return new StampedReadLock(rlock);
			}
		}
		
	}
	
	public @NonNull IWriteLock write() {
		return new StampedWriteLock(slock.writeLock());
	}
	
	public @NonNull IWriteLock writeInterruptibly() throws InterruptedException {
		return new StampedWriteLock(slock.writeLockInterruptibly());
	}
	
	public @Nullable IWriteLock tryWrite() {
		long wlock = slock.tryWriteLock();
		if (wlock == 0) return null;
		return new StampedWriteLock(wlock);
	}
	
	public @Nullable IWriteLock tryWrite(long time, TimeUnit units) throws InterruptedException {
		long wlock = slock.tryWriteLock(time, units);
		if (wlock == 0) return null;
		return new StampedWriteLock(wlock);
	}
	
}
