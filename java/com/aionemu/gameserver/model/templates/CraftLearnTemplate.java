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

/**
 * Represents the template data for learning a new crafting skill.<br>
 * This class stores the requirements and properties needed to unlock specific crafts.
 */
public class CraftLearnTemplate
{
	private final int skillId;
	private final boolean isCraftSkill;
	
	/**
	 * Checks if the current template represents a crafting skill.<br>
	 * This method returns {@code true} for crafting skills.<br>
	 * It returns {@code false} for all other types of skills.
	 * @return {@code true} if it is a craft skill, otherwise {@code false}.
	 */
	public boolean isCraftSkill()
	{
		return isCraftSkill;
	}
	
	/**
	 * Creates a new instance of {@link CraftLearnTemplate}.<br>
	 * This constructor initializes the skill data.
	 * @param skillId The unique identifier for the skill.
	 * @param isCraftSkill Set to {@code true} if it is a crafting skill, otherwise {@code false}.
	 * @param skillName The display name of the skill.
	 */
	public CraftLearnTemplate(int skillId, boolean isCraftSkill, String skillName)
	{
		this.skillId = skillId;
		this.isCraftSkill = isCraftSkill;
	}
	
	/**
	 * Retrieves the unique identifier for the skill associated with this AI.
	 * @return The {@code int} value of the skill ID.
	 */
	public int getSkillId()
	{
		return skillId;
	}
}
