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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.skillengine.model.Skill;

/**
 * Checks if a player has moved during the execution of a skill.<br>
 * This condition ensures that movement occurs before allowing a {@link Skill} to proceed.
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PlayerMovedCondition")
public class PlayerMovedCondition extends Condition
{
	@XmlAttribute(required = true)
	protected boolean allow;
	
	/**
	 * Checks if the {@code allow} property is enabled.<br>
	 * This method returns the current state of the condition.
	 * @return {@code true} if allowed, or {@code false} otherwise.
	 */
	public boolean isAllow()
	{
		return allow;
	}
	
	/**
	 * Validates the {@link Skill} against the movement condition.<br>
	 * It compares the {@code allow} property with the effector's movement state.
	 * @param skill The {@code Skill} object to be validated.
	 * @return {@code true} if the states match, otherwise {@code false}.
	 */
	@Override
	public boolean validate(Skill skill)
	{
		return allow == skill.getConditionChangeListener().isEffectorMoved();
	}
}
