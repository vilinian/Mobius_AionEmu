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
 * This effect heals the caster when a target dies.<br>
 * It is triggered by the {@link EffectTemplate} system during combat.
 * @author Sippolo
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HealCastorOnTargetDeadEffect")
public class HealCastorOnTargetDeadEffect extends EffectTemplate
{
	@XmlAttribute
	protected HealType type; // unhandled for now
	@XmlAttribute
	protected float range;
	@XmlAttribute
	protected boolean healparty;
	
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
	 * Starts the {@link Effect} and sets up a death observer.<br>
	 * This method heals the caster and their party members when the target dies.<br>
	 * It checks if players are within the specified range before applying the heal.
	 * @param effect The {@code Effect} object to be started.
	 */
	@Override
	public void startEffect(Effect effect)
	{
		super.startEffect(effect);
		
		final Player player = (Player) effect.getEffector();
		final int valueWithDelta = value + (delta * effect.getSkillLevel());
		
		final ActionObserver observer = new ActionObserver(ObserverType.DEATH)
		{
			@Override
			public void died(Creature creature)
			{
				// Heal Caster first
				if (MathUtil.isIn3dRange(effect.getEffected(), player, range))
				{
					player.getController().onRestore(HealType.HP, valueWithDelta);
				}
				
				// Then check for party if healparty parameter is set
				if (healparty)
				{
					if (player.getPlayerGroup2() != null)
					{
						for (Player p : player.getPlayerGroup2().getMembers())
						{
							if (p == player)
							{
								continue;
							}
							
							if (MathUtil.isIn3dRange(effect.getEffected(), p, range))
							{
								p.getController().onRestore(HealType.HP, valueWithDelta);
							}
						}
					}
					else if (player.isInAlliance2())
					{
						for (Player p : player.getPlayerAllianceGroup2().getMembers())
						{
							if (!p.isOnline() || p.equals(player))
							{
								continue;
							}
							
							if (MathUtil.isIn3dRange(effect.getEffected(), p, range))
							{
								p.getController().onRestore(HealType.HP, valueWithDelta);
							}
						}
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
		if ((!effect.getEffected().getLifeStats().isAlreadyDead()) && (observer != null))
		{
			effect.getEffected().getObserveController().removeObserver(observer);
		}
	}
}
