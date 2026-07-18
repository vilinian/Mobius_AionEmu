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
 * This class handles the logic for an instant Mana Point (MP) heal effect triggered by a proc.<br>
 * It extends {@link AbstractHealEffect} to provide specific behavior for immediate healing.
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProcMPHealInstantEffect")
public class ProcMPHealInstantEffect extends AbstractHealEffect
{
	/**
	 * Calculates the attributes for a specific {@code Effect}.<br>
	 * This method updates the {@code effect} to include an MP boost.<br>
	 * It also links this instance as a success effect.
	 * @param effect The {@code Effect} object to be updated.
	 */
	@Override
	public void calculate(Effect effect)
	{
		super.calculate(effect, HealType.MP);
	}
	
	/**
	 * Applies an {@code Effect} as an MP heal.<br>
	 * This method calls the superclass to process the healing logic.<br>
	 * It specifically uses the {@code HealType.MP} type.
	 * @param effect The {@code Effect} object to be applied.
	 */
	@Override
	public void applyEffect(Effect effect)
	{
		super.applyEffect(effect, HealType.MP);
	}
	
	/**
	 * Retrieves the current MP value from the target of the given {@code Effect}.<br>
	 * This method accesses the life stats of the creature affected by the effect.
	 * @param effect The {@link Effect} object containing the target data.
	 * @return The current MP value as an {@code int}.
	 */
	@Override
	protected int getCurrentStatValue(Effect effect)
	{
		return effect.getEffected().getLifeStats().getCurrentMp();
	}
	
	/**
	 * Retrieves the maximum MP value for an {@link Effect}.<br>
	 * It accesses the game stats of the entity affected by the effect.<br>
	 * The result is returned as a current integer value.
	 * @param effect The {@code Effect} object to check.
	 * @return The maximum MP value as an {@code int}.
	 */
	@Override
	protected int getMaxStatValue(Effect effect)
	{
		return effect.getEffected().getGameStats().getMaxMp().getCurrent();
	}
}
