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

import com.aionemu.gameserver.controllers.attack.AttackUtil;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.LOG;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import com.aionemu.gameserver.skillengine.effect.modifier.ActionModifier;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * This class handles the immediate execution of a spell attack that has been delayed.<br>
 * It ensures that the {@code DamageEffect} is applied instantly when the delay period expires.
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DelayedSpellAttackInstantEffect")
public class DelayedSpellAttackInstantEffect extends DamageEffect
{
	@XmlAttribute
	protected int delay;
	
	/**
	 * Schedules the application of an {@code Effect} after a specific delay.<br>
	 * This method uses {@link ThreadPoolManager} to run the task asynchronously.<br>
	 * It checks if the target is an enemy before calling {@code calculateAndApplyDamage}.
	 * @param effect The {@code Effect} object to be applied.
	 */
	@Override
	public void applyEffect(Effect effect)
	{
		ThreadPoolManager.getInstance().schedule(new Runnable()
		{
			@Override
			public void run()
			{
				if (effect.getEffector().isEnemy(effect.getEffected()))
				{
					calculateAndApplyDamage(effect);
				}
			}
		}, delay);
	}
	
	/**
	 * This method calculates the damage for a specific {@code Effect}.<br>
	 * It checks if the target is an instance of {@link Player}.<br>
	 * The final value is applied to the player's FP stats.
	 * @param effect The {@code Effect} object containing the damage data.
	 */
	private void calculateAndApplyDamage(Effect effect)
	{
		final int skillLvl = effect.getSkillLevel();
		final int valueWithDelta = value + (delta * skillLvl);
		final ActionModifier modifier = getActionModifiers(effect);
		final int critAddDmg = critAddDmg2 + (critAddDmg1 * effect.getSkillLevel());
		AttackUtil.calculateMagicalSkillResult(effect, valueWithDelta, modifier, getElement(), true, true, false, getMode(), critProbMod2, critAddDmg, shared, true);
		effect.getEffected().getController().onAttack(effect.getEffector(), effect.getSkillId(), TYPE.DELAYDAMAGE, effect.getReserved1(), true, LOG.PROCATKINSTANT);
		effect.getEffector().getObserveController().notifyAttackObservers(effect.getEffected());
	}
}
