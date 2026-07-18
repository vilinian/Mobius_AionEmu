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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.Race;

/**
 * This class serves as a data template for hidden stigmas in the game.<br>
 * It defines the properties and attributes associated with specific player classes.<br>
 * Use this model to load stigma configurations from XML files via {@link DataManager}.
 * @author Cain
 */
@XmlRootElement(name = "PlayerClass")
@XmlAccessorType(XmlAccessType.FIELD)
public class HiddenStigmasTemplate
{
	@XmlAttribute(name = "classname", required = true)
	private String classname;
	
	@XmlElement(name = "hiddenstigma")
	private List<HiddenStigmaTemplate> hiddenStigmas;
	
	/**
	 * Retrieves the name of the class associated with this template.<br>
	 * This value is used to identify the specific character class.
	 * @return The {@code String} representing the class name.
	 */
	public String getClassname()
	{
		return classname;
	}
	
	/**
	 * Retrieves the list of hidden stigmas for this template.<br>
	 * This method returns all {@code HiddenStigmaTemplate} objects associated with the class.
	 * @return a {@code List} of {@code HiddenStigmaTemplate} objects.
	 */
	public List<HiddenStigmaTemplate> getHiddenStigmas()
	{
		return hiddenStigmas;
	}
	
	/**
	 * Retrieves the ID of a regular stigma skill for a specific race and level.<br>
	 * This method searches through {@code HiddenStigmaTemplate} objects.<br>
	 * It returns the value from the {@code DataManager} if no hidden requirement is found.
	 * @param race The {@code Race} type of the character.
	 * @param skillLevel The current level of the skill.
	 * @return The integer ID of the stigma skill, or 0 if not found.
	 */
	public int getRegularStigmaSkillId(Race race, int skillLevel)
	{
		for (HiddenStigmaTemplate hst : hiddenStigmas)
		{
			if (hst.getRequiredId() == null)
			{
				return DataManager.SKILL_TREE_DATA.getStigmaTree().get(race).get(hst.getId()).get(skillLevel);
			}
		}
		
		return 0;
	}
	
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "HiddenStigmaList")
	public static class HiddenStigmaTemplate
	{
		@XmlAttribute(name = "id", required = true)
		private String id;
		
		@XmlAttribute(name = "requiredid")
		private String requiredid;
		
		@XmlAttribute(name = "customids")
		private String customids;
		
		ArrayList<String> customStacks = new ArrayList<>();
		
		public void dataProcessing()
		{
			if (getCustomids() != null)
			{
				for (String stack : getCustomids().split(","))
				{
					customStacks.add(stack);
				}
			}
		}
		
		public ArrayList<String> getCustomStacks()
		{
			return customStacks;
		}
		
		public String getId()
		{
			return id;
		}
		
		public String getCustomids()
		{
			return customids;
		}
		
		public String getRequiredId()
		{
			return requiredid;
		}
	}
	
}
