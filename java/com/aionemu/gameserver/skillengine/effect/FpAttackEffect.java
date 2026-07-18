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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.model.Effect;

/**
 * Represents an effect that applies damage to a target's Force Points.<br>
 * This class handles the logic for reducing {@code fp} values over time.<br>
 * It extends {@link AbstractOverTimeEffect} to manage its duration and execution.
 * @author Sippolo
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FpAttackEffect")
public class FpAttackEffect extends AbstractOverTimeEffect
{
	/**
	 * Calculates the attributes for a specific {@code Effect}.<br>
	 * This method updates the {@code effect} to include an AP boost.<br>
	 * It also links this instance as a success effect.
	 * @param effect The {@code Effect} object to be updated.
	 */
	@Override
	public void calculate(Effect effect)
	{
		// Only players have FP
		if (effect.getEffected() instanceof Player)
		{
			super.calculate(effect, null, null);
		}
	}
	
	/**
	 * Applies the periodic damage to a player's FP.<br>
	 * This method calculates the amount based on either a flat value or a percentage of max FP.<br>
	 * It then reduces the {@code Player} life stats accordingly.
	 * @param effect The {@code Effect} object containing the reduction details.
	 */
	@Override
	public void onPeriodicAction(Effect effect)
	{
		final Player effected = (Player) effect.getEffected();
		final int maxFP = effected.getLifeStats().getMaxFp();
		int newValue = value;
		
		// Support for values in percentage
		if (percent)
		{
			newValue = (maxFP * value) / 100;
		}
		
		effected.getLifeStats().reduceFp(newValue);
	}
}
