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
package com.aionemu.gameserver.spawnengine;

import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.templates.walker.WalkerTemplate;

/**
 * This class stores the necessary data required to spawn {@link Npc} entities.<br>
 * It is primarily used for organizing walker groups and managing cluster spawns.
 * @author vlog
 * @modified Rolandas
 */
public class ClusteredNpc
{
	private Npc npc;
	private final int instance;
	private final WalkerTemplate walkTemplate;
	private float x;
	private float y;
	private final int walkerIdx;
	
	/**
	 * Creates a new {@link ClusteredNpc} object.<br>
	 * This constructor initializes the NPC data and its position.<br>
	 * It sets the coordinates based on the provided {@code npc}.
	 * @param npc The {@link Npc} object to associate with this cluster.
	 * @param instance The unique ID for the current world instance.
	 * @param walkTemplate The {@link WalkerTemplate} used for movement behavior.
	 */
	public ClusteredNpc(Npc npc, int instance, WalkerTemplate walkTemplate)
	{
		this.npc = npc;
		this.instance = instance;
		this.walkTemplate = walkTemplate;
		x = npc.getSpawn().getX();
		y = npc.getSpawn().getY();
		walkerIdx = npc.getSpawn().getWalkerIndex();
	}
	
	/**
	 * Retrieves the {@link Npc} object associated with this cluster.<br>
	 * This method returns the current NPC instance.
	 * @return The {@code Npc} object.
	 */
	public Npc getNpc()
	{
		return npc;
	}
	
	/**
	 * Retrieves the unique instance identifier for this NPC cluster.<br>
	 * This value is assigned during object creation.
	 * @return The {@code int} value of the instance.
	 */
	public int getInstance()
	{
		return instance;
	}
	
	/**
	 * Spawns the NPC into the game world.<br>
	 * This method uses the {@code z} coordinate to place the entity.<br>
	 * It calls {@code int, int, float, float, float, float)} to complete the action.
	 * @param z The vertical coordinate for the spawn location.
	 */
	public void spawn(float z)
	{
		SpawnEngine.bringIntoWorld(npc, npc.getSpawn().getWorldId(), instance, x, y, z, npc.getSpawn().getHeading());
	}
	
	/**
	 * Removes the NPC from the game world.<br>
	 * This method stops all current movements and cancels pending tasks.<br>
	 * It triggers the {@code onDelete()} logic for the associated {@link Npc}.
	 */
	public void despawn()
	{
		npc.getMoveController().abortMove();
		npc.getController().cancelTask(TaskId.RESPAWN);
		npc.getController().onDelete();
	}
	
	/**
	 * Updates the {@link Npc} associated with this cluster.<br>
	 * This method synchronizes the walker group shift from the old NPC to the new one.<br>
	 * It also updates the current X and Y coordinates based on the new NPC's spawn location.
	 * @param npc The new {@code Npc} object to assign to this cluster.
	 */
	public void setNpc(Npc npc)
	{
		npc.setWalkerGroupShift(this.npc.getWalkerGroupShift());
		this.npc = npc;
		x = npc.getSpawn().getX();
		y = npc.getSpawn().getY();
	}
	
	/**
	 * Checks if this NPC is at the same coordinates as another one.<br>
	 * It compares the {@code x} and {@code y} values of two {@link ClusteredNpc} objects.
	 * @param other The other {@link ClusteredNpc} to compare against.
	 * @return {@code true} if the positions match, otherwise {@code false}.
	 */
	public boolean hasSamePosition(ClusteredNpc other)
	{
		if (this == other)
		{
			return true;
		}
		
		if (other == null)
		{
			return false;
		}
		
		return (x == other.x) && (y == other.y);
	}
	
	/**
	 * Calculates a unique hash value based on the current coordinates.<br>
	 * This is used to identify positions quickly.
	 * @return The calculated integer hash of the {@code x} and {@code y} values.
	 */
	public int getPositionHash()
	{
		final int prime = 31;
		int result = 1;
		result = (prime * result) + Float.floatToIntBits(x);
		result = (prime * result) + Float.floatToIntBits(y);
		return result;
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
	 * Calculates the distance between the current X position and the next route step.<br>
	 * This value is used to determine how far the NPC needs to move along the X axis.
	 * @return The difference between the first route step's X coordinate and the current {@code x} value.
	 */
	public float getXDelta()
	{
		return walkTemplate.getRouteStep(1).getX() - x;
	}
	
	/**
	 * Updates the {@code x} coordinate of this object.<br>
	 * This method marks the state as requiring an update if the value changes.<br>
	 * It also updates the position if it is not {@code null}.
	 * @param x The new {@code float} value for the horizontal coordinate.
	 */
	public void setX(float x)
	{
		this.x = x;
		getNpc().getSpawn().setX(x);
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
	 * Calculates the vertical distance to the next route step.<br>
	 * It subtracts the current {@code y} coordinate from the target position.
	 * @return The difference between the target Y and the current Y.
	 */
	public float getYDelta()
	{
		return walkTemplate.getRouteStep(1).getY() - y;
	}
	
	/**
	 * Updates the {@code y} coordinate of this object.<br>
	 * This method marks the state as requiring an update if the value changes.<br>
	 * It also updates the position if it is not {@code null}.
	 * @param y The new {@code float} value for the vertical coordinate.
	 */
	public void setY(float y)
	{
		this.y = y;
		getNpc().getSpawn().setY(y);
	}
	
	/**
	 * Retrieves the {@code WalkerTemplate} associated with this NPC.<br>
	 * This template defines how the walker moves and behaves.
	 * @return the {@link WalkerTemplate} for this instance.
	 */
	public WalkerTemplate getWalkTemplate()
	{
		return walkTemplate;
	}
	
	/**
	 * Retrieves the index of the walker for this spawn spot.<br>
	 * It returns {@code 0} if no index is defined.
	 * @return The current {@code walkerIndex} as an {@code int}.
	 */
	public int getWalkerIndex()
	{
		return walkerIdx;
	}
}
