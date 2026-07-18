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
package com.aionemu.gameserver.model.templates.quest;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.QuestStateList;
import com.aionemu.gameserver.model.templates.QuestTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This class handles the validation of requirements for starting a quest.<br>
 * It processes conditions defined in the {@code quest_data.xml} file.<br>
 * It ensures that a {@link Player} meets all necessary criteria before a quest can begin.
 * @author antness
 * @reworked vlog
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QuestStartConditions")
public class XMLStartCondition
{
	@XmlElement(name = "finished")
	protected List<FinishedQuestCond> finished;
	@XmlList
	@XmlElement(name = "unfinished", type = Integer.class)
	protected List<Integer> unfinished;
	@XmlList
	@XmlElement(name = "noacquired", type = Integer.class)
	protected List<Integer> noacquired;
	@XmlList
	@XmlElement(name = "acquired", type = Integer.class)
	protected List<Integer> acquired;
	@XmlList
	@XmlElement(name = "equipped", type = Integer.class)
	protected List<Integer> equipped;
	
	/**
	 * Verifies if the player has completed all required quests.<br>
	 * It checks the {@code QuestStateList} against the defined finished conditions.<br>
	 * This method also validates reward counts and repeat limits for specific quests.
	 * @param qsl The list of quest states to check.
	 * @return {@code true} if all requirements are met, otherwise {@code false}.
	 */
	private boolean checkFinishedQuests(QuestStateList qsl)
	{
		if ((finished != null) && (finished.size() > 0))
		{
			for (FinishedQuestCond fqc : finished)
			{
				final int questId = fqc.getQuestId();
				final int reward = fqc.getReward();
				final QuestState qs = qsl.getQuestState(questId);
				if ((qs == null) || (qs.getStatus() != QuestStatus.COMPLETE) || !checkReward(questId, reward, qs.getReward()))
				{
					return false;
				}
				
				final QuestTemplate template = DataManager.QUEST_DATA.getQuestById(questId);
				if ((template != null) && template.isRepeatable())
				{
					if ((template.getMaxRepeatCount() != 255) && (qs.getCompleteCount() != template.getMaxRepeatCount()))
					{
						return false;
					}
				}
			}
		}
		
		return true;
	}
	
	/**
	 * Verifies if the player has any unfinished quests from the required list.<br>
	 * This method checks the {@code QuestStateList} for specific quest statuses.<br>
	 * It returns {@code false} if any required quest is already marked as {@code COMPLETE}.
	 * @param qsl The list of current quest states for the player.
	 * @return {@code true} if all required quests are still unfinished, or {@code false} otherwise.
	 */
	private boolean checkUnfinishedQuests(QuestStateList qsl)
	{
		if ((unfinished != null) && (unfinished.size() > 0))
		{
			for (Integer questId : unfinished)
			{
				final QuestState qs = qsl.getQuestState(questId);
				if ((qs != null) && (qs.getStatus() == QuestStatus.COMPLETE))
				{
					return false;
				}
			}
		}
		
		return true;
	}
	
	/**
	 * Verifies that the player has not started specific quests.<br>
	 * It checks if any quest in the {@code noacquired} list is currently active or completed.
	 * @param qsl The {@link QuestStateList} containing the player's current quest statuses.
	 * @return {@code true} if none of the restricted quests are acquired, {@code false} otherwise.
	 */
	private boolean checkNoAcquiredQuests(QuestStateList qsl)
	{
		if ((noacquired != null) && (noacquired.size() > 0))
		{
			for (Integer questId : noacquired)
			{
				final QuestState qs = qsl.getQuestState(questId);
				if ((qs != null) && ((qs.getStatus() == QuestStatus.START) || (qs.getStatus() == QuestStatus.REWARD) || (qs.getStatus() == QuestStatus.COMPLETE)))
				{
					return false;
				}
			}
		}
		
		return true;
	}
	
	/**
	 * Verifies if the player has acquired all required quests.<br>
	 * It checks the {@code acquired} list against the provided {@code QuestStateList}.<br>
	 * Returns {@code false} if any required quest is missing or locked.
	 * @param qsl The list of current quest states for the player.
	 * @return {@code true} if all conditions are met, otherwise {@code false}.
	 */
	private boolean checkAcquiredQuests(QuestStateList qsl)
	{
		if ((acquired != null) && (acquired.size() > 0))
		{
			for (Integer questId : acquired)
			{
				final QuestState qs = qsl.getQuestState(questId);
				if ((qs == null) || (qs.getStatus() == QuestStatus.NONE) || (qs.getStatus() == QuestStatus.LOCKED))
				{
					return false;
				}
			}
		}
		
		return true;
	}
	
	/**
	 * Verifies if the {@link Player} has all required items equipped.<br>
	 * This method checks the {@code equipped} list against the player's current gear.<br>
	 * If a required item is missing and {@code warn} is {@code true}, it sends an error message.
	 * @param player The {@link Player} object to check.
	 * @param warn Whether to send a warning message if items are missing.
	 * @return {@code true} if all requirements are met or if {@code warn} is {@code false}; otherwise {@code false}.
	 */
	private boolean checkEquippedItems(Player player, boolean warn)
	{
		if (!warn)
		{
			return true;
		}
		
		if ((equipped != null) && (equipped.size() > 0))
		{
			for (int itemId : equipped)
			{
				if (!player.getEquipment().getEquippedItemIds().contains(itemId))
				{
					final int requiredItemNameId = DataManager.ITEM_DATA.getItemTemplate(itemId).getNameId();
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_QUEST_ACQUIRE_ERROR_EQUIP_ITEM(new DescriptionId(requiredItemNameId)));
					return false;
				}
			}
		}
		
		return true;
	}
	
	/**
	 * Verifies if the {@link Player} meets all required quest start conditions.<br>
	 * This method checks finished, unfinished, acquired, and unacquired quests.<br>
	 * It also validates equipped items based on the provided warning flag.
	 * @param player The {@code Player} object to check.
	 * @param warn A boolean indicating whether to trigger a warning message.
	 * @return {@code true} if all conditions are met, otherwise {@code false}.
	 */
	public boolean check(Player player, boolean warn)
	{
		final QuestStateList qsl = player.getQuestStateList();
		return checkFinishedQuests(qsl) && checkUnfinishedQuests(qsl) && checkAcquiredQuests(qsl) && checkNoAcquiredQuests(qsl) && checkEquippedItems(player, warn);
	}
	
	/**
	 * Verifies if the player has received the correct amount of rewards for a quest.<br>
	 * This method handles specific exceptions for certain quest IDs.
	 * @param questId The unique identifier for the quest.
	 * @param neededReward The total reward amount required.
	 * @param currentReward The reward amount currently held by the player.
	 * @return {@code true} if the rewards are correct or exempt, {@code false} otherwise.
	 */
	private boolean checkReward(int questId, int neededReward, int currentReward)
	{
		// Temporary exceptions-quests till abyss entry quests work with correct reward
		if ((neededReward != currentReward) && (questId != 2947) && (questId != 1922))
		{
			return false;
		}
		
		return true;
	}
	
	/**
	 * Retrieves the list of completed quest requirements.<br>
	 * This method returns the {@code finished} conditions defined in the data.
	 * @return A {@code List} of {@link FinishedQuestCond} objects.
	 */
	public List<FinishedQuestCond> getFinishedPreconditions()
	{
		return finished;
	}
}
