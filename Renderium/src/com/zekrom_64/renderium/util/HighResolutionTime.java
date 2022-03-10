package com.zekrom_64.renderium.util;

import java.util.concurrent.locks.LockSupport;

/** High-precision time utilities.
 * 
 * @author Zekrom_64
 *
 */
public class HighResolutionTime {

	/** Gets a nanosecond-based timestamp of the best resolution possible.
	 * 
	 * @return Nanosecond timestamp
	 */
	public static long getNanoseconds() {
		return System.nanoTime();
	}
	
	/** Waits for approximately the given number of nanoseconds, with the best resolution possible.
	 * 
	 * @param nanos The number of nanoseconds to wait
	 */
	public static void waitNanoseconds(long nanos) {
		LockSupport.parkNanos(nanos);
	}
	
}
