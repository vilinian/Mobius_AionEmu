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
package com.aionemu.gameserver.model.team2.common.legacy;

/**
 * Defines the different methods for distributing loot among players.<br>
 * This enum is used by {@link com.aionemu.gameserver.model.team2.common.legacy.LootDistribution} to determine how items are shared.
 * @author KKnD
 */
public enum LootDistribution
{
	NORMAL(0),
	ROLL_DICE(2),
	BID(3);
	
	private final int id;
	
	/**
	 * Creates a new {@code LootDistribution} instance.<br>
	 * This constructor assigns an internal identifier to the distribution type.
	 * @param id The unique integer ID for this distribution type.
	 */
	LootDistribution(int id)
	{
		this.id = id;
	}
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
	}
}
