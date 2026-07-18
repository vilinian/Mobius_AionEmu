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

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.controllers.observer.ActionObserver;
import com.aionemu.gameserver.controllers.observer.ObserverType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.ProvokeTarget;
import com.aionemu.gameserver.utils.MathUtil;

/**
 * Handles the logic for effects that provoke a target creature.<br>
 * This class manages how an entity triggers a {@link ProvokeTarget} state.<br>
 * It extends {@link ShieldEffect} to provide specific provocation behavior.
 * @author ATracer modified by kecimis
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProvokerEffect")
public class ProvokerEffect extends ShieldEffect
{
	@XmlAttribute(name = "provoke_target")
	protected ProvokeTarget provokeTarget;
	@XmlAttribute(name = "skill_id")
	protected int skillId;
	
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
		ActionObserver observer = null;
		final Creature effector = effect.getEffector();
		final float prob2 = hitTypeProb / 10;
		final int radius = this.radius;
		switch (hitType)
		{
			case NMLATK:// ATTACK
				observer = new ActionObserver(ObserverType.ATTACK)
				{
					
					@Override
					public void attack(Creature creature)
					{
						if (Rnd.get(0, 100) <= prob2)
						{
							final Creature target = getProvokeTarget(provokeTarget, effector, creature);
							createProvokedEffect(effector, target);
						}
					}
				};
				break;
			case EVERYHIT:// ATTACKED
				observer = new ActionObserver(ObserverType.ATTACKED)
				{
					
					@Override
					public void attacked(Creature creature)
					{
						if (radius > 0)
						{
							if (!MathUtil.isIn3dRange(effector, creature, radius))
							{
								return;
							}
						}
						
						if (Rnd.get(0, 100) <= prob2)
						{
							final Creature target = getProvokeTarget(provokeTarget, effector, creature);
							createProvokedEffect(effector, target);
						}
					}
				};
				break;
			case BACKATK: // for skill Scroundrel's Bond, Need to check retail, on how actually this works.
				observer = new ActionObserver(ObserverType.ATTACKED)
				{
					
					@Override
					public void attacked(Creature creature)
					{
						if (Rnd.get(0, 100) <= prob2)
						{
							final Creature target = getProvokeTarget(provokeTarget, effector, creature);
							createProvokedEffect(effector, target);
						}
					}
				};
				break;
			case PHHIT:// ATTACKED
				observer = new ActionObserver(ObserverType.ATTACKED)
				{
					
					@Override
					public void attacked(Creature creature)
					{
						final int physical = creature.getGameStats().getMainHandPAttack().getBase();
						final int magical = creature.getGameStats().getMainHandMAttack().getBase();
						if (Rnd.get(0, 100) <= prob2)
						{
							if (physical > magical)
							{
								final Creature target = getProvokeTarget(provokeTarget, effector, creature);
								createProvokedEffect(effector, target);
							}
						}
					}
				};
				break;
			case MAHIT:// ATTACKED
				observer = new ActionObserver(ObserverType.ATTACKED)
				{
					
					@Override
					public void attacked(Creature creature)
					{
						final int physical = creature.getGameStats().getMainHandPAttack().getBase();
						final int magical = creature.getGameStats().getMainHandMAttack().getBase();
						if (Rnd.get(0, 100) <= prob2)
						{
							if (physical < magical)
							{
								final Creature target = getProvokeTarget(provokeTarget, effector, creature);
								createProvokedEffect(effector, target);
							}
						}
					}
				};
				break;
			default:
				break;
			
			// TODO Better implementation on MAHIT and PHHIT
		}
		
		if (observer == null)
		{
			return;
		}
		
		effect.setActionObserver(observer, position);
		effect.getEffected().getObserveController().addObserver(observer);
	}
	
	/**
	 * This method creates a provoked effect between two creatures.<br>
	 * It uses the {@code skillId} to apply the effect directly.<br>
	 * The effect is applied from the {@code effector} to the {@code target}.
	 * @param effector The creature that initiates the provocation.
	 * @param target The creature that receives the provocation.
	 */
	private void createProvokedEffect(Creature effector, Creature target)
	{
		SkillEngine.getInstance().applyEffectDirectly(skillId, effector, target, 0);
	}
	
	/**
	 * Determines which {@link Creature} should be the target of a provocation.<br>
	 * It checks the {@code provokeTarget} type to decide between the effector or the target.
	 * @param provokeTarget The type of creature to be provoked.
	 * @param effector The creature performing the action.
	 * @param target The primary target of the skill.
	 * @return The {@link Creature} that becomes the provocation target.
	 */
	private Creature getProvokeTarget(ProvokeTarget provokeTarget, Creature effector, Creature target)
	{
		switch (provokeTarget)
		{
			case ME:
				return effector;
			case OPPONENT:
				return target;
		}
		
		throw new IllegalArgumentException("Provoker target is invalid " + provokeTarget);
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
		final ActionObserver observer = effect.getActionObserver(position);
		if (observer != null)
		{
			effect.getEffected().getObserveController().removeObserver(observer);
		}
	}
}
