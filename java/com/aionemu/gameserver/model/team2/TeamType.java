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

import com.aionemu.gameserver.model.instance.StageType;

/**
 * Defines the different types of teams available within the game world.<br>
 * This enumeration is used to categorize players and group behaviors.<br>
 * It provides a standard way to identify team affiliations across the {@code team2} module.
 * @author Source
 */
public enum TeamType
{
	GROUP(0x3F, 0),
	AUTO_GROUP(0x02, 1),
	ALLIANCE(0x3F, 0),
	ALLIANCE_DEFENCE(0x3F, 4),
	ALLIANCE_OFFENCE(0x02, 3);
	// TODO UPDATE OR ADD NEW F6
	
	private final int type;
	private final int subType;
	
	/**
	 * Creates a new instance of {@link TeamType}.<br>
	 * This constructor initializes the internal numeric codes.
	 * @param type The primary category code for the team.
	 * @param subType The specific subtype identifier for the team.
	 */
	private TeamType(int type, int subType)
	{
		this.type = type;
		this.subType = subType;
	}
	
	/**
	 * Retrieves the category type of this {@link StageType}.<br>
	 * This value is used to distinguish between different types of stages.
	 * @return The integer value representing the stage type.
	 */
	public int getType()
	{
		return type;
	}
	
	/**
	 * Retrieves the specific subtype associated with this {@link TeamType}.<br>
	 * This value distinguishes between different categories of the same team type.
	 * @return The integer value of the subtype.
	 */
	public int getSubType()
	{
		return subType;
	}
	
	/**
	 * Checks if the team type is an automatic group.<br>
	 * This method compares the {@code type} value to {@code 0x02}.
	 * @return {@code true} if it is an auto team, {@code false} otherwise.
	 */
	public boolean isAutoTeam()
	{
		return getType() == 0x02;
	}
	
	/**
	 * Checks if the team type is designated for offensive actions.<br>
	 * This method returns {@code true} if the sub-type matches the value {@code 3}.
	 * @return {@code true} if this is an offence type, {@code false} otherwise.
	 */
	public boolean isOffence()
	{
		return getSubType() == 3;
	}
	
	/**
	 * Checks if the team type is a defence type.<br>
	 * This method returns {@code true} if the sub-type is equal to {@code 4}.
	 * @return {@code true} if this is a defence team, {@code false} otherwise.
	 */
	public boolean isDefence()
	{
		return getSubType() == 4;
	}
}
