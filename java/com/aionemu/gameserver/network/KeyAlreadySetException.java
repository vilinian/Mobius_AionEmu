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
package com.aionemu.gameserver.network;

/**
 * This exception is thrown when the {@code setKey} method of the {@link Crypt} class is called more than once.<br>
 * It indicates that a key has already been initialized for the current session.
 * @author -Nemesiss-
 */
@SuppressWarnings("serial")
public class KeyAlreadySetException extends RuntimeException
{
	/**
	 * Constructs a new {@link KeyAlreadySetException}.<br>
	 * This exception occurs when the {@code setKey} method is called more than once.<br>
	 * It provides no specific detail message.
	 */
	public KeyAlreadySetException()
	{
		super();
	}
	
	/**
	 * Creates a new instance of {@link KeyAlreadySetException}.<br>
	 * This exception occurs when the {@code setKey} method is called more than once.
	 * @param s The detail message to be displayed.
	 */
	public KeyAlreadySetException(String s)
	{
		super(s);
	}
	
	/**
	 * Creates a new {@link KeyAlreadySetException} with a specific message.<br>
	 * This constructor also accepts an optional underlying cause.
	 * @param message The detail message explaining the reason for the exception.
	 * @param cause The underlying cause of the exception, or {@code null}.
	 */
	public KeyAlreadySetException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	/**
	 * Creates a new {@link KeyAlreadySetException} with the specified underlying cause.<br>
	 * This is used when an error occurs while trying to set a key that already exists.
	 * @param cause The underlying reason for this exception.
	 */
	public KeyAlreadySetException(Throwable cause)
	{
		super(cause);
	}
}
