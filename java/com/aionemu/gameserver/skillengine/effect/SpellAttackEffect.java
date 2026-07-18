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

import com.aionemu.gameserver.controllers.attack.AttackUtil;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.LOG;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import com.aionemu.gameserver.skillengine.model.Effect;

/**
 * Represents an effect that applies to a spell attack over a period of time.<br>
 * This class handles the logic for ongoing effects triggered by specific skill attacks.
 * @author kecimis
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SpellAttackEffect")
public class SpellAttackEffect extends AbstractOverTimeEffect
{
	/**
	 * Starts a new {@link Effect} instance.<br>
	 * This method initializes the effect and begins its execution.<br>
	 * It is a convenience method that passes {@code null} for the abnormal state.
	 * @param effect The {@code Effect} object to be started.
	 */
	@Override
	public void startEffect(Effect effect)
	{
		final int valueWithDelta = value + (delta * effect.getSkillLevel());
		final int critAddDmg = critAddDmg2 + (critAddDmg1 * effect.getSkillLevel());
		final int finalDamage = AttackUtil.calculateMagicalOverTimeSkillResult(effect, valueWithDelta, element, position, true, critProbMod2, critAddDmg);
		effect.setReservedInt(position, finalDamage);
		super.startEffect(effect);
	}
	
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
		final Creature effected = effect.getEffected();
		final Creature effector = effect.getEffector();
		effected.getController().onAttack(effector, effect.getSkillId(), TYPE.DAMAGE, effect.getReservedInt(position), false, LOG.SPELLATK);
		effected.getObserveController().notifyDotAttackedObservers(effector, effect);
	}
}
