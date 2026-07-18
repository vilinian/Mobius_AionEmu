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
 * This exception is thrown when a specific DAO implementation cannot be found.<br>
 * It indicates that the system failed to locate the required data access object.
 * @author SoulKeeper
 */
public class DAONotFoundException extends DAOException
{
	/**
	 * SerialID
	 */
	private static final long serialVersionUID = 4241980426435305296L;
	
	/**
	 * Constructs a new instance of {@link DAONotFoundException}.<br>
	 * This exception is thrown when a DAO implementation cannot be found.
	 */
	public DAONotFoundException()
	{
	}
	
	/**
	 * Constructs a new {@link DAONotFoundException} with a specific error message.<br>
	 * Use this constructor to provide details about why the DAO was not found.
	 * @param message The detail message explaining the reason for the exception.
	 */
	public DAONotFoundException(String message)
	{
		super(message);
	}
	
	/**
	 * Constructs a new {@link DAONotFoundException} with a specific message and cause.<br>
	 * Use this constructor when you need to provide details about why the DAO was missing.
	 * @param message The detail message explaining the reason for the exception.
	 * @param cause The underlying reason for the exception, if any.
	 */
	public DAONotFoundException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	/**
	 * Constructs a new {@link DAONotFoundException} with a specified underlying cause.<br>
	 * This is used when an error occurs during the initialization of a DAO.
	 * @param cause The original exception that triggered this error.
	 */
	public DAONotFoundException(Throwable cause)
	{
		super(cause);
	}
}
