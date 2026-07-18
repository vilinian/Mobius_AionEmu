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
import javax.xml.bind.annotation.XmlType;

/**
 * Represents a skill requirement for an item.<br>
 * This class defines the specific skills and levels needed to use or equip an item.<br>
 * It is used by item to validate player actions.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RequireSkill")
public class RequireSkill
{
	@XmlAttribute
	protected List<Integer> skillIds;
	
	/**
	 * Retrieves the list of required skill identifiers.<br>
	 * This method ensures a non-null {@code List<Integer>} is returned.
	 * @return a {@code List<Integer>} containing all skill IDs.
	 */
	public List<Integer> getSkillIds()
	{
		if (skillIds == null)
		{
			skillIds = new ArrayList<>();
		}
		
		return skillIds;
	}
}
