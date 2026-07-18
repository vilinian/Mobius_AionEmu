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
package com.aionemu.gameserver.questEngine.handlers.models;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents a monster entity within the quest system.<br>
 * This model stores data required to identify and track specific monsters for quest objectives.
 * @author MrPoke
 * @reworked vlog, Bobobear
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Monster")
public class Monster
{
	@XmlAttribute(name = "var", required = true)
	protected int var;
	@XmlAttribute(name = "start_var")
	protected Integer startVar;
	@XmlAttribute(name = "end_var", required = true)
	protected int endVar;
	@XmlAttribute(name = "npc_ids")
	protected List<Integer> npcIds;
	@XmlAttribute(name = "npc_seq")
	private Integer npcSequence;
	
	/**
	 * Retrieves the current variable value for this {@link Monster}.<br>
	 * This value is stored in the {@code var} field.
	 * @return The integer value of the variable.
	 */
	public int getVar()
	{
		return var;
	}
	
	/**
	 * Retrieves the starting variable value for this {@link Monster}.<br>
	 * This value is used to track progress during a quest.
	 * @return the {@code Integer} value of the start variable, or {@code null} if not set.
	 */
	public Integer getStartVar()
	{
		return startVar;
	}
	
	/**
	 * Retrieves the ending variable value for this {@link Monster}.<br>
	 * This value is used to determine the completion state.
	 * @return The integer value of the {@code endVar} attribute.
	 */
	public int getEndVar()
	{
		return endVar;
	}
	
	/**
	 * Retrieves the list of NPC identifiers for this group.<br>
	 * Returns an empty {@code List} if no IDs are defined.
	 * @return a {@code List<Integer>} containing the NPC IDs.
	 */
	public List<Integer> getNpcIds()
	{
		return npcIds;
	}
	
	/**
	 * Retrieves the sequence number for NPCs.<br>
	 * This value is used to determine the order of NPCs in a specific group.
	 * @return The {@code Integer} representing the NPC sequence.
	 */
	public Integer getNpcSequence()
	{
		return npcSequence;
	}
}
