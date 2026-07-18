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
package com.aionemu.gameserver.questEngine.model;

import javax.xml.bind.annotation.XmlEnum;

/**
 * Represents the possible states of a quest in the game.<br>
 * This enum is used by the {@code QuestManager} to track progress.<br>
 * It defines whether a quest is active, completed, or failed.
 * @author MrPoke
 */
@XmlEnum
public enum QuestStatus
{
	NONE(0), // Default status. Aborted quests and the quests, where the quest timer ended. Used for beginning a new
	
	// Stored together with other quests in the player's quest list and invisible to them, do not count these.
	START(3), // Accepted quests
	REWARD(4), // The quests, that are finished. "Go and get your reward"
	COMPLETE(5), // Completed quests
	LOCKED(6); // Not (yet) available quests
	
	private final int id;
	
	/**
	 * Creates a new instance of {@link QuestStatus}.<br>
	 * This constructor maps the enum to its internal integer ID.
	 * @param id The unique integer value for this status.
	 */
	private QuestStatus(int id)
	{
		this.id = id;
	}
	
	/**
	 * Retrieves the unique identifier for this cape.<br>
	 * This corresponds to the internal {@code id} field.
	 * @return The integer value of the cape ID.
	 */
	public int value()
	{
		return id;
	}
}
