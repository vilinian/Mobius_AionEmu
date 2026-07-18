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
package com.aionemu.gameserver.model.templates.item;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents the data model for item skill enhancement properties.<br>
 * This class defines how skills are modified or granted when enhancing an item.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ItemSkillEnhance")
public class ItemSkillEnhance
{
	@XmlAttribute(name = "id")
	protected int id;
	@XmlAttribute(name = "skill_id")
	protected List<Integer> skillId;
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Retrieves the list of skill IDs associated with this item enhancement.<br>
	 * If no skills are assigned, it returns an empty {@code List}
	 * @return a {@code List<Integer>} containing the skill IDs.
	 */
	public List<Integer> getSkillId()
	{
		if (skillId == null)
		{
			skillId = new ArrayList<>();
		}
		
		return skillId;
	}
}
