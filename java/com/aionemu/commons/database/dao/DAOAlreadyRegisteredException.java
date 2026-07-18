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
 * This exception is thrown when an attempt is made to register a {@code DAO} that is already present in the {@link com.aionemu.commons.database.dao.DAOManager}.<br>
 * It indicates a conflict during the registration process of database access objects.
 * @author SoulKeeper
 */
public class DAOAlreadyRegisteredException extends DAOException
{
	/**
	 * SerialID
	 */
	private static final long serialVersionUID = -4966845154050833016L;
	
	/**
	 * Constructs a new exception.<br>
	 * This is thrown when a {@code DAO} is already registered in the {@link com.aionemu.commons.database.dao.DAOManager}.
	 */
	public DAOAlreadyRegisteredException()
	{
	}
	
	/**
	 * Constructs a new exception with a custom error message.<br>
	 * This is used when a {@link com.aionemu.commons.database.dao.DAOManager} detects a duplicate registration.
	 * @param message The detail message explaining the reason for the error.
	 */
	public DAOAlreadyRegisteredException(String message)
	{
		super(message);
	}
	
	/**
	 * Constructs a new exception with a custom message and an underlying cause.<br>
	 * Use this when a {@link com.aionemu.commons.database.dao.DAOManager} registration fails.
	 * @param message The detail message explaining why the error occurred.
	 * @param cause The underlying reason for the failure, if any.
	 */
	public DAOAlreadyRegisteredException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	/**
	 * Constructs a new exception with a specified underlying cause.<br>
	 * This is used when an error occurs during the registration process.
	 * @param cause The {@code Throwable} that triggered this exception.
	 */
	public DAOAlreadyRegisteredException(Throwable cause)
	{
		super(cause);
	}
}
