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
 * Defines the different types of history records for a {@link com.aionemu.gameserver.model.team.legion.Legion}.<br>
 * This enum is used to categorize various historical events or actions associated with a legion.
 * @author Simple
 */
public enum LegionHistoryType
{
	CREATE(0), // No parameters
	JOIN(1), // Parameter: name
	KICK(2), // Parameter: name
	LEVEL_UP(3), // Parameter: legion level
	APPOINTED(4), // Parameter: legion level
	EMBLEM_REGISTER(5), // No parameters
	EMBLEM_MODIFIED(6), // No parameters
	ITEM_DEPOSIT(15), // Parameter: name
	ITEM_WITHDRAW(16), // Parameter: name
	KINAH_DEPOSIT(17), // Parameter: name
	KINAH_WITHDRAW(18); // Parameter: name
	
	private final byte historyType;
	
	/**
	 * Creates a new instance of {@link LegionHistoryType}.<br>
	 * This constructor maps an integer ID to a specific history type.
	 * @param historyType The unique identifier for the history event.
	 */
	private LegionHistoryType(int historyType)
	{
		this.historyType = (byte) historyType;
	}
	
	/**
	 * Retrieves the unique identifier for this {@link LegionHistoryType}.<br>
	 * This value is used to identify different types of legion actions.
	 * @return The {@code byte} ID associated with the history type.
	 */
	public byte getHistoryId()
	{
		return historyType;
	}
}
