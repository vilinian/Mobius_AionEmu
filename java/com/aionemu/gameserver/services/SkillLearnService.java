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
package com.aionemu.gameserver.services;

import com.aionemu.gameserver.configs.main.MembershipConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.skill.PlayerSkillEntry;
import com.aionemu.gameserver.model.skill.PlayerSkillList;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SKILL_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SKILL_REMOVE;
import com.aionemu.gameserver.skillengine.model.SkillLearnTemplate;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This service handles the logic for players learning new skills.<br>
 * It manages {@link PlayerSkillList} updates and validates requirements against {@link SkillTemplate}.<br>
 * Use this class to process skill acquisition requests from the game client.
 * @author ATracer, xTz
 */
public class SkillLearnService
{
	/**
	 * This method updates the skills for a specific {@link Player}.<br>
	 * It handles special logic for certain skill transitions.<br>
	 * It then calls {@code int, PlayerClass, Race)} to finalize the list.
	 * @param player The {@code Player} object to update.
	 */
	public static void addNewSkills(Player player)
	{
		final int level = player.getCommonData().getLevel();
		final PlayerClass playerClass = player.getCommonData().getPlayerClass();
		final Race playerRace = player.getRace();
		
		if ((level == 10) && (player.getSkillList().getSkillEntry(30001) != null))
		{
			final int skillLevel = player.getSkillList().getSkillLevel(30001);
			removeSkill(player, 30001);
			PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(player, player.getSkillList().getBasicSkills()));
			
			// Why adding after the packet ?
			player.getSkillList().addSkill(player, 30002, skillLevel);
		}
		
