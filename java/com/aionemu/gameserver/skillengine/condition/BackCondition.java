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
 * This class checks if a target is facing away from the attacker.<br>
 * It is used by the {@link Skill} engine to determine if a skill hits from behind.
 * @author kecimis
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BackCondition")
public class BackCondition extends Condition
{
	/**
	 * Checks if the effector is positioned behind the first target.<br>
	 * This method returns {@code false} if either the effector or the first target is {@code null}.<br>
	 * It uses {@code Skill)} to perform the check.
	 * @param env The {@link Skill} object containing the current environment and targets.
	 * @return {@code true} if the effector is behind the target, otherwise {@code false}.
	 */
	@Override
	public boolean validate(Skill env)
	{
		if ((env.getFirstTarget() == null) || (env.getEffector() == null))
		{
			return false;
		}
		
		return PositionUtil.isBehindTarget(env.getEffector(), env.getFirstTarget());
	}
	
	/**
	 * Checks if the {@code effect} occurs from behind the target.<br>
	 * It verifies that both the effector and the effected entity are not {@code null}.<br>
	 * It uses {@code Skill)} to check positions.
	 * @param effect The {@code Effect} object to validate.
	 * @return {@code true} if the effector is behind the target, otherwise {@code false}.
	 */
	@Override
	public boolean validate(Effect effect)
	{
		if ((effect.getEffected() == null) || (effect.getEffector() == null))
		{
			return false;
		}
		
		return PositionUtil.isBehindTarget(effect.getEffector(), effect.getEffected());
	}
}
