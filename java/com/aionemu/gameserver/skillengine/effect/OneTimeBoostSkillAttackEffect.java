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

import com.aionemu.gameserver.controllers.observer.AttackCalcObserver;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.SkillType;

/**
 * This class handles a one-time boost to the attack power of a skill.<br>
 * It applies a temporary increase to the {@code SkillType} damage calculation.<br>
 * Use this effect when a skill needs a single instance of increased potency.
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OneTimeBoostSkillAttackEffect")
public class OneTimeBoostSkillAttackEffect extends BuffEffect
{
	@XmlAttribute
	private int count;
	@XmlAttribute
	private SkillType type;
	
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
		
		final int stopCount = count;
		final float percent = 1.0f + (value / 100.0f);
		AttackCalcObserver observer = null;
		
		switch (type)
		{
			case MAGICAL:
				observer = new AttackCalcObserver()
				{
					
					private int count = 0;
					
					@Override
					public float getBaseMagicalDamageMultiplier()
					{
						if (count++ < stopCount)
						{
							return percent;
						}
						
						effect.getEffected().getEffectController().removeEffect(effect.getSkillId());
						
						return 1.0f;
					}
				};
				break;
			case PHYSICAL:
				observer = new AttackCalcObserver()
				{
					
					private int count = 0;
					
					@Override
					public float getBasePhysicalDamageMultiplier(boolean isSkill)
					{
						if (!isSkill)
						{
							return 1f;
						}
						
						if (count++ < stopCount)
						{
							if (count == stopCount)
							{
								effect.getEffected().getEffectController().removeEffect(effect.getSkillId());
							}
							
							return percent;
						}
						
						return 1.0f;
					}
				};
				break;
			case ALL:
				observer = new AttackCalcObserver()
				{
					
					private int count = 0;
					
					@Override
					public float getBaseMagicalDamageMultiplier()
					{
						if (count++ < stopCount)
						{
							return percent;
						}
						
						effect.getEffected().getEffectController().removeEffect(effect.getSkillId());
						
						return 1.0f;
					}
					
					@Override
					public float getBasePhysicalDamageMultiplier(boolean isSkill)
					{
						if (!isSkill)
						{
							return 1f;
						}
						
						if (count++ < stopCount)
						{
							if (count == stopCount)
							{
								effect.getEffected().getEffectController().removeEffect(effect.getSkillId());
							}
							
							return percent;
						}
						
						return 1.0f;
					}
				};
				break;
			default:
				break;
			
		}
		
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
}
