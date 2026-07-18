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
package com.aionemu.gameserver.utils.idfactory;

/**
 * Represents an error encountered during the operation of the {@link IDFactory}.<br>
 * This exception is thrown when the system fails to generate or manage unique identifiers.
 * @author SoulKeeper
 */
@SuppressWarnings("serial")
public class IDFactoryError extends Error
{
	/**
	 * Creates a new instance of {@link IDFactoryError}.<br>
	 * This is used to signal an error within the {@code IDFactory}.
	 */
	public IDFactoryError()
	{
	}
	
	/**
	 * Creates a new {@link IDFactoryError} with a specific message.<br>
	 * This error is used to signal problems within the {@code IDFactory}.
	 * @param message The detail message explaining the error.
	 */
	public IDFactoryError(String message)
	{
		super(message);
	}
	
	/**
	 * Creates a new {@link IDFactoryError} with a specific message and cause.<br>
	 * This is used to report serious issues within the {@code IDFactory}.
	 * @param message The detail message explaining what went wrong.
	 * @param cause The underlying exception that triggered this error.
	 */
	public IDFactoryError(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	/**
	 * Creates a new {@link IDFactoryError} instance.<br>
	 * This constructor wraps an underlying exception.
	 * @param cause The original {@code Throwable} that triggered this error.
	 */
	public IDFactoryError(Throwable cause)
	{
		super(cause);
	}
}
