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
import com.aionemu.gameserver.model.stats.calc.StatCondition;
import com.aionemu.gameserver.model.stats.calc.functions.IStatFunction;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.Skill;

/**
 * Represents a base condition used to evaluate skill effects.<br>
 * This class provides the foundation for checking specific requirements or states within the {@link Skill} engine.
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Condition")
public abstract class Condition implements StatCondition
{
	/**
	 * Validate condition specified in template
	 * @param env
	 * @return true or false
	 */
	public abstract boolean validate(Skill env);
	
	/**
	 * Checks if the current {@link IStatFunction} is valid for a specific statistic.<br>
	 * It evaluates the conditions attached to the function.<br>
	 * If no conditions exist, it returns {@code true}.
	 * @param stat The {@code Stat2} object to check against.
	 * @param statFunction The {@link IStatFunction} being validated.
	 * @return {@code true} if the conditions are met or missing, {@code false} otherwise.
	 */
	@Override
	public boolean validate(Stat2 stat, IStatFunction statFunction)
	{
		return true;
	}
	
	/**
	 * Validates the provided {@code effect}.<br>
	 * This method currently always returns {@code true}.
	 * @param effect The {@code Effect} object to validate.
	 * @return {@code true} if the validation passes.
	 */
	public boolean validate(Effect effect)
	{
		return true;
	}
}
