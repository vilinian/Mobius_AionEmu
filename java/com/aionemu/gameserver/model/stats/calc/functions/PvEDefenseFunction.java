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
 * This class handles the calculation logic for {@code PvEDefense} stats.<br>
 * It determines how defense values are applied during player vs environment encounters.<br>
 * It extends {@link DuplicateStatFunction} to manage specific stat behaviors.
 */
class PvEDefenseFunction extends DuplicateStatFunction
{
	/**
	 * Initializes the function for {@code PVE_DEFENSE}.<br>
	 * This method sets the target stat to PVE Defense.
	 */
	PvEDefenseFunction()
	{
		stat = StatEnum.PVE_DEFENSE;
	}
}
