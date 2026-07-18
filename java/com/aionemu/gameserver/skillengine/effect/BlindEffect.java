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

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.controllers.attack.AttackStatus;
import com.aionemu.gameserver.controllers.observer.AttackCalcObserver;
import com.aionemu.gameserver.controllers.observer.AttackStatusObserver;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.skillengine.model.Effect;

/**
 * Represents a status effect that causes a character to become blinded.<br>
 * This class handles the logic for applying blindness to targets within the {@link Effect} system.
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BlindEffect")
public class BlindEffect extends EffectTemplate
{
	/**
	 * Applies a specific {@code Effect} to a target.<br>
	 * This method checks if the target is an instance of {@link Player}.<br>
	 * It processes the logic required for the effect to take place.
	 * @param effect The {@code Effect} object to be applied.
	 */
	@Override
	public void applyEffect(Effect effect)
	{
		effect.getEffected().getEffectController().removeHideEffects();
		effect.addToEffectedController();
	}
	
	/**
	 * Calculates the attributes for a specific {@code Effect}.<br>
	 * This method updates the {@code effect} to include an AP boost.<br>
	 * It also links this instance as a success effect.
	 * @param effect The {@code Effect} object to be updated.
	 */
	@Override
	public void calculate(Effect effect)
	{
		super.calculate(effect, StatEnum.BLIND_RESISTANCE, null);
	}
	
	/**
	 * Starts a specific {@link Effect} instance.<br>
	 * This method configures the effect with the blind status.<br>
	 * It also registers an observer to handle dodge chance calculations.
	 * @param effect The {@code Effect} object to be started.
	 */
	@Override
	public void startEffect(Effect effect)
	{
		effect.setAbnormal(AbnormalState.BLIND.getId());
		effect.getEffected().getEffectController().setAbnormal(AbnormalState.BLIND.getId());
		final AttackCalcObserver acObserver = new AttackStatusObserver(value, AttackStatus.DODGE)
		{
			@Override
			public boolean checkAttackerStatus(AttackStatus status)
			{
				return Rnd.get(0, 100) <= value;
			}
		};
		effect.getEffected().getObserveController().addAttackCalcObserver(acObserver);
		effect.setAttackStatusObserver(acObserver, position);
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
		final AttackCalcObserver acObserver = effect.getAttackStatusObserver(position);
		if (acObserver != null)
		{
			effect.getEffected().getObserveController().removeAttackCalcObserver(acObserver);
		}
		
		effect.getEffected().getEffectController().unsetAbnormal(AbnormalState.BLIND.getId());
	}
}
