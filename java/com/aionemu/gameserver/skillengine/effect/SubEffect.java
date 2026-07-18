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

/**
 * Represents a secondary effect associated with a primary skill effect.<br>
 * It allows for the definition of nested behaviors within the {@code Effect} system.
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SubEffect")
public class SubEffect
{
	@XmlAttribute(name = "skill_id", required = true)
	private int skillId;
	@XmlAttribute
	private int chance = 100;
	@XmlAttribute(name = "addeffect")
	private boolean addEffect = false;
	
	/**
	 * Retrieves the unique identifier for the skill associated with this AI.
	 * @return The {@code int} value of the skill ID.
	 */
	public int getSkillId()
	{
		return skillId;
	}
	
	/**
	 * Retrieves the success rate for an enchantment.<br>
	 * This value represents the percentage chance of a successful outcome.
	 * @return The current {@code chance} value as an {@code int}.
	 */
	public int getChance()
	{
		return chance;
	}
	
	/**
	 * Checks if the effect should be added.<br>
	 * This method returns the value of the {@code addEffect} attribute.
	 * @return {@code true} if the effect is to be added, {@code false} otherwise.
	 */
	public boolean isAddEffect()
	{
		return addEffect;
	}
}
