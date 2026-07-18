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
package com.aionemu.gameserver.model.templates.teleport;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.templates.base.BaseTemplate;

/**
 * This class represents a list of locations associated with a teleport scroll.<br>
 * It is used to define where a player can be transported when using the item.
 */
@XmlType(name = "ScrollItemLocationList")
public class ScrollItemLocationList
{
	@XmlAttribute(name = "worldid")
	protected int worldid;
	@XmlAttribute(name = "desc")
	protected String desc;
	
	/**
	 * Retrieves the unique identifier for the world.<br>
	 * This value is fetched from the associated {@link BaseTemplate}.
	 * @return The {@code int} ID of the world.
	 */
	public int getWorldId()
	{
		return worldid;
	}
	
	/**
	 * Retrieves the descriptive text for this title.<br>
	 * This corresponds to the {@code desc} attribute in the XML configuration.
	 * @return The description string or {@code null} if no description is provided.
	 */
	public String getDesc()
	{
		return desc;
	}
}
