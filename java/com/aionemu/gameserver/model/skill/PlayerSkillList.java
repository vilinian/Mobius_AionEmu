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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.HiddenStigmasTemplate;
import com.aionemu.gameserver.model.templates.item.Stigma.StigmaSkill;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SKILL_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.SkillLearnService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This class manages the collection of skills associated with a {@link Player}.<br>
 * It provides methods to retrieve and handle skill data for specific characters.
 * @author IceReaper, orfeo087, Avol, AEJTester
 */
public final class PlayerSkillList implements SkillList<Player>
{
	private final Map<Integer, PlayerSkillEntry> basicSkills;
	private final Map<Integer, PlayerSkillEntry> stigmaSkills;
	
	private final List<PlayerSkillEntry> deletedSkills;
	
	/**
	 * Creates a new instance of {@code PlayerSkillList}.<br>
	 * This constructor initializes the internal skill maps and lists.<br>
	 * It prepares an empty collection for basic skills, stigma skills, and deleted skills.
	 */
	public PlayerSkillList()
	{
		basicSkills = new HashMap<>(0);
		stigmaSkills = new HashMap<>(0);
		deletedSkills = new ArrayList<>(0);
	}
	
	/**
	 * Creates a new {@link PlayerSkillList} from a list of skills.<br>
	 * This constructor categorizes each {@code PlayerSkillEntry} into basic or stigma skills.
	 * @param skills The list of {@code PlayerSkillEntry} objects to initialize the skill list.
	 */
	public PlayerSkillList(List<PlayerSkillEntry> skills)
	{
		this();
		for (PlayerSkillEntry entry : skills)
		{
			if (entry.isStigma() || entry.isLinked())
			{
				stigmaSkills.put(entry.getSkillId(), entry);
			}
			else
			{
				basicSkills.put(entry.getSkillId(), entry);
			}
		}
	}
	
	/**
	 * Retrieves a list of every skill associated with the player.<br>
	 * This includes both basic skills and stigma skills.
	 * @return an array containing all {@link PlayerSkillEntry} objects.
	 */
	public PlayerSkillEntry[] getAllSkills()
	{
		final List<PlayerSkillEntry> allSkills = new ArrayList<>();
		allSkills.addAll(basicSkills.values());
		allSkills.addAll(stigmaSkills.values());
		return allSkills.toArray(new PlayerSkillEntry[allSkills.size()]);
	}
	
	/**
	 * Retrieves all basic skills for the player.<br>
	 * This method converts the internal {@code basicSkills} map into an array.
	 * @return An array of {@link PlayerSkillEntry} objects representing basic skills.
	 */
	public PlayerSkillEntry[] getBasicSkills()
	{
		return basicSkills.values().toArray(new PlayerSkillEntry[basicSkills.size()]);
	}
	
	/**
	 * Retrieves all skills associated with the player's stigmas.<br>
	 * This method returns an array of {@link PlayerSkillEntry} objects.
	 * @return An array containing all stigma skill entries.
	 */
	public PlayerSkillEntry[] getStigmaSkills()
	{
		return stigmaSkills.values().toArray(new PlayerSkillEntry[stigmaSkills.size()]);
	}
	
	/**
	 * Retrieves a specific stigma skill entry from the list.<br>
	 * This method looks up the entry using the provided {@code skillId}.
	 * @param skillId The unique identifier for the stigma skill.
	 * @return The {@link PlayerSkillEntry} associated with the ID, or {@code null} if not found.
	 */
	public PlayerSkillEntry getStigmaSkillEntry(int skillId)
	{
		return stigmaSkills.get(skillId);
	}
	
	/**
	 * Checks if the specified {@link Player} possesses any hidden stigmas.<br>
	 * This method verifies the player's class against known hidden stigma data.<br>
	 * It returns {@code true} if a matching skill is found in the player's skills.
	 * @param player The {@code Player} object to check.
	 * @return {@code true} if the player has a hidden stigma, otherwise {@code false}.
	 */
	public boolean isHaveHiddenStigma(Player player)
	{
		for (HiddenStigmasTemplate hst : DataManager.HIDDEN_STIGMA_DATA.getHiddenStigmasByClass())
		{
			if (hst.getClassname().equals(player.getPlayerClass().name()))
			{
				for (PlayerSkillEntry pse : getStigmaSkills())
				{
					for (HiddenStigmasTemplate.HiddenStigmaTemplate oneStigma : hst.getHiddenStigmas())
					{
						if (oneStigma.getId().equals(pse.getSkillTemplate().getStack()))
						{
							return true;
						}
					}
				}
				
				return false;
			}
		}
		
		return false;
	}
	
