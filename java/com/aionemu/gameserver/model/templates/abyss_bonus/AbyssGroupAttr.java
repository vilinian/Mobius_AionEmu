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
package com.aionemu.gameserver.model.templates.abyss_bonus;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents the attributes associated with an abyss group.<br>
 * This class stores configuration data for specific bonus effects within the game world.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbyssGroupAttr")
public class AbyssGroupAttr
{
	@XmlAttribute(name = "buff_id", required = true)
	protected int buffId;
	
	@XmlAttribute(name = "world")
	protected List<Integer> world;
	
	@XmlAttribute(name = "name", required = true)
	private String name;
	
	/**
	 * Retrieves the unique buffer identifier.<br>
	 * This value is stored in the {@code buffId} field.
	 * @return The integer ID of the buff.
	 */
	public int getBuffId()
	{
		return buffId;
	}
	
	/**
	 * Sets the unique identifier for the buff.<br>
	 * This updates the {@code buffId} field in this object.
	 * @param value The new integer ID to assign to the buff.
	 */
	public void setBuffId(int value)
	{
		buffId = value;
	}
	
	/**
	 * Retrieves the name of the bookmark.<br>
	 * This method returns the {@code String`name`} associated with this object.
	 * @return The name of the bookmark as a {@code String}.
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Retrieves the list of world identifiers associated with this attribute.<br>
	 * If no worlds are defined, it returns an empty {@code List}.
	 * @return a {@code List<Integer>} containing the world IDs.
	 */
	public List<Integer> getWorldId()
	{
		if (world == null)
		{
			world = Collections.emptyList();
		}
		
		return world;
	}
}
