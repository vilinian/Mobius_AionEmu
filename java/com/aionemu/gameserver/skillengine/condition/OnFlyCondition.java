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

import com.aionemu.gameserver.model.stats.calc.Stat2;
import com.aionemu.gameserver.model.stats.calc.functions.IStatFunction;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.Skill;

/**
 * This class represents a condition that is checked while an entity is in flight.<br>
 * It allows the {@link Skill} engine to evaluate specific behaviors for flying units.
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OnFlyCondition")
public class OnFlyCondition extends Condition
{
	/**
	 * Checks if the effector of the skill is currently flying.<br>
	 * This method retrieves the {@link Skill} environment to verify the flight status.
	 * @param env The {@link Skill} object containing the current environment and targets.
	 * @return {@code true} if the effector is flying, otherwise {@code false}.
	 */
	@Override
	public boolean validate(Skill env)
	{
		return env.getEffector().isFlying();
	}
	
	/**
	 * Checks if the owner of the {@code Stat2} is currently flying.<br>
	 * This method validates the flight status for a specific statistic.
	 * @param stat The {@link Stat2} object containing the owner information.
	 * @param statFunction The {@link IStatFunction} being validated.
	 * @return {@code true} if the owner is flying, {@code false} otherwise.
	 */
	@Override
	public boolean validate(Stat2 stat, IStatFunction statFunction)
	{
		return stat.getOwner().isFlying();
	}
	
	/**
	 * Checks if the target of the {@code effect} is currently flying.<br>
	 * This method verifies the flight status of the affected entity.
	 * @param effect The {@code Effect} object to validate.
	 * @return {@code true} if the target is flying, otherwise {@code false}.
	 */
	@Override
	public boolean validate(Effect effect)
	{
		return effect.getEffected().isFlying();
	}
}
