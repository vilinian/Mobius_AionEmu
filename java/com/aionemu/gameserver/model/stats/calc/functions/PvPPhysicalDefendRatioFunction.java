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
 * Calculates the physical defense ratio specifically for {@code PvP} combat.<br>
 * This function determines how much physical damage is mitigated based on player stats.<br>
 * It extends {@link DuplicateStatFunction} to handle specific calculation logic.
 */
public class PvPPhysicalDefendRatioFunction extends DuplicateStatFunction
{
	/**
	 * This function calculates the physical defense ratio for PvP.<br>
	 * It targets the {@code StatEnum.PVP_DEFEND_RATIO_PHYSICAL} stat.
	 */
	PvPPhysicalDefendRatioFunction()
	{
		stat = StatEnum.PVP_DEFEND_RATIO_PHYSICAL;
	}
}
