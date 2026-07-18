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
 * This exception is thrown when an {@code AionObject} is stored more than once.<br>
 * It indicates a serious error in the game world logic.
 * @author -Nemesiss-
 */
@SuppressWarnings("serial")
public class DuplicateAionObjectException extends RuntimeException
{
	/**
	 * Creates a new instance of {@link DuplicateAionObjectException}.<br>
	 * This exception occurs when an object is stored more than once.<br>
	 * It indicates a serious error in the system.
	 */
	public DuplicateAionObjectException()
	{
		super();
	}
	
	/**
	 * Creates a new instance of {@link DuplicateAionObjectException}.<br>
	 * This exception occurs when an object is stored more than once.
	 * @param s The detail message explaining the reason for the error.
	 */
	public DuplicateAionObjectException(String s)
	{
		super(s);
	}
	
	/**
	 * Creates a new instance of {@link DuplicateAionObjectException}.<br>
	 * This constructor allows you to provide a custom error message.<br>
	 * It also accepts an underlying cause for debugging purposes.
	 * @param message The detail message explaining the reason for the exception.
	 * @param cause The underlying throwable that triggered this exception.
	 */
	public DuplicateAionObjectException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	/**
	 * Constructs a new {@link DuplicateAionObjectException} with the specified underlying cause.<br>
	 * This is used when an error occurs during the creation of this exception.
	 * @param cause The underlying {@code Throwable} that triggered this exception.
	 */
	public DuplicateAionObjectException(Throwable cause)
	{
		super(cause);
	}
}
