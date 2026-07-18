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
 * This class handles damage modifications applied when an attack hits a target from behind.<br>
 * It allows the skill engine to calculate increased damage based on the relative position of the attacker and the victim.
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BackDamageModifier")
public class BackDamageModifier extends ActionModifier
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
	 * Checks if the effector is positioned behind the target.<br>
	 * It uses {@code Object)} to verify the locations.
	 * @param effect The {@code Effect} containing the effector and target positions.
	 * @return {@code true} if the effector is behind the target, otherwise {@code false}.
	 */
	@Override
	public boolean check(Effect effect)
	{
		return PositionUtil.isBehindTarget(effect.getEffector(), effect.getEffected());
	}
}
