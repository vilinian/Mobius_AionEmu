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
package com.aionemu.commons.utils;

/**
 * This class provides a centralized collection of standard exit codes used by the server.<br>
 * It helps ensure consistent error handling and process termination across the application.
 * @author SoulKeeper
 */
public final class ExitCode
{
	/**
	 * This is a private constructor.<br>
	 * It prevents other classes from creating an instance of {@link ExitCode}.<br>
	 * Use the static constants provided by this class instead.
	 */
	private ExitCode()
	{
	}
	
	/**
	 * Indicates that server successfully finished it's work
	 */
	public static final int CODE_NORMAL = 0;
	
	/**
	 * Indicates that server successfully finished it's work and should be restarted
	 */
	public static final int CODE_RESTART = 2;
	
	/**
	 * Indicates that error happened in server and it's need to shutdown.<br>
	 * Shit happens :(
	 */
	public static final int CODE_ERROR = 1;
}
