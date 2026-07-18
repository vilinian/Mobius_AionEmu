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

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.stats.calc.Stat2;
import com.aionemu.gameserver.model.stats.calc.StatOwner;
import com.aionemu.gameserver.model.stats.calc.functions.IStatFunction;
import com.aionemu.gameserver.skillengine.model.Skill;

/**
 * This class checks if a specific {@link Item} has been charged.<br>
 * It is used as a condition within the skill engine to verify item states.
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ItemChargeCondition")
public class ItemChargeCondition extends ChargeCondition
{
	/**
	 * Checks if an {@link Item} has enough charge.<br>
	 * This method verifies the condition against a specific {@code IStatFunction}.<br>
	 * It returns {@code true} if the item meets the required level.
	 * @param env The environment context for the calculation.
	 * @param statFunction The function providing the owner of the statistic.
	 * @return {@code true} if the condition is met, otherwise {@code false}.
	 */
	@Override
	public boolean validate(Stat2 env, IStatFunction statFunction)
	{
		final StatOwner owner = statFunction.getOwner();
		if (owner instanceof Item)
		{
			final Item item = (Item) owner;
			return item.getChargeLevel() >= value;
		}
		
		return false;
	}
	
	/**
	 * Validates the condition based on the provided environment.<br>
	 * This method currently always returns {@code false}.
	 * @param env The {@link Skill} object containing the current environment and targets.
	 * @return {@code false} for all inputs.
	 */
	@Override
	public boolean validate(Skill env)
	{
		return false;
	}
}
