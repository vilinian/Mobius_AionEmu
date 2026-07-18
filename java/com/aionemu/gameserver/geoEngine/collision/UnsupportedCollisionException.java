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
package com.aionemu.gameserver.geoEngine.collision;

/**
 * This exception is thrown when the {@code geoEngine} encounters a collision type that is not supported.<br>
 * It indicates that the current system cannot process the specific collision logic requested.
 * @author Kirill
 */
@SuppressWarnings("serial")
public class UnsupportedCollisionException extends UnsupportedOperationException
{
	/**
	 * Creates a new {@link UnsupportedCollisionException} instance.<br>
	 * This constructor wraps an existing exception.
	 * @param arg0 The underlying cause of the error.
	 */
	public UnsupportedCollisionException(Throwable arg0)
	{
		super(arg0);
	}
	
	/**
	 * Constructs a new {@link UnsupportedCollisionException} with a custom message.<br>
	 * This exception is thrown when an unsupported collision occurs.
	 * @param arg0 The detail message explaining the reason for the error.
	 * @param arg1 The underlying cause of the exception, or {@code null}.
	 */
	public UnsupportedCollisionException(String arg0, Throwable arg1)
	{
		super(arg0, arg1);
	}
	
	/**
	 * Constructs a new exception for an unsupported collision.<br>
	 * This error occurs when the engine cannot process a specific collision type.
	 * @param arg0 The detail message explaining why the collision is unsupported.
	 */
	public UnsupportedCollisionException(String arg0)
	{
		super(arg0);
	}
	
	/**
	 * Constructs a new {@link UnsupportedCollisionException}.<br>
	 * This exception is thrown when the engine encounters an unsupported collision.
	 */
	public UnsupportedCollisionException()
	{
		super();
	}
}