	/**
	 * Calculates the maximum available level for hidden stigmas.<br>
	 * It checks all skills in the {@code getStigmaSkills} list.<br>
	 * If no stigma skills are found, it returns {@code 1}.
	 * @return The lowest level among all current stigma skills or {@code 1} if none exist.
	 */
	public int getMaxAvailHiddenStigmaLvl()
	{
		int lvl = Integer.MAX_VALUE;
		for (PlayerSkillEntry playerSkillEntry : getStigmaSkills())
		{
			if (playerSkillEntry.getSkillTemplate().getLvl() < lvl)
			{
				lvl = playerSkillEntry.getSkillTemplate().getLvl();
			}
		}
		
		return lvl < Integer.MAX_VALUE ? lvl : 1;
	}
	
	/**
	 * Removes all hidden stigmas from the specified {@link Player}.<br>
	 * This operation is performed silently without notifying the user.<br>
	 * It calls the internal {@code deleteHiddenStigmaAct} method with a {@code true} flag.
	 * @param player The {@code Player} object to modify.
	 */
	public void deleteHiddenStigmaSilent(Player player)
	{
		deleteHiddenStigmaAct(player, true);
	}
	
	/**
	 * Removes a hidden stigma from the specified {@link Player}.<br>
	 * This action will trigger a notification to the user.
	 * @param player The {@code Player} object whose skill list will be updated.
	 */
	public void deleteHiddenStigma(Player player)
	{
		deleteHiddenStigmaAct(player, false);
	}
	
