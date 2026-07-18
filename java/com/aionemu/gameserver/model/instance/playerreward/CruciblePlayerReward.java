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
package com.aionemu.gameserver.model.instance.playerreward;

/**
 * Represents a reward granted to a player specifically for completing a {@link com.aionemu.gameserver.model.instance.playerreward.InstancePlayerReward} in the Crucible.<br>
 * This class handles the logic for distributing items or currency associated with Crucible achievements.
 * @author xTz
 */
public class CruciblePlayerReward extends InstancePlayerReward
{
	private int spawnPosition;
	private boolean isRewarded = false;
	private int insignia;
	private boolean isPlayerLeave = false;
	private boolean isPlayerDefeated = false;
	
	/**
	 * Creates a new {@link CruciblePlayerReward} instance.<br>
	 * This constructor initializes the reward using the provided object.
	 * @param object The base object used to initialize the parent class.
	 */
	public CruciblePlayerReward(Integer object)
	{
		super(object);
	}
	
	/**
	 * Checks if the reward has been granted.<br>
	 * This method returns {@code true} if the player has received their reward.<br>
	 * It returns {@code false} otherwise.
	 * @return {@code true} if rewarded, {@code false} otherwise.
	 */
	public boolean isRewarded()
	{
		return isRewarded;
	}
	
	/**
	 * Marks the reward as successfully granted.<br>
	 * This sets the {@code isRewarded} flag to {@code true}.
	 */
	public void setRewarded()
	{
		isRewarded = true;
	}
	
	/**
	 * Sets the {@code insignia} value for this reward.<br>
	 * This updates the internal state of the {@link CruciblePlayerReward}.
	 * @param insignia The new integer value to assign.
	 */
	public void setInsignia(int insignia)
	{
		this.insignia = insignia;
	}
	
	/**
	 * Retrieves the current insignia value.<br>
	 * This method returns the integer stored in the {@code insignia} field.
	 * @return The current {@code int} value of the insignia.
	 */
	public int getInsignia()
	{
		return insignia;
	}
	
	/**
	 * Sets the starting position for a player.<br>
	 * This updates the {@code spawnPosition} field of this object.
	 * @param spawnPosition The new position value to assign.
	 */
	public void setSpawnPosition(int spawnPosition)
	{
		this.spawnPosition = spawnPosition;
	}
	
	/**
	 * Retrieves the current spawn position.<br>
	 * This value is used to determine where a player appears.
	 * @return The {@code int} value of the spawn position.
	 */
	public int getSpawnPosition()
	{
		return spawnPosition;
	}
	
	/**
	 * Checks if the player has left the instance.<br>
	 * This method returns the current state of the {@code isPlayerLeave} flag.
	 * @return {@code true} if the player has left, {@code false} otherwise.
	 */
	public boolean isPlayerLeave()
	{
		return isPlayerLeave;
	}
	
	/**
	 * Marks the player as having left the instance.<br>
	 * This sets the {@code isPlayerLeave} flag to {@code true}.
	 */
	public void setPlayerLeave()
	{
		isPlayerLeave = true;
	}
	
	/**
	 * Updates the defeat status of the player.<br>
	 * This method sets the {@code isPlayerDefeated} flag to the provided {@code value}.
	 * @param value The new status to set. Use {@code true} if the player is defeated and {@code false} otherwise.
	 */
	public void setPlayerDefeated(boolean value)
	{
		isPlayerDefeated = value;
	}
	
	/**
	 * Checks if the player has been defeated.<br>
	 * This method returns the current state of the {@code isPlayerDefeated} flag.
	 * @return {@code true} if the player is defeated, {@code false} otherwise.
	 */
	public boolean isPlayerDefeated()
	{
		return isPlayerDefeated;
	}
}
