package com.zekrom_64.renderium.util;

import org.slf4j.Logger;
import org.slf4j.event.Level;

/** Logging utility functions.
 * 
 * @author Zekrom_64
 *
 */
public class LogUtil {

	/** Logs a message to a logger with a specified logging level.
	 * 
	 * @param logger Logger to log to
	 * @param level Logging level
	 * @param message Message to log
	 */
	public static void log(Logger logger, Level level, String message) {
		switch(level) {
		case ERROR:
			logger.error(message);
			break;
		case WARN:
			logger.warn(message);
			break;
		case INFO:
			logger.info(message);
			break;
		case DEBUG:
			logger.debug(message);
			break;
		case TRACE:
			logger.trace(message);
			break;
		}
	}
	
}
