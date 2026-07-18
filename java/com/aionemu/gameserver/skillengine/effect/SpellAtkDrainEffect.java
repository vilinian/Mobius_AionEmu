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
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.LOG;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import com.aionemu.gameserver.skillengine.model.Effect;

/**
 * This class handles the logic for a spell attack drain effect.<br>
 * It reduces the target's ability to perform spell attacks over time.<br>
 * It extends {@link AbstractOverTimeEffect} to manage its duration and application.
 * @author Sippolo
 * @author kecimis
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SpellAtkDrainEffect")
public class SpellAtkDrainEffect extends AbstractOverTimeEffect
{
	@XmlAttribute(name = "hp_percent")
	protected int hp_percent;
	@XmlAttribute(name = "mp_percent")
	protected int mp_percent;
	
	/**
	 * Executes the periodic logic for a specific {@code Effect}.<br>
	 * This method checks if the effector is online and applies the effect to nearby players.<br>
	 * It handles group range calculations and ensures effects are applied correctly during duels.<br>
	 * Finally, it broadcasts the updated effect packet to the effector.
	 * @param effect The {@code Effect} object to be processed.
	 */
	@Override
	public void onPeriodicAction(Effect effect)
	{
		final int valueWithDelta = value + (delta * effect.getSkillLevel());
		final int critAddDmg = critAddDmg2 + (critAddDmg1 * effect.getSkillLevel());
		final int damage = AttackUtil.calculateMagicalOverTimeSkillResult(effect, valueWithDelta, element, position, true, critProbMod2, critAddDmg);
		effect.getEffected().getController().onAttack(effect.getEffector(), effect.getSkillId(), TYPE.REGULAR, damage, true, LOG.SPELLATKDRAIN);
		effect.getEffector().getObserveController().notifyAttackObservers(effect.getEffected());
		
		// Drain (heal) portion of damage inflicted
		if (hp_percent != 0)
		{
			effect.getEffector().getLifeStats().increaseHp(TYPE.HP, (damage * hp_percent) / 100, effect.getSkillId(), LOG.SPELLATKDRAIN);
		}
		
		if (mp_percent != 0)
		{
			effect.getEffector().getLifeStats().increaseMp(TYPE.MP, (damage * mp_percent) / 100, effect.getSkillId(), LOG.SPELLATKDRAIN);
		}
	}
}
