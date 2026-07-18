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
package com.aionemu.gameserver.model.npcdrops;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import com.aionemu.gameserver.model.Race;

/**
 * Represents a group of items that can be dropped by an NPC as defined in the XML configuration.<br>
 * This class maps the data structure used to handle loot tables and drop probabilities.
 * @author Falke_34
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlDropGroup
{
	@XmlElement(name = "drop")
	protected List<XmlDrop> drop;
	@XmlAttribute
	protected Race race = Race.PC_ALL;
	@XmlAttribute(name = "use_category")
	protected Boolean useCategory = Boolean.valueOf(true);
	@XmlAttribute(name = "name")
	protected String group_name;
	
	/**
	 * Retrieves the list of drops associated with this group.<br>
	 * This method returns the {@code drop} field from the current object.
	 * @return a {@link List} of {@link XmlDrop} objects.
	 */
	public List<XmlDrop> getDrop()
	{
		return drop;
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
	 * Checks if the category filter is currently enabled.<br>
	 * This determines how drops are grouped during calculation.
	 * @return {@code true} if categories are used, {@code false} otherwise.
	 */
	public Boolean isUseCategory()
	{
		return useCategory;
	}
	
	/**
	 * Retrieves the name of the drop group.<br>
	 * Returns an empty {@code String} if the name is {@code null}.
	 * @return The name of the group as a {@code String}.
	 */
	public String getGroupName()
	{
		if (group_name == null)
		{
			return "";
		}
		
		return group_name;
	}
}
