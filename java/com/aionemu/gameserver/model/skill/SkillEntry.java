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
package com.aionemu.gameserver.model.skill;

import com.aionemu.gameserver.ai2.AbstractAI;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;

/**
 * Represents a base entry for skill data within the game server.<br>
 * It serves as an abstract model to hold core properties shared by different types of skills.<br>
 * This class is used by {@link SkillTemplate} to define how skills behave in the game world.
 * @author ATracer
 */
public abstract class SkillEntry
{
	protected final int skillId;
	protected int skillLevel;
	protected int skillAnimation;
	protected int skillAnimationEnabled;
	
	/**
	 * Creates a new instance of {@link SkillEntry}.<br>
	 * This constructor initializes the basic properties of a skill.
	 * @param skillId The unique identifier for the skill.
	 * @param skillLevel The current level of the skill.
	 * @param skillAnimation The animation ID associated with the skill.
	 * @param skillAnimationEnabled A flag indicating if the animation is active.
	 */
	SkillEntry(int skillId, int skillLevel, int skillAnimation, int skillAnimationEnabled)
	{
		this.skillId = skillId;
		this.skillLevel = skillLevel;
		this.skillAnimation = skillAnimation;
		this.skillAnimationEnabled = skillAnimationEnabled;
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
	 * Retrieves the current level of the skill.<br>
	 * This value is used to determine the power of the action performed by the {@link AbstractAI}.
	 * @return The integer level of the skill.
	 */
	public int getSkillLevel()
	{
		return skillLevel;
	}
	
	/**
	 * Retrieves the animation ID for this skill.<br>
	 * This value is used to play the correct visual effect.
	 * @return The {@code int} representing the skill animation.
	 */
	public int getSkillAnimation()
	{
		return skillAnimation;
	}
	
	/**
	 * Checks if the animation for this skill is currently enabled.<br>
	 * This value is used to determine how the skill should be displayed.
	 * @return The {@code Integer} status of the skill animation.
	 */
	public Integer getSkillAnimationEnabled()
	{
		// For Testing
		return skillAnimationEnabled;
	}
	
	/**
	 * Retrieves the display name of the skill.<br>
	 * This method looks up the {@link SkillTemplate} using the current {@code skillId}.
	 * @return The name of the skill as a {@code String}.
	 */
	public String getSkillName()
	{
		return DataManager.SKILL_DATA.getSkillTemplate(getSkillId()).getName();
	}
	
	/**
	 * Updates the {@code skillLevel} of this entry.<br>
	 * This method sets the new level to the internal variable.
	 * @param skillLevel The new level to assign to the skill.
	 */
	public void setSkillLvl(int skillLevel)
	{
		this.skillLevel = skillLevel;
	}
	
	/**
	 * Sets the animation ID for this skill.<br>
	 * This value is used to determine which visual effect plays when the skill is cast.
	 * @param skillAnimation The unique identifier for the skill animation.
	 */
	public void setSkillAnimation(int skillAnimation)
	{
		this.skillAnimation = skillAnimation;
	}
	
	/**
	 * Sets whether the animation for this skill is enabled.<br>
	 * This updates the {@code skillAnimationEnabled} field.
	 * @param skillAnimationEnabled The status of the animation as an {@code int}.
	 */
	public void setSkillAnimationEnabled(int skillAnimationEnabled)
	{
		this.skillAnimationEnabled = skillAnimationEnabled;
	}
	
	/**
	 * Retrieves the {@link SkillTemplate} associated with this skill.<br>
	 * It uses the current {@code skillId} to fetch data from the {@code DataManager}.
	 * @return The {@code SkillTemplate} object for this skill.
	 */
	public SkillTemplate getSkillTemplate()
	{
		return DataManager.SKILL_DATA.getSkillTemplate(getSkillId());
	}
}
