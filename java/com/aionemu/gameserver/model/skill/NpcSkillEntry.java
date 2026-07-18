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
package com.aionemu.gameserver.model.skill;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.model.templates.npcskill.NpcSkillTemplate;

/**
 * Represents a specific instance of an NPC skill within the game world.<br>
 * This class serves as a base for handling skills used by non-player characters.<br>
 * It links to a {@link NpcSkillTemplate} to define the underlying skill properties.
 * @author ATracer, nrg
 */
public abstract class NpcSkillEntry extends SkillEntry
{
	protected long lastTimeUsed = 0;
	
	/**
	 * Creates a new instance of an {@link NpcSkillEntry}.<br>
	 * This constructor initializes the basic properties for an NPC skill.
	 * @param skillId The unique identifier for the skill.
	 * @param skillLevel The level of the skill being assigned.
	 * @param skillAnimation The animation ID associated with the skill.
	 * @param skillAnimationEnabled A flag indicating if the animation should play.
	 */
	public NpcSkillEntry(int skillId, int skillLevel, int skillAnimation, int skillAnimationEnabled)
	{
		super(skillId, skillLevel, 0, 0);
	}
	
	public abstract boolean isReady(int hpPercentage, long fightingTimeInMSec);
	
	public abstract boolean chanceReady();
	
	public abstract boolean hpReady(int hpPercentage);
	
	public abstract boolean timeReady(long fightingTimeInMSec);
	
	public abstract boolean hasCooldown();
	
	public abstract boolean UseInSpawned();
	
	/**
	 * Retrieves the timestamp of when this skill was last used.<br>
	 * This value is stored in milliseconds.
	 * @return The {@code long} value representing the last usage time.
	 */
	public long getLastTimeUsed()
	{
		return lastTimeUsed;
	}
	
	/**
	 * Updates the {@code lastTimeUsed} field to the current system time.<br>
	 * This method records when the skill was last activated.<br>
	 * It uses {@code System.currentTimeMillis()} to get the timestamp.
	 */
	public void setLastTimeUsed()
	{
		lastTimeUsed = System.currentTimeMillis();
	}
}

/**
 * Skill entry which inherits properties from template (regular npc skills)
 */
class NpcSkillTemplateEntry extends NpcSkillEntry
{
	private final NpcSkillTemplate template;
	
	/**
	 * Creates a new {@code NpcSkillTemplateEntry} using a provided template.<br>
	 * This constructor initializes the entry with data from the {@code NpcSkillTemplate}.
	 * @param template The {@code NpcSkillTemplate} used to populate this entry.
	 */
	public NpcSkillTemplateEntry(NpcSkillTemplate template)
	{
		super(template.getSkillid(), template.getSkillLevel(), 0, 0);
		this.template = template;
	}
	
	/**
	 * Checks if the skill is ready to be used.<br>
	 * This method evaluates the current status based on health and time.
	 * @param hpPercentage The current health percentage of the NPC.
	 * @param fightingTimeInMSec The total time spent in combat in milliseconds.
	 * @return {@code true} if the skill can be used, otherwise {@code false}.
	 */
	@Override
	public boolean isReady(int hpPercentage, long fightingTimeInMSec)
	{
		if (hasCooldown() || !chanceReady())
		{
			return false;
		}
		
		switch (template.getConjunctionType())
		{
			case XOR:
				return (hpReady(hpPercentage) && !timeReady(fightingTimeInMSec)) || (!hpReady(hpPercentage) && timeReady(fightingTimeInMSec));
			case OR:
				return hpReady(hpPercentage) || timeReady(fightingTimeInMSec);
			case AND:
				return hpReady(hpPercentage) && timeReady(fightingTimeInMSec);
			default:
				return false;
		}
	}
	
	/**
	 * Checks if the skill can be used based on a random probability.<br>
	 * It compares a random number against the value from {@link NpcSkillTemplate}.
	 * @return {@code true} if the random check passes, otherwise {@code false}.
	 */
	@Override
	public boolean chanceReady()
	{
		return Rnd.get(0, 100) < template.getProbability();
	}
	
