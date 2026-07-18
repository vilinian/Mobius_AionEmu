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
package com.aionemu.gameserver.model.team2;

import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * Represents a {@link Player} who is a member of a specific team.<br>
 * This class links a player entity to the team system.
 * @author ATracer
 */
public class PlayerTeamMember implements TeamMember<Player>
{
	final Player player;
	private long lastOnlineTime;
	
	/**
	 * Creates a new {@link PlayerTeamMember} instance.<br>
	 * This constructor links a specific {@link Player} to the team member object.
	 * @param player The {@code Player} object to be added to the team.
	 */
	public PlayerTeamMember(Player player)
	{
		this.player = player;
	}
	
	/**
	 * Retrieves the unique identifier for this {@link Player}.<br>
	 * This ID is used to identify the player in the game world.
	 * @return The {@code Integer} object ID.
	 */
	@Override
	public Integer getObjectId()
	{
		return player.getObjectId();
	}
	
	/**
	 * Retrieves the name of the player.<br>
	 * This method returns the {@code String} name associated with the {@link Player}.
	 * @return The name of the player as a {@code String}.
	 */
	@Override
	public String getName()
	{
		return player.getName();
	}
	
	/**
	 * Retrieves the {@link Player} associated with this team member.<br>
	 * This method returns the underlying game object.
	 * @return The {@code Player} object.
	 */
	@Override
	public Player getObject()
	{
		return player;
	}
	
	/**
	 * Retrieves the timestamp of when the player was last online.<br>
	 * This value is stored as a {@code long}.
	 * @return The last online time as a {@code long}.
	 */
	public long getLastOnlineTime()
	{
		return lastOnlineTime;
	}
	
	/**
	 * Updates the {@code lastOnlineTime} field to the current system time.<br>
	 * This method records when the player was last active.<br>
	 * It uses {@code System.currentTimeMillis()} to get the timestamp.
	 */
	public void updateLastOnlineTime()
	{
		lastOnlineTime = System.currentTimeMillis();
	}
	
	/**
	 * Checks if the player is currently online.<br>
	 * This method returns the current status of the {@code isOnline} flag.
	 * @return {@code true} if the player is online, {@code false} otherwise.
	 */
	public boolean isOnline()
	{
		return player.isOnline();
	}
	
	/**
	 * Retrieves the X coordinate of the {@link Player}.<br>
	 * This value represents the horizontal position in the world.
	 * @return The {@code float} value of the X coordinate.
	 */
	public float getX()
	{
		return player.getX();
	}
	
	/**
	 * Retrieves the vertical coordinate of the {@link Player}.<br>
	 * This value represents the height in the game world.
	 * @return The {@code float} value of the Y coordinate.
	 */
	public float getY()
	{
		return player.getY();
	}
	
	/**
	 * Retrieves the vertical coordinate of the player.<br>
	 * This value represents the height in the game world.
	 * @return The {@code float} value of the Z coordinate.
	 */
	public float getZ()
	{
		return player.getZ();
	}
	
	/**
	 * Retrieves the current rotation direction of the object.<br>
	 * This value is stored as a {@code byte}.
	 * @return The heading value of the object.
	 */
	public byte getHeading()
	{
		return player.getHeading();
	}
	
	/**
	 * Retrieves the level of the associated {@link Player}.<br>
	 * This value is fetched from the {@code Player} object.
	 * @return The level as a {@code byte}.
	 */
	public byte getLevel()
	{
		return player.getLevel();
	}
}
