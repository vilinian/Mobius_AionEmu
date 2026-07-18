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

import com.aionemu.gameserver.controllers.observer.ActionObserver;
import com.aionemu.gameserver.controllers.observer.ObserverType;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.Skill;

/**
 * This class handles effects that modify the cost of using a {@link Skill}.<br>
 * It allows for boosting or reducing skill costs based on specific conditions.
 * @author Rama and Sippolo
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BoostSkillCostEffect")
public class BoostSkillCostEffect extends BuffEffect
{
	@XmlAttribute
	protected boolean percent;
	
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
		
		final ActionObserver observer = new ActionObserver(ObserverType.SKILLUSE)
		{
			@Override
			public void skilluse(Skill skill)
			{
				skill.setBoostSkillCost(value);
			}
		};
		
		effect.getEffected().getObserveController().addObserver(observer);
		effect.setActionObserver(observer, position);
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
		final ActionObserver observer = effect.getActionObserver(position);
		effect.getEffected().getObserveController().removeObserver(observer);
	}
}
