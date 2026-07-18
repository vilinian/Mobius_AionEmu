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

import javax.xml.bind.annotation.XmlAttribute;

import com.aionemu.gameserver.controllers.observer.ActionObserver;
import com.aionemu.gameserver.controllers.observer.ObserverType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.LOG;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.HealType;

/**
 * Handles healing effects that are triggered based on specific conditions.<br>
 * This class extends {@link AbstractHealEffect} to provide specialized logic for conditional healing.
 * @author kecimis
 */
public class CaseHealEffect extends AbstractHealEffect
{
	@XmlAttribute(name = "cond_value")
	protected int condValue;
	@XmlAttribute
	protected HealType type;
	
	/**
	 * Retrieves the current value of a specific stat based on the {@code HealType}.<br>
	 * It checks if the effect is for HP or MP.<br>
	 * Returns 0 if the type is not recognized.
	 * @param effect The {@link Effect} object containing the target creature data.
	 * @return The current HP or MP value as an {@code int}.
	 */
	@Override
	protected int getCurrentStatValue(Effect effect)
	{
		if (type == HealType.HP)
		{
			return effect.getEffected().getLifeStats().getCurrentHp();
		}
		else if (type == HealType.MP)
		{
			return effect.getEffected().getLifeStats().getCurrentMp();
		}
		
		return 0;
	}
	
	/**
	 * Retrieves the maximum value of a specific stat for an {@link Effect}.<br>
	 * It checks if the effect type is {@code HealType.HP} or {@code HealType.MP}.<br>
	 * If no valid type is found, it returns {@code 0}.
	 * @param effect The {@code Effect} object to check.
	 * @return The current maximum value of the stat as an {@code int}.
	 */
	@Override
	protected int getMaxStatValue(Effect effect)
	{
		if (type == HealType.HP)
		{
			return effect.getEffected().getGameStats().getMaxHp().getCurrent();
		}
		else if (type == HealType.MP)
		{
			return effect.getEffected().getGameStats().getMaxMp().getCurrent();
		}
		
		return 0;
	}
	
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
	
	/**
	 * Starts a new {@link Effect} instance.<br>
	 * This method initializes the effect and begins its execution.<br>
	 * It is a convenience method that passes {@code null} for the abnormal state.
	 * @param effect The {@code Effect} object to be started.
	 */
	@Override
	public void startEffect(Effect effect)
	{
		final ActionObserver observer = new ActionObserver(ObserverType.ATTACKED)
		{
			@Override
			public void attacked(Creature creature)
			{
				calculateHeal(effect);
			}
		};
		effect.getEffected().getObserveController().addObserver(observer);
		effect.setActionObserver(observer, position);
		calculateHeal(effect);
	}
	
	/**
	 * Calculates and applies the healing amount for a specific {@code Effect}.<br>
	 * It checks if the target meets the required conditions before healing.<br>
	 * The method handles both HP and MP types based on the effect configuration.
	 * @param effect The {@code Effect} object containing the heal data.
	 */
	private void calculateHeal(Effect effect)
	{
		final int valueWithDelta = value + (delta * effect.getSkillLevel());
		final int currentValue = getCurrentStatValue(effect);
		final int maxValue = getMaxStatValue(effect);
		if (currentValue <= ((maxValue * condValue) / 100))
		{
			int possibleHealValue = 0;
			if (percent)
			{
				possibleHealValue = (maxValue * valueWithDelta) / 100;
			}
			else
			{
				possibleHealValue = valueWithDelta;
			}
			
			int finalHeal = effect.getEffected().getGameStats().getStat(StatEnum.HEAL_SKILL_BOOST, possibleHealValue).getCurrent();
			finalHeal = effect.getEffected().getGameStats().getStat(StatEnum.HEAL_SKILL_DEBOOST, finalHeal).getCurrent();
			finalHeal = (maxValue - currentValue) < finalHeal ? (maxValue - currentValue) : finalHeal;
			
			if ((type == HealType.HP) && effect.getEffected().getEffectController().isAbnormalSet(AbnormalState.DISEASE))
			{
				finalHeal = 0;
			}
			
			// apply heal
			if (type == HealType.HP)
			{
				effect.getEffected().getLifeStats().increaseHp(TYPE.HP, finalHeal, effect.getSkillId(), LOG.REGULAR);
			}
			else if (type == HealType.MP)
			{
				effect.getEffected().getLifeStats().increaseMp(TYPE.MP, finalHeal, effect.getSkillId(), LOG.REGULAR);
			}
			
			effect.endEffect();
		}
	}
}
