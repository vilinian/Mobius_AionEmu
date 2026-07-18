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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.calc.functions.IStatFunction;
import com.aionemu.gameserver.model.stats.calc.functions.StatDualWeaponMasteryFunction;
import com.aionemu.gameserver.skillengine.model.Effect;

/**
 * This class handles effects related to dual weapon mastery.<br>
 * It manages how stats are modified when a player uses two weapons simultaneously.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WeaponDualEffect")
public class WeaponDualEffect extends BuffEffect
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
		if (change == null)
		{
			return;
		}
		
		if (effect.getEffected() instanceof Player)
		{
			((Player) effect.getEffected()).setDualEffectValue(value);
		}
		
		final List<IStatFunction> modifiers = getModifiers(effect);
		final List<IStatFunction> masteryModifiers = new ArrayList<>(modifiers.size());
		for (IStatFunction modifier : modifiers)
		{
			masteryModifiers.add(new StatDualWeaponMasteryFunction(effect, modifier));
		}
		
		if (masteryModifiers.size() > 0)
		{
			effect.getEffected().getGameStats().addEffect(effect, masteryModifiers);
		}
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
		if (effect.getEffected() instanceof Player)
		{
			((Player) effect.getEffected()).setDualEffectValue(0);
		}
		
		super.endEffect(effect);
	}
}
