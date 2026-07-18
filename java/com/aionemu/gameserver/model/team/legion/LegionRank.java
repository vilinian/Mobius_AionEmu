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
package com.aionemu.gameserver.model.team.legion;

/**
 * Represents the different rank levels available within a {@link com.aionemu.gameserver.model.team.legion.Legion}.<br>
 * This enum defines the hierarchy and permissions for members of a legion.
 * @author Simple
 */
public enum LegionRank
{
	/**
	 * All Legion Ranks *
	 */
	BRIGADE_GENERAL(0),
	DEPUTY(1),
	CENTURION(2),
	LEGIONARY(3),
	VOLUNTEER(4);
	
	private final byte rank;
	
	/**
	 * Creates a new instance of {@link LegionRank}.<br>
	 * This constructor sets the internal rank value.
	 * @param rank The integer value representing the rank level.
	 */
	private LegionRank(int rank)
	{
		this.rank = (byte) rank;
	}
	
	/**
	 * Retrieves the unique identifier for this {@link LegionRank}.<br>
	 * This value is used to identify the rank in the database.
	 * @return The {@code byte} ID of the rank.
	 */
	public byte getRankId()
	{
		return rank;
	}
}
