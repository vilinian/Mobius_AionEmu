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
package com.aionemu.gameserver.model.stats.calc.functions;

import com.aionemu.gameserver.model.stats.calc.Stat2;
import com.aionemu.gameserver.model.stats.container.StatEnum;

/**
 * This class handles the calculation logic for {@code PHYSICAL_ATTACK}.<br>
 * It determines how physical attack values are derived from character statistics.<br>
 * Use this class to implement specific modifiers or formulas related to physical damage.
 */
class PhysicalAttackFunction extends StatFunction
{
	/**
	 * Initializes the calculation for {@code PHYSICAL_ATTACK}.<br>
	 * This method sets the target stat to {@code PHYSICAL_ATTACK}.
	 */
	PhysicalAttackFunction()
	{
		stat = StatEnum.PHYSICAL_ATTACK;
	}
	
	/**
	 * Updates the base value of a {@link Stat2} object.<br>
	 * This method calculates a new value based on the owner's power.<br>
	 * It uses the current power percentage to adjust the result.
	 * @param stat The {@code Stat2} object to be updated.
	 */
	@Override
	public void apply(Stat2 stat)
	{
		final float power = stat.getOwner().getGameStats().getPower().getCurrent();
		stat.setBase(Math.round((stat.getBase() * power) / 100f));
	}
	
	/**
	 * Returns the execution priority of this function.<br>
	 * This value determines the order in which functions are applied.<br>
	 * The current priority is set to {@code 30}.
	 * @return The integer priority level.
	 */
	@Override
	public int getPriority()
	{
		return 30;
	}
}
