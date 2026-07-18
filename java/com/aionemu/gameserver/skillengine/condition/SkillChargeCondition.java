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
package com.aionemu.gameserver.skillengine.condition;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.model.ChargeSkillEntry;
import com.aionemu.gameserver.skillengine.model.ChargedSkill;
import com.aionemu.gameserver.skillengine.model.Skill;

/**
 * This class handles conditions related to the charging state of a skill.<br>
 * It determines if a {@link Skill} can be charged or is currently in a charging state.<br>
 * It extends {@link ChargeCondition} to provide specific logic for {@link ChargedSkill} entries.
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SkillChargeCondition")
public class SkillChargeCondition extends ChargeCondition
{
	/**
	 * Validates the condition and updates the cast time for a {@link Player}.<br>
	 * This method adds skills to the environment based on the current {@code value}.<br>
	 * It calculates the total duration from all charged skills.
	 * @param env The {@link Skill} object containing the current environment and targets.
	 * @return Always returns {@code true}.
	 */
	@Override
	public boolean validate(Skill env)
	{
		int castTime = 0;
		if (env.getEffector() instanceof Player)
		{
			final ChargeSkillEntry skillCharge = DataManager.SKILL_CHARGE_DATA.getChargedSkillEntry(value);
			env.getChargeSkillList().addAll(skillCharge.getSkills());
			for (ChargedSkill skill : env.getChargeSkillList())
			{
				castTime += skill.getTime();
			}
			
			env.setDuration(castTime);
		}
		
		return true;
	}
	
	/**
	 * Retrieves the current numerical value.<br>
	 * This method returns the {@code int} stored in the internal variable.
	 * @return The current value.
	 */
	public int getValue()
	{
		return value;
	}
}
