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
package com.aionemu.gameserver.services.craft;

import com.aionemu.gameserver.configs.main.CraftConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.craft.ExpertQuestsList;
import com.aionemu.gameserver.model.craft.MasterQuestsList;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.skill.PlayerSkillEntry;
import com.aionemu.gameserver.model.templates.CraftLearnTemplate;
import com.aionemu.gameserver.model.templates.recipe.RecipeTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_COMPLETED_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SKILL_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.services.trade.PricesService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This class manages the status and logic for relinquishing crafting progress.<br>
 * It handles the transition of craft states when a player chooses to give up on a current task.
 * @author synchro2
 */
public class RelinquishCraftStatus
{
	private static final int expertMinValue = 399;
	private static final int expertMaxValue = 499;
	private static final int masterMinValue = 499;
	private static final int masterMaxValue = 549;
	private static final int expertPrice = 120895;
	private static final int masterPrice = 3497448;
	private static final int systemMessageId = 1300388;
	private static final int skillMessageId = 1401127;
	
	/**
	 * Provides the singleton instance of this class.<br>
	 * Use this method to access the global {@link RelinquishCraftStatus} service.
	 * @return The singleton instance of {@code RelinquishCraftStatus}.
	 */
	public static RelinquishCraftStatus getInstance()
	{
		return SingletonHolder.instance;
	}
	
