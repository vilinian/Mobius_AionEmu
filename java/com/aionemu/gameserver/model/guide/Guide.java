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
package com.aionemu.gameserver.model.guide;

import com.aionemu.gameserver.model.Petition;

/**
 * Represents a guide entity within the game world.<br>
 * This class stores information used to display instructions or tutorials to players.
 * @author xTz
 */
public class Guide
{
	private final int guide_id;
	private final int player_id;
	private final String title;
	
	/**
	 * Creates a new instance of the {@link Guide} class.<br>
	 * This constructor initializes the guide with specific details.
	 * @param guide_id The unique identifier for the guide.
	 * @param player_id The unique identifier for the player.
	 * @param title The name or heading of the guide.
	 */
	public Guide(int guide_id, int player_id, String title)
	{
		this.guide_id = guide_id;
		this.player_id = player_id;
		this.title = title;
	}
	
	/**
	 * Retrieves the unique identifier for this {@link Guide}.<br>
	 * This ID is used to distinguish one guide from another.
	 * @return The {@code int} value of the guide ID.
	 */
	public int getGuideId()
	{
		return guide_id;
	}
	
	/**
	 * Retrieves the unique identifier for the player.<br>
	 * This value is stored in the {@code playerId} field.
	 * @return The {@code int} ID of the player.
	 */
	public int getPlayerId()
	{
		return player_id;
	}
	
	/**
	 * Retrieves the title of the {@link Petition}.<br>
	 * This returns the name given to the petition.
	 * @return The {@code String} representing the title.
	 */
	public String getTitle()
	{
		return title;
	}
}
