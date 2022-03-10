package com.zekrom_64.renderium.util.threading;

import java.util.concurrent.locks.Lock;

/** A simple wrapper class that applies the properties of an {@link ICloseableLock} to an
 * ordinary java {@link Lock}
 * 
 * @author Zekrom_64
 *
 */
public class CloseableLockWrapper implements ICloseableLock {

	public final Lock lock;
	
	public CloseableLockWrapper(Lock lock) {
		this.lock = lock;
	}

	@Override
	public void close() {
		lock.unlock();
	}

}
