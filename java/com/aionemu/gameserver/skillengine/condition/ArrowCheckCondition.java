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
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.skillengine.model.Skill;

/**
 * This class checks if a target is currently being hit by an arrow.<br>
 * It is used as a condition within the {@link Skill} engine to validate specific combat requirements.
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrowCheckCondition")
public class ArrowCheckCondition extends Condition
{
	/**
	 * Checks if the provided {@link Skill} meets the required conditions.<br>
	 * This method currently always returns {@code true}.
	 * @param skill The {@code Skill} object to be validated.
	 * @return {@code true} if the skill is valid, otherwise {@code false}.
	 */
	@Override
	public boolean validate(Skill skill)
	{
		return true;
	}
}
