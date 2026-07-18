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

import com.aionemu.gameserver.controllers.attack.AttackStatus;
import com.aionemu.gameserver.controllers.observer.AttackCalcObserver;
import com.aionemu.gameserver.controllers.observer.AttackStatusObserver;
import com.aionemu.gameserver.skillengine.model.Effect;

/**
 * This effect ensures that the target always successfully dodges incoming attacks.<br>
 * It overrides standard dodge mechanics to provide a permanent evasion state.
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AlwaysDodgeEffect")
public class AlwaysDodgeEffect extends EffectTemplate
{
	/**
	 * Adds the specified {@code Effect} to the controller.<br>
	 * This updates the internal state of the effect's target.
	 * @param effect The {@code Effect} object to be added.
	 */
	@Override
	public void applyEffect(Effect effect)
	{
		effect.addToEffectedController();
	}
	
	/**
	 * Starts a new {@link Effect} instance.<br>
	 * This method initializes the effect and begins its execution.<br>
	 * It is a convenience method that passes {@code null} for the abnormal state.
	 * @param effect The {@code Effect} object to be started.
	 */
	@Override
	public void startEffect(Effect effect)
	{
		final AttackCalcObserver acObserver = new AttackStatusObserver(value, AttackStatus.DODGE)
		{
			@Override
			public boolean checkStatus(AttackStatus status)
			{
				if (status == AttackStatus.DODGE)
				{
					if (value <= 1)
					{
						effect.endEffect();
					}
					else
					{
						value--;
					}
					
					return true;
				}
				
				return false;
			}
		};
		effect.getEffected().getObserveController().addAttackCalcObserver(acObserver);
		effect.setAttackStatusObserver(acObserver, position);
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
		final AttackCalcObserver acObserver = effect.getAttackStatusObserver(position);
		if (acObserver != null)
		{
			effect.getEffected().getObserveController().removeAttackCalcObserver(acObserver);
		}
	}
}
