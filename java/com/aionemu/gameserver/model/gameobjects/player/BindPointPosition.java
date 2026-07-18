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
package com.aionemu.gameserver.model.gameobjects.player;

import com.aionemu.gameserver.model.assemblednpc.AssembledNpc;
import com.aionemu.gameserver.model.gameobjects.PersistentState;

/**
 * Represents the spatial coordinates of a player's bind point.<br>
 * This class stores the {@code x}, {@code y}, and {@code z} positions for persistent location data.
 * @author evilset
 */
public class BindPointPosition
{
	private final int mapId;
	private final float x;
	private final float y;
	private final float z;
	private final byte heading;
	private PersistentState persistentState;
	
	/**
	 * Creates a new {@link BindPointPosition} object.<br>
	 * This constructor initializes the coordinates and map data.<br>
	 * It also sets the initial state to {@code PersistentState.NEW}.
	 * @param mapId The unique identifier for the map.
	 * @param x The X coordinate of the position.
	 * @param y The Y coordinate of the position.
	 * @param z The Z coordinate of the position.
	 * @param heading The direction the player faces at this point.
	 */
	public BindPointPosition(int mapId, float x, float y, float z, byte heading)
	{
		this.mapId = mapId;
		this.x = x;
		this.y = y;
		this.z = z;
		this.heading = heading;
		persistentState = PersistentState.NEW;
	}
	
	/**
	 * Retrieves the unique identifier for the map.<br>
	 * This value is assigned during the creation of the {@link AssembledNpc}.
	 * @return The {@code int} representing the map ID.
	 */
	public int getMapId()
	{
		return mapId;
	}
	
	/**
	 * Retrieves the X coordinate of the bookmark.<br>
	 * This value represents the horizontal position in the world.
	 * @return The {@code float} value of the X coordinate.
	 */
	public float getX()
	{
		return x;
	}
	
	/**
	 * Retrieves the vertical coordinate of the bookmark.<br>
	 * This value represents the height in the game world.
	 * @return The {@code float} value of the Y coordinate.
	 */
	public float getY()
	{
		return y;
	}
	
	/**
	 * Retrieves the vertical coordinate of the bookmark.<br>
	 * This value represents the height in the game world.
	 * @return The {@code float} value of the Z coordinate.
	 */
	public float getZ()
	{
		return z;
	}
	
	/**
	 * Retrieves the current rotation direction of the object.<br>
	 * This value is stored as a {@code byte}.
	 * @return The heading value of the object.
	 */
	public byte getHeading()
	{
		return heading;
	}
	
	/**
	 * Retrieves the current state of this challenge.<br>
	 * This information is saved between game sessions.
	 * @return the {@link PersistentState} object.
	 */
	public PersistentState getPersistentState()
	{
		return persistentState;
	}
	
	/**
	 * Updates the {@code persistentState} of this quest.<br>
	 * This method prevents changing from {@code PersistentState.NEW} to {@code PersistentState.UPDATE_REQUIRED}.
	 * @param persistentState The new {@link PersistentState} to assign.
	 */
	public void setPersistentState(PersistentState persistentState)
	{
		switch (persistentState)
		{
			case UPDATE_REQUIRED:
				if (this.persistentState == PersistentState.NEW)
				{
					break;
				}
			default:
				this.persistentState = persistentState;
		}
	}
}
