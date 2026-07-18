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
 * This exception is thrown when an object is spawned more than once without being despawned.<br>
 * It prevents duplicate instances of the same entity from existing in the world.
 * @author -Nemesiss-
 */
@SuppressWarnings("serial")
public class AlreadySpawnedException extends RuntimeException
{
	/**
	 * Creates a new instance of {@link AlreadySpawnedException}.<br>
	 * This exception occurs when an object is spawned more than once.<br>
	 * It provides no specific error message.
	 */
	public AlreadySpawnedException()
	{
		super();
	}
	
	/**
	 * Creates a new instance of {@link AlreadySpawnedException}.<br>
	 * This is used when an object is spawned more than once.
	 * @param s The detail message to be displayed.
	 */
	public AlreadySpawnedException(String s)
	{
		super(s);
	}
	
	/**
	 * Constructs a new {@code AlreadySpawnedException} with a specific message.<br>
	 * This constructor allows you to include the underlying cause of the error.
	 * @param message The detail message explaining why the exception occurred.
	 * @param cause The underlying reason for this exception, or {@code null}.
	 */
	public AlreadySpawnedException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	/**
	 * Creates a new instance of {@link AlreadySpawnedException}.<br>
	 * This constructor wraps an underlying exception.
	 * @param cause The original exception that triggered this error.
	 */
	public AlreadySpawnedException(Throwable cause)
	{
		super(cause);
	}
}
