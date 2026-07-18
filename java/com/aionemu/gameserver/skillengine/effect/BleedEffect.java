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
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.LOG;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import com.aionemu.gameserver.skillengine.model.Effect;

/**
 * Represents a damage-over-time effect that causes a {@link Creature} to bleed.<br>
 * This class handles the logic for applying periodic damage based on the skill's properties.
 * @author ATracer
 * @author kecimis
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BleedEffect")
public class BleedEffect extends AbstractOverTimeEffect
{
	/**
	 * Calculates the attributes for a specific {@code Effect}.<br>
	 * This method updates the {@code effect} to include an AP boost.<br>
	 * It also links this instance as a success effect.
	 * @param effect The {@code Effect} object to be updated.
	 */
	@Override
	public void calculate(Effect effect)
	{
		super.calculate(effect, StatEnum.BLEED_RESISTANCE, null);
	}
	
	/**
	 * Starts the {@link Effect} with specific damage calculations.<br>
	 * This method calculates the final damage based on skill levels and attributes.<br>
	 * It then applies the effect using the {@code BLEED} abnormal state.
	 * @param effect The {@code Effect} object to be started.
	 */
	@Override
	public void startEffect(Effect effect)
	{
		final int valueWithDelta = value + (delta * effect.getSkillLevel());
		final int critAddDmg = critAddDmg2 + (critAddDmg1 * effect.getSkillLevel());
		final int finalDamage = AttackUtil.calculateMagicalOverTimeSkillResult(effect, valueWithDelta, element, position, false, critProbMod2, critAddDmg);
		effect.setReservedInt(position, finalDamage);
		super.startEffect(effect, AbnormalState.BLEED);
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
		super.endEffect(effect, AbnormalState.BLEED);
	}
	
	/**
	 * Handles the periodic logic for a specific {@code Effect}.<br>
	 * This method applies damage to the target and notifies observers.<br>
	 * It uses the {@code SkillId} and {@code ReservedInt} from the {@code effect} object.
	 * @param effect The {@code Effect} object to be processed.
	 */
	@Override
	public void onPeriodicAction(Effect effect)
	{
		final Creature effected = effect.getEffected();
		final Creature effector = effect.getEffector();
		effected.getController().onAttack(effector, effect.getSkillId(), TYPE.DAMAGE, effect.getReservedInt(position), false, LOG.BLEED);
		effected.getObserveController().notifyDotAttackedObservers(effector, effect);
	}
}
