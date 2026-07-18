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
 * Handles the instant healing effect triggered by a {@code ProcDP} event.<br>
 * This class extends {@link AbstractHealEffect} to apply immediate health restoration.
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProcDPHealInstantEffect")
public class ProcDPHealInstantEffect extends AbstractHealEffect
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
	 * Applies a specific {@code Effect} to a target.<br>
	 * This method checks if the target is an instance of {@link Player}.<br>
	 * It processes the logic required for the effect to take place.
	 * @param effect The {@code Effect} object to be applied.
	 */
	@Override
	public void applyEffect(Effect effect)
	{
		super.applyEffect(effect, HealType.DP);
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
