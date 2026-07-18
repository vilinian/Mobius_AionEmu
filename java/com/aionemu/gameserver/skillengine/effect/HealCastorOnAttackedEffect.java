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
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.HealType;
import com.aionemu.gameserver.utils.MathUtil;

/**
 * This effect heals the caster whenever they successfully attack a target.<br>
 * It is used to provide sustain for offensive skills by applying {@link HealType} logic.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HealCastorOnAttackedEffect")
public class HealCastorOnAttackedEffect extends EffectTemplate
{
	@XmlAttribute
	protected HealType type;
	@XmlAttribute
	protected float range;
	
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
	 * Calculates the values for a specific {@code Effect}.<br>
	 * This method checks if the target is a {@link Player}.<br>
	 * It then calls the superclass calculation logic.
	 * @param effect The {@code Effect} object to be processed.
	 */
	@Override
	public void calculate(Effect effect)
	{
		if (effect.getEffected() instanceof Player)
		{
			super.calculate(effect, null, null);
		}
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
		
		final Player player = (Player) effect.getEffector();
		final int valueWithDelta = value + (delta * effect.getSkillLevel());
		
		final ActionObserver observer = new ActionObserver(ObserverType.ATTACKED)
		{
			@Override
			public void attacked(Creature creature)
			{
				if (player.getPlayerGroup2() != null)
				{
					for (Player p : player.getPlayerGroup2().getMembers())
					{
						if (MathUtil.isIn3dRange(effect.getEffected(), p, range))
						{
							p.getController().onRestore(type, valueWithDelta);
						}
					}
				}
				else if (player.isInAlliance2())
				{
					for (Player p : player.getPlayerAllianceGroup2().getMembers())
					{
						if (!p.isOnline())
						{
							continue;
						}
						
						if (MathUtil.isIn3dRange(effect.getEffected(), p, range))
						{
							p.getController().onRestore(type, valueWithDelta);
						}
					}
				}
				else
				{
					if (MathUtil.isIn3dRange(effect.getEffected(), player, range))
					{
						player.getController().onRestore(type, valueWithDelta);
					}
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
