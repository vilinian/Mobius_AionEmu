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
package com.aionemu.gameserver.model.ai;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents a numerical value expressed as a percentage.<br>
 * This class is used to handle and calculate proportions within the AI system.
 * @author xTz
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Percentage")
public class Percentage
{
	@XmlAttribute(name = "percent")
	protected int percent;
	@XmlAttribute(name = "skillId")
	protected int skillId = 0;
	@XmlAttribute(name = "isIndividual")
	protected boolean isIndividual = false;
	@XmlElement(name = "summonGroup")
	protected List<SummonGroup> summons;
	
	/**
	 * Retrieves the list of summon groups.<br>
	 * This method returns all {@link SummonGroup} objects associated with this percentage.
	 * @return a {@code List} of {@link SummonGroup} objects.
	 */
	public List<SummonGroup> getSummons()
	{
		return summons;
	}
	
	/**
	 * Retrieves the current percentage value.<br>
	 * This method returns the {@code percent} field as an {@code int}.
	 * @return The integer value of the percentage.
	 */
	public int getPercent()
	{
		return percent;
	}
	
	/**
	 * Retrieves the unique identifier for the skill associated with this AI.
	 * @return The {@code int} value of the skill ID.
	 */
	public int getSkillId()
	{
		return skillId;
	}
	
	/**
	 * Checks if the percentage applies to an individual entity.<br>
	 * This method returns {@code true} if it is an individual value.<br>
	 * It returns {@code false} otherwise.
	 * @return The status of whether the value is individual.
	 */
	public boolean isIndividual()
	{
		return isIndividual;
	}
}
