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

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.LOG;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.HealType;

/**
 * Represents an effect that heals a target over a period of time.<br>
 * This class handles the logic for periodic healing based on {@link HealType}.<br>
 * It extends {@link AbstractOverTimeEffect} to manage duration and repetition.
 * @author ATracer
 * @author kecimis
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HealOverTimeEffect")
public abstract class HealOverTimeEffect extends AbstractOverTimeEffect
{
	/**
	 * Calculates the final healing value for a specific effect.<br>
	 * This method determines the amount based on the {@code HealType}.<br>
	 * It also applies relevant stats like boost and deboost bonuses.<br>
	 * The result is stored in the {@link Effect} object.
	 * @param effect The {@code Effect} instance to update with the calculated value.
	 * @param healType The type of healing being applied, such as HP.
	 */
	public void calculate(Effect effect, HealType healType)
	{
		if (!super.calculate(effect, null, null))
		{
			return;
		}
		
		final Creature effector = effect.getEffector();
		if (effect.getEffected() instanceof Npc)
		{
			value = effector.getAi2().modifyHealValue(value);
		}
		
		final int valueWithDelta = value + (delta * effect.getSkillLevel());
		final int maxCurValue = getMaxStatValue(effect);
		int possibleHealValue = 0;
		if (percent)
		{
			possibleHealValue = (maxCurValue * valueWithDelta) / 100;
		}
		else
		{
			possibleHealValue = valueWithDelta;
		}
		
		int finalHeal = possibleHealValue;
		
		if (healType == HealType.HP)
		{
			final int baseHeal = possibleHealValue;
			if (effect.getItemTemplate() == null)
			{
				final int boostHealAdd = effector.getGameStats().getStat(StatEnum.HEAL_BOOST, 0).getCurrent();
				
				// Apply percent Heal Boost bonus (ex. Passive skills)
				int boostHeal = (effector.getGameStats().getStat(StatEnum.HEAL_BOOST, baseHeal).getCurrent() - boostHealAdd);
				
				// Apply Add Heal Boost bonus (ex. Skills like Benevolence)
				if (boostHealAdd > 0)
				{
					boostHeal += (boostHeal * boostHealAdd) / 1000;
				}
				
				finalHeal = effector.getGameStats().getStat(StatEnum.HEAL_SKILL_BOOST, boostHeal).getCurrent();
			}
			
			finalHeal = effector.getGameStats().getStat(StatEnum.HEAL_SKILL_DEBOOST, finalHeal).getCurrent();
		}
		
		effect.setReservedInt(position, finalHeal);
		effect.addSucessEffect(this);
	}
	
	/**
	 * This method handles the periodic healing logic for an effect.<br>
	 * It calculates the amount to heal based on current stats and limits.<br>
	 * The healing is applied to the target depending on the {@code HealType}.
	 * @param effect The {@link Effect} object containing the data for this action.
	 * @param healType The type of resource to restore, such as HP or MP.
	 */
	public void onPeriodicAction(Effect effect, HealType healType)
	{
		final Creature effected = effect.getEffected();
		
		final int currentValue = getCurrentStatValue(effect);
		final int maxCurValue = getMaxStatValue(effect);
		final int possibleHealValue = effect.getReservedInt(position);
		
		final int healValue = (maxCurValue - currentValue) < possibleHealValue ? (maxCurValue - currentValue) : possibleHealValue;
		
		if (healValue <= 0)
		{
			return;
		}
		
		switch (healType)
		{
			case HP:
				effected.getLifeStats().increaseHp(TYPE.HP, healValue, effect.getSkillId(), LOG.HEAL);
				break;
			case MP:
				effected.getLifeStats().increaseMp(TYPE.MP, healValue, effect.getSkillId(), LOG.MPHEAL);
				break;
			case FP:
				((Player) effected).getLifeStats().increaseFp(TYPE.FP, healValue, effect.getSkillId(), LOG.FPHEAL);
				break;
			case DP:
				((Player) effected).getCommonData().addDp(healValue);
				break;
		}
		
	}
	
	protected abstract int getCurrentStatValue(Effect effect);
	
	protected abstract int getMaxStatValue(Effect effect);
}
