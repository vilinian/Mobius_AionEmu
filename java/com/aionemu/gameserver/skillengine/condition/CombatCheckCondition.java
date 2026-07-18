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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.model.Skill;

/**
 * This class handles conditions that require a combat check to be performed.<br>
 * It is used by the {@link Skill} engine to validate if specific combat requirements are met.
 * @author nrg
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CombatCheckCondition")
public class CombatCheckCondition extends Condition
{
	/**
	 * Checks if the provided {@link Skill} meets specific combat requirements.<br>
	 * It ensures that a {@code Player} effector is not currently in combat.
	 * @param skill The {@code Skill} object to be validated.
	 * @return {@code true} if the skill is valid, otherwise {@code false}.
	 */
	@Override
	public boolean validate(Skill skill)
	{
		if (skill.getEffector() instanceof Player)
		{
			return !((Player) skill.getEffector()).getController().isInCombat();
		}
		
		return true;
	}
}
