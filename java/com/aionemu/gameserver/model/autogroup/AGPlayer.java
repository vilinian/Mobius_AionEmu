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
package com.aionemu.gameserver.model.autogroup;

import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * Represents a player within the auto-grouping system.<br>
 * This class stores data related to a {@link Player} for group management purposes.
 * @author xTz
 */
public class AGPlayer
{
	private final Integer objectId;
	private final Race race;
	private final PlayerClass playerClass;
	private final String name;
	private boolean isInInstance;
	private boolean isOnline;
	private boolean isPressEnter;
	
	/**
	 * Creates a new {@link AGPlayer} instance from an existing {@code Player}.<br>
	 * This constructor copies the basic data from the {@code Player} object.<br>
	 * It also sets the {@code isOnline} status to {@code true}.
	 * @param player The {@code Player} object used to initialize this instance.
	 */
	public AGPlayer(Player player)
	{
		objectId = player.getObjectId();
		race = player.getRace();
		playerClass = player.getPlayerClass();
		name = player.getName();
		isOnline = true;
	}
	
	/**
	 * Retrieves the unique identifier for this {@link AGPlayer}.<br>
	 * This ID is used to identify the player in the game world.
	 * @return The {@code Integer} object ID.
	 */
	public Integer getObjectId()
	{
		return objectId;
	}
	
	/**
	 * Retrieves the {@code Race} of the player.<br>
	 * This method returns the current character race.
	 * @return The {@link Race} of the player.
	 */
	public Race getRace()
	{
		return race;
	}
	
	/**
	 * Retrieves the name of the bookmark.<br>
	 * This method returns the {@code String`name`} associated with this object.
	 * @return The name of the bookmark as a {@code String}.
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Retrieves the character class of the player.<br>
	 * This method returns the {@code PlayerClass} associated with this ranking result.
	 * @return The {@code PlayerClass} of the player.
	 */
	public PlayerClass getPlayerClass()
	{
		return playerClass;
	}
	
	/**
	 * Updates the instance status of the player.<br>
	 * This method sets whether the player is currently inside an instance.
	 * @param result The new value to set for {@code isInInstance}.
	 */
	public void setInInstance(boolean result)
	{
		isInInstance = result;
	}
	
	/**
	 * Checks if the player is currently inside an instance.<br>
	 * This method returns the current status of the {@code isInInstance} field.
	 * @return {@code true} if the player is in an instance, {@code false} otherwise.
	 */
	public boolean isInInstance()
	{
		return isInInstance;
	}
	
	/**
	 * Checks if the player is currently online.<br>
	 * This method returns the current status of the {@code isOnline} flag.
	 * @return {@code true} if the player is online, {@code false} otherwise.
	 */
	public boolean isOnline()
	{
		return isOnline;
	}
	
	/**
	 * Updates the online status of the player.<br>
	 * This method sets the {@code isOnline} flag to the provided value.
	 * @param result The new online status to set.
	 */
	public void setOnline(boolean result)
	{
		isOnline = result;
	}
	
	/**
	 * Checks if the player has pressed the {@code Enter} key.<br>
	 * This method returns the current state of the {@code isPressEnter} flag.
	 * @return {@code true} if the key was pressed, {@code false} otherwise.
	 */
	public boolean isPressedEnter()
	{
		return isPressEnter;
	}
	
	/**
	 * Updates the press enter status of the player.<br>
	 * This method sets the {@code isPressEnter} flag to the provided value.
	 * @param result The new value to set for the press enter state.
	 */
	public void setPressEnter(boolean result)
	{
		isPressEnter = result;
	}
}
