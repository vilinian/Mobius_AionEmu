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
package com.aionemu.gameserver.model.templates.abyss_op;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.Race;

/**
 * Represents the data model for an Abyss Operation.<br>
 * This class stores configuration details used to define specific abyss instances.
 */
@XmlType(name = "abyss_op")
@XmlAccessorType(XmlAccessType.NONE)
public class AbyssOp
{
	@XmlAttribute(name = "id", required = true)
	private int id;
	
	@XmlAttribute(name = "npc_id", required = true)
	private int npcId;
	
	@XmlAttribute(name = "type", required = true)
	private AbyssOpType abyssOpType;
	
	@XmlAttribute(name = "siege_id", required = true)
	private int siegeId;
	
	@XmlAttribute(name = "race")
	protected Race race = Race.PC_ALL;
	
	@XmlAttribute(name = "group_id", required = true)
	private int groupId;
	
	@XmlAttribute(name = "points", required = true)
	private int points;
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Retrieves the type of the abyss operation.<br>
	 * This method returns the {@code AbyssOpType} associated with this object.
	 * @return the {@code AbyssOpType} of the current operation.
	 */
	public AbyssOpType getAbyssOpType()
	{
		return abyssOpType;
	}
	
	/**
	 * Retrieves the {@code Race} of the player.<br>
	 * This method returns the current character race.
	 * @return The {@link Race} of the player.
	 */
	public Race getRace()
	{
		return race;
	}
	
	/**
	 * Retrieves the unique identifier for this siege.<br>
	 * This ID identifies which specific siege event the {@code SiegeNpc} belongs to.
	 * @return The {@code int} value of the siege ID.
	 */
	public int getSiegeId()
	{
		return siegeId;
	}
	
	/**
	 * Retrieves the unique identifier for the group.<br>
	 * This value is used to associate an {@link AbyssOp} with a specific group.
	 * @return The {@code int} value of the group ID.
	 */
	public int getGroupId()
	{
		return groupId;
	}
	
	/**
	 * Retrieves the current number of points for this rank.<br>
	 * This value is used to determine the player's standing in the arena.
	 * @return The current {@code int} value of points.
	 */
	public int getPoints()
	{
		return points;
	}
	
	/**
	 * Retrieves the unique identifier for the NPC.<br>
	 * This value is stored as an {@code int}.
	 * @return The unique integer ID of the NPC.
	 */
	public int getNpcId()
	{
		return npcId;
	}
}
