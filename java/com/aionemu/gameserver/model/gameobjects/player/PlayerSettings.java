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

import com.aionemu.gameserver.model.gameobjects.PersistentState;

/**
 * This class stores the configuration settings for a {@link com.aionemu.gameserver.model.gameobjects.player.Player}.<br>
 * It manages persistent data such as user preferences and account-specific options.
 * @author ATracer
 */
public class PlayerSettings
{
	private PersistentState persistentState;
	private byte[] uiSettings;
	private byte[] shortcuts;
	private byte[] houseBuddies;
	private int deny = 0;
	private int display = 0;
	
	/**
	 * Creates a new instance of {@link PlayerSettings}.<br>
	 * This initializes the default settings for a player.
	 */
	public PlayerSettings()
	{
	}
	
	/**
	 * Creates a new {@link PlayerSettings} object with specific configurations.<br>
	 * This constructor initializes all player-related data fields.
	 * @param uiSettings The byte array containing user interface settings.
	 * @param shortcuts The byte array for player hotkeys and shortcuts.
	 * @param houseBuddies The byte array identifying the player's house buddies.
	 * @param deny The integer value representing the current denial status.
	 * @param display The integer value for the display configuration.
	 */
	public PlayerSettings(byte[] uiSettings, byte[] shortcuts, byte[] houseBuddies, int deny, int display)
	{
		this.uiSettings = uiSettings;
		this.shortcuts = shortcuts;
		this.houseBuddies = houseBuddies;
		this.deny = deny;
		this.display = display;
	}
	
	/**
	 * Retrieves the current state of this challenge.<br>
	 * This information is saved between game sessions.
	 * @return the {@link PersistentState} object.
	 */
	public PersistentState getPersistentState()
	{
		return persistentState;
	}
	
	/**
	 * Updates the {@code persistentState} of this decoration.<br>
	 * This method assigns a new {@link PersistentState} to the object.
	 * @param persistentState The new {@link PersistentState} to assign.
	 */
	public void setPersistentState(PersistentState persistentState)
	{
		this.persistentState = persistentState;
	}
	
	/**
	 * Retrieves the user interface settings for the player.<br>
	 * This method returns the raw data stored in the {@code uiSettings} field.
	 * @return a {@code byte[]} array containing the UI configuration.
	 */
	public byte[] getUiSettings()
	{
		return uiSettings;
	}
	
	/**
	 * Updates the user interface settings for the player.<br>
	 * This method sets the {@code uiSettings} field.<br>
	 * It also updates the {@link PersistentState} to {@code UPDATE_REQUIRED}.
	 * @param uiSettings The new array of bytes containing UI configuration data.
	 */
	public void setUiSettings(byte[] uiSettings)
	{
		this.uiSettings = uiSettings;
		persistentState = PersistentState.UPDATE_REQUIRED;
	}
	
	/**
	 * Retrieves the player's shortcut settings.<br>
	 * This method returns the {@code byte[]} array containing shortcut data.
	 * @return a {@code byte[]} array of shortcuts.
	 */
	public byte[] getShortcuts()
	{
		return shortcuts;
	}
	
	/**
	 * Updates the player's shortcut configuration.<br>
	 * This method sets the {@code shortcuts} array.<br>
	 * It also marks the {@link PersistentState} as {@code UPDATE_REQUIRED}.
	 * @param shortcuts The new array of shortcut data.
	 */
	public void setShortcuts(byte[] shortcuts)
	{
		this.shortcuts = shortcuts;
		persistentState = PersistentState.UPDATE_REQUIRED;
	}
	
	/**
	 * Retrieves the house buddies data for the player.<br>
	 * This returns the raw byte array stored in the {@code houseBuddies} field.
	 * @return a {@code byte[]} containing the house buddies information.
	 */
	public byte[] getHouseBuddies()
	{
		return houseBuddies;
	}
	
	/**
	 * Updates the list of house buddies for the player.<br>
	 * This method also marks the {@link PersistentState} as requiring an update.
	 * @param houseBuddies The new array of house buddy data.
	 */
	public void setHouseBuddies(byte[] houseBuddies)
	{
		this.houseBuddies = houseBuddies;
		persistentState = PersistentState.UPDATE_REQUIRED;
	}
	
	/**
	 * Retrieves the current display setting.<br>
	 * This value is stored in the {@code display} field.
	 * @return The integer value of the display setting.
	 */
	public int getDisplay()
	{
		return display;
	}
	
	/**
	 * Updates the display value for the player.<br>
	 * This method also marks the {@link PersistentState} as requiring an update.
	 * @param display The new integer value to set for the display.
	 */
	public void setDisplay(int display)
	{
		this.display = display;
		persistentState = PersistentState.UPDATE_REQUIRED;
	}
	
	/**
	 * Retrieves the current denial value for the player.<br>
	 * This value is used to determine if certain actions are restricted.
	 * @return The {@code int} value of the denial status.
	 */
	public int getDeny()
	{
		return deny;
	}
	
	/**
	 * Sets the {@code deny} status for the player.<br>
	 * This method updates the internal state to require an update.
	 * @param deny The new integer value for the deny status.
	 */
	public void setDeny(int deny)
	{
		this.deny = deny;
		persistentState = PersistentState.UPDATE_REQUIRED;
	}
	
	/**
	 * Checks if the player is currently in a specific denied status.<br>
	 * It compares the current {@code deny} value with the provided {@link DeniedStatus}.
	 * @param deny The {@link DeniedStatus} to check against.
	 * @return {@code true} if the status matches, otherwise {@code false}.
	 */
	public boolean isInDeniedStatus(DeniedStatus deny)
	{
		final int isDeniedStatus = this.deny & deny.getId();
		
		if (isDeniedStatus == deny.getId())
		{
			return true;
		}
		
		return false;
	}
}
