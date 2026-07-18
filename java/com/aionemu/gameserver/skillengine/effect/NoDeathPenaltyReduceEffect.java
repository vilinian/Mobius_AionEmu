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
package com.aionemu.gameserver.skillengine.effect;

import com.aionemu.gameserver.skillengine.model.Effect;

/**
 * This effect reduces the penalty incurred by a character upon death.<br>
 * It handles the logic for mitigating experience or item losses during gameplay.
 */
public class NoDeathPenaltyReduceEffect extends BuffEffect
{
	/**
	 * Links this instance as a success effect.<br>
	 * This updates the provided {@code Effect} object.
	 * @param effect The {@code Effect} object to be updated.
	 */
	@Override
	public void calculate(Effect effect)
	{
		effect.addSucessEffect(this);
	}
	
	/**
	 * Activates the no death penalty reduction for a specific effect.<br>
	 * This method sets the {@code setNoDeathPenaltyReduce} property to {@code true}.
	 * @param effect The {@link Effect} object to modify.
	 */
	@Override
	public void startEffect(Effect effect)
	{
		effect.setNoDeathPenaltyReduce(true);
	}
}
