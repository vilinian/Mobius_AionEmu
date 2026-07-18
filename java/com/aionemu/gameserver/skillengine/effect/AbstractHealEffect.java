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

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.LOG;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.HealType;

/**
 * Provides a base template for all healing effects within the skill engine.<br>
 * It defines common logic for applying {@link HealType} to a {@link Creature}.<br>
 * Subclasses should extend this class to implement specific healing behaviors.
 * @author ATracer modified by Wakizashi, kecimis
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractHealEffect")
public abstract class AbstractHealEffect extends EffectTemplate
{
	@XmlAttribute
	protected boolean percent;
	
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
		
		final int valueWithDelta = value + (delta * effect.getSkillLevel());
		final int currentValue = getCurrentStatValue(effect);
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
				boostHeal += (boostHeal * boostHealAdd) / 1000;
				finalHeal = effector.getGameStats().getStat(StatEnum.HEAL_SKILL_BOOST, boostHeal).getCurrent();
			}
			
			finalHeal = effector.getGameStats().getStat(StatEnum.HEAL_SKILL_DEBOOST, finalHeal).getCurrent();
		}
		
		if (finalHeal < 0)
		{
			finalHeal = currentValue > -finalHeal ? finalHeal : -currentValue;
		}
		else
		{
			finalHeal = (maxCurValue - currentValue) < finalHeal ? (maxCurValue - currentValue) : finalHeal;
		}
		
		if ((healType == HealType.HP) && effect.getEffected().getEffectController().isAbnormalSet(AbnormalState.DISEASE))
		{
			finalHeal = 0;
		}
		
		effect.setReservedInt(position, finalHeal);
		effect.setReserved1(-finalHeal);
	}
	
	/**
	 * Applies a healing effect to a target creature.<br>
	 * This method updates stats based on the provided {@code HealType}.<br>
	 * It retrieves the heal value from the {@code Effect} object.
	 * @param effect The {@code Effect} containing the target and the amount to heal.
	 * @param healType The type of resource to restore, such as HP or MP.
	 */
	public void applyEffect(Effect effect, HealType healType)
	{
		final Creature effected = effect.getEffected();
		final int healValue = effect.getReservedInt(position);
		
		if (healValue == 0)
		{
			return;
		}
		
		switch (healType)
		{
			case HP:
				if (this instanceof ProcHealInstantEffect)// item heal, eg potions
				{
					effected.getLifeStats().increaseHp(TYPE.HP, healValue, 0, LOG.REGULAR);
				}
				else // TODO shouldnt send value, on retail sm_attack_status is send only to update hp bar
				if (healValue > 0)
				{
					effected.getLifeStats().increaseHp(TYPE.REGULAR, healValue, 0, LOG.REGULAR);
				}
				else
				{
					effected.getLifeStats().reduceHp(-healValue, effected);
				}
				break;
			case MP:
				if (this instanceof ProcMPHealInstantEffect)// item heal, eg potions
				{
					effected.getLifeStats().increaseMp(TYPE.MP, healValue, 0, LOG.REGULAR);
				}
				else
				{
					effected.getLifeStats().increaseMp(TYPE.HEAL_MP, healValue, 0, LOG.REGULAR);
				}
				break;
			case FP:
				effected.getLifeStats().increaseFp(TYPE.FP, healValue);
				break;
			case DP:
				((Player) effected).getCommonData().addDp(healValue);
				break;
		}
	}
	
	protected abstract int getCurrentStatValue(Effect effect);
	
	protected abstract int getMaxStatValue(Effect effect);
}
