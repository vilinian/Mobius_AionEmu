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
package com.aionemu.gameserver.model.templates.achievement;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents a specific action required to complete an achievement.<br>
 * This class defines the criteria or triggers that must be met for a player to earn an award.
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlType(name = "AchievementAction")
public class AchievementAction
{
	@XmlAttribute(name = "ids")
	protected List<Integer> ids;
	
	/**
	 * Retrieves the list of unique identifiers for this action.<br>
	 * If no IDs exist, it returns an empty {@code List}.
	 * @return a {@code List<Integer>} containing the IDs.
	 */
	public List<Integer> getIds()
	{
		if (ids == null)
		{
			ids = new ArrayList<>();
		}
		
		return ids;
	}
}
