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

/**
 * Checks if the target character is currently in a flying state.<br>
 * This condition returns {@code true} if the character is not flying.<br>
 * It is used by the {@link Skill} engine to restrict actions based on movement status.
 * @author Sippolo
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NoFlyingCondition")
public class NoFlyingCondition extends Condition
{
	/**
	 * Checks if the effector is not currently flying.<br>
	 * This method verifies the flight status of the {@code env} effector.<br>
	 * It returns {@code true} if the effector is on the ground.
	 * @param env The {@link Skill} object containing the current environment and targets.
	 * @return {@code true} if the condition is met, otherwise {@code false}.
	 */
	@Override
	public boolean validate(Skill env)
	{
		return (!env.getEffector().isFlying());
	}
	
	/**
	 * Checks if the target of the {@code effect} is not flying.<br>
	 * It returns {@code true} if the target is on the ground.
	 * @param effect The {@code Effect} object to validate.
	 * @return {@code true} if the condition is met, otherwise {@code false}.
	 */
	@Override
	public boolean validate(Effect effect)
	{
		return (!effect.getEffected().isFlying());
	}
}
