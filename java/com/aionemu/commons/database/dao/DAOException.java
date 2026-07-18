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
package com.aionemu.commons.database.dao;

/**
 * This class serves as a generic exception for errors occurring within the ...DAO layer.<br>
 * It is used to handle database-related issues during data access operations.
 * @author SoulKeeper
 */
public class DAOException extends RuntimeException
{
	/**
	 * SerialID
	 */
	private static final long serialVersionUID = 7637014806313099318L;
	
	/**
	 * Creates a new instance of {@link DAOException}.<br>
	 * This is the default constructor for database access errors.
	 */
	public DAOException()
	{
	}
	
	/**
	 * Creates a new {@link DAOException} with a specific error message.<br>
	 * This is used to signal problems during database operations.
	 * @param message The detail message explaining the reason for the exception.
	 */
	public DAOException(String message)
	{
		super(message);
	}
	
	/**
	 * Creates a new {@link DAOException} with a specific error message.<br>
	 * This constructor also accepts the underlying cause of the error.
	 * @param message The detail message explaining why the exception occurred.
	 * @param cause The underlying exception that triggered this error, or {@code null}.
	 */
	public DAOException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	/**
	 * Creates a new {@link DAOException} instance.<br>
	 * This constructor wraps an underlying exception as the root cause.
	 * @param cause The original {@code Throwable} that triggered this exception.
	 */
	public DAOException(Throwable cause)
	{
		super(cause);
	}
}