	/**
	 * Removes a hidden stigma skill from the {@link Player}.<br>
	 * This method identifies and deletes the specific skill based on the player's class.<br>
	 * It also removes any active effects associated with that skill.
	 * @param player The {@code Player} object to modify.
	 * @param silent If {@code true}, no system message is sent to the player.
	 */
	private void deleteHiddenStigmaAct(Player player, boolean silent)
	{
		for (HiddenStigmasTemplate hst : DataManager.HIDDEN_STIGMA_DATA.getHiddenStigmasByClass())
		{
			if (hst.getClassname().equals(player.getPlayerClass().name()))
			{
				for (PlayerSkillEntry pse : getStigmaSkills())
				{
					for (HiddenStigmasTemplate.HiddenStigmaTemplate oneStigma : hst.getHiddenStigmas())
					{
						if (oneStigma.getId().equals(pse.getSkillTemplate().getStack()))
						{
							final int skillLvl = pse.getSkillLevel();
							SkillLearnService.removeLinkedSkill(player, pse.getSkillId());
							if (!silent)
							{
								PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_STIGMA_DELETE_HIDDEN_SKILL(new DescriptionId(DataManager.SKILL_DATA.getSkillTemplate(pse.getSkillId()).getNameId()), skillLvl));
							}
							
							player.getEffectController().removeEffect(pse.getSkillId());
							return;
						}
					}
				}
				return;
			}
		}
	}
	
	/**
	 * Retrieves a list of skills that have been removed.<br>
	 * This method returns all entries stored in the {@code deletedSkills} collection.
	 * @return An array containing {@link PlayerSkillEntry} objects for all deleted skills.
	 */
	public PlayerSkillEntry[] getDeletedSkills()
	{
		return deletedSkills.toArray(new PlayerSkillEntry[deletedSkills.size()]);
	}
	
	/**
	 * Retrieves a specific skill entry from the player's list.<br>
	 * It searches through both basic and stigma skills.
	 * @param skillId The unique identifier of the skill to find.
	 * @return The {@code PlayerSkillEntry} associated with the ID, or {@code null} if not found.
	 */
	public PlayerSkillEntry getSkillEntry(int skillId)
	{
		if (basicSkills.containsKey(skillId))
		{
			return basicSkills.get(skillId);
		}
		
		return stigmaSkills.get(skillId);
	}
	
	/**
	 * Adds a new skill to the {@link Player} object.<br>
	 * This method updates the player's skill list with the specified ID and level.<br>
	 * It returns {@code true} if the operation was successful.
	 * @param player The {@code Player} who will receive the skill.
	 * @param skillId The unique identifier for the skill.
	 * @param skillLevel The level at which the skill is added.
	 * @return {@code true} if the skill was added successfully, otherwise {@code false}.
	 */
	@Override
	public boolean addSkill(Player player, int skillId, int skillLevel)
	{
		return addSkill(player, skillId, skillLevel, false, false, PersistentState.NEW);
	}
	
	/**
	 * Adds a new skill to the {@link Player} without saving it to the database.<br>
	 * This method updates the player's current session data only.<br>
	 * It is useful for temporary skill changes that do not need to persist.
	 * @param player The {@code Player} object to modify.
	 * @param skillId The unique identifier of the skill.
	 * @param skillLevel The level at which the skill is added.
	 * @return {@code true} if the skill was successfully added, otherwise {@code false}.
	 */
	public boolean addSkillWithoutSave(Player player, int skillId, int skillLevel)
	{
		return addSkill(player, skillId, skillLevel, false, false, PersistentState.NOACTION);
	}
	
	/**
	 * Adds a new stigma skill to the specified {@link Player}.<br>
	 * This method updates the player's skill list with the given {@code skillId} and {@code skillLevel}.<br>
	 * It handles the internal logic for adding a stigma type skill.
	 * @param player The {@code Player} object to receive the new skill.
	 * @param skillId The unique identifier for the skill.
	 * @param skillLevel The level at which the skill is learned.
	 * @return {@code true} if the skill was added successfully, otherwise {@code false}.
	 */
	public boolean addStigmaSkill(Player player, int skillId, int skillLevel)
	{
		return addSkill(player, skillId, skillLevel, true, false, PersistentState.NEW);
	}
	
	/**
	 * Adds a transformation skill to the specified {@link Player}.<br>
	 * This method updates the player's skill list with the new level.
	 * @param player The {@code Player} object receiving the skill.
	 * @param skillId The unique identifier for the transformation skill.
	 * @param skillLevel The level of the skill to be added.
	 * @return {@code true} if the skill was added successfully, otherwise {@code false}.
	 */
	public boolean addTransformationSkill(Player player, int skillId, int skillLevel)
	{
		return addSkill(player, skillId, skillLevel, false, false, PersistentState.NOACTION);
	}
	
	/**
	 * Adds a new skill to the {@link Player} object.<br>
	 * This method handles both basic and stigma skills.<br>
	 * It updates the player's skill list based on the provided parameters.
	 * @param player The {@code Player} receiving the skill.
	 * @param skillId The unique identifier for the skill.
	 * @param skillLevel The level of the skill to be added.
	 * @param isStigma Set to {@code true} if the skill is a stigma.
	 * @param isLinked Set to {@code true} if the skill is linked to another skill.
	 * @param state The {@code PersistentState} used for saving the data.
	 * @return {@code true} if the skill was added successfully, otherwise {@code false}.
	 */
	private boolean addSkill(Player player, int skillId, int skillLevel, boolean isStigma, boolean isLinked, PersistentState state)
	{
		return addSkillAct(player, skillId, skillLevel, isStigma, isLinked, state, false);
	}
	
	/**
	 * Adds a new GMSkill to the specified {@link Player}.<br>
	 * This method updates the player's skill list with the given {@code skillId} and {@code skillLevel}.
	 * @param player The {@code Player} object receiving the skill.
	 * @param skillId The unique identifier for the skill.
	 * @param skillLevel The level of the skill to be added.
	 * @return {@code true} if the skill was successfully added, otherwise {@code false}.
	 */
	public boolean addGMSkill(Player player, int skillId, int skillLevel)
	{
		return addSkillAct(player, skillId, skillLevel, true, false, PersistentState.NOACTION, true);
	}
	
	/**
	 * Adds a new abyss skill to the specified {@link Player}.<br>
	 * This method updates the player's skill list with the given {@code skillId} and {@code skillLevel}.<br>
	 * It returns {@code true} if the operation was successful.
	 * @param player The {@code Player} object to receive the new skill.
	 * @param skillId The unique identifier for the abyss skill.
	 * @param skillLevel The level of the skill being added.
	 * @return {@code true} if the skill was added successfully, otherwise {@code false}.
	 */
	public boolean addAbyssSkill(Player player, int skillId, int skillLevel)
	{
		return addSkill(player, skillId, skillLevel, false, false, PersistentState.NOACTION);
	}
	
	/**
	 * Adds a list of {@link StigmaSkill} objects to the player.<br>
	 * This method updates the internal skill map for the character.<br>
	 * If {@code equipedByNpc} is {@code true}, it sends a packet to the client.
	 * @param player The {@link Player} receiving the skills.
	 * @param skills The list of {@link StigmaSkill} objects to add.
	 * @param equipedByNpc A boolean flag indicating if the skill was equipped by an NPC.
	 */
	public void addStigmaSkill(Player player, List<StigmaSkill> skills, boolean equipedByNpc)
	{
		for (StigmaSkill sSkill : skills)
		{
			final PlayerSkillEntry skill = new PlayerSkillEntry(sSkill.getSkillId(), true, false, sSkill.getSkillLvl(), PersistentState.NOACTION);
			stigmaSkills.put(sSkill.getSkillId(), skill);
			if (equipedByNpc)
			{
				PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(skill, 1300401, false));
			}
		}
	}
	
	/**
	 * Adds a stigma skill to the specified {@link Player}.<br>
	 * This method updates the internal skill list and handles optional packet notifications.
	 * @param player The {@code Player} object receiving the skill.
	 * @param skillId The unique identifier for the skill.
	 * @param skillLevel The level of the skill to be added.
	 * @param withMsg Whether to include a system message in the packet if sent.
	 * @param equipedByNpc If {@code true}, sends an {@code SM_SKILL_LIST} packet to the player.
	 */
	public void addStigmaSkill(Player player, int skillId, int skillLevel, boolean withMsg, boolean equipedByNpc)
	{
		final PlayerSkillEntry skill = new PlayerSkillEntry(skillId, true, false, skillLevel, PersistentState.NOACTION);
		stigmaSkills.put(skillId, skill);
		if (equipedByNpc)
		{
			PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(skill, withMsg ? 1300401 : 0, false));
		}
	}
	
	/**
	 * Adds a hidden stigma skill to the specified {@link Player}.<br>
	 * This method updates the internal skill map and sends the necessary network packets.<br>
	 * It notifies the player with a system message regarding the new skill.
	 * @param player The {@code Player} object receiving the skill.
	 * @param skillId The unique identifier for the skill.
	 * @param skillLvl The level of the skill being added.
	 */
	public void addHiddenStigmaSkill(Player player, int skillId, int skillLvl)
	{
		final PlayerSkillEntry skill = new PlayerSkillEntry(skillId, false, true, skillLvl, PersistentState.NOACTION);
		stigmaSkills.put(skillId, skill);
		PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(player, skill));
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_STIGMA_GET_HIDDEN_SKILL(new DescriptionId(DataManager.SKILL_DATA.getSkillTemplate(skill.getSkillId()).getNameId()), skillLvl));
	}
	
	/**
	 * Adds a new skill or updates an existing one for a player.<br>
	 * This method handles basic skills, stigma skills, and linked skills.<br>
	 * It also sends a notification message to the player if they are currently spawned.
	 * @param player The {@link Player} object receiving the skill.
	 * @param skillId The unique identifier for the skill.
	 * @param skillLevel The level of the skill to set.
	 * @param isStigma Determines if the skill should be treated as a stigma.
	 * @param isLinked Determines if the skill is a linked skill.
	 * @param state The {@link PersistentState} associated with the skill.
	 * @param isGMSkill Indicates if the skill is a GMS specific skill.
	 * @return Always returns {@code true}.
	 */
	private synchronized boolean addSkillAct(Player player, int skillId, int skillLevel, boolean isStigma, boolean isLinked, PersistentState state, boolean isGMSkill)
	{
		final PlayerSkillEntry existingSkill = isStigma ? stigmaSkills.get(skillId) : basicSkills.get(skillId);
		
		boolean isNew = false;
		if (existingSkill != null)
		{
			// if (existingSkill.getSkillLevel() >= skillLevel) {
			// return false;
			// }
			
			existingSkill.setSkillLvl(skillLevel);
		}
		else
		{
			if (isStigma)
			{
				stigmaSkills.put(skillId, new PlayerSkillEntry(skillId, true, false, skillLevel, state));
			}
			else if (isLinked)
			{
				stigmaSkills.put(skillId, new PlayerSkillEntry(skillId, false, true, skillLevel, state));
			}
			else
			{
				basicSkills.put(skillId, new PlayerSkillEntry(skillId, false, false, skillLevel, state));
				isNew = true;
			}
		}
		
		if (player.isSpawned())
		{
			if (!isStigma || isGMSkill)
			{
				sendMessage(player, skillId, isNew);
			}
		}
		
		return true;
	}
	
	/**
	 * Adds experience points to a specific skill for a player.<br>
	 * This method checks if the level difference is valid before updating.<br>
	 * It also handles special logic for certain skill IDs and auto-learning recipes.
	 * @param player The {@link Player} receiving the experience.
	 * @param skillId The unique identifier of the skill to update.
	 * @param xpReward The amount of experience points to add.
	 * @param objSkillPoints The number of skill points used for calculation.
	 * @return {@code true} if the experience was successfully added, {@code false} otherwise.
	 */
	public boolean addSkillXp(Player player, int skillId, int xpReward, int objSkillPoints)
	{
		final PlayerSkillEntry skillEntry = getSkillEntry(skillId);
		final int maxDiff = 40;
		final int SkillLvlDiff = skillEntry.getSkillLevel() - objSkillPoints;
		if (maxDiff < SkillLvlDiff)
		{
			return false;
		}
		
		if (skillEntry.getSkillId() == 40011)
		{
			// Magic Morph
			if (skillEntry.getSkillLevel() == 300)
			{
				return false;
			}
		}
		
		switch (skillEntry.getSkillId())
		{
			case 30001:
				if (skillEntry.getSkillLevel() == 49)
				{
					return false;
				}
			case 30002:
			case 30003:
				if (skillEntry.getSkillLevel() == 449)
				{
					return false;
				}
			case 40001:
			case 40002:
			case 40003:
			case 40004:
			case 40007:
			case 40008:
			case 40010:
				switch (skillEntry.getSkillLevel())
				{
					case 99:
					case 199:
					case 299:
					case 399:
					case 449:
					case 499:
					case 549:
						return false;
				}
				
				player.getRecipeList().autoLearnRecipe(player, skillId, skillEntry.getSkillLevel());
		}
		
		final boolean updateSkill = skillEntry.getSkillId() == 40011 ? skillEntry.addMagicCraftSkillXp(player, xpReward) : skillEntry.addSkillXp(player, xpReward);
		if (updateSkill)
		{
			sendMessage(player, skillId, false);
		}
		
		return true;
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
		return basicSkills.containsKey(skillId) || stigmaSkills.containsKey(skillId);
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
		if (basicSkills.containsKey(skillId))
		{
			return basicSkills.get(skillId).getSkillLevel();
		}
		
		return stigmaSkills.get(skillId).getSkillLevel();
	}
	
	/**
	 * Removes a skill from the active list based on its unique ID.<br>
	 * This method marks the skill as {@code DELETED} and moves it to the deleted collection.<br>
	 * It checks both basic and stigma skill maps for the provided ID.
	 * @param skillId The unique identifier of the skill to remove.
	 * @return {@code true} if the skill was found and removed, or {@code false} otherwise.
	 */
	@Override
	public synchronized boolean removeSkill(int skillId)
	{
		PlayerSkillEntry entry = basicSkills.get(skillId);
		if (entry == null)
		{
			entry = stigmaSkills.get(skillId);
		}
		
		if (entry != null)
		{
			entry.setPersistentState(PersistentState.DELETED);
			deletedSkills.add(entry);
			basicSkills.remove(skillId);
			stigmaSkills.remove(skillId);
		}
		
		return entry != null;
	}
	
	/**
	 * Returns the total number of skills.<br>
	 * This counts both {@code basicSkills} and {@code stigmaSkills}.
	 * @return The sum of all active skill entries.
	 */
	@Override
	public int size()
	{
		return basicSkills.size() + stigmaSkills.size();
	}
	
	/**
	 * Sends a skill list packet to the specified player.<br>
	 * This method handles different logic based on the {@code skillId}.<br>
	 * It uses {@code sendPacket} to deliver the data.
	 * @param player The {@code Player} who will receive the message.
	 * @param skillId The unique identifier for the skill.
	 * @param isNew A boolean flag indicating if the skill is new.
	 */
	private void sendMessage(Player player, int skillId, boolean isNew)
	{
		switch (skillId)
		{
			case 30001:
			case 30002:
				PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(player.getSkillList().getSkillEntry(skillId), 1330005, false));
				break;
			case 30003:
				PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(player.getSkillList().getSkillEntry(skillId), 1330005, false));
				break;
			case 40001:
			case 40002:
			case 40003:
			case 40004:
			case 40005:
			case 40006:
			case 40007:
			case 40008:
			case 40009:
			case 40010:
			case 40011:
				PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(player.getSkillList().getSkillEntry(skillId), 1330053, false));
				break;
			default:
				if (player.getSkillList().getSkillEntry(skillId).getSkillLevel() > 1)
				{
					PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(player.getSkillList().getSkillEntry(skillId), 0, isNew));
				}
				else
				{
					PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(player.getSkillList().getSkillEntry(skillId), 1300050, isNew));
				}
		}
	}
}
