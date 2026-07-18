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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.FlyingRestriction;
import com.aionemu.gameserver.skillengine.model.Skill;

/**
 * Checks if the target of a skill is currently in a flying state.<br>
 * This condition is used to determine if a {@link Skill} can be applied based on the target's movement status.
 * @author Sippolo
 * @author kecimis
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TargetFlyingCondition")
public class TargetFlyingCondition extends Condition
{
	@XmlAttribute(required = true)
	protected FlyingRestriction restriction = FlyingRestriction.FLY;
	
	/**
	 * Checks if the target's flying status matches the required restriction.<br>
	 * This method verifies the first target of the {@link Skill}.<br>
	 * It returns {@code false} if there is no target.
	 * @param env The {@link Skill} object containing the current environment and targets.
	 * @return {@code true} if the condition is met, otherwise {@code false}.
	 */
	@Override
	public boolean validate(Skill env)
	{
		if (env.getFirstTarget() == null)
		{
			return false;
		}
		
		switch (restriction)
		{
			case FLY:
				return env.getFirstTarget().isFlying();
			case GROUND:
				return !env.getFirstTarget().isFlying();
			default:
				break;
		}
		
		return true;
	}
	
	/**
	 * Checks if the {@code effect} meets the flying restrictions.<br>
	 * It verifies whether the target is currently flying or on the ground.
	 * @param effect The {@code Effect} object to validate.
	 * @return {@code true} if the condition is met, otherwise {@code false}.
	 */
	@Override
	public boolean validate(Effect effect)
	{
		if (effect.getEffected() == null)
		{
			return false;
		}
		
		switch (restriction)
		{
			case FLY:
				return effect.getEffected().isFlying();
			case GROUND:
				return !effect.getEffected().isFlying();
			default:
				break;
		}
		
		return true;
	}
}
