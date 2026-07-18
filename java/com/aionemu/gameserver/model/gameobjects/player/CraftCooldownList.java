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
 * This class manages the cooldown periods for various crafting actions.<br>
 * It tracks when a player can perform specific crafts again to prevent spamming.<br>
 * It provides a structured way to store and check {@code CraftCooldown} data.
 * @author synchro2
 */
public class CraftCooldownList
{
	private Map<Integer, Long> craftCooldowns;
	
	/**
	 * Creates a new {@code CraftCooldownList} for a specific player.<br>
	 * This method initializes the cooldown data for the provided {@link Player}.
	 * @param owner The {@code Player} who will own this cooldown list.
	 */
	CraftCooldownList(Player owner)
	{
	}
	
	/**
	 * Checks if a specific craft is ready to be performed.<br>
	 * It verifies the cooldown status for the given {@code delayId}.<br>
	 * Returns {@code true} if no cooldown exists or has expired.
	 * @param delayId The unique identifier for the craft cooldown.
	 * @return {@code true} if the player can craft, otherwise {@code false}.
	 */
	public boolean isCanCraft(int delayId)
	{
		if ((craftCooldowns == null) || !craftCooldowns.containsKey(delayId))
		{
			return true;
		}
		
		final Long coolDown = craftCooldowns.get(delayId);
		if (coolDown == null)
		{
			return true;
		}
		
		if (coolDown < System.currentTimeMillis())
		{
			craftCooldowns.remove(delayId);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Retrieves the remaining cooldown time for a specific craft.<br>
	 * It checks if the {@code delayId} exists in the internal map.<br>
	 * If no cooldown is found, it returns {@code 0}.
	 * @param delayId The unique identifier for the crafting action.
	 * @return The remaining cooldown time in milliseconds.
	 */
	public long getCraftCooldown(int delayId)
	{
		if ((craftCooldowns == null) || !craftCooldowns.containsKey(delayId))
		{
			return 0;
		}
		
		return craftCooldowns.get(delayId);
	}
	
	/**
	 * Retrieves the current list of crafting cooldowns.<br>
	 * This method returns a {@code Map} containing all active delays.
	 * @return A {@code Map} where keys are delay IDs and values are timestamps.
	 */
	public Map<Integer, Long> getCraftCoolDowns()
	{
		return craftCooldowns;
	}
	
	/**
	 * Updates the internal collection of crafting cooldowns.<br>
	 * This method replaces the current {@code craftCooldowns} map with a new one.
	 * @param craftCoolDowns The new {@link Map} containing item IDs and their remaining cooldown times.
	 */
	public void setCraftCoolDowns(Map<Integer, Long> craftCoolDowns)
	{
		craftCooldowns = craftCoolDowns;
	}
	
	/**
	 * Adds a new cooldown for a specific crafting action.<br>
	 * This method calculates the next available time based on the provided delay.<br>
	 * It updates the {@code craftCooldowns} map with the new timestamp.
	 * @param delayId The unique identifier for the crafting action.
	 * @param delay The amount of time to wait in seconds before the action can be used again.
	 */
	public void addCraftCooldown(int delayId, int delay)
	{
		if (craftCooldowns == null)
		{
			craftCooldowns = new HashMap<>();
		}
		
		final long nextUseTime = System.currentTimeMillis() + (delay * 1000);
		craftCooldowns.put(delayId, nextUseTime);
	}
}
