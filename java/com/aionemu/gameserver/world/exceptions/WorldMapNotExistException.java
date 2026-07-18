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
 * This exception is thrown when an object references a world map that does not exist.<br>
 * It indicates a serious error within the server's world management system.
 * @author -Nemesiss-
 */
@SuppressWarnings("serial")
public class WorldMapNotExistException extends RuntimeException
{
	/**
	 * Creates a new instance of {@link WorldMapNotExistException}.<br>
	 * This exception is thrown when an object references a non-existent world map.<br>
	 * It indicates a serious error in the system.
	 */
	public WorldMapNotExistException()
	{
		super();
	}
	
	/**
	 * Creates a new instance of {@link WorldMapNotExistException}.<br>
	 * This exception occurs when an object refers to a non-existent world map.
	 * @param s The detail message explaining the reason for the error.
	 */
	public WorldMapNotExistException(String s)
	{
		super(s);
	}
}
