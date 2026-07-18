/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 *
 *  Aion-Lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Aion-Lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details. *
 *  You should have received a copy of the GNU General Public License
 *  along with Aion-Lightning.
 *  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.utils;

import java.lang.Thread.UncaughtExceptionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides a global handler for catching exceptions thrown by threads.<br>
 * It ensures that any {@code java.lang.Thread} errors are properly logged to the system console.<br>
 * It helps prevent the server from crashing silently when an unexpected error occurs.
 * @author -Nemesiss-
 */
public class ThreadUncaughtExceptionHandler implements UncaughtExceptionHandler
{
	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(ThreadUncaughtExceptionHandler.class);
	
	/**
	 * Handles exceptions that are not caught by any catch block.<br>
	 * This method logs the error and checks for specific issues like {@code OutOfMemoryError}.
	 * @param t The {@code Thread} that experienced the exception.
	 * @param e The {@code Throwable} instance that was thrown.
	 */
	@Override
	public void uncaughtException(Thread t, Throwable e)
	{
		log.error("Critical Error - Thread: " + t.getName() + " terminated abnormaly: " + e, e);
		if (e instanceof OutOfMemoryError)
		{
			// TODO try get some memory or restart
			log.error("Out of memory! You should get more memory!");
		}
		
		// TODO! some threads should be "restarted" on error
	}
}
