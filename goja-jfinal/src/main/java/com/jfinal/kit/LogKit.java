/**
 * Copyright (c) 2011-2016, James Zhan 詹波 (jfinal@126.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jfinal.kit;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LogKit.
 */
public class LogKit {

	private static final Logger log = LoggerFactory.getLogger("com.jfinal");


	/**
	 * Do nothing.
	 */
	public static void logNothing(Throwable t) {
		
	}
	
	public static void debug(String message) {
		log.debug(message);
	}
	
	public static void debug(String message, Throwable t) {
		log.debug(message, t);
	}
	
	public static void info(String message) {
		log.info(message);
	}
	
	public static void info(String message, Throwable t) {
		log.info(message, t);
	}
	
	public static void warn(String message) {
		log.warn(message);
	}
	
	public static void warn(String message, Throwable t) {
		log.warn(message, t);
	}
	
	public static void error(String message) {
		log.error(message);
	}
	
	public static void error(String message, Throwable t) {
		log.error(message, t);
	}

	
	public static boolean isDebugEnabled() {
		return log.isDebugEnabled();
	}
	
	public static boolean isInfoEnabled() {
		return log.isInfoEnabled();
	}
	
	public static boolean isWarnEnabled() {
		return log.isWarnEnabled();
	}
	
	public static boolean isErrorEnabled() {
		return log.isErrorEnabled();
	}
	

}

