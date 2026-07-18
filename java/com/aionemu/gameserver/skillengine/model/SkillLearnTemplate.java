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
package com.aionemu.gameserver.skillengine.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.ai2.AbstractAI;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.Race;

/**
 * This class represents the configuration template for learning a specific skill.<br>
 * It defines the requirements and data needed for a {@link PlayerClass} to acquire a new ability.
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "skill")
public class SkillLearnTemplate
{
	@XmlAttribute(name = "classId", required = true)
	private PlayerClass classId = PlayerClass.ALL;
	@XmlAttribute(name = "skillId", required = true)
	private int skillId;
	@XmlAttribute(name = "skillLevel", required = true)
	private int skillLevel;
	@XmlAttribute(name = "name", required = true)
	private String name;
	@XmlAttribute(name = "race", required = true)
	private Race race;
	@XmlAttribute(name = "minLevel", required = true)
	private int minLevel;
	@XmlAttribute(name = "required_skill", required = false)
	private int requiredSkill;
	@XmlAttribute
	private boolean autolearn;
	@XmlAttribute
	private boolean stigma = false;
	@XmlAttribute
	private boolean daevanionEnchant = false;
	
	/**
	 * Retrieves the {@link PlayerClass} associated with this skill template.<br>
	 * This identifies which character class can learn the skill.
	 * @return the {@code PlayerClass} identifier.
	 */
	public PlayerClass getClassId()
	{
		return classId;
	}
	
	/**
	 * Retrieves the unique identifier for the skill associated with this AI.
	 * @return The {@code int} value of the skill ID.
	 */
	public int getSkillId()
	{
		return skillId;
	}
	
	/**
	 * Retrieves the current level of the skill.<br>
	 * This value is used to determine the power of the action performed by the {@link AbstractAI}.
	 * @return The integer level of the skill.
	 */
	public int getSkillLevel()
	{
		return skillLevel;
	}
	
	/**
	 * Retrieves the skill ID needed to learn this skill.<br>
	 * This value is used to check prerequisites for the player.
	 * @return the {@code int} value of the required skill.
	 */
	public int getRequiredSkill()
	{
		return requiredSkill;
	}
	
	/**
	 * Retrieves the name of the bookmark.<br>
	 * This method returns the {@code String`name`} associated with this object.
	 * @return The name of the bookmark as a {@code String}.
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Retrieves the minimum level required for this auto group.<br>
	 * This value is fetched from the underlying template.
	 * @return The minimum level as an {@code int}.
	 */
	public int getMinLevel()
	{
		return minLevel;
	}
	
	/**
	 * Retrieves the {@code Race} of the player.<br>
	 * This method returns the current character race.
	 * @return The {@link Race} of the player.
	 */
	public Race getRace()
	{
		return race;
	}
	
	/**
	 * Checks if the skill is automatically learned by the player.<br>
	 * This method returns the value of the {@code autolearn} attribute.
	 * @return {@code true} if the skill is autolearned, {@code false} otherwise.
	 */
	public boolean isAutolearn()
	{
		return autolearn;
	}
	
	/**
	 * Checks if the skill is a stigma.<br>
	 * This method returns the status of the {@code isStigma} flag.
	 * @return {@code true} if the skill is a stigma, {@code false} otherwise.
	 */
	public boolean isStigma()
	{
		return stigma;
	}
	
	/**
	 * Checks if the skill has a Daevanion enchantment.<br>
	 * This method returns the value of the {@code daevanionEnchant} field.
	 * @return {@code true} if it is enchanted, {@code false} otherwise.
	 */
	public boolean isDaevanionEnchant()
	{
		return daevanionEnchant;
	}
}
