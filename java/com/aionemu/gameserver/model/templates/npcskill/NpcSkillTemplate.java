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
package com.aionemu.gameserver.model.templates.npcskill;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.ai2.AbstractAI;
import com.aionemu.gameserver.model.templates.item.GodstoneInfo;

/**
 * This class represents the template data for skills used by {@code Npc} entities.<br>
 * It defines the properties and behaviors of an NPC skill as stored in the game configuration.
 * @author AionChs Master, nrg
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "npcskill")
public class NpcSkillTemplate
{
	@XmlAttribute(name = "id")
	protected int id;
	@XmlAttribute(name = "skillid")
	protected int skillid;
	@XmlAttribute(name = "skilllevel")
	protected int skilllevel;
	@XmlAttribute(name = "probability")
	protected int probability;
	@XmlAttribute(name = "minhp")
	protected int minhp = 0;
	@XmlAttribute(name = "maxhp")
	protected int maxhp = 0;
	@XmlAttribute(name = "maxtime")
	protected int maxtime = 0;
	@XmlAttribute(name = "mintime")
	protected int mintime = 0;
	@XmlAttribute(name = "conjunction")
	protected ConjunctionType conjunction = ConjunctionType.AND;
	@XmlAttribute(name = "cooldown")
	protected int cooldown = 0;
	@XmlAttribute(name = "useinspawned")
	protected boolean useinspawned = false;
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Retrieves the unique identifier for the skill associated with this {@link GodstoneInfo}.<br>
	 * This value is used to identify which specific skill is linked to the item.
	 * @return the {@code skillid} as an {@code int}.
	 */
	public int getSkillid()
	{
		return skillid;
	}
	
	/**
	 * Retrieves the current level of the skill.<br>
	 * This value is used to determine the power of the action performed by the {@link AbstractAI}.
	 * @return The integer level of the skill.
	 */
	public int getSkillLevel()
	{
		return skilllevel;
	}
	
	/**
	 * Retrieves the success chance for this item.<br>
	 * This value is stored in the {@code probability} field.
	 * @return the current probability as an {@code int}.
	 */
	public int getProbability()
	{
		return probability;
	}
	
	/**
	 * Retrieves the minimum health required for this skill.<br>
	 * This value is used to check if an NPC meets the health threshold.
	 * @return the {@code minhp} value as an {@code int}.
	 */
	public int getMinhp()
	{
		return minhp;
	}
	
	/**
	 * Retrieves the maximum health point value.<br>
	 * This value is used to check skill requirements.
	 * @return the {@code maxhp} value as an {@code int}.
	 */
	public int getMaxhp()
	{
		return maxhp;
	}
	
	/**
	 * Retrieves the minimum time for this skill.<br>
	 * This value is stored in the {@code mintime} field.
	 * @return the minimum time as an {@code int}.
	 */
	public int getMinTime()
	{
		return mintime;
	}
	
	/**
	 * Retrieves the maximum time for this skill.<br>
	 * This value is stored in the {@code maxtime} field.
	 * @return the maximum time as an {@code int}.
	 */
	public int getMaxTime()
	{
		return maxtime;
	}
	
	/**
	 * Retrieves the logical conjunction type for this skill.<br>
	 * This determines how multiple conditions are evaluated.
	 * @return the {@code ConjunctionType} of the skill.
	 */
	public ConjunctionType getConjunctionType()
	{
		return conjunction;
	}
	
	/**
	 * Retrieves the cooldown time for this skill.<br>
	 * This value determines how long to wait before the skill can be used again.
	 * @return the {@code int} value of the cooldown.
	 */
	public int getCooldown()
	{
		return cooldown;
	}
	
	/**
	 * Checks if the skill can be used by spawned NPCs.<br>
	 * This returns the value of the {@code useinspawned} attribute.
	 * @return {@code true} if the skill is allowed for spawned NPCs, {@code false} otherwise.
	 */
	public boolean getUseInSpawned()
	{
		return useinspawned;
	}
}
