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
package com.aionemu.gameserver.model.ranking;

/**
 * Defines the different types of rankings available for players.<br>
 * This enumeration is used to categorize player achievements and standings within the game.
 */
public enum PlayerRankingEnum
{
	ARENA_OF_DISCIPLINE(541),
	ARENA_OF_COOPERATION(741);
	
	private final int tableId;
	
	/**
	 * Creates a new instance of {@link PlayerRankingEnum}.<br>
	 * This constructor assigns the unique identifier to the ranking.
	 * @param tableId The unique ID for the ranking table.
	 */
	private PlayerRankingEnum(int tableId)
	{
		this.tableId = tableId;
	}
	
	/**
	 * Retrieves the unique identifier for this {@link PlayerRankingEnum}.<br>
	 * This value corresponds to the internal table ID.
	 * @return The integer ID of the ranking.
	 */
	public int getId()
	{
		return tableId;
	}
}
