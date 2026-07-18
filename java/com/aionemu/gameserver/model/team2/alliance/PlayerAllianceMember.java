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
package com.aionemu.gameserver.model.team2.alliance;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.PlayerTeamMember;

/**
 * Represents a {@link Player} who is a member of an alliance.<br>
 * This class extends {@link PlayerTeamMember} to handle alliance-specific data.
 * @author ATracer
 */
public class PlayerAllianceMember extends PlayerTeamMember
{
	private int allianceId;
	
	/**
	 * Creates a new {@link PlayerAllianceMember} for a specific player.<br>
	 * This constructor initializes the member using the provided {@code Player} object.
	 * @param player The {@code Player} to be added as an alliance member.
	 */
	public PlayerAllianceMember(Player player)
	{
		super(player);
	}
	
	/**
	 * Retrieves the unique identifier for the alliance.<br>
	 * This value is stored in the {@code allianceId} field.
	 * @return The {@code int} ID of the alliance.
	 */
	public int getAllianceId()
	{
		return allianceId;
	}
	
	/**
	 * Sets the unique identifier for the alliance.<br>
	 * This updates the {@code allianceId} field of the current object.
	 * @param allianceId The new ID to assign to the alliance.
	 */
	public void setAllianceId(int allianceId)
	{
		this.allianceId = allianceId;
	}
	
	/**
	 * Retrieves the {@link PlayerAllianceGroup} associated with this member.<br>
	 * This method fetches the group data from the underlying {@code Player} object.
	 * @return The {@code PlayerAllianceGroup} for this player, or {@code null} if none exists.
	 */
	public PlayerAllianceGroup getPlayerAllianceGroup()
	{
		return getObject().getPlayerAllianceGroup2();
	}
	
	/**
	 * Sets the alliance group for the current player.<br>
	 * This updates the {@code PlayerAllianceGroup} associated with the object.
	 * @param playerAllianceGroup The new {@link PlayerAllianceGroup} to assign.
	 */
	public void setPlayerAllianceGroup(PlayerAllianceGroup playerAllianceGroup)
	{
		getObject().setPlayerAllianceGroup2(playerAllianceGroup);
	}
}
