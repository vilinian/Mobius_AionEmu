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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.templates.npcskill.NpcSkillTemplate;
import com.aionemu.gameserver.model.templates.npcskill.NpcSkillTemplates;

/**
 * This class manages a collection of skills associated with {@link Npc} objects.<br>
 * It provides helper methods to handle and retrieve skill data for non-player characters.
 * @author ATracer
 */
public class NpcSkillList implements SkillList<Npc>
{
	private List<NpcSkillEntry> skills;
	
	/**
	 * Creates a new {@code NpcSkillList} for a specific NPC.<br>
	 * This constructor initializes the skills based on the owner's ID.
	 * @param owner The {@link Npc} that owns this skill list.
	 */
	public NpcSkillList(Npc owner)
	{
		initSkillList(owner.getNpcId());
	}
	
	/**
	 * This method populates the skill list for a specific NPC.<br>
	 * It fetches data from {@link DataManager}.<br>
	 * It calls {@code initSkills} to prepare the internal list.
	 * @param npcId The unique identifier of the NPC.
	 */
	private void initSkillList(int npcId)
	{
		final NpcSkillTemplates npcSkillList = DataManager.NPC_SKILL_DATA.getNpcSkillList(npcId);
		if (npcSkillList != null)
		{
			initSkills();
			for (NpcSkillTemplate template : npcSkillList.getNpcSkills())
			{
				skills.add(new NpcSkillTemplateEntry(template));
			}
		}
	}
	
	/**
	 * Adds a new skill to the specified {@link Npc}.<br>
	 * This method initializes the skills list and stores the new entry.
	 * @param creature The {@code Npc} object that will receive the skill.
	 * @param skillId The unique identifier for the skill.
	 * @param skillLevel The level of the skill to be added.
	 * @return {@code true} if the skill was successfully added.
	 */
	@Override
	public boolean addSkill(Npc creature, int skillId, int skillLevel)
	{
		initSkills();
		skills.add(new NpcSkillParameterEntry(skillId, skillLevel));
		return true;
	}
	
	/**
	 * Removes a specific skill from the list.<br>
	 * It searches for the skill using the provided {@code skillId}.
	 * @param skillId The unique identifier of the skill to remove.
	 * @return {@code true} if the skill was found and removed, otherwise {@code false}.
	 */
	@Override
	public boolean removeSkill(int skillId)
	{
		final Iterator<NpcSkillEntry> iter = skills.iterator();
		while (iter.hasNext())
		{
			final NpcSkillEntry next = iter.next();
			if (next.getSkillId() == skillId)
			{
				iter.remove();
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Checks if a specific skill exists in the list.<br>
	 * This method returns {@code true} if the skill is found.<br>
	 * It returns {@code false} if the skill is missing or the list is null.
	 * @param skillId The unique identifier of the skill to check.
	 * @return {@code true} if the skill exists, otherwise {@code false}.
	 */
	@Override
	public boolean isSkillPresent(int skillId)
	{
		if (skills == null)
		{
			return false;
		}
		
		return getSkill(skillId) != null;
	}
	
	/**
	 * Retrieves the current level of a specific skill.<br>
	 * This method looks up the skill using the provided {@code skillId}.<br>
	 * It returns the integer value representing the skill level.
	 * @param skillId The unique identifier for the skill to check.
	 * @return The level of the requested skill.
	 */
	@Override
	public int getSkillLevel(int skillId)
	{
		return getSkill(skillId).getSkillLevel();
	}
	
	/**
	 * Returns the total number of skills in this list.<br>
	 * It returns {@code 0} if the internal skill list is {@code null}.
	 * @return The count of skills currently available.
	 */
	@Override
	public int size()
	{
		return skills != null ? skills.size() : 0;
	}
	
	/**
	 * Initializes the {@code skills} list.<br>
	 * This method ensures that the internal list is not {@code null}.<br>
	 * It creates a new {@code ArrayList} if needed.
	 */
	private void initSkills()
	{
		if (skills == null)
		{
			skills = new ArrayList<>();
		}
	}
	
	/**
	 * Selects a random skill from the list of available NPC skills.<br>
	 * This method uses {@code int)} to pick an index.
	 * @return A random {@code NpcSkillEntry} object.
	 */
	public NpcSkillEntry getRandomSkill()
	{
		return skills.get(Rnd.get(0, skills.size() - 1));
	}
	
	/**
	 * Finds a specific skill by its unique identifier.<br>
	 * It searches through the internal list of skills.
	 * @param skillId The {@code int} ID of the skill to find.
	 * @return The matching {@link SkillEntry} object or {@code null} if not found.
	 */
	private SkillEntry getSkill(int skillId)
	{
		for (SkillEntry entry : skills)
		{
			if (entry.getSkillId() == skillId)
			{
				return entry;
			}
		}
		
		return null;
	}
	
	/**
	 * Retrieves the first skill that is marked to be used in spawned entities.<br>
	 * It searches through the {@code skills} list for an entry where {@code UseInSpawned} is true.
	 * @return The matching {@code NpcSkillEntry} or {@code null} if no such skill exists.
	 */
	public NpcSkillEntry getUseInSpawnedSkill()
	{
		if (skills == null)
		{
			return null;
		}
		
		final Iterator<NpcSkillEntry> iter = skills.iterator();
		while (iter.hasNext())
		{
			final NpcSkillEntry next = iter.next();
			final NpcSkillTemplateEntry tmpEntry = (NpcSkillTemplateEntry) next;
			if (tmpEntry.UseInSpawned())
			{
				return next;
			}
		}
		
		return null;
	}
}
