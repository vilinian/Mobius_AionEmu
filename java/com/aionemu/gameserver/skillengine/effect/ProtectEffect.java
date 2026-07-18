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

import com.aionemu.gameserver.controllers.observer.ActionObserver;
import com.aionemu.gameserver.controllers.observer.AttackCalcObserver;
import com.aionemu.gameserver.controllers.observer.AttackShieldObserver;
import com.aionemu.gameserver.controllers.observer.ObserverType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Summon;
import com.aionemu.gameserver.skillengine.model.Effect;

/**
 * This class handles the logic for protection effects applied to creatures.<br>
 * It extends {@link ShieldEffect} to provide specific defensive behaviors.<br>
 * Use this class when a skill needs to grant a protective shield or buff.
 * @author Sippolo modified by kecimis
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProtectEffect")
public class ProtectEffect extends ShieldEffect
{
	/**
	 * Starts a new {@link Effect} instance.<br>
	 * This method initializes the effect and begins its execution.<br>
	 * It is a convenience method that passes {@code null} for the abnormal state.
	 * @param effect The {@code Effect} object to be started.
	 */
	@Override
	public void startEffect(Effect effect)
	{
		final AttackShieldObserver asObserver = new AttackShieldObserver(value, hitvalue, radius, percent, effect, hitType, getType(), hitTypeProb);
		
		effect.getEffected().getObserveController().addAttackCalcObserver(asObserver);
		effect.setAttackShieldObserver(asObserver, position);
		
		if (effect.getEffector() instanceof Summon)
		{
			final ActionObserver summonRelease = new ActionObserver(ObserverType.SUMMONRELEASE)
			{
				@Override
				public void summonrelease()
				{
					effect.endEffect();
				}
			};
			effect.getEffector().getObserveController().attach(summonRelease);
			effect.setActionObserver(summonRelease, position);
		}
		else
		{
			final ActionObserver death = new ActionObserver(ObserverType.DEATH)
			{
				@Override
				public void died(Creature creature)
				{
					effect.endEffect();
				}
			};
			effect.getEffector().getObserveController().attach(death);
			effect.setActionObserver(death, position);
		}
		
	}
	
	/**
	 * Stops a specific {@code Effect} from being active.<br>
	 * This method removes the associated observers from the target controller.<br>
	 * Use this to clean up effects when they expire or are removed.
	 * @param effect The {@code Effect} object to stop.
	 */
	@Override
	public void endEffect(Effect effect)
	{
		final AttackCalcObserver acObserver = effect.getAttackShieldObserver(position);
		if (acObserver != null)
		{
			effect.getEffected().getObserveController().removeAttackCalcObserver(acObserver);
		}
		
		final ActionObserver aObserver = effect.getActionObserver(position);
		if (aObserver != null)
		{
			effect.getEffector().getObserveController().removeObserver(aObserver);
		}
	}
	
	/**
	 * Returns the unique identifier for this effect type.<br>
	 * This value is used to identify {@link ProtectEffect} in the system.
	 * @return The integer value representing the effect type.
	 */
	@Override
	public int getType()
	{
		return 8;
	}
}
