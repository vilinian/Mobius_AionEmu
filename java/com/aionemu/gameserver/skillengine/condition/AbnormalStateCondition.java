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
package com.aionemu.gameserver.skillengine.condition;

import javax.xml.bind.annotation.XmlAttribute;

import com.aionemu.gameserver.skillengine.effect.AbnormalState;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.Skill;

/**
 * This class handles conditions related to {@link AbnormalState} effects.<br>
 * It determines if a character is currently affected by specific status ailments.<br>
 * Use this to check for states like stun, silence, or poison during skill execution.
 * @author kecimis
 */
public class AbnormalStateCondition extends Condition
{
	@XmlAttribute(required = true)
	protected AbnormalState value;
	
	/**
	 * Checks if the target is in a specific abnormal state.<br>
	 * This method verifies the {@code value} against the first target of the skill.<br>
	 * It returns {@code false} if there is no target.
	 * @param env The {@link Skill} object containing the current environment and targets.
	 * @return {@code true} if the condition is met, otherwise {@code false}.
	 */
	
	@Override
	public boolean validate(Skill env)
	{
		if (env.getFirstTarget() != null)
		{
			return (env.getFirstTarget().getEffectController().isAbnormalSet(value));
		}
		
		return false;
	}
	
	/**
	 * Checks if the {@code effect} meets the required conditions.<br>
	 * It verifies if the target has the correct {@link AbnormalState}.
	 * @param effect The {@code Effect} object to validate.
	 * @return {@code true} if the condition is met, otherwise {@code false}.
	 */
	@Override
	public boolean validate(Effect effect)
	{
		if (effect.getEffected() != null)
		{
			return (effect.getEffected().getEffectController().isAbnormalSet(value));
		}
		
		return false;
	}
}
