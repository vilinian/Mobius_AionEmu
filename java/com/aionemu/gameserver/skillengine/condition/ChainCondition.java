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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.model.Skill;

/**
 * Represents a condition that triggers a sequence of actions or effects.<br>
 * It allows for the chaining of multiple {@link Condition} logic steps.<br>
 * This class is used by the skill engine to evaluate complex requirements.
 * @author ATracer
 * @edited kecimis
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ChainCondition")
public class ChainCondition extends Condition
{
	@XmlAttribute(name = "selfcount")
	private int selfCount;
	@XmlAttribute(name = "precount")
	private int preCount;
	@XmlAttribute(name = "category")
	private String category;
	@XmlAttribute(name = "precategory")
	private String precategory;
	@XmlAttribute(name = "time")
	private int time;
	
	/**
	 * Validates if the skill can be executed based on chain conditions.<br>
	 * This method checks requirements for multicast, basic, and pre-chain skills.<br>
	 * It updates the environment with the required category if valid.
	 * @param env The {@link Skill} object containing the current environment and targets.
	 * @return {@code true} if the condition is met, otherwise {@code false}.
	 */
	@Override
	public boolean validate(Skill env)
	{
		if ((env.getEffector() instanceof Player) && ((precategory != null) || (selfCount > 0)))
		{
			final Player pl = (Player) env.getEffector();
			
			if (selfCount > 0)
			{// multicast
				boolean canUse = false;
				
				if ((precategory != null) && pl.getChainSkills().chainSkillEnabled(precategory, time))
				{
					canUse = true;
				}
				
				if (pl.getChainSkills().chainSkillEnabled(category, time))
				{
					canUse = true;
				}
				else if (precategory == null)
				{
					canUse = true;
				}
				
				if (!canUse || (selfCount <= pl.getChainSkills().getChainCount(pl, env.getSkillTemplate(), category)))
				{
					return false;
				}
				
				env.setIsMultiCast(true);
			}
			else if (preCount > 0)
			{
				if (!pl.getChainSkills().chainSkillEnabled(precategory, time) || (preCount != pl.getChainSkills().getChainCount(pl, env.getSkillTemplate(), precategory)))
				{
					return false;
				}
			}
			else
			{// basic chain skill
				if (!pl.getChainSkills().chainSkillEnabled(precategory, time))
				{
					return false;
				}
			}
		}
		
		env.setChainCategory(category);
		return true;
	}
	
	/**
	 * Retrieves the current count for the self-related condition.<br>
	 * This value is stored in the {@code selfCount} field.
	 * @return The integer value of the {@code selfCount}.
	 */
	public int getSelfCount()
	{
		return selfCount;
	}
	
	/**
	 * Retrieves the category associated with this {@link ChainCondition}.<br>
	 * This value is used to group different types of conditions.
	 * @return The category name as a {@code String}.
	 */
	public String getCategory()
	{
		return category;
	}
	
	/**
	 * Retrieves the duration associated with this {@code AutoGroupType}.<br>
	 * The value is returned in milliseconds.
	 * @return the time value as an {@code int}
	 */
	public int getTime()
	{
		return time;
	}
}
