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
 * This class handles the calculation of modifiers for the {@code AGILITY} stat.<br>
 * It provides logic to determine how agility values are adjusted during stat processing.<br>
 * It extends the base {@link StatFunction} class.
 */
class AgilityModifierFunction extends StatFunction
{
	private final float modifier;
	
	/**
	 * Creates a new instance of {@code AgilityModifierFunction}.<br>
	 * This function applies a specific numerical change to an agility-related statistic.<br>
	 * It is used during the calculation process of {@link Stat2}.
	 * @param stat The {@code StatEnum} type that this modifier will affect.
	 * @param modifier The float value to be added or subtracted from the stat.
	 */
	AgilityModifierFunction(StatEnum stat, float modifier)
	{
		this.stat = stat;
		this.modifier = modifier;
	}
	
	/**
	 * Updates the base value of a {@link Stat2} object.<br>
	 * This method calculates a new value based on the owner's agility.<br>
	 * It uses the internal modifier to adjust the result.
	 * @param stat The {@code Stat2} object to be updated.
	 */
	@Override
	public void apply(Stat2 stat)
	{
		final float agility = stat.getOwner().getGameStats().getAgility().getCurrent();
		stat.setBase(Math.round(stat.getBase() + ((stat.getBase() * (agility - 100) * modifier) / 100f)));
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
