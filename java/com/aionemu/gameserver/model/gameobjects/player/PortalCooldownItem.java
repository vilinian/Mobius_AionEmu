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

import com.aionemu.gameserver.model.templates.base.BaseTemplate;

/**
 * Represents an item used to manage the cooldown period for portal usage.<br>
 * This class helps track and enforce timing restrictions on {@link com.aionemu.gameserver.model.gameobjects.player.Player} portals.
 * @author Lyras
 */
public class PortalCooldownItem
{
	private final int worldId;
	private int entryCount;
	private long cooldown;
	
	/**
	 * Creates a new instance of {@link PortalCooldownItem}.<br>
	 * This constructor initializes the portal data.
	 * @param worldId The unique identifier for the world.
	 * @param entryCount The number of allowed entries.
	 * @param cooldown The time in milliseconds until the portal resets.
	 */
	public PortalCooldownItem(int worldId, int entryCount, long cooldown)
	{
		this.worldId = worldId;
		this.entryCount = entryCount;
		this.cooldown = cooldown;
	}
	
	/**
	 * Retrieves the unique identifier for the world.<br>
	 * This value is fetched from the associated {@link BaseTemplate}.
	 * @return The {@code int} ID of the world.
	 */
	public int getWorldId()
	{
		return worldId;
	}
	
	/**
	 * Retrieves the total number of entries allowed.<br>
	 * This value is stored in the {@code entryCount} field.
	 * @return The current count of entries as an {@code int}.
	 */
	public int getEntryCount()
	{
		return entryCount;
	}
	
	/**
	 * Updates the number of entries allowed for this portal.<br>
	 * This method sets the {@code entryCount} field to a new value.
	 * @param entryCount The new number of entries to set.
	 */
	public void setEntryCount(int entryCount)
	{
		this.entryCount = entryCount;
	}
	
	/**
	 * Retrieves the current cooldown time.<br>
	 * This value is used to track how long a portal remains inactive.
	 * @return The cooldown duration as a {@code long}.
	 */
	public long getCooldown()
	{
		return cooldown;
	}
	
	/**
	 * Sets the cooldown time for this portal item.<br>
	 * This updates the {@code cooldown} field.
	 * @param cooldown The new cooldown value in milliseconds.
	 */
	public void setCooldown(long cooldown)
	{
		this.cooldown = cooldown;
	}
}
