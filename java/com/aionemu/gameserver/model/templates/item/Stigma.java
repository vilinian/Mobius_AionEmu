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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a item that functions as a stigma.<br>
 * This class stores the data and properties for specific stigma types used within the game world.
 * @author ATracer
 * @reworked Kill3r
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "Stigma")
public class Stigma
{
	@XmlElement(name = "require_skill")
	protected List<RequireSkill> requireSkill;
	@XmlAttribute
	protected List<String> skill;
	@XmlAttribute
	protected int kinah;
	
	/**
	 * Retrieves the list of skills associated with this {@link Stigma}.<br>
	 * It parses the raw skill strings into {@code StigmaSkill} objects.
	 * @return a {@code List} of {@code StigmaSkill} objects.
	 */
	public List<StigmaSkill> getSkills()
	{
		final List<StigmaSkill> list = new ArrayList<>();
		for (String st : skill)
		{
			final String[] array = st.split(":");
			list.add(new StigmaSkill(Integer.parseInt(array[0]), Integer.parseInt(array[1])));
		}
		
		return list;
	}
	
	/**
	 * Retrieves a list of skill IDs from the {@code skill} attribute.<br>
	 * This method parses strings to extract only the numeric ID part.<br>
	 * It handles both single and multiple skills correctly.
	 * @return A {@code List<Integer>} containing the extracted skill IDs.
	 */
	public List<Integer> getSkillIdOnly()
	{
		final List<Integer> ids = new ArrayList<>();
		final List<String> skill = this.skill;
		if (skill.size() != 1)
		{
			// Dual Skills like Exhausting Wave
			String[] tempArray = new String[0];
			for (String parts : skill)
			{
				// loops each of the 1:534 and 1:4342
				tempArray = parts.split(":");
				ids.add(Integer.parseInt(tempArray[1]));
			}
			
			return ids;
		}
		
		// Single 1 Skill
		for (String st : this.skill)
		{
			final String[] array = st.split(":");
			ids.add(Integer.parseInt(array[1]));
		}
		
		return ids;
	}
	
	/**
	 * Retrieves the amount of {@code kinah} required for this stigma.<br>
	 * This value is stored in the {@code kinah} field.
	 * @return The total {@code kinah} cost as an {@code int}.
	 */
	public int getKinah()
	{
		return kinah;
	}
	
	/**
	 * Retrieves the list of skills required for this {@link Stigma}.<br>
	 * If no requirements exist, it returns an empty {@code List}.
	 * @return a {@code List} of {@link RequireSkill} objects.
	 */
	public List<RequireSkill> getRequireSkill()
	{
		if (requireSkill == null)
		{
			requireSkill = new ArrayList<>();
		}
		
		return requireSkill;
	}
	
	public static class StigmaSkill
	{
		private final int skillId;
		private final int skillLvl;
		
		public StigmaSkill(int skillLvl, int skillId)
		{
			this.skillId = skillId;
			this.skillLvl = skillLvl;
		}
		
		public int getSkillLvl()
		{
			return skillLvl;
		}
		
		public int getSkillId()
		{
			return skillId;
		}
	}
}
