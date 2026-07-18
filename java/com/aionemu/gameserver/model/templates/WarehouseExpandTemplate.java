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
package com.aionemu.gameserver.model.templates;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.model.templates.expand.Expand;
import com.aionemu.gameserver.utils.Util;

/**
 * This class defines the data structure for warehouse expansion templates.<br>
 * It maps XML configuration to {@link Expand} objects used by warehouse NPCs.
 * @author Simple
 */
@XmlRootElement(name = "warehouse_npc")
@XmlAccessorType(XmlAccessType.FIELD)
public class WarehouseExpandTemplate
{
	@XmlElement(name = "expand", required = true)
	protected List<Expand> warehouseExpands;
	/**
	 * NPC ID
	 */
	@XmlAttribute(name = "id", required = true)
	protected int id;
	/**
	 * NPC name
	 */
	@XmlAttribute(name = "name", required = true)
	protected String name = "";
	
	/**
	 * Retrieves the unique identifier for this NPC.<br>
	 * This value is stored in the {@code id} field.
	 * @return The integer ID of the NPC.
	 */
	public int getNpcId()
	{
		return id;
	}
	
	/**
	 * Retrieves the list of expansions for the warehouse.<br>
	 * This method returns all {@link Expand} objects associated with this template.
	 * @return A {@code List} of {@code Expand} objects.
	 */
	public List<Expand> getWarehouseExpand()
	{
		return warehouseExpands;
	}
	
	/**
	 * Retrieves the name of the warehouse NPC.<br>
	 * This method returns the {@code String`name`} associated with this object.
	 * @return The name of the warehouse NPC as a {@code String}.
	 */
	public String getName()
	{
		return Util.convertName(name);
	}
	
	/**
	 * Checks if a specific expansion level exists in the warehouse.<br>
	 * It iterates through all available {@link Expand} objects.
	 * @param level The integer level to search for.
	 * @return {@code true} if the level is found, otherwise {@code false}.
	 */
	public boolean contains(int level)
	{
		for (Expand expand : warehouseExpands)
		{
			if (expand.getLevel() == level)
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Retrieves an {@link Expand} object based on a specific level.<br>
	 * It searches through the list of warehouse expansions.<br>
	 * If no match is found, it returns {@code null}.
	 * @param level The level to search for.
	 * @return The {@code Expand} object at the given level, or {@code null}.
	 */
	public Expand get(int level)
	{
		for (Expand expand : warehouseExpands)
		{
			if (expand.getLevel() == level)
			{
				return expand;
			}
		}
		
		return null;
	}
}
