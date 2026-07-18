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
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.SpellStatus;

/**
 * Handles the visual and functional effects for open aerial skills.<br>
 * This class extends {@link EffectTemplate} to define specific behaviors for airborne actions.
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OpenAerialEffect")
public class OpenAerialEffect extends EffectTemplate
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
		if (!effect.getEffected().getEffectController().hasPhysicalStateEffect() && !effect.getEffected().getEffectController().isAbnormalSet(AbnormalState.STUMBLE))
		{
			effect.addToEffectedController();
			effect.setIsPhysicalState(true);
			effect.getEffected().getEffectController().removeParalyzeEffects();
		}
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
		super.calculate(effect, StatEnum.OPENAREIAL_RESISTANCE, SpellStatus.OPENAERIAL);
	}
	
	/**
	 * Starts the {@link Effect} and applies specific movement restrictions.<br>
	 * This method cancels the current skill and aborts any active movement for the target.<br>
	 * It also sets the {@code AbnormalState} to {@code OPENAERIAL}.
	 * @param effect The {@code Effect} object to be started.
	 */
	@Override
	public void startEffect(Effect effect)
	{
		final Creature effected = effect.getEffected();
		effected.getController().cancelCurrentSkill();
		effected.getMoveController().abortMove();
		effected.getEffectController().setAbnormal(AbnormalState.OPENAERIAL.getId());
		effect.setAbnormal(AbnormalState.OPENAERIAL.getId());
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
		effect.setIsPhysicalState(false);
		effect.getEffected().getEffectController().unsetAbnormal(AbnormalState.OPENAERIAL.getId());
	}
}
