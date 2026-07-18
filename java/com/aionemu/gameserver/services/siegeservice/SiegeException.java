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
package com.aionemu.gameserver.services.siegeservice;

/**
 * This exception is thrown when an error occurs during siege-related operations.<br>
 * It serves as a custom runtime exception for the {@code SiegeService} logic.
 */
public class SiegeException extends RuntimeException
{
	private static final long serialVersionUID = 8834569185793190327L;
	
	/**
	 * Creates a new instance of {@link SiegeException}.<br>
	 * This is used when an error occurs during siege operations.
	 */
	public SiegeException()
	{
	}
	
	/**
	 * Creates a new {@link SiegeException} with a specific error message.<br>
	 * This exception is used to handle errors during siege operations.
	 * @param message The detail message explaining the reason for the error.
	 */
	public SiegeException(String message)
	{
		super(message);
	}
	
	/**
	 * Creates a new {@link SiegeException} with a specific error message.<br>
	 * This constructor also allows you to include the underlying cause of the error.
	 * @param message The detail message explaining why the exception occurred.
	 * @param cause The underlying exception that triggered this error, or {@code null}.
	 */
	public SiegeException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	/**
	 * Creates a new {@link SiegeException} with the specified underlying cause.<br>
	 * This is used when another error triggers this exception.
	 * @param cause The original {@code Throwable} that triggered this exception.
	 */
	public SiegeException(Throwable cause)
	{
		super(cause);
	}
}
