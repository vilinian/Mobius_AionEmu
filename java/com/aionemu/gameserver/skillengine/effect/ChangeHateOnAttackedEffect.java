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
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.skillengine.model.Effect;

/**
 * This effect modifies the hate value of a {@link Creature} when it is attacked.<br>
 * It allows for dynamic adjustment of aggression levels during combat.
 * @author Sippolo
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ChangeHateOnAttackedEffect")
public class ChangeHateOnAttackedEffect extends EffectTemplate
{
	@XmlAttribute
	protected int value1; // delta
	@XmlAttribute
	protected int value2;
	
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
		
		// TODO: maybe this isn't correct formula?
		final int finalValue = value1 + value2;
		
		final ActionObserver observer = new ActionObserver(ObserverType.ATTACKED)
		{
			@Override
			public void attacked(Creature creature)
			{
				if (creature instanceof Npc)
				{
					creature.getAggroList().addHate(effect.getEffected(), finalValue);
				}
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
		if (observer != null)
		{
			effect.getEffected().getObserveController().removeObserver(observer);
		}
	}
}