		addSkills(player, level, playerClass, playerRace);
	}
	
	/**
	 * This method ensures that a {@link Player} has all required skills for their level.<br>
	 * It checks the player's current level and class to grant any missing abilities.<br>
	 * It also handles special skill logic for non-starting classes.
	 * @param player The {@code Player} object to update.
	 */
	public static void addMissingSkills(Player player)
	{
		final int level = player.getCommonData().getLevel();
		final PlayerClass playerClass = player.getCommonData().getPlayerClass();
		final Race playerRace = player.getRace();
		
		for (int i = 0; i <= level; i++)
		{
			addSkills(player, i, playerClass, playerRace);
		}
		
		if (!playerClass.isStartingClass())
		{
			final PlayerClass startinClass = PlayerClass.getStartingClassFor(playerClass);
			
			for (int i = 1; i < 10; i++)
			{
				addSkills(player, i, startinClass, playerRace);
			}
			
			if (player.getSkillList().getSkillEntry(30001) != null)
			{
				final int skillLevel = player.getSkillList().getSkillLevel(30001);
				player.getSkillList().removeSkill(30001);
				
				// Not sure about that, mysterious code
				PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(player, player.getSkillList().getBasicSkills()));
				for (PlayerSkillEntry stigmaSkill : player.getSkillList().getStigmaSkills())
				{
					PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(player, stigmaSkill));
				}
				
				// Why adding after the packet ?
				player.getSkillList().addSkill(player, 30002, skillLevel);
			}
		}
	}
	
	/**
	 * This method grants skills to a {@link Player}.<br>
	 * It checks which skills are available based on the player's class and race.<br>
	 * It also verifies if the player meets the requirements for each skill.
	 * @param player The {@link Player} receiving the new skills.
	 * @param level The current level of the {@link Player}.
	 * @param playerClass The {@link PlayerClass} of the character.
	 * @param playerRace The {@link Race} of the character.
	 */
	private static void addSkills(Player player, int level, PlayerClass playerClass, Race playerRace)
	{
		final SkillLearnTemplate[] skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesFor(playerClass, level, playerRace);
		final PlayerSkillList playerSkillList = player.getSkillList();
		
		for (SkillLearnTemplate template : skillTemplates)
		{
			if (!checkLearnIsPossible(player, playerSkillList, template))
			{
				continue;
			}
			
			if (template.isStigma())
			{
				playerSkillList.addStigmaSkill(player, template.getSkillId(), template.getSkillLevel());
			}
			else
			{
				playerSkillList.addSkill(player, template.getSkillId(), template.getSkillLevel());
			}
		}
	}
	
	/**
	 * Checks if a player is allowed to learn a specific skill.<br>
	 * It verifies if the skill is already owned, if auto-learn permissions exist,<br>
	 * or if the skill template allows for automatic learning.
	 * @param player The {@link Player} object representing the character.
	 * @param playerSkillList The list of skills currently owned by the player.
	 * @param template The {@link SkillLearnTemplate} containing the requirements for the skill.
	 * @return {@code true} if the player can learn the skill, otherwise {@code false}.
	 */
	private static boolean checkLearnIsPossible(Player player, PlayerSkillList playerSkillList, SkillLearnTemplate template)
	{
		if (playerSkillList.isSkillPresent(template.getSkillId()) || (player.havePermission(MembershipConfig.STIGMA_AUTOLEARN) && template.isStigma()) || template.isAutolearn())
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * Allows a {@link Player} to learn a specific skill using a book.<br>
	 * This method finds the maximum level for the given {@code skillId}.<br>
	 * It updates the player's skill list and refreshes passive stats if needed.
	 * @param player The {@link Player} who is learning the skill.
	 * @param skillId The unique identifier of the skill to learn.
	 */
	public static void learnSkillBook(Player player, int skillId)
	{
		SkillLearnTemplate[] skillTemplates = null;
		int maxLevel = 0;
		final SkillTemplate passiveSkill = DataManager.SKILL_DATA.getSkillTemplate(skillId);
		for (int i = 1; i <= player.getLevel(); i++)
		{
			skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesFor(player.getPlayerClass(), i, player.getRace());
			
			for (SkillLearnTemplate skill : skillTemplates)
			{
				if (skillId == skill.getSkillId())
				{
					if (skill.getSkillLevel() > maxLevel)
					{
						maxLevel = skill.getSkillLevel();
					}
				}
			}
		}
		
		player.getSkillList().addSkill(player, skillId, maxLevel);
		if (passiveSkill.isPassive())
		{
			player.getController().updatePassiveStats();
		}
	}
	
	/**
	 * Removes a specific skill from a {@link Player}.<br>
	 * This method checks if the skill exists before removing it.<br>
	 * It also sends an {@code SM_SKILL_REMOVE} packet to the client.
	 * @param player The {@code Player} object whose skills will be modified.
	 * @param skillId The unique identifier of the skill to remove.
	 */
	public static void removeSkill(Player player, int skillId)
	{
		if (player.getSkillList().isSkillPresent(skillId))
		{
			Integer skillLevel = player.getSkillList().getSkillLevel(skillId);
			if (skillLevel <= 0)
			{
				skillLevel = 1;
			}
			
			PacketSendUtility.sendPacket(player, new SM_SKILL_REMOVE(skillId, skillLevel, player.getSkillList().getSkillEntry(skillId).isStigma()));
			player.getSkillList().removeSkill(skillId);
		}
	}
	
	/**
	 * Calculates the level a player can learn for a specific skill.<br>
	 * This method checks the skill tree data and compares it against the player's current level.<br>
	 * It ensures the result does not exceed the maximum allowed level or the player's experience limits.
	 * @param skillId The unique identifier of the skill to check.
	 * @param playerLevel The current level of the player.
	 * @param wantedSkillLevel The desired level the player wants to reach.
	 * @return The calculated learnable level as an {@code int}.
	 */
	public static int getSkillLearnLevel(int skillId, int playerLevel, int wantedSkillLevel)
	{
		final SkillLearnTemplate[] skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesForSkill(skillId);
		int learnFinishes = 0;
		int maxLevel = 0;
		
		for (SkillLearnTemplate template : skillTemplates)
		{
			if (maxLevel < template.getSkillLevel())
			{
				maxLevel = template.getSkillLevel();
			}
		}
		
		// no data in skill tree, use as wanted
		if (maxLevel == 0)
		{
			return wantedSkillLevel;
		}
		
		learnFinishes = playerLevel + maxLevel;
		
		if (learnFinishes > DataManager.PLAYER_EXPERIENCE_TABLE.getMaxLevel())
		{
			learnFinishes = DataManager.PLAYER_EXPERIENCE_TABLE.getMaxLevel();
		}
		
		return Math.max(wantedSkillLevel, Math.min((playerLevel - (learnFinishes - maxLevel)) + 1, maxLevel));
	}
	
	/**
	 * Calculates the minimum level required for a specific skill.<br>
	 * It checks if the {@code wantedSkillLevel} is achievable based on the current {@code playerLevel}.<br>
	 * If no valid template is found, it returns the current {@code playerLevel}.
	 * @param skillId The unique identifier of the skill.
	 * @param playerLevel The current level of the player.
	 * @param wantedSkillLevel The target level for the skill.
	 * @return The minimum required level as an {@code int}.
	 */
	public static int getSkillMinLevel(int skillId, int playerLevel, int wantedSkillLevel)
	{
		final SkillLearnTemplate[] skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesForSkill(skillId);
		SkillLearnTemplate foundTemplate = null;
		
		for (SkillLearnTemplate template : skillTemplates)
		{
			if ((template.getSkillLevel() <= wantedSkillLevel) && (template.getMinLevel() <= playerLevel))
			{
				foundTemplate = template;
			}
		}
		
		if (foundTemplate == null)
		{
			return playerLevel;
		}
		
		return foundTemplate.getMinLevel();
	}
	
	/**
	 * Removes a specific linked skill from a {@link Player}.<br>
	 * This method checks if the skill exists before removing it.<br>
	 * It sends an {@code SM_SKILL_REMOVE} packet to the client.
	 * @param player The {@code Player} object whose skills are being modified.
	 * @param skillId The unique identifier of the skill to remove.
	 */
	public static void removeLinkedSkill(Player player, int skillId)
	{
		if (player.getSkillList().isSkillPresent(skillId))
		{
			Integer skillLevel = player.getSkillList().getSkillLevel(skillId);
			if (skillLevel <= 0)
			{
				skillLevel = 1;
			}
			
			PacketSendUtility.sendPacket(player, new SM_SKILL_REMOVE(skillId, skillLevel, false, player.getSkillList().getSkillEntry(skillId).isLinked()));
			player.getSkillList().removeSkill(skillId);
		}
	}
	
}
