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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.controllers.attack.AttackStatus;
import com.aionemu.gameserver.controllers.observer.AttackCalcObserver;
import com.aionemu.gameserver.controllers.observer.AttackerCriticalStatus;
import com.aionemu.gameserver.controllers.observer.AttackerCriticalStatusObserver;
import com.aionemu.gameserver.skillengine.model.Effect;

/**
 * Handles a one-time critical hit boost for skills.<br>
 * This effect applies a temporary increase to the {@link AttackerCriticalStatus} of a character.<br>
 * It is used by the {@link AttackCalcObserver} to modify damage calculations during specific skill executions.
 * @author Sippolo
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OneTimeBoostSkillCriticalEffect")
public class OneTimeBoostSkillCriticalEffect extends EffectTemplate
{
	@XmlAttribute
	private int count;
	@XmlAttribute
	private boolean percent;
	
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
		super.startEffect(effect);
		
		final AttackerCriticalStatusObserver observer = new AttackerCriticalStatusObserver(AttackStatus.CRITICAL, count, value, percent)
		{
			@Override
			public AttackerCriticalStatus checkAttackerCriticalStatus(AttackStatus stat, boolean isSkill)
			{
				if ((stat == status) && isSkill)
				{
					if (getCount() <= 1)
					{
						effect.endEffect();
					}
					else
					{
						decreaseCount();
					}
					
					acStatus.setResult(true);
				}
				else
				{
					acStatus.setResult(false);
				}
				
				return acStatus;
			}
		};
		effect.getEffected().getObserveController().addAttackCalcObserver(observer);
		effect.setAttackStatusObserver(observer, position);
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
		super.endEffect(effect);
		
		final AttackCalcObserver observer = effect.getAttackStatusObserver(position);
		effect.getEffected().getObserveController().removeAttackCalcObserver(observer);
	}
	
	/**
	 * Checks if the status value is represented as a percentage.<br>
	 * This method returns the current state of the {@code isPercent} flag.
	 * @return {@code true} if the value is a percentage, {@code false} otherwise.
	 */
	public boolean isPercent()
	{
		return percent;
	}
}
