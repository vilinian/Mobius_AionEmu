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
 * This class handles instant healing effects for the {@code FP} (Force Point) system.<br>
 * It extends {@link AbstractHealEffect} to provide immediate health restoration.
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FPHealInstantEffect")
public class FPHealInstantEffect extends AbstractHealEffect
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
		super.calculate(effect, HealType.FP);
	}
	
	/**
	 * Applies an {@code Effect} to a target.<br>
	 * This method uses the {@code FP} type for processing.<br>
	 * It calls the superclass logic to handle the effect application.
	 * @param effect The {@code Effect} object to be applied.
	 */
	@Override
	public void applyEffect(Effect effect)
	{
		super.applyEffect(effect, HealType.FP);
	}
	
	/**
	 * Retrieves the current FP value from the affected target.<br>
	 * This method accesses the {@code LifeStats} of the creature.
	 * @param effect The {@link Effect} object containing the target data.
	 * @return The current FP value as an {@code int}.
	 */
	@Override
	protected int getCurrentStatValue(Effect effect)
	{
		return effect.getEffected().getLifeStats().getCurrentFp();
	}
	
	/**
	 * Retrieves the maximum FP value from an {@link Effect}.<br>
	 * It accesses the life stats of the entity affected by the effect.
	 * @param effect The {@code Effect} object to check.
	 * @return The maximum FP value as an {@code int}.
	 */
	@Override
	protected int getMaxStatValue(Effect effect)
	{
		return effect.getEffected().getLifeStats().getMaxFp();
	}
}
