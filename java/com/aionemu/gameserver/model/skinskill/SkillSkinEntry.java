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
package com.aionemu.gameserver.model.skinskill;

import com.aionemu.gameserver.ai2.AbstractAI;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.templates.SkillSkinTemplate;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;

/**
 * Represents an individual instance of a {@link SkillSkinTemplate} within the game world.<br>
 * This class serves as a data model for tracking specific skill skin properties and states.
 * @author Ghostfur (Aion-Unique)
 */
public abstract class SkillSkinEntry
{
	
	protected final int skinId;
	protected int skillLevel;
	
	/**
	 * Creates a new instance of {@link SkillSkinEntry}.<br>
	 * This constructor initializes the basic properties for a skill skin.
	 * @param skinId The unique identifier for the skin.
	 * @param skillLevel The level associated with this skill skin.
	 */
	SkillSkinEntry(int skinId, int skillLevel)
	{
		this.skinId = skinId;
		this.skillLevel = skillLevel;
	}
	
	/**
	 * Retrieves the unique identifier for the current skin.<br>
	 * This value corresponds to the {@code skinId} field.
	 * @return The integer ID of the skill skin.
	 */
	public int getSkinId()
	{
		return skinId;
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
	 * Retrieves the display name of the skill.<br>
	 * This method looks up the {@link SkillTemplate} using the current {@code skillId}.
	 * @return The name of the skill as a {@code String}.
	 */
	public String getSkillName()
	{
		return DataManager.SKILL_SKIN_DATA.getSkillSkinTemplate(getSkinId()).getName();
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
	 * Retrieves the {@link SkillSkinTemplate} for this skill skin.<br>
	 * It uses the current {@code skinId} to look up the data.
	 * @return The {@code SkillSkinTemplate} associated with this entry.
	 */
	public SkillSkinTemplate getSkillSkinTemplate()
	{
		return DataManager.SKILL_SKIN_DATA.getSkillSkinTemplate(getSkinId());
	}
}
