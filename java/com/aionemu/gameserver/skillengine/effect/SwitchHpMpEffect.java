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
import com.aionemu.gameserver.model.stats.container.CreatureLifeStats;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import com.aionemu.gameserver.skillengine.model.Effect;

/**
 * Handles the logic for swapping {@code HP} and {@code MP} values between creatures.<br>
 * This effect is triggered by specific skills to exchange life and mana points.
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SwitchHpMpEffect")
public class SwitchHpMpEffect extends EffectTemplate
{
	/**
	 * Swaps the HP and MP values of a target.<br>
	 * This method calculates the difference between {@code currentHp} and {@code currentMp}.<br>
	 * It then updates the life stats by applying those differences to the opposite pools.
	 * @param effect The {@code Effect} object containing the target creature.
	 */
	@Override
	public void applyEffect(Effect effect)
	{
		final CreatureLifeStats<? extends Creature> lifeStats = effect.getEffected().getLifeStats();
		final int currentHp = lifeStats.getCurrentHp();
		final int currentMp = lifeStats.getCurrentMp();
		
		lifeStats.increaseHp(TYPE.NATURAL_HP, currentMp - currentHp);
		lifeStats.increaseMp(TYPE.NATURAL_MP, currentHp - currentMp);
	}
}
