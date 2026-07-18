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

import com.aionemu.gameserver.model.stats.container.StatEnum;

/**
 * This class calculates the casting time boost for a character.<br>
 * It handles the logic for applying various modifiers to the base casting speed.<br>
 * It extends {@link DuplicateStatFunction} to manage stat calculations.
 */
class BoostCastingTimeFunction extends DuplicateStatFunction
{
	/**
	 * Initializes a function to calculate the {@code BOOST_CASTING_TIME} stat.<br>
	 * This class handles how casting time bonuses are applied to characters.<br>
	 * It extends {@link DuplicateStatFunction}.
	 */
	BoostCastingTimeFunction()
	{
		stat = StatEnum.BOOST_CASTING_TIME;
	}
}
