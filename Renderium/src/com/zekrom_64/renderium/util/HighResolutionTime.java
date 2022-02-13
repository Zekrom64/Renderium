package com.zekrom_64.renderium.util;

import java.util.concurrent.locks.LockSupport;

public class HighResolutionTime {

	public static long getNanoseconds() {
		return System.nanoTime();
	}
	
	public static void waitNanoseconds(long nanos) {
		LockSupport.parkNanos(nanos);
	}
	
}
