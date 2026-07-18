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
package com.aionemu.gameserver.services.reward;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.dataholders.ItemGroupsData;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.QuestTemplate;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.itemgroups.BonusItemGroup;
import com.aionemu.gameserver.model.templates.itemgroups.CraftGroup;
import com.aionemu.gameserver.model.templates.itemgroups.ItemRaceEntry;
import com.aionemu.gameserver.model.templates.itemgroups.ManastoneGroup;
import com.aionemu.gameserver.model.templates.itemgroups.MedalGroup;
import com.aionemu.gameserver.model.templates.quest.QuestBonuses;
import com.aionemu.gameserver.model.templates.quest.QuestItems;
import com.aionemu.gameserver.model.templates.rewards.BonusType;
import com.aionemu.gameserver.model.templates.rewards.CraftItem;
import com.aionemu.gameserver.model.templates.rewards.MedalItem;

/**
 * This service handles the logic for processing and distributing various types of rewards.<br>
 * It manages bonuses such as {@code CraftItem}, {@code MedalItem}, and other quest-related rewards.<br>
 * You can use this class to interact with {@link QuestTemplate} data to grant items or effects to a {@link Player}.
 * @author Rolandas
 */
public class BonusService
{
	private static BonusService instance = new BonusService();
	private ItemGroupsData itemGroups = DataManager.ITEM_GROUPS_DATA;
	private static final Logger log = LoggerFactory.getLogger(BonusService.class);
	
	/**
	 * Private constructor for the {@link BonusService} class.<br>
	 * This prevents other classes from creating new instances of this service.<br>
	 * It ensures that only the singleton instance is used throughout the application.
	 */
	private BonusService()
	{
	}
	
	/**
	 * Provides the global singleton instance of the {@link BonusService}.<br>
	 * Use this method to access the service from anywhere in the code.
	 * @return The single shared instance of {@code BonusService}.
	 */
	public static BonusService getInstance()
	{
		return instance;
	}
	
	/**
	 * Gets the singleton instance of {@link BonusService}.<br>
	 * This method updates the internal data with the provided {@code ItemGroupsData}.
	 * @param itemGroups The new data to use for bonus calculations.
	 * @return The active {@code BonusService} instance.
	 */
	public static BonusService getInstance(ItemGroupsData itemGroups)
	{
		instance.itemGroups = itemGroups;
		return instance;
	}
	
	/**
	 * Retrieves a list of {@link BonusItemGroup} objects based on the specified category.<br>
	 * This method filters groups from the data manager using the provided {@code type}.<br>
	 * It returns {@code null} if the type is not implemented or supported.
	 * @param type The {@code BonusType} used to filter the item groups.
	 * @return An array of {@link BonusItemGroup} matching the category, or {@code null}.
	 */
	public BonusItemGroup[] getGroupsByType(BonusType type)
	{
		switch (type)
		{
			case BOSS:
				return itemGroups.getBossGroups();
			case ENCHANT:
				return itemGroups.getEnchantGroups();
			case FOOD:
				return itemGroups.getFoodGroups();
			case GATHER:
			{
				final BonusItemGroup[] oreGroups = itemGroups.getOreGroups();
				final BonusItemGroup[] gatherGroups = itemGroups.getGatherGroups();
				final BonusItemGroup[] _joined = Arrays.copyOf(oreGroups, oreGroups.length + gatherGroups.length);
				System.arraycopy(gatherGroups, 0, _joined, oreGroups.length, gatherGroups.length);
				return _joined;
			}
			case MANASTONE:
				return itemGroups.getManastoneGroups();
			case MEDICINE:
				return itemGroups.getMedicineGroups();
			case TASK:
				return itemGroups.getCraftGroups();
			case MOVIE:
				return null;
			default:
				log.warn("Bonus of type " + type + " is not implemented");
				return null;
		}
	}
	
