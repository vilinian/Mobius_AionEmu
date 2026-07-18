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
 * This class handles the calculation logic for Physical Defense stats.<br>
 * It determines how physical damage is mitigated based on character attributes.<br>
 * It extends {@link StatFunction} to provide specific defense formulas.
 */
class PDefFunction extends StatFunction
{
	/**
	 * Initializes a new {@link PDefFunction}.<br>
	 * This function targets the {@code StatEnum.PHYSICAL_DEFENSE} stat.<br>
	 * It is used to calculate physical defense values.
	 */
	PDefFunction()
	{
		stat = StatEnum.PHYSICAL_DEFENSE;
	}
	
	/**
	 * Adjusts the bonus value of a {@link Stat2} object based on flight status.<br>
	 * This method reduces the bonus if the owner is currently in a flying state.<br>
	 * The reduction amount is half of the current base value.
	 * @param stat The {@code Stat2} object to be updated.
	 */
	@Override
	public void apply(Stat2 stat)
	{
		if (stat.getOwner().isInFlyingState())
		{
			stat.setBonus(stat.getBonus() - (stat.getBase() / 2));
		}
	}
	
	/**
	 * Returns the execution priority of this function.<br>
	 * This value determines the order in which functions are applied.<br>
	 * The current priority is set to {@code 60}.
	 * @return The integer priority level.
	 */
	@Override
	public int getPriority()
	{
		return 60;
	}
}
