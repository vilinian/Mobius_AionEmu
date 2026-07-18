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
package com.aionemu.gameserver.model.templates.tradelist;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * Defines the different types of NPCs available in the trade list.<br>
 * This enum is used to categorize trading entities within the game world.
 * @author namedrisk
 */
@XmlType(name = "npc_type")
@XmlEnum
public enum TradeNpcType
{
	NORMAL(1),
	ABYSS(2),
	REWARD(4);
	
	private final int index;
	
	/**
	 * Creates a new instance of {@link TradeNpcType}.<br>
	 * This constructor initializes the internal {@code index} value.
	 * @param index The unique integer ID for the NPC type.
	 */
	private TradeNpcType(int index)
	{
		this.index = index;
	}
	
	/**
	 * Retrieves the unique numerical identifier for this {@link TradeNpcType}.<br>
	 * This value is used to identify different types of NPCs in the trade system.
	 * @return The integer value associated with the enum constant.
	 */
	public int index()
	{
		return index;
	}
}
