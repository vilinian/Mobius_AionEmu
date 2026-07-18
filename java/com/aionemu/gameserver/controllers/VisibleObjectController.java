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
package com.aionemu.gameserver.controllers;

import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.world.World;

/**
 * This class manages the behavior of {@link VisibleObject} entities such as players and NPCs.<br>
 * It handles core logic including movement, visibility, and other state updates.
 * @author -Nemesiss-
 * @param <T>
 */
public abstract class VisibleObjectController<T extends VisibleObject>
{
	/**
	 * Object that is controlled by this controller.
	 */
	private T owner;
	
	/**
	 * Sets the {@code owner} for this controller.<br>
	 * The {@code owner} is the {@link VisibleObject} being managed by this class.<br>
	 * This method updates the internal reference to the object.
	 * @param owner The {@code VisibleObject} to be assigned as the owner.
	 */
	public void setOwner(T owner)
	{
		this.owner = owner;
	}
	
	/**
	 * Retrieves the object that this controller manages.<br>
	 * This method returns the {@code owner} associated with this instance.
	 * @return The {@code VisibleObject} being controlled by this controller.
	 */
	public T getOwner()
	{
		return owner;
	}
	
	/**
	 * This method registers a {@link Player} as an observer.<br>
	 * It creates a new {@code FlyRingObserver} for the target.<br>
	 * The observer is added to the player's observation controller.
	 * @param object The {@code VisibleObject} representing the player to observe.
	 */
	public void see(VisibleObject object)
	{
	}
	
	/**
	 * Updates the visibility status of a specific object.<br>
	 * This method also clears the target if the object is the current target.
	 * @param object The {@code VisibleObject} that is no longer seen.
	 * @param isOutOfRange Whether the object is outside of the visible range.
	 */
	public void notSee(VisibleObject object, boolean isOutOfRange)
	{
	}
	
	/**
	 * Removes the owner object from the game world.<br>
	 * This method first despawns the object if it is currently spawned.<br>
	 * It then calls {@code removeObject} to delete it.
	 */
	public void delete()
	{
		/**
		 * despawn object from world.
		 */
		if (getOwner().isSpawned())
		{
			World.getInstance().despawn(getOwner());
		}
		/**
		 * Delete object from World.
		 */
		World.getInstance().removeObject(getOwner());
	}
	
	/**
	 * This method is called before an object is spawned.<br>
	 * Use this to perform any necessary setup tasks.
	 */
	public void onBeforeSpawn()
	{
	}
	
	/**
	 * This method is called after the object has been spawned.<br>
	 * It triggers a revalidation of zones for the owner.
	 */
	public void onAfterSpawn()
	{
	}
	
	/**
	 * Handles the logic when a creature is despawned.<br>
	 * This method cancels the {@code DECAY} task.<br>
	 * It also clears the aggro list and observation controller of the owner if they are still spawned.
	 */
	public void onDespawn()
	{
	}
	
	/**
	 * This method is called when the object respawns.<br>
	 * It handles any logic needed to reset the state of the {@code getOwner} object.
	 */
	public void onRespawn()
	{
	}
	
	/**
	 * Handles the logic for removing this controller from the game world.<br>
	 * It checks if the {@code getOwner} is currently in the world.<br>
	 * If it is, it triggers {@code onDespawn} and calls {@code delete}.
	 */
	public void onDelete()
	{
		if (getOwner().isInWorld())
		{
			this.onDespawn();
			this.delete();
		}
	}
}
