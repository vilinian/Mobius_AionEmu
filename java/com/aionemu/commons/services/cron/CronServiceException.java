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
package com.aionemu.commons.services.cron;

/**
 * This exception is thrown when an error occurs during the execution of a {@code CronService} task.<br>
 * It serves as a custom runtime exception for handling issues related to scheduled cron jobs.
 */
public class CronServiceException extends RuntimeException
{
	private static final long serialVersionUID = -354186843536711803L;
	
	/**
	 * Constructs a new {@link CronServiceException}.<br>
	 * This exception is thrown when an error occurs within the cron service.
	 */
	public CronServiceException()
	{
	}
	
	/**
	 * Creates a new {@link CronServiceException} with a specific error message.<br>
	 * This exception is used to signal errors within the cron service.
	 * @param message The detail message explaining why the exception was thrown.
	 */
	public CronServiceException(String message)
	{
		super(message);
	}
	
	/**
	 * Creates a new {@link CronServiceException} with a specific message.<br>
	 * This constructor also accepts the underlying cause of the error.
	 * @param message The detail message explaining why the exception occurred.
	 * @param cause The underlying exception that triggered this error, or {@code null}.
	 */
	public CronServiceException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	/**
	 * Creates a new {@link CronServiceException} with the specified underlying cause.<br>
	 * This is used when an error occurs during cron service operations.
	 * @param cause The original exception that triggered this error.
	 */
	public CronServiceException(Throwable cause)
	{
		super(cause);
	}
}
