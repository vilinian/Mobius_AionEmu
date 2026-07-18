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
 * Handles the logic for transferring {@code DP} between players.<br>
 * This effect is triggered when a skill requires moving resources from one target to another.
 * @author Sippolo
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DPTransferEffect")
public class DPTransferEffect extends EffectTemplate
{
	/**
	 * Applies a specific {@code Effect} to a target.<br>
	 * This method checks if the target is an instance of {@link Player}.<br>
	 * It processes the logic required for the effect to take place.
	 * @param effect The {@code Effect} object to be applied.
	 */
	@Override
	public void applyEffect(Effect effect)
	{
		((Player) effect.getEffected()).getCommonData().addDp(-effect.getReserved1());
		((Player) effect.getEffector()).getCommonData().addDp(effect.getReserved1());
	}
	
	/**
	 * Calculates the attributes for a specific {@code Effect}.<br>
	 * This method updates the {@code effect} to include an AP boost.<br>
	 * It also links this instance as a success effect.
	 * @param effect The {@code Effect} object to be updated.
	 */
	@Override
	public void calculate(Effect effect)
	{
		if (!super.calculate(effect, null, null))
		{
			return;
		}
		
		effect.setReserved1(-getCurrentStatValue(effect));
	}
	
	/**
	 * Retrieves the current DP value of the player who triggered the {@code Effect}.<br>
	 * This method casts the effector to a {@link Player} object.<br>
	 * It then accesses the common data to find the {@code dp} value.
	 * @param effect The {@code Effect} object containing the effector information.
	 * @return The current DP value as an {@code int}.
	 */
	private int getCurrentStatValue(Effect effect)
	{
		return ((Player) effect.getEffector()).getCommonData().getDp();
	}
	
	/**
	 * Retrieves the current DP value of the target affected by an {@code Effect}.<br>
	 * This method casts the target to a {@link Player} object.<br>
	 * It then accesses the player's common data to find the DP stat.
	 * @param effect The {@code Effect} containing the target information.
	 * @return The current DP value of the affected player as an {@code int}.
	 */
	@SuppressWarnings("unused")
	private int getEffectedCurrentStatValue(Effect effect)
	{
		return ((Player) effect.getEffected()).getCommonData().getDp();
	}
	
	/**
	 * Retrieves the current maximum DP value for a player.<br>
	 * This method casts the {@code effect} to a {@link Player}.<br>
	 * It accesses the game stats of that player.
	 * @param effect The {@code Effect} object containing the target player.
	 * @return The current maximum DP value as an {@code int}.
	 */
	@SuppressWarnings("unused")
	private int getMaxStatValue(Effect effect)
	{
		return ((Player) effect.getEffected()).getGameStats().getMaxDp().getCurrent();
	}
}
