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
 * Defines the different types of rules used for loot distribution.<br>
 * This enum helps determine how items are assigned to players during game events.
 * @author Lyahim
 */
public enum LootRuleType
{
	FREEFORALL(0),
	ROUNDROBIN(1),
	LEADER(2);
	
	private final int id;
	
	/**
	 * Creates a new instance of {@link LootRuleType}.<br>
	 * This constructor assigns the internal identifier to the enum constant.
	 * @param id The unique integer ID for this loot rule type.
	 */
	private LootRuleType(int id)
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
