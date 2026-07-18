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
package com.aionemu.gameserver.model.autogroup;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.geoEngine.collision.CollisionResults;

/**
 * Represents a group of players or entities managed by the auto-grouping system.<br>
 * This class stores configuration data for automated grouping logic within the game server.
 * @author MrPoke
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AutoGroup")
public class AutoGroup
{
	@XmlAttribute(required = true)
	protected int id;
	@XmlAttribute(required = true)
	protected int instanceId;
	@XmlAttribute(name = "name_id")
	protected int nameId;
	@XmlAttribute(name = "title_id")
	protected int titleId;
	@XmlAttribute(name = "min_lvl")
	protected int minLvl;
	@XmlAttribute(name = "max_lvl")
	protected int maxLvl;
	@XmlAttribute(name = "register_quick")
	protected boolean registerQuick;
	@XmlAttribute(name = "register_group")
	protected boolean registerGroup;
	@XmlAttribute(name = "register_new")
	protected boolean registerNew;
	@XmlAttribute(name = "npc_ids")
	protected List<Integer> npcIds;
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Retrieves the unique identifier for this instance.<br>
	 * This ID was provided during the construction of {@link CollisionResults}.
	 * @return The {@code int} value representing the instance ID.
	 */
	public int getInstanceId()
	{
		return instanceId;
	}
	
	/**
	 * Retrieves the unique identifier for the group name.<br>
	 * This value corresponds to the {@code name_id} attribute.
	 * @return The integer ID of the name.
	 */
	public int getNameId()
	{
		return nameId;
	}
	
	/**
	 * Retrieves the unique identifier for the title.<br>
	 * This value corresponds to the {@code title_id} attribute.
	 * @return The integer ID of the title.
	 */
	public int getTitleId()
	{
		return titleId;
	}
	
	/**
	 * Retrieves the minimum level required for this group.<br>
	 * This value is used to filter eligible players.
	 * @return The {@code int} value of the minimum level.
	 */
	public int getMinLvl()
	{
		return minLvl;
	}
	
	/**
	 * Retrieves the maximum level allowed for this group.<br>
	 * This value is stored in the {@code maxLvl} field.
	 * @return The maximum level as an {@code int}.
	 */
	public int getMaxLvl()
	{
		return maxLvl;
	}
	
	/**
	 * Checks if the quick registration feature is enabled.<br>
	 * This method returns the value of the {@code registerQuick} attribute.
	 * @return {@code true} if quick registration is active, {@code false} otherwise.
	 */
	public boolean hasRegisterQuick()
	{
		return registerQuick;
	}
	
	/**
	 * Checks if the group registration feature is enabled.<br>
	 * This method returns the value of the {@code registerGroup} attribute.
	 * @return {@code true} if the group registration is active, {@code false} otherwise.
	 */
	public boolean hasRegisterGroup()
	{
		return registerGroup;
	}
	
	/**
	 * Checks if the {@code registerNew} flag is enabled.<br>
	 * This determines if new registrations are allowed for this group.
	 * @return {@code true} if registration is enabled, {@code false} otherwise.
	 */
	public boolean hasRegisterNew()
	{
		return registerNew;
	}
	
	/**
	 * Retrieves the list of NPC identifiers for this group.<br>
	 * Returns an empty {@code List} if no IDs are defined.
	 * @return a {@code List<Integer>} containing the NPC IDs.
	 */
	public List<Integer> getNpcIds()
	{
		if (npcIds == null)
		{
			npcIds = Collections.emptyList();
		}
		
		return npcIds;
	}
}
