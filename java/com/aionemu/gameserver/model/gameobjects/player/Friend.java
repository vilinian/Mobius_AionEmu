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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.gameobjects.player.FriendList.Status;
import com.aionemu.gameserver.world.WorldPosition;

/**
 * Represents a friend entity within the social system of a {@link Player}.<br>
 * This class stores information about a player's connections and their current status.
 * @author Ben
 */
public class Friend
{
	private static final Logger log = LoggerFactory.getLogger(Friend.class);
	private PlayerCommonData pcd;
	
	private String friendNote = "";
	
	/**
	 * Creates a new {@link Friend} object.<br>
	 * This constructor initializes the friend with the provided player data.
	 * @param pcd The {@code PlayerCommonData} of the player to be added as a friend.
	 */
	public Friend(PlayerCommonData pcd)
	{
		this.pcd = pcd;
	}
	
	/**
	 * Retrieves the current online status of the friend.<br>
	 * This method checks if the player is currently connected to the server.<br>
	 * It returns {@code FriendList.Status.OFFLINE} if the player is not found or is disconnected.
	 * @return The {@link Status} of the friend.
	 */
	public Status getStatus()
	{
		// second check is temporary
		if ((pcd.getPlayer() == null) || !pcd.isOnline())
		{
			return FriendList.Status.OFFLINE;
		}
		
		return pcd.getPlayer().getFriendList().getStatus();
	}
	
	/**
	 * Sets the {@code PlayerCommonData} for this friend.<br>
	 * This method updates the internal reference to the player's data.
	 * @param pcd The {@link PlayerCommonData} object to assign.
	 */
	public void setPCD(PlayerCommonData pcd)
	{
		this.pcd = pcd;
	}
	
	/**
	 * Retrieves the name of the player.<br>
	 * This method returns the {@code String} name from the underlying {@link PlayerCommonData}.
	 * @return The name of the player as a {@code String}.
	 */
	public String getName()
	{
		return pcd.getName();
	}
	
	/**
	 * Retrieves the current level of the player.<br>
	 * This value is obtained from the {@code PlayerCommonData}.
	 * @return The integer level of the player.
	 */
	public int getLevel()
	{
		return pcd.getLevel();
	}
	
	/**
	 * Retrieves the note associated with this friend.<br>
	 * This method fetches the data from the {@link PlayerCommonData} object.
	 * @return The note as a {@code String}.
	 */
	public String getNote()
	{
		return pcd.getNote();
	}
	
	/**
	 * Retrieves the personal note associated with this {@link Friend}.<br>
	 * This string can be used to store extra information about a specific friend.
	 * @return The {@code String} containing the friend's note.
	 */
	public String getFriendNote()
	{
		return friendNote;
	}
	
	/**
	 * Updates the personal note for this friend.<br>
	 * This method stores a new {@code String} value in the internal field.
	 * @param note The new note to assign to the friend.
	 */
	public void setNote(String note)
	{
		friendNote = note;
	}
	
	/**
	 * Retrieves the character class of the player.<br>
	 * This method returns the {@code PlayerClass} associated with this ranking result.
	 * @return The {@code PlayerClass} of the player.
	 */
	public PlayerClass getPlayerClass()
	{
		return pcd.getPlayerClass();
	}
	
	/**
	 * Gets the unique identifier for the map where the friend is located.<br>
	 * This method retrieves the ID from the {@link PlayerCommonData} position.<br>
	 * It returns 0 if the position data is null.
	 * @return The {@code int} representing the map ID.
	 */
	public int getMapId()
	{
		final WorldPosition position = pcd.getPosition();
		if (position == null)
		{
			// doubt its possible, but need check warnings
			log.warn("Null friend position: {}", pcd.getPlayerObjId());
			return 0;
		}
		
		return position.getMapId();
	}
	
	/**
	 * Retrieves the last time the friend was online.<br>
	 * It returns the time in Unix seconds.<br>
	 * If the player is currently online or has no recorded login, it returns {@code 0}.
	 * @return The last online time as an {@code int} in seconds.
	 */
	public int getLastOnlineTime()
	{
		if ((pcd.getLastOnline() == null) || isOnline())
		{
			return 0;
		}
		
		return (int) (pcd.getLastOnline().getTime() / 1000); // Convert to int, unix time format (ms -> seconds)
	}
	
	/**
	 * Retrieves the unique object identifier for this friend.<br>
	 * This value is obtained from the associated {@link PlayerCommonData}.
	 * @return The integer ID of the player object.
	 */
	public int getOid()
	{
		return pcd.getPlayerObjId();
	}
	
	/**
	 * Retrieves the {@link Player} associated with this friend.<br>
	 * This method fetches the player data from the internal {@code PlayerCommonData}.
	 * @return The {@code Player} object representing the friend.
	 */
	public Player getPlayer()
	{
		return pcd.getPlayer();
	}
	
	/**
	 * Checks if the player is currently online.<br>
	 * This method returns the current status of the {@code isOnline} flag.
	 * @return {@code true} if the player is online, {@code false} otherwise.
	 */
	public boolean isOnline()
	{
		return pcd.isOnline();
	}
}
