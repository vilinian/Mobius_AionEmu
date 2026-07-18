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
 * Calculates the magical defense ratio specifically for {@code PvP} combat.<br>
 * This function determines how much magical defense is applied during player versus player encounters.
 */
public class PvPMagicalDefendRatioFunction extends DuplicateStatFunction
{
	/**
	 * Initializes the function for calculating the magical defense ratio in PvP.<br>
	 * This method sets the target stat to {@code StatEnum.PVP_DEFEND_RATIO_MAGICAL}.
	 */
	PvPMagicalDefendRatioFunction()
	{
		stat = StatEnum.PVP_DEFEND_RATIO_MAGICAL;
	}
}
