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
package com.aionemu.gameserver.services.transfers;

/**
 * This class handles the logic for fast travel between different game locations.<br>
 * It manages the transition of a player's character to a new destination quickly.
 * @author Eloann - Enomine
 */
public class FastTrack
{
	private final int serverId;
	private final int iconSet;
	private final int minlevel, maxlevel;
	
	/**
	 * Creates a new instance of {@link FastTrack}.<br>
	 * This constructor initializes the server settings and level requirements.
	 * @param serverId The unique identifier for the server.
	 * @param sendIcon Set to {@code true} to use icon set {@code 257}, or {@code false} for {@code 513}.
	 * @param minLevel The minimum level required for this track.
	 * @param maxLevel The maximum level allowed for this track.
	 */
	public FastTrack(int serverId, boolean sendIcon, int minLevel, int maxLevel)
	{
		this.serverId = serverId;
		iconSet = sendIcon ? 257 : 513;
		minlevel = minLevel;
		maxlevel = maxLevel;
	}
	
	/**
	 * Retrieves the unique identifier for the server.<br>
	 * This value is assigned during the creation of a {@link FastTrack} object.
	 * @return The {@code int} ID of the server.
	 */
	public int getServerId()
	{
		return serverId;
	}
	
	/**
	 * Retrieves the unique identifier for the current icon set.<br>
	 * This value is used to determine which icons are displayed.
	 * @return The {@code int} value representing the icon set.
	 */
	public int getIconSet()
	{
		return iconSet;
	}
	
	/**
	 * Retrieves the minimum level required for this auto group.<br>
	 * This value is fetched from the underlying template.
	 * @return The minimum level as an {@code int}.
	 */
	public int getMinLevel()
	{
		return minlevel;
	}
	
	/**
	 * Retrieves the maximum level allowed for this faction.<br>
	 * This value is stored in the {@code maxlevel} field.
	 * @return The maximum level as an {@code int}.
	 */
	public int getMaxLevel()
	{
		return maxlevel;
	}
}
