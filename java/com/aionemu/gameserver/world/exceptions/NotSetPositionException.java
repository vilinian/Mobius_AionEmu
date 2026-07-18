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
package com.aionemu.gameserver.world.exceptions;

/**
 * This exception is thrown when an object is spawned or despawned without a defined position.<br>
 * It indicates that a developer forgot to set the {@code position} before performing these actions.
 * @author -Nemesiss-
 */
@SuppressWarnings("serial")
public class NotSetPositionException extends RuntimeException
{
	/**
	 * Creates a new instance of {@link NotSetPositionException}.<br>
	 * This is used when an object is spawned or despawned without a position.<br>
	 * It helps identify errors where the coder forgot to set coordinates.
	 */
	public NotSetPositionException()
	{
		super();
	}
	
	/**
	 * Creates a new {@code NotSetPositionException} with a custom message.<br>
	 * This is used when an object is spawned or despawned without a valid position.
	 * @param s The detail message to be displayed in the exception.
	 */
	public NotSetPositionException(String s)
	{
		super(s);
	}
	
	/**
	 * Creates a new {@code NotSetPositionException} with a specific message and cause.<br>
	 * Use this when an object is spawned or despawned without a defined position.
	 * @param message The detail message explaining the reason for the exception.
	 * @param cause The underlying reason for the exception, if any.
	 */
	public NotSetPositionException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	/**
	 * Constructs a new {@link NotSetPositionException} with the specified underlying cause.<br>
	 * This is used when an error occurs while spawning or despawning an object without a position.
	 * @param cause The underlying {@code Throwable} that triggered this exception.
	 */
	public NotSetPositionException(Throwable cause)
	{
		super(cause);
	}
}