	/**
	 * Selects a random group from an array of groups based on their individual chances.<br>
	 * This method calculates the total weight and picks one {@code BonusItemGroup}.<br>
	 * It returns {@code null} if the input array is {@code null} or if all weights are zero.
	 * @param groups The array of {@code BonusItemGroup} objects to choose from.
	 * @return The selected {@code BonusItemGroup} based on random probability, or {@code null}.
	 */
	public BonusItemGroup getRandomGroup(BonusItemGroup[] groups)
	{
		float total = 0;
		if (groups == null)
		{
			return null;
		}
		
		for (BonusItemGroup gr : groups)
		{
			total += gr.getChance();
		}
		
		if (total == 0)
		{
			return null;
		}
		
		BonusItemGroup chosenGroup = null;
		int percent = 100;
		for (BonusItemGroup gr : groups)
		{
			final float chance = getNormalizedChance(gr.getChance(), total);
			if (Rnd.get(0, percent) <= chance)
			{
				chosenGroup = gr;
				break;
			}
			
			percent -= chance;
		}
		
		return chosenGroup;
	}
	
	/**
	 * This method calculates a percentage based on two values.<br>
	 * It converts the {@code chance} into a value between {@code 0.0} and {@code 100.0}.<br>
	 * The result is then divided by the {@code total} amount.
	 * @param chance The raw weight or chance value to be processed.
	 * @param total The total sum of all possible weights.
	 * @return The calculated percentage as a {@code float}.
	 */
	float getNormalizedChance(float chance, float total)
	{
		return (chance * 100f) / total;
	}
	
	/**
	 * Selects a random group of items based on the specified reward type.<br>
	 * This method first retrieves all groups for the given {@code BonusType}.<br>
	 * It then picks one group from that list at random.
	 * @param type The category of bonus to filter by.
	 * @return A randomly selected {@link BonusItemGroup} object.
	 */
	public BonusItemGroup getRandomGroup(BonusType type)
	{
		return getRandomGroup(getGroupsByType(type));
	}
	
	/**
	 * Retrieves the quest bonus items for a specific player and quest.<br>
	 * This method checks the {@code QuestTemplate} for available bonuses.<br>
	 * It returns different rewards based on the {@link BonusType}.
	 * @param player The {@link Player} receiving the reward.
	 * @param questTemplate The {@link QuestTemplate} containing the bonus data.
	 * @return A {@link QuestItems} object containing the rewards, or {@code null} if no valid bonus exists.
	 */
	public QuestItems getQuestBonus(Player player, QuestTemplate questTemplate)
	{
		final List<QuestBonuses> bonuses = questTemplate.getBonus();
		if (bonuses.isEmpty())
		{
			return null;
		}
		
		// Only one
		final QuestBonuses bonus = bonuses.get(0);
		if (bonus.getType() == BonusType.NONE)
		{
			return null;
		}
		
		switch (bonus.getType())
		{
			case TASK:
				return getCraftBonus(player, questTemplate);
			case MANASTONE:
				return getManastoneBonus(player, bonus);
			case MEDAL:
				return getMedalBonus(player, questTemplate);
			case MOVIE:
				return null;
			default:
				log.warn("Bonus of type " + bonus.getType() + " is not implemented");
				return null;
		}
	}
	
