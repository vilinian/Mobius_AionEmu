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
package com.aionemu.gameserver.model.account;

import java.sql.Timestamp;
import java.util.List;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.PlayerAppearance;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.model.team.legion.Legion;
import com.aionemu.gameserver.model.team.legion.LegionMember;

/**
 * This class holds information about a player displayed on the character selection screen.<br>
 * It contains data such as {@link PlayerCommonData}, {@link PlayerAppearance}, and creation or deletion timestamps.
 * @author Luno
 * @see PlayerCommonData
 * @see PlayerAppearance
 */
public class PlayerAccountData
{
	private final CharacterBanInfo cbi;
	private PlayerCommonData playerCommonData;
	private final PlayerAppearance appereance;
	private List<Item> equipment;
	private Timestamp creationDate;
	private Timestamp deletionDate;
	private final LegionMember legionMember;
	
	/**
	 * Creates a new instance of {@link PlayerAccountData}.<br>
	 * This constructor initializes the account with all necessary player details.
	 * @param playerCommonData The general data for the player.
	 * @param cbi The ban information associated with this character.
	 * @param appereance The visual appearance of the player.
	 * @param equipment A list of items currently equipped by the player.
	 * @param legionMember The membership details for the player's legion.
	 */
	public PlayerAccountData(PlayerCommonData playerCommonData, CharacterBanInfo cbi, PlayerAppearance appereance, List<Item> equipment, LegionMember legionMember)
	{
		this.playerCommonData = playerCommonData;
		this.cbi = cbi;
		this.appereance = appereance;
		this.equipment = equipment;
		this.legionMember = legionMember;
	}
	
	/**
	 * Retrieves the ban information for this character.<br>
	 * This method returns the {@code CharacterBanInfo} object associated with the account.
	 * @return The {@code CharacterBanInfo} of the character.
	 */
	public CharacterBanInfo getCharBanInfo()
	{
		return cbi;
	}
	
	/**
	 * Retrieves the date and time when the account was created.<br>
	 * This value is stored as a {@code Timestamp}.
	 * @return The {@code Timestamp} representing the creation date.
	 */
	public Timestamp getCreationDate()
	{
		return creationDate;
	}
	
	/**
	 * Sets the date when the player account was deleted.<br>
	 * This updates the {@code deletionDate} field in the {@link PlayerAccountData} object.
	 * @param deletionDate The {@code Timestamp} representing the deletion time.
	 */
	public void setDeletionDate(Timestamp deletionDate)
	{
		this.deletionDate = deletionDate;
	}
	
	/**
	 * Retrieves the date and time when the account was deleted.<br>
	 * Returns {@code null} if the account has not been deleted.
	 * @return The {@code Timestamp} of the deletion date.
	 */
	public Timestamp getDeletionDate()
	{
		return deletionDate;
	}
	
	/**
	 * Returns the time when the account was deleted.<br>
	 * The value is calculated from {@code getDeletionDate}.<br>
	 * It returns {@code 0} if no deletion date exists.
	 * @return The number of seconds since the Unix epoch until deletion.
	 */
	public int getDeletionTimeInSeconds()
	{
		return deletionDate == null ? 0 : (int) (deletionDate.getTime() / 1000);
	}
	
	/**
	 * Retrieves the common data for the player.<br>
	 * This information is used to display details on the character selection screen.
	 * @return the {@code PlayerCommonData} object associated with this account.
	 */
	public PlayerCommonData getPlayerCommonData()
	{
		return playerCommonData;
	}
	
	/**
	 * Updates the common data for a player.<br>
	 * This method sets the {@code playerCommonData} field of this object.
	 * @param playerCommonData The new {@link PlayerCommonData} to assign.
	 */
	public void setPlayerCommonData(PlayerCommonData playerCommonData)
	{
		this.playerCommonData = playerCommonData;
	}
	
	/**
	 * Retrieves the visual appearance of the player.<br>
	 * This information is used for the character selection screen.
	 * @return the {@link PlayerAppearance} object.
	 */
	public PlayerAppearance getAppereance()
	{
		return appereance;
	}
	
	/**
	 * Sets the date when the account was created.<br>
	 * This updates the {@code creationDate} field.
	 * @param creationDate The {@code Timestamp} to set as the creation date.
	 */
	public void setCreationDate(Timestamp creationDate)
	{
		this.creationDate = creationDate;
	}
	
	/**
	 * Retrieves the {@link Legion} associated with this account.<br>
	 * This method fetches data from the internal {@code legionMember} object.
	 * @return the {@code Legion} object or {@code null} if no legion exists.
	 */
	public Legion getLegion()
	{
		return legionMember.getLegion();
	}
	
	/**
	 * Checks if the player is currently part of a {@link Legion}.<br>
	 * This method verifies if the {@code legionMember} object is not {@code null}.
	 * @return {@code true} if the player is a member, {@code false} otherwise.
	 */
	public boolean isLegionMember()
	{
		return legionMember != null;
	}
	
	/**
	 * Retrieves the list of items equipped by the player.<br>
	 * This method returns all {@link Item} objects currently held in the equipment slot.
	 * @return a {@code List} of {@link Item} objects.
	 */
	public List<Item> getEquipment()
	{
		return equipment;
	}
	
	/**
	 * Updates the list of items equipped by the player.<br>
	 * This method replaces the current {@code equipment} list with a new one.
	 * @param equipment The new list of {@link Item} objects to assign.
	 */
	public void setEquipment(List<Item> equipment)
	{
		this.equipment = equipment;
	}
}
