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

import java.util.HashMap;
import java.util.Map;

/**
 * Manages a collection of cooldown timers for objects located within a player's house.<br>
 * This class ensures that specific actions or interactions are restricted based on elapsed time.
 * @author Rolandas
 */
public class HouseObjectCooldownList
{
	private Map<Integer, Long> houseObjectCooldowns;
	
	/**
	 * Creates a new {@code HouseObjectCooldownList} for a specific player.<br>
	 * This list tracks the cooldown times for house objects owned by the {@link Player}.
	 * @param owner The {@code Player} who owns these house objects.
	 */
	HouseObjectCooldownList(Player owner)
	{
	}
	
	/**
	 * Checks if a specific object is ready to be used.<br>
	 * It verifies if the cooldown for the given {@code objectId} has expired.
	 * @param objectId The unique identifier of the house object.
	 * @return {@code true} if the object can be used, otherwise {@code false}.
	 */
	public boolean isCanUseObject(int objectId)
	{
		if ((houseObjectCooldowns == null) || !houseObjectCooldowns.containsKey(objectId))
		{
			return true;
		}
		
		final Long coolDown = houseObjectCooldowns.get(objectId);
		if (coolDown == null)
		{
			return true;
		}
		
		if (coolDown < System.currentTimeMillis())
		{
			houseObjectCooldowns.remove(objectId);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Retrieves the remaining cooldown time for a specific house object.<br>
	 * It checks if the {@code objectId} exists in the internal map.<br>
	 * If no cooldown is found, it returns {@code 0}.
	 * @param objectId The unique identifier of the house object.
	 * @return The remaining cooldown time in milliseconds.
	 */
	public long getHouseObjectCooldown(int objectId)
	{
		if ((houseObjectCooldowns == null) || !houseObjectCooldowns.containsKey(objectId))
		{
			return 0;
		}
		
		return houseObjectCooldowns.get(objectId);
	}
	
	/**
	 * Retrieves the collection of all house object cooldowns.<br>
	 * This map links an {@code Integer} object ID to its remaining cooldown time.
	 * @return A {@link Map} containing all current cooldowns.
	 */
	public Map<Integer, Long> getHouseObjectCooldowns()
	{
		return houseObjectCooldowns;
	}
	
	/**
	 * Updates the collection of cooldown times for house objects.<br>
	 * This method sets the {@code houseObjectCooldowns} field to a new map.
	 * @param houseObjectCooldowns The new {@link Map} containing object IDs and their cooldown values.
	 */
	public void setHouseObjectCooldowns(Map<Integer, Long> houseObjectCooldowns)
	{
		this.houseObjectCooldowns = houseObjectCooldowns;
	}
	
	/**
	 * Adds a cooldown for a specific house object.<br>
	 * This method calculates the next available time based on the provided delay.<br>
	 * It updates the internal map used by {@code isCanUseObject}.
	 * @param objectId The unique identifier of the house object.
	 * @param delay The amount of time to wait in seconds before the object can be used again.
	 */
	public void addHouseObjectCooldown(int objectId, int delay)
	{
		if (houseObjectCooldowns == null)
		{
			houseObjectCooldowns = new HashMap<>();
		}
		
		final long nextUseTime = System.currentTimeMillis() + (delay * 1000);
		houseObjectCooldowns.put(objectId, nextUseTime);
	}
	
	/**
	 * Calculates the remaining time until an object can be used again.<br>
	 * It checks if the object is currently available.<br>
	 * If it is on cooldown, it returns the seconds left.<br>
	 * Otherwise, it returns {@code 0}.
	 * @param objectId The unique identifier of the house object.
	 * @return The number of seconds remaining until the object is ready for reuse.
	 */
	public int getReuseDelay(int objectId)
	{
		if (isCanUseObject(objectId))
		{
			return 0;
		}
		
		final long cd = getHouseObjectCooldown(objectId);
		final int delay = (int) ((cd - System.currentTimeMillis()) / 1000);
		return delay;
	}
}