	/**
	 * Checks if a skill is ready based on the current health percentage.<br>
	 * This method currently always returns {@code true}.
	 * @param hpPercentage The current health of the NPC as a percentage.
	 * @return {@code true} if the skill can be used, otherwise {@code false}.
	 */
	@Override
	public boolean hpReady(int hpPercentage)
	{
		if ((template.getMaxhp() == 0) && (template.getMinhp() == 0)) // it's not about hp
		{
			return true;
		}
		else if ((template.getMaxhp() >= hpPercentage) && (template.getMinhp() <= hpPercentage)) // in hp range
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * Checks if the skill is ready based on the elapsed time.<br>
	 * This method currently always returns {@code true}.
	 * @param fightingTimeInMSec The amount of time spent in combat in milliseconds.
	 * @return {@code true} if the skill can be used, otherwise {@code false}.
	 */
	@Override
	public boolean timeReady(long fightingTimeInMSec)
	{
		if ((template.getMaxTime() == 0) && (template.getMinTime() == 0)) // it's not about time
		{
			return true;
		}
		else if ((template.getMaxTime() >= fightingTimeInMSec) && (template.getMinTime() <= fightingTimeInMSec)) // in time range
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * Checks if the skill is currently on cooldown.<br>
	 * It compares the required cooldown time against the time since it was last used.
	 * @return {@code true} if the skill is still cooling down, otherwise {@code false}.
	 */
	@Override
	public boolean hasCooldown()
	{
		return template.getCooldown() > (System.currentTimeMillis() - lastTimeUsed);
	}
	
	/**
	 * Checks if the skill can be used while the NPC is in a spawned state.<br>
	 * This method currently always returns {@code true}.
	 * @return {@code true} if the skill is allowed to be used in a spawned state.
	 */
	@Override
	public boolean UseInSpawned()
	{
		return template.getUseInSpawned();
	}
	/**
	 * Creates a new instance of {@code NpcSkillParameterEntry}.<br>
	 * This constructor initializes the entry with specific skill data.<br>
	 * It sets the animation values to {@code 0} by default.
	 * @param skillId The unique identifier for the skill.
	 * @param skillLevel The level of the skill being assigned.
	 */
}

/**
 * Skill entry which can be created on the fly (skills of servants, traps)
 */
class NpcSkillParameterEntry extends NpcSkillEntry
{
	public NpcSkillParameterEntry(int skillId, int skillLevel)
	{
		super(skillId, skillLevel, 0, 0);
	}
	
	/**
	 * Checks if the skill is ready to be used.<br>
	 * This method evaluates the current status based on health and time.
	 * @param hpPercentage The current health percentage of the NPC.
	 * @param fightingTimeInMSec The total time spent in combat in milliseconds.
	 * @return {@code true} if the skill can be used, otherwise {@code false}.
	 */
	@Override
	public boolean isReady(int hpPercentage, long fightingTimeInMSec)
	{
		return true;
	}
	
	/**
	 * Checks if the skill is ready based on a random chance.<br>
	 * This method currently always returns {@code true}.
	 * @return {@code true} if the skill can be used, otherwise {@code false}.
	 */
	@Override
	public boolean chanceReady()
	{
		return true;
	}
	
	/**
	 * Checks if a skill is ready based on the current health percentage.<br>
	 * This method currently always returns {@code true}.
	 * @param hpPercentage The current health of the NPC as a percentage.
	 * @return {@code true} if the skill can be used, otherwise {@code false}.
	 */
	@Override
	public boolean hpReady(int hpPercentage)
	{
		return true;
	}
	
	/**
	 * Checks if the skill is ready based on the elapsed time.<br>
	 * This method currently always returns {@code true}.
	 * @param fightingTimeInMSec The amount of time spent in combat in milliseconds.
	 * @return {@code true} if the skill can be used, otherwise {@code false}.
	 */
	@Override
	public boolean timeReady(long fightingTimeInMSec)
	{
		return true;
	}
	
	/**
	 * Checks if the skill currently has a cooldown period.<br>
	 * This method returns {@code false} for all skills in this context.
	 * @return {@code true} if the skill is on cooldown, otherwise {@code false}.
	 */
	@Override
	public boolean hasCooldown()
	{
		return false;
	}
	
	/**
	 * Checks if the skill can be used while the NPC is in a spawned state.<br>
	 * This method currently always returns {@code true}.
	 * @return {@code true} if the skill is allowed to be used in a spawned state.
	 */
	@Override
	public boolean UseInSpawned()
	{
		return true;
	}
}