	/**
	 * Removes the expert status from a specific craft for a {@link Player}.<br>
	 * This method checks if the player meets the requirements and pays the necessary cost.<br>
	 * It then resets the skill level to the minimum expert value.
	 * @param player The {@link Player} who is relinquishing their status.
	 * @param npc The {@link Npc} associated with the craft skill.
	 */
	public static void relinquishExpertStatus(Player player, Npc npc)
	{
		final CraftLearnTemplate craftLearnTemplate = CraftSkillUpdateService.npcBySkill.get(npc.getNpcId());
		final int skillId = craftLearnTemplate.getSkillId();
		final PlayerSkillEntry skill = player.getSkillList().getSkillEntry(skillId);
		if (!canRelinquishCraftStatus(player, skill, craftLearnTemplate, expertMinValue, expertMaxValue) || !successDecreaseKinah(player, expertPrice))
		{
			return;
		}
		
		skill.setSkillLvl(expertMinValue);
		PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(skill, skillMessageId, false));
		removeRecipesAbove(player, skillId, expertMinValue);
		deleteCraftStatusQuests(skillId, player, true);
	}
	
	/**
	 * Removes the master status from a player's craft skill.<br>
	 * This method checks if the {@code Player} meets the requirements to downgrade.<br>
	 * It deducts the required price and updates the skill level to the minimum master value.<br>
	 * It also removes associated recipes and quests for that skill.
	 * @param player The {@link Player} who is relinquishing their status.
	 * @param npc The {@link Npc} associated with the craft skill.
	 */
	public static void relinquishMasterStatus(Player player, Npc npc)
	{
		final CraftLearnTemplate craftLearnTemplate = CraftSkillUpdateService.npcBySkill.get(npc.getNpcId());
		final int skillId = craftLearnTemplate.getSkillId();
		final PlayerSkillEntry skill = player.getSkillList().getSkillEntry(skillId);
		if (!canRelinquishCraftStatus(player, skill, craftLearnTemplate, masterMinValue, masterMaxValue) || !successDecreaseKinah(player, masterPrice))
		{
			return;
		}
		
		skill.setSkillLvl(masterMinValue);
		PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(skill, skillMessageId, false));
		removeRecipesAbove(player, skillId, masterMinValue);
		deleteCraftStatusQuests(skillId, player, false);
	}
	
	/**
	 * Checks if a player is eligible to relinquish their craft status.<br>
	 * This method verifies the skill level and template requirements.
	 * @param player The {@link Player} attempting the action.
	 * @param skill The {@link PlayerSkillEntry} of the skill being checked.
	 * @param craftLearnTemplate The {@link CraftLearnTemplate} associated with the craft.
	 * @param minValue The minimum required level for the status.
	 * @param maxValue The maximum allowed level for the status.
	 * @return {@code true} if the player meets all requirements, otherwise {@code false}.
	 */
	private static boolean canRelinquishCraftStatus(Player player, PlayerSkillEntry skill, CraftLearnTemplate craftLearnTemplate, int minValue, int maxValue)
	{
		if ((craftLearnTemplate == null) || !craftLearnTemplate.isCraftSkill())
		{
			return false;
		}
		
		return !((skill == null) || (skill.getSkillLevel() < minValue) || (skill.getSkillLevel() > maxValue));
	}
	
	/**
	 * Checks if the {@link Player} has enough currency to pay for a service.<br>
	 * It calculates the price based on the {@code basePrice} and the player's race.<br>
	 * If the payment fails, it sends a system message to the player.
	 * @param player The {@link Player} who will be charged.
	 * @param basePrice The base value used to calculate the final cost.
	 * @return {@code true} if the currency was successfully deducted, {@code false} otherwise.
	 */
	private static boolean successDecreaseKinah(Player player, int basePrice)
	{
		if (!player.getInventory().tryDecreaseKinah(PricesService.getPriceForService(basePrice, player.getRace())))
		{
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(systemMessageId));
			return false;
		}
		
		return true;
	}
	
	/**
	 * Removes recipes from a {@link Player} that exceed a specific skill level.<br>
	 * This method checks all templates in {@code DataManager}.<br>
	 * It deletes recipes where the skill ID matches and the skill point is greater than or equal to {@code level}.
	 * @param player The {@link Player} whose recipes will be modified.
	 * @param skillId The unique identifier for the skill to check.
	 * @param level The minimum skill level threshold for removal.
	 */
	public static void removeRecipesAbove(Player player, int skillId, int level)
	{
		for (RecipeTemplate recipe : DataManager.RECIPE_DATA.getRecipeTemplates().valueCollection())
		{
			if ((recipe.getSkillid() != skillId) || (recipe.getSkillpoint() < level))
			{
				continue;
			}
			
			player.getRecipeList().deleteRecipe(player, recipe.getId());
		}
	}
	
	/**
	 * Removes craft status quests for a specific skill.<br>
	 * This method resets quest progress and deletes them from the {@code Player}.<br>
	 * It handles both master and expert quests based on the provided flag.
	 * @param skillId The unique identifier of the skill to process.
	 * @param player The {@link Player} whose quests will be modified.
	 * @param isExpert A boolean indicating if expert quests should also be deleted.
	 */
	public static void deleteCraftStatusQuests(int skillId, Player player, boolean isExpert)
	{
		for (int questId : MasterQuestsList.getSkillsIds(skillId, player.getRace()))
		{
			final QuestState qs = player.getQuestStateList().getQuestState(questId);
			if (qs != null)
			{
				qs.setQuestVar(0);
				qs.setCompleteCount(0);
				qs.setStatus(null);
				qs.setPersistentState(PersistentState.DELETED);
			}
		}
		
		if (isExpert)
		{
			for (int questId : ExpertQuestsList.getSkillsIds(skillId, player.getRace()))
			{
				final QuestState qs = player.getQuestStateList().getQuestState(questId);
				if (qs != null)
				{
					qs.setQuestVar(0);
					qs.setCompleteCount(0);
					qs.setStatus(null);
					qs.setPersistentState(PersistentState.DELETED);
				}
			}
		}
		
		PacketSendUtility.sendPacket(player, new SM_QUEST_COMPLETED_LIST(player.getQuestStateList().getAllFinishedQuests()));
		player.getController().updateZone();
		player.getController().updateNearbyQuests();
	}
	
	/**
	 * Removes excess crafting status skills from a {@link Player}.<br>
	 * This method ensures the player does not exceed the maximum allowed skills.<br>
	 * It checks both expert and master levels based on the {@code isExpert} flag.
	 * @param player The {@link Player} whose skills will be checked.
	 * @param isExpert A boolean indicating if the check should target expert crafting status.
	 */
	public static void removeExcessCraftStatus(Player player, boolean isExpert)
	{
		final int minValue = isExpert ? expertMinValue : masterMinValue;
		final int maxValue = isExpert ? expertMaxValue : masterMaxValue;
		int skillId;
		int skillLevel;
		final int maxCraftStatus = isExpert ? CraftConfig.MAX_EXPERT_CRAFTING_SKILLS : CraftConfig.MAX_MASTER_CRAFTING_SKILLS;
		int countCraftStatus;
		for (PlayerSkillEntry skill : player.getSkillList().getBasicSkills())
		{
			countCraftStatus = isExpert ? CraftSkillUpdateService.getTotalMasterCraftingSkills(player) + CraftSkillUpdateService.getTotalExpertCraftingSkills(player) : CraftSkillUpdateService.getTotalMasterCraftingSkills(player);
			if (countCraftStatus > maxCraftStatus)
			{
				skillId = skill.getSkillId();
				skillLevel = skill.getSkillLevel();
				if (CraftSkillUpdateService.isCraftingSkill(skillId) && (skillLevel > minValue) && (skillLevel <= maxValue))
				{
					skill.setSkillLvl(minValue);
					PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(skill, skillMessageId, false));
					removeRecipesAbove(player, skillId, minValue);
					deleteCraftStatusQuests(skillId, player, isExpert);
				}
				continue;
			}
			break;
		}
		
		if (!isExpert)
		{
			removeExcessCraftStatus(player, true);
		}
	}
	
	/**
	 * Retrieves the minimum value for an expert craft status.<br>
	 * This value is used to check eligibility during crafting.
	 * @return the {@code int} value of {@code expertMinValue}.
	 */
	public static int getExpertMinValue()
	{
		return expertMinValue;
	}
	
	/**
	 * Retrieves the maximum value for an expert craft status.<br>
	 * This value is used to determine the upper limit of the expert range.
	 * @return The {@code int} value representing the maximum expert level.
	 */
	public static int getExpertMaxValue()
	{
		return expertMaxValue;
	}
	
	/**
	 * Retrieves the minimum value for the master craft status.<br>
	 * This value is used to determine eligibility for master level crafting.
	 * @return The {@code int} value representing the master minimum.
	 */
	public static int getMasterMinValue()
	{
		return masterMinValue;
	}
	
	/**
	 * Retrieves the maximum value for the {@code master} craft status.<br>
	 * This constant is used to define the upper limit of the master level range.
	 * @return The maximum integer value for master status.
	 */
	public static int getMasterMaxValue()
	{
		return masterMaxValue;
	}
	
	/**
	 * Retrieves the unique identifier for the skill message.<br>
	 * This ID is used to display messages related to craft status.
	 * @return The {@code int} value of the skill message ID.
	 */
	public static int getSkillMessageId()
	{
		return skillMessageId;
	}
	
	private static class SingletonHolder
	{
		protected static final RelinquishCraftStatus instance = new RelinquishCraftStatus();
	}
}
