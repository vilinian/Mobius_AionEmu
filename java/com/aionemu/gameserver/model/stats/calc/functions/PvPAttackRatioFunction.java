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
 * This class calculates the {@code PvPAttackRatio} statistic.<br>
 * It handles the logic for determining how much a character's attack power is scaled during player versus player combat.
 */
class PvPAttackRatioFunction extends DuplicateStatFunction
{
	/**
	 * This function calculates the {@code PvP_ATTACK_RATIO} stat.<br>
	 * It is used to determine damage multipliers in player versus player combat.<br>
	 * It extends the functionality of {@link DuplicateStatFunction}.
	 */
	PvPAttackRatioFunction()
	{
		stat = StatEnum.PVP_ATTACK_RATIO;
	}
}
