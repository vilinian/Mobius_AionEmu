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
package com.aionemu.gameserver.model.gameobjects.player.title;

import com.aionemu.gameserver.model.IExpirable;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.TitleTemplate;

/**
 * Represents a title that can be assigned to a {@link Player}.<br>
 * This class handles the data and expiration logic for player titles.<br>
 * It uses {@link TitleTemplate} as a base configuration.
 * @author Mr. Poke
 */
public class Title implements IExpirable
{
	private final TitleTemplate template;
	private final int id;
	private final int dispearTime;
	
	/**
	 * Creates a new instance of a {@link Title}.<br>
	 * This constructor initializes the title with its template, unique identifier, and duration.
	 * @param template The {@code TitleTemplate} used to define the title properties.
	 * @param id The unique integer {@code id} for this title.
	 * @param dispearTime The time in seconds before the title disappears.
	 */
	public Title(TitleTemplate template, int id, int dispearTime)
	{
		this.template = template;
		this.id = id;
		this.dispearTime = dispearTime;
	}
	
	/**
	 * Retrieves the {@code TitleTemplate} associated with this title.<br>
	 * This method returns the base configuration for the title.
	 * @return The {@link TitleTemplate} object.
	 */
	public TitleTemplate getTemplate()
	{
		return template;
	}
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Calculates the time left before this emotion expires.<br>
	 * It subtracts the current Unix timestamp from the {@code dispearTime}.<br>
	 * If {@code dispearTime} is {@code 0}, it returns {@code 0}.
	 * @return The remaining time in seconds as an {@code int}.
	 */
	public int getRemainingTime()
	{
		if (dispearTime == 0)
		{
			return 0;
		}
		
		return dispearTime - (int) (System.currentTimeMillis() / 1000);
	}
	
	/**
	 * Retrieves the expiration time of this object.<br>
	 * This value represents when the object will expire.
	 * @return The expiration time as an {@code int}.
	 */
	@Override
	public int getExpireTime()
	{
		return dispearTime;
	}
	
	/**
	 * Removes the title from the player's list when it expires.<br>
	 * This method updates the {@link Player} object by removing the specific title.
	 * @param player The {@link Player} who owns the expiring title.
	 */
	@Override
	public void expireEnd(Player player)
	{
		player.getTitleList().removeTitle(id);
	}
	
	/**
	 * Sends an expiration message to a specific player.<br>
	 * This method notifies the {@code Player} that an object is expiring.<br>
	 * It uses the provided {@code time} value to format the remaining duration.
	 * @param player The {@code Player} who will receive the notification.
	 * @param time The amount of time left before expiration.
	 */
	@Override
	public void expireMessage(Player player, int time)
	{
	}
	
	/**
	 * Checks if the chair object is allowed to expire at this moment.<br>
	 * This method currently always returns {@code true}.
	 * @return {@code true} if the object can expire now, otherwise {@code false}.
	 */
	@Override
	public boolean canExpireNow()
	{
		return true;
	}
}
