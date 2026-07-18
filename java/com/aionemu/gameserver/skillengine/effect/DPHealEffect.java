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
import com.aionemu.gameserver.skillengine.model.HealType;

/**
 * Handles the logic for a Damage Point (DP) heal effect over time.<br>
 * This class extends {@link HealOverTimeEffect} to provide specific healing behavior based on DP values.
 * @author kecimis
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DPHealEffect")
public class DPHealEffect extends HealOverTimeEffect
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
		super.calculate(effect, HealType.DP);
	}
	
	/**
	 * Executes the periodic logic for a specific {@code Effect}.<br>
	 * This method checks if the effector is online and applies the effect to nearby players.<br>
	 * It handles group range calculations and ensures effects are applied correctly during duels.<br>
	 * Finally, it broadcasts the updated effect packet to the effector.
	 * @param effect The {@code Effect} object to be processed.
	 */
	@Override
	public void onPeriodicAction(Effect effect)
	{
		super.onPeriodicAction(effect, HealType.DP);
	}
	
	/**
	 * Retrieves the current DP value of the target player.<br>
	 * It casts the {@code Effect} to a {@link Player} object.<br>
	 * Returns the DP value from the common data.
	 * @param effect The {@link Effect} object containing the target creature data.
	 * @return The current DP value as an {@code int}.
	 */
	@Override
	protected int getCurrentStatValue(Effect effect)
	{
		return ((Player) effect.getEffected()).getCommonData().getDp();
	}
	
	/**
	 * Retrieves the current maximum DP value for an {@link Effect}.<br>
	 * This method casts the target of the {@code effect} to a {@code Player}.<br>
	 * It then fetches the current value from the player's max DP stat.
	 * @param effect The {@code Effect} object containing the target.
	 * @return The current maximum DP value as an {@code int}.
	 */
	@Override
	protected int getMaxStatValue(Effect effect)
	{
		return ((Player) effect.getEffected()).getGameStats().getMaxDp().getCurrent();
	}
}
