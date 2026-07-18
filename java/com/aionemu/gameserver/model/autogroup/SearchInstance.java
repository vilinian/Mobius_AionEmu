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

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * Represents a search criteria for finding specific game instances.<br>
 * It is used by the {@link com.aionemu.gameserver.model.autogroup.AutoGroup} system to filter and locate suitable matches.
 * @author xTz
 * @author GiGatR00n v4.7.5.x
 */
public class SearchInstance
{
	private final long registrationTime = System.currentTimeMillis();
	private final int instanceMaskId;
	private final EntryRequestType ert;
	private List<Integer> members;
	
	/**
	 * Creates a new {@code SearchInstance} object.<br>
	 * This constructor initializes the instance with specific request details.<br>
	 * It converts the provided {@code Player} objects into a list of IDs.
	 * @param instanceMaskId The unique identifier mask for the instance.
	 * @param ert The type of entry request being made.
	 * @param members A collection of {@link Player} objects involved in the search.
	 */
	public SearchInstance(int instanceMaskId, EntryRequestType ert, Collection<Player> members)
	{
		this.instanceMaskId = instanceMaskId;
		this.ert = ert;
		if (members != null)
		{
			this.members = members.stream().map(Player::getObjectId).collect(Collectors.toList());
		}
	}
	
	/**
	 * Retrieves the list of member IDs for this instance.<br>
	 * This method returns all players currently in the group.
	 * @return a {@code List<Integer>} containing the member IDs.
	 */
	public List<Integer> getMembers()
	{
		return members;
	}
	
	/**
	 * Retrieves the unique identifier for this auto group instance.<br>
	 * This value is used to distinguish between different instances of the same type.
	 * @return The {@code int} mask ID associated with this {@link AutoGroupType}.
	 */
	public int getInstanceMaskId()
	{
		return instanceMaskId;
	}
	
	/**
	 * Calculates the time left before the instance expires.<br>
	 * This value is based on the {@code registrationTime}.<br>
	 * The result is scaled by a factor of {@code 256}.
	 * @return The remaining time in seconds as an {@code int}.
	 */
	public int getRemainingTime()
	{
		return ((int) (System.currentTimeMillis() - registrationTime) / 1000) * 256;
	}
	
	/**
	 * Retrieves the type of entry request for this instance.<br>
	 * This method returns the {@code EntryRequestType} associated with the search.
	 * @return the {@code EntryRequestType} of the current instance.
	 */
	public EntryRequestType getEntryRequestType()
	{
		return ert;
	}
	
	/**
	 * Checks if the current group type is a Dredgion.<br>
	 * This method returns {@code true} for specific Dredgion types.<br>
	 * It returns {@code false} for all other types.
	 * @return {@code true} if it is a Dredgion, otherwise {@code false}.
	 */
	public boolean isDredgion()
	{
		return (instanceMaskId == 1) || (instanceMaskId == 2) || (instanceMaskId == 3) || (instanceMaskId == 121);
	}
	
	/**
	 * Checks if the current group type is a Kamar battlefield.<br>
	 * This method returns {@code true} only for {@code KAMAR_BATTLEFIELD}.<br>
	 * It returns {@code false} for all other types.
	 * @return {@code true} if it is a Kamar battlefield, otherwise {@code false}
	 */
	public boolean isKamar()
	{
		return instanceMaskId == 107;
	}
	
	/**
	 * Checks if the current group type is a {@code JORMUNGAND_MARCHING_ROUTE}.<br>
	 * This method helps identify specific route types for auto-grouping.
	 * @return {@code true} if it matches the Jormungand route, otherwise {@code false}.
	 */
	public boolean isJormungand()
	{
		return instanceMaskId == 108;
	}
	
	/**
	 * Checks if the current instance is a Bastion.<br>
	 * It compares the {@code instanceMaskId} against the value {@code 109}.
	 * @return {@code true} if it is a Bastion, {@code false} otherwise.
	 */
	public boolean isBastion()
	{
		return instanceMaskId == 109;
	}
	
	/**
	 * Checks if the current group type is {@code RUNATORIUM}.<br>
	 * This method returns {@code true} only for the Runatorium area.
	 * @return {@code true} if this is a Runatorium, otherwise {@code false}
	 */
	public boolean isRunatorium()
	{
		return instanceMaskId == 111;
	}
	
	/**
	 * Checks if the current group type is set to {@code BALAUR_MARCHING_ROUTE}.<br>
	 * This helps identify specific marching behaviors.
	 * @return {@code true} if it matches the Balaur route, otherwise {@code false}.
	 */
	public boolean isBalaurMarching()
	{
		return instanceMaskId == 122;
	}
	
	/**
	 * Checks if the current group type is {@code RUNATORIUM_RUINS}.<br>
	 * This method returns {@code true} only for this specific location.
	 * @return {@code true} if it matches, otherwise {@code false}.
	 */
	public boolean isRunatoriumRuins()
	{
		return instanceMaskId == 123;
	}
	
	/**
	 * Checks if the current instance is a Golden Crusible.<br>
	 * This method compares the {@code instanceMaskId} to the value {@code 125}.
	 * @return {@code true} if it is a Golden Crusible, otherwise {@code false}.
	 */
	public boolean isGoldenCrusible()
	{
		return instanceMaskId == 125;
	}
	
	/**
	 * Checks if the current group type is a {@code SANCTUM_BATTLEFIELD}.<br>
	 * This method helps identify specific battlefield types for logic handling.
	 * @return {@code true} if this instance is {@code SANCTUM_BATTLEFIELD}, otherwise {@code false}.
	 */
	public boolean isSanctumBattlefield()
	{
		return instanceMaskId == 416;
	}
	
	/**
	 * Checks if the current group type is a Pandaemonium Battlefield.<br>
	 * This method returns {@code true} only for the {@code PANDAEMONIUM_BATTLEFIELD} case.
	 * @return {@code true} if it is a Pandaemonium Battlefield, otherwise {@code false}.
	 */
	public boolean isPandaemoniumBattlefield()
	{
		return instanceMaskId == 417;
	}
}