	/**
	 * Calculates the crafting bonus for a specific player and quest.<br>
	 * This method selects a random reward from available craft groups based on skill requirements.<br>
	 * It filters rewards by the player's race and determines the final item count.
	 * @param player The {@link Player} receiving the reward.
	 * @param questTemplate The {@link QuestTemplate} containing the crafting requirements.
	 * @return A {@link QuestItems} object containing the reward ID and amount, or {@code null} if no reward is found.
	 */
	QuestItems getCraftBonus(Player player, QuestTemplate questTemplate)
	{
		BonusItemGroup[] groups = itemGroups.getCraftGroups();
		CraftGroup group = null;
		ItemRaceEntry[] allRewards = null;
		
		while ((groups != null) && (groups.length > 0) && (group == null))
		{
			group = (CraftGroup) getRandomGroup(groups);
			if (group == null)
			{
				break;
			}
			
			allRewards = group.getRewards(questTemplate.getCombineSkill(), questTemplate.getCombineSkillPoint());
			if (allRewards.length == 0)
			{
				final List<BonusItemGroup> temp = new ArrayList<>();
				Collections.addAll(temp, groups);
				temp.remove(group);
				group = null;
				groups = temp.toArray(new BonusItemGroup[0]);
			}
		}
		
		if (group == null) // probably all chances set to 0
		{
			return null;
		}
		
		final List<ItemRaceEntry> finalList = new ArrayList<>();
		
		if (allRewards != null)
		{
			for (int i = 0; i < allRewards.length; i++)
			{
				final ItemRaceEntry r = allRewards[i];
				if (!r.checkRace(player.getCommonData().getRace()))
				{
					continue;
				}
				
				finalList.add(r);
			}
		}
		
		if (finalList.isEmpty())
		{
			return null;
		}
		
		final int itemIndex = Rnd.get(finalList.size());
		int itemCount = 1;
		
		final ItemRaceEntry reward = finalList.get(itemIndex);
		if (reward instanceof CraftItem)
		{
			itemCount = Rnd.get(3, 5);
		}
		
		return new QuestItems(reward.getId(), itemCount);
	}
	
	/**
	 * Calculates the random medal bonus for a specific player and quest.<br>
	 * This method selects an item from a {@code MedalGroup} based on the level defined in the {@code QuestTemplate}.<br>
	 * It returns a {@code QuestItems} object if a valid reward is found.
	 * @param player The {@link Player} receiving the reward.
	 * @param template The {@link QuestTemplate} containing the bonus requirements.
	 * @return A {@code QuestItems} object representing the rewarded medal, or {@code null} if no reward is available.
	 */
	QuestItems getMedalBonus(Player player, QuestTemplate template)
	{
		final BonusItemGroup[] groups = itemGroups.getMedalGroups();
		final MedalGroup group = (MedalGroup) getRandomGroup(groups);
		final int bonusLevel = template.getBonus().get(0).getLevel();
		
		MedalItem finalReward = null;
		
		float total = 0;
		for (MedalItem medal : group.getItems())
		{
			if (medal.getLevel() == bonusLevel)
			{
				total += medal.getChance();
			}
		}
		
		if (total == 0)
		{
			return null;
		}
		
		final float rnd = (Rnd.get() * total);
		float luck = 0;
		for (MedalItem medal : group.getItems())
		{
			if (medal.getLevel() != bonusLevel)
			{
				continue;
			}
			
			luck += medal.getChance();
			
			if (rnd <= luck)
			{
				finalReward = medal;
				break;
			}
		}
		
		return finalReward != null ? new QuestItems(finalReward.getId(), finalReward.getCount()) : null;
	}
	
	/**
	 * Calculates the manastone bonus for a specific player.<br>
	 * This method selects a random item from the {@code MANASTONE} group.<br>
	 * It filters the items based on the level defined in {@code QuestBonuses}.
	 * @param player The {@link Player} receiving the reward.
	 * @param bonus The {@link QuestBonuses} containing the required level.
	 * @return A new {@link QuestItems} object with the selected item ID and a count of 1, or {@code null} if no items match.
	 */
	QuestItems getManastoneBonus(Player player, QuestBonuses bonus)
	{
		final ManastoneGroup group = (ManastoneGroup) getRandomGroup(BonusType.MANASTONE);
		final ItemRaceEntry[] allRewards = group.getRewards();
		final List<ItemRaceEntry> finalList = new ArrayList<>();
		for (int i = 0; i < allRewards.length; i++)
		{
			final ItemRaceEntry r = allRewards[i];
			final ItemTemplate template = DataManager.ITEM_DATA.getItemTemplate(r.getId());
			if (bonus.getLevel() != template.getLevel())
			{
				continue;
			}
			
			finalList.add(r);
		}
		
		if (finalList.isEmpty())
		{
			return null;
		}
		
		final int itemIndex = Rnd.get(finalList.size());
		final ItemRaceEntry reward = finalList.get(itemIndex);
		return new QuestItems(reward.getId(), 1);
	}
}
