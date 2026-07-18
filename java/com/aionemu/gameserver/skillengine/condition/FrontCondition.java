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

import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.utils.PositionUtil;

/**
 * Checks if a target is positioned in front of the caster.<br>
 * This condition is used by the {@link Skill} engine to validate spatial requirements.
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FrontCondition")
public class FrontCondition extends Condition
{
	/**
	 * Checks if the effector is positioned in front of the first target.<br>
	 * This method verifies the spatial relationship between the {@code effector} and the {@code firstTarget}.<br>
	 * It returns {@code false} if either the target or the effector is {@code null}.
	 * @param env The {@link Skill} object containing the current environment and targets.
	 * @return {@code true} if the condition is met, otherwise {@code false}.
	 */
	@Override
	public boolean validate(Skill env)
	{
		if ((env.getFirstTarget() == null) || (env.getEffector() == null))
		{
			return false;
		}
		
		return PositionUtil.isInFrontOfTarget(env.getEffector(), env.getFirstTarget());
	}
	
	/**
	 * Checks if the {@code effect} is valid based on position.<br>
	 * It verifies that both the effector and the effected target are not {@code null}.<br>
	 * It checks if the effector is positioned in front of the target using {@code Skill)}.
	 * @param effect The {@code Effect} object to validate.
	 * @return {@code true} if the position is valid, otherwise {@code false}.
	 */
	@Override
	public boolean validate(Effect effect)
	{
		if ((effect.getEffected() == null) || (effect.getEffector() == null))
		{
			return false;
		}
		
		return PositionUtil.isInFrontOfTarget(effect.getEffector(), effect.getEffected());
	}
}
