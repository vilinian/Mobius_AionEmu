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
package com.aionemu.gameserver.model.templates.petskill;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.templates.pet.PetTemplate;

/**
 * Represents the configuration data for a pet skill.<br>
 * This template defines the properties and attributes used by {@code Pet} skills.
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "pet_skill")
public class PetSkillTemplate
{
	@XmlAttribute(name = "skill_id")
	protected int skillId;
	@XmlAttribute(name = "pet_id")
	protected int petId;
	@XmlAttribute(name = "order_skill")
	protected int orderSkill;
	
	/**
	 * Retrieves the unique identifier for the skill associated with this AI.
	 * @return The {@code int} value of the skill ID.
	 */
	public int getSkillId()
	{
		return skillId;
	}
	
	/**
	 * Retrieves the unique identifier for this pet.<br>
	 * This value is obtained from the {@link PetTemplate}.
	 * @return The {@code int} ID of the pet template.
	 */
	public int getPetId()
	{
		return petId;
	}
	
	/**
	 * Retrieves the display order of the pet skill.<br>
	 * This value determines where the skill appears in a list.
	 * @return the {@code orderSkill} value.
	 */
	public int getOrderSkill()
	{
		return orderSkill;
	}
}
