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

import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.HealType;

/**
 * Represents an effect that restores health to a target.<br>
 * This class extends {@link HealOverTimeEffect} to handle specific healing logic.
 * @author kecimis
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HealEffect")
public class HealEffect extends HealOverTimeEffect
{
	/**
	 * Calculates the healing values for a specific {@code Effect}.<br>
	 * This method updates the {@code effect} using the {@code HealType.HP} type.
	 * @param effect The {@code Effect} object to be updated.
	 */
	@Override
	public void calculate(Effect effect)
	{
		super.calculate(effect, HealType.HP);
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
		super.onPeriodicAction(effect, HealType.HP);
	}
	
	/**
	 * Retrieves the current HP value of the target.<br>
	 * This method accesses the {@code LifeStats} of the creature affected by the {@code Effect}.
	 * @param effect The {@link Effect} object containing the target creature data.
	 * @return The current HP value as an {@code int}.
	 */
	@Override
	protected int getCurrentStatValue(Effect effect)
	{
		return effect.getEffected().getLifeStats().getCurrentHp();
	}
	
	/**
	 * Retrieves the maximum HP value for the target of an {@link Effect}.<br>
	 * It accesses the game stats of the entity affected by the effect.<br>
	 * The result is returned as an {@code int}.
	 * @param effect The {@code Effect} object to check.
	 * @return The current maximum HP value as an {@code int}.
	 */
	@Override
	protected int getMaxStatValue(Effect effect)
	{
		return effect.getEffected().getGameStats().getMaxHp().getCurrent();
	}
}
