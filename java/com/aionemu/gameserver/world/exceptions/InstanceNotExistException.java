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
 * This exception is thrown when an object attempts to reference an {@code Instance} that does not currently exist.<br>
 * It helps identify cases where a reference points to a null or deleted world instance.
 * @author -Nemesiss-
 */
@SuppressWarnings("serial")
public class InstanceNotExistException extends RuntimeException
{
	/**
	 * Creates a new {@link InstanceNotExistException} with no message.<br>
	 * This exception is thrown when an object refers to a non-existent instance.
	 */
	public InstanceNotExistException()
	{
		super();
	}
	
	/**
	 * Creates a new {@link InstanceNotExistException} with a custom message.<br>
	 * This exception occurs when an object refers to an instance that does not exist.
	 * @param s The detail message to be displayed as the reason for the exception.
	 */
	public InstanceNotExistException(String s)
	{
		super(s);
	}
}
