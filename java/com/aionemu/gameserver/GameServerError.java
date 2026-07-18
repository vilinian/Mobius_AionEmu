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
package com.aionemu.gameserver;

/**
 * This class serves as the base superclass for all errors occurring within the {@link com.aionemu.gameserver.GameServer}.<br>
 * It provides a centralized structure for handling server-side exceptions.
 * @author Aquanox
 */
public class GameServerError extends Error
{
	private static final long serialVersionUID = -7445873741878754767L;
	
	/**
	 * Creates a new instance of {@link GameServerError}.<br>
	 * This constructor initializes the error with a {@code null} detail message.<br>
	 * The cause is not set and can be added later using {@code initCause}.
	 */
	public GameServerError()
	{
	}
	
	/**
	 * Creates a new {@link GameServerError} instance.<br>
	 * This constructor wraps an underlying exception as the root cause.
	 * @param cause The original {@code Throwable} that triggered this error.
	 */
	public GameServerError(Throwable cause)
	{
		super(cause);
	}
	
	/**
	 * Creates a new {@link GameServerError} with a specific message.<br>
	 * This constructor passes the provided string to the superclass.
	 * @param message The error message to display.
	 */
	public GameServerError(String message)
	{
		super(message);
	}
	
	/**
	 * Creates a new {@link GameServerError} with a specific message.<br>
	 * This constructor also accepts an underlying cause for the error.
	 * @param message The detail message explaining the error.
	 * @param cause The underlying exception that triggered this error.
	 */
	public GameServerError(String message, Throwable cause)
	{
		super(message, cause);
	}
}
