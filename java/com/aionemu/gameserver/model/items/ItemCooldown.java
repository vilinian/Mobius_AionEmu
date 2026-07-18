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
package com.aionemu.gameserver.model.items;

/**
 * Represents the cooldown state for a specific item.<br>
 * It tracks how much time remains before an item can be used again.<br>
 * This class is used by {@code Item} to manage usage restrictions.
 * @author ATracer
 */
public class ItemCooldown
{
	/**
	 * time of next reuse
	 */
	private final long time;
	/**
	 * Use delay in ms
	 */
	private final int useDelay;
	
	/**
	 * Creates a new {@link ItemCooldown} instance.<br>
	 * This constructor sets the reuse time and the delay for an item.
	 * @param time The timestamp of the next allowed reuse.
	 * @param useDelay The delay in milliseconds before the item can be used again.
	 */
	public ItemCooldown(long time, int useDelay)
	{
		this.time = time;
		this.useDelay = useDelay;
	}
	
	/**
	 * Gets the timestamp for when the item can be used again.<br>
	 * This value is stored in milliseconds.
	 * @return The next available reuse time as a {@code long}.
	 */
	public long getReuseTime()
	{
		return time;
	}
	
	/**
	 * Retrieves the usage delay for this item.<br>
	 * The value represents the time in milliseconds.
	 * @return The {@code int} value of the use delay.
	 */
	public int getUseDelay()
	{
		return useDelay;
	}
}
