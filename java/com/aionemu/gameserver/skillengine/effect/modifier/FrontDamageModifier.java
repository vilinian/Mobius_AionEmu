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
package com.aionemu.gameserver.skillengine.effect.modifier;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.utils.PositionUtil;

/**
 * This class modifies the damage dealt by a skill based on the target's position.<br>
 * It specifically handles calculations for attacks directed at the front of an entity.<br>
 * It extends {@link ActionModifier} to provide specific logic for frontal damage scaling.
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FrontDamageModifier")
public class FrontDamageModifier extends ActionModifier
{
	/**
	 * This method calculates the modified value of an {@link Effect}.<br>
	 * It takes the base value and adds a bonus based on the skill level.
	 * @param effect The {@code Effect} object to be analyzed.
	 * @return The final calculated integer value.
	 */
	@Override
	public int analyze(Effect effect)
	{
		return value + (effect.getSkillLevel() * delta);
	}
	
	/**
	 * Checks if the {@code Effect} occurs in front of the target.<br>
	 * It uses {@code Object)} to verify the positions.
	 * @param effect The {@code Effect} to be checked.
	 * @return {@code true} if the effector is in front of the effected target, otherwise {@code false}.
	 */
	@Override
	public boolean check(Effect effect)
	{
		return PositionUtil.isInFrontOfTarget(effect.getEffector(), effect.getEffected());
	}
}
