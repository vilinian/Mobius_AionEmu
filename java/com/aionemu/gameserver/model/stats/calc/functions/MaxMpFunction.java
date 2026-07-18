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
 * This class handles the calculation logic for the maximum MP stat.<br>
 * It is used by {@link Stat2} to determine how much mana a character can hold.
 */
class MaxMpFunction extends StatFunction
{
	/**
	 * This function handles the calculation for {@code MAXMP}.<br>
	 * It sets the target stat to {@code MAXMP}.
	 */
	MaxMpFunction()
	{
		stat = StatEnum.MAXMP;
	}
	
	/**
	 * Updates the base value of a {@link Stat2} object.<br>
	 * This method calculates a new value based on the owner's will.<br>
	 * It uses the internal modifier to adjust the result.
	 * @param stat The {@code Stat2} object to be updated.
	 */
	@Override
	public void apply(Stat2 stat)
	{
		final float will = stat.getOwner().getGameStats().getWill().getCurrent();
		stat.setBase(Math.round((stat.getBase() * will) / 100f));
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
