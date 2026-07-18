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
package com.aionemu.gameserver.skillengine.effect.modifier;

import javax.xml.bind.annotation.XmlAttribute;

import com.aionemu.gameserver.skillengine.effect.AbnormalState;
import com.aionemu.gameserver.skillengine.model.Effect;

/**
 * This class handles damage modifications applied to {@link AbnormalState} effects.<br>
 * It allows the skill engine to adjust how much damage is dealt when an abnormal state is active.
 * @author kecimis
 */
public class AbnormalDamageModifier extends ActionModifier
{
	@XmlAttribute(required = true)
	protected AbnormalState state;
	
	/**
	 * This method calculates the modified value of an {@link Effect}.<br>
	 * It takes the base value and adds a bonus based on the skill level.
	 * @param effect The {@code Effect} object to be analyzed.
	 * @return The final calculated integer value.
	 */
	
	@Override
	public int analyze(Effect effect)
	{
		return (value + (effect.getSkillLevel() * delta));
	}
	
	/**
	 * Checks if the given {@code Effect} matches the current abnormal state.<br>
	 * It verifies the status of the target's effect controller.
	 * @param effect The {@code Effect} to be checked.
	 * @return {@code true} if the condition is met, otherwise {@code false}.
	 */
	@Override
	public boolean check(Effect effect)
	{
		return effect.getEffected().getEffectController().isAbnormalSet(state);
	}
}
