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

/**
 * This exception is thrown when a numerical value exceeds its allocated memory limit.<br>
 * It helps identify {@code overflow} errors during game server calculations.
 * @author MrPoke
 */
public class OverfowException extends Error
{
	/**
	 *
	 */
	private static final long serialVersionUID = 488570750616236378L;
	
	/**
	 * Creates a new instance of {@link OverfowException}.<br>
	 * This exception is thrown when an overflow occurs.
	 * @param message The detail message explaining the reason for the error.
	 */
	public OverfowException(String message)
	{
		super(message);
	}
}
