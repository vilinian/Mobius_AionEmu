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
package com.aionemu.gameserver.model.team2.common.legacy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.aionemu.gameserver.model.actions.PlayerMode;
import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.gameobjects.player.InRoll;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemQuality;
import com.aionemu.gameserver.services.drop.DropDistributionService;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * Defines the logic and constraints for loot distribution within a group.<br>
 * It manages how {@link DropItem} objects are shared among {@link Player} instances.<br>
 * This class ensures that loot rules are applied consistently across different game scenarios.
 * @author ATracer, xTz
 */
public class LootGroupRules
{
	private final LootRuleType lootRule;
	private final LootDistribution autodistribution;
	private final int common_item_above;
	private final int superior_item_above;
	private final int heroic_item_above;
	private final int fabled_item_above;
	private final int ethernal_item_above;
	private final int mythic_item_above;
	private final int ancient_item_above;
	private final int relic_item_above;
	private final int finality_item_above;
	private int misc;
	private int nrMisc;
	private int nrRoundRobin;
	private final List<DropItem> itemsToBeDistributed = new ArrayList<>();
	
	/**
	 * Creates a new instance of {@code LootGroupRules} with default values.<br>
	 * This constructor initializes the loot rules to {@code ROUNDROBIN}.<br>
	 * It sets the distribution type to {@code ROLL_DICE}.
	 */
	public LootGroupRules()
	{
		lootRule = LootRuleType.ROUNDROBIN;
		autodistribution = LootDistribution.ROLL_DICE;
		common_item_above = 0;
		superior_item_above = 2;
		heroic_item_above = 2;
		fabled_item_above = 2;
		ethernal_item_above = 2;
		mythic_item_above = 2;
		ancient_item_above = 2;
		relic_item_above = 2;
		finality_item_above = 2;
		
	}
	
	/**
	 * Creates a new {@code LootGroupRules} instance with specific distribution settings.<br>
	 * This constructor defines the rules for how loot is shared among players.
	 * @param lootRule The type of rule to apply for looting.
	 * @param autodistribution The method used for automatic item distribution.
	 * @param commonItemAbove The threshold value for common items.
	 * @param superiorItemAbove The threshold value for superior items.
	 * @param heroicItemAbove The threshold value for heroic items.
	 * @param fabledItemAbove The threshold value for fabled items.
	 * @param ethernalItemAbove The threshold value for ethernal items.
	 * @param mythicItemAbove The threshold value for mythic items.
	 * @param ancientItemAbove The threshold value for ancient items.
	 * @param relicItemAbove The threshold value for relic items.
	 * @param finalityItemAbove The threshold value for finality items.
	 * @param misc The miscellaneous item count.
	 */
	public LootGroupRules(LootRuleType lootRule, LootDistribution autodistribution, int commonItemAbove, int superiorItemAbove, int heroicItemAbove, int fabledItemAbove, int ethernalItemAbove, int mythicItemAbove, int ancientItemAbove, int relicItemAbove, int finalityItemAbove, int misc)
	{
		super();
		this.lootRule = lootRule;
		this.autodistribution = autodistribution;
		this.misc = misc;
		common_item_above = commonItemAbove;
		superior_item_above = superiorItemAbove;
		heroic_item_above = heroicItemAbove;
		fabled_item_above = fabledItemAbove;
		ethernal_item_above = ethernalItemAbove;
		mythic_item_above = mythicItemAbove;
		ancient_item_above = ancientItemAbove;
		relic_item_above = relicItemAbove;
		finality_item_above = finalityItemAbove;
		
	}
	
	/**
	 * Checks if a specific item quality has an active rule.<br>
	 * This method compares the provided {@code ItemQuality} against the configured thresholds.<br>
	 * It returns {@code true} if the threshold for that quality is not zero.
	 * @param quality The {@link ItemQuality} to check.
	 * @return {@code true} if a rule exists for the given quality, otherwise {@code false}.
	 */
	public boolean getQualityRule(ItemQuality quality)
	{
		switch (quality)
		{
			case COMMON: // White
				return common_item_above != 0;
			case RARE: // Green
				return superior_item_above != 0;
			case LEGEND: // Blue
				return heroic_item_above != 0;
			case UNIQUE: // Yellow
				return fabled_item_above != 0;
			case EPIC: // Orange
				return ethernal_item_above != 0;
			case MYTHIC: // Purple
				return mythic_item_above != 0;
			case ANCIENT:
				return ancient_item_above != 0;
			case RELIC:
				return relic_item_above != 0;
			case FINALITY:
				return finality_item_above != 0;
			default:
				break;
		}
		
		return false;
	}
	
	/**
	 * Checks if the given item quality is considered a miscellaneous item.<br>
	 * This returns {@code true} only if the quality is {@code JUNK} and the misc count is set to {@code 1}.
	 * @param quality The {@link ItemQuality} to check.
	 * @return {@code true} if the quality matches the miscellaneous criteria, otherwise {@code false}.
	 */
	public boolean isMisc(ItemQuality quality)
	{
		return quality.equals(ItemQuality.JUNK) && (misc == 1);
	}
	
	/**
	 * Retrieves the current {@code LootRuleType} for this group.<br>
	 * This value determines how items are distributed among players.
	 * @return The {@code LootRuleType} assigned to these rules.
	 */
	public LootRuleType getLootRule()
	{
		return lootRule;
	}
	
	/**
	 * Retrieves the automatic distribution settings for loot.<br>
	 * This method returns the {@code LootDistribution} object associated with these rules.
	 * @return The current {@code LootDistribution} configuration.
	 */
	public LootDistribution getAutodistribution()
	{
		return autodistribution;
	}
	
	/**
	 * Retrieves the threshold value for common items.<br>
	 * This value determines the limit for {@code common_item_above}.
	 * @return The integer value of the common item threshold.
	 */
	public int getCommonItemAbove()
	{
		return common_item_above;
	}
	
	/**
	 * Retrieves the threshold value for superior items.<br>
	 * This value determines the limit for {@code ItemQuality.SUPERIOR} types.
	 * @return The integer value representing the superior item limit.
	 */
	public int getSuperiorItemAbove()
	{
		return superior_item_above;
	}
	
	/**
	 * Retrieves the threshold value for {@code Heroic} quality items.<br>
	 * This value determines the limit for loot distribution rules.
	 * @return The integer value of the {@code heroic_item_above} field.
	 */
	public int getHeroicItemAbove()
	{
		return heroic_item_above;
	}
	
	/**
	 * Retrieves the threshold value for {@code fabled} quality items.<br>
	 * This value determines the limit for loot distribution rules.
	 * @return The integer value of the {@code fabled_item_above} property.
	 */
	public int getFabledItemAbove()
	{
		return fabled_item_above;
	}
	
	/**
	 * Retrieves the threshold for {@code ethernal} quality items.<br>
	 * This value determines the limit for distribution rules.
	 * @return The integer value of the {@code ethernal_item_above} limit.
	 */
	public int getEthernalItemAbove()
	{
		return ethernal_item_above;
	}
	
	/**
	 * Retrieves the threshold for {@code mythic} quality items.<br>
	 * This value determines the limit for loot distribution rules.
	 * @return the integer value of {@code mythic_item_above}
	 */
	public int getMythicItemAbove()
	{
		return mythic_item_above;
	}
	
	/**
	 * Retrieves the threshold value for {@code ancient} quality items.<br>
	 * This value determines the limit for loot distribution rules.
	 * @return The integer value of the {@code ancient_item_above} property.
	 */
	public int getAncientItemAbove()
	{
		return ancient_item_above;
	}
	
	/**
	 * Retrieves the threshold value for {@code relic} quality items.<br>
	 * This value determines the limit for distributing these specific items.
	 * @return The integer value representing the {@code relic} item limit.
	 */
	public int getRelicItemAbove()
	{
		return relic_item_above;
	}
	
	/**
	 * Retrieves the threshold value for {@code finality} quality items.<br>
	 * This value determines the limit for these specific items in loot distribution.
	 * @return The integer value representing the {@code finality_item_above} limit.
	 */
	public int getFinalityItemAbove()
	{
		return finality_item_above;
	}
	
	/**
	 * Retrieves the number of miscellaneous items.<br>
	 * This value is used to determine distribution rules.
	 * @return the current count of {@code nrMisc} items.
	 */
	public int getNrMisc()
	{
		return nrMisc;
	}
	
	/**
	 * Sets the number of miscellaneous items.<br>
	 * This updates the {@code nrMisc} field in this class.
	 * @param nrMisc The new count for miscellaneous items.
	 */
	public void setNrMisc(int nrMisc)
	{
		this.nrMisc = nrMisc;
	}
	
	/**
	 * Schedules a task to process loot distribution for specific players.<br>
	 * This method checks if players are in the {@code IN_ROLL} mode.<br>
	 * It handles rolls or bids based on the provided index and NPC ID.
	 * @param players The collection of {@link Player} objects to check.
	 * @param time The delay in milliseconds before executing the task.
	 * @param index The specific roll index to match.
	 * @param npcId The unique identifier for the NPC involved in the roll.
	 */
	public void setPlayersInRoll(Collection<Player> players, int time, int index, int npcId)
	{
		ThreadPoolManager.getInstance().schedule(new Runnable()
		{
			@Override
			public void run()
			{
				for (Player player : players)
				{
					if (player.isInPlayerMode(PlayerMode.IN_ROLL))
					{
						final InRoll inRoll = player.inRoll;
						switch (inRoll.getRollType())
						{
							case 2:
								if ((inRoll.getIndex() == index) && (inRoll.getNpcId() == npcId))
								{
									DropDistributionService.getInstance().handleRoll(player, 0, inRoll.getItemId(), inRoll.getNpcId(), inRoll.getIndex());
								}
								break;
							case 3:
								if ((inRoll.getIndex() == index) && (inRoll.getNpcId() == npcId))
								{
									DropDistributionService.getInstance().handleBid(player, 0, inRoll.getItemId(), inRoll.getNpcId(), inRoll.getIndex());
								}
								break;
						}
					}
				}
			}
		}, time);
	}
	
	/**
	 * Retrieves the number of round robin cycles.<br>
	 * This value is used to determine distribution logic.
	 * @return The current {@code int} value for {@code nrRoundRobin}.
	 */
	public int getNrRoundRobin()
	{
		return nrRoundRobin;
	}
	
	/**
	 * Sets the number of round robin cycles.<br>
	 * This value determines how many times the distribution rotates.
	 * @param nrRoundRobin The new number for {@code nrRoundRobin}.
	 */
	public void setNrRoundRobin(int nrRoundRobin)
	{
		this.nrRoundRobin = nrRoundRobin;
	}
	
	/**
	 * Retrieves the miscellaneous value for this loot group.<br>
	 * This value is used to determine specific distribution rules.
	 * @return The current {@code int} value of {@code misc}.
	 */
	public int getMisc()
	{
		return misc;
	}
	
	/**
	 * Adds a new item to the list of items that need to be distributed.<br>
	 * This method stores the {@code dropItem} in the internal collection.
	 * @param dropItem The {@link DropItem} object to add to the distribution queue.
	 */
	public void addItemToBeDistributed(DropItem dropItem)
	{
		itemsToBeDistributed.add(dropItem);
	}
	
	/**
	 * Checks if a specific item is in the distribution list.<br>
	 * This method verifies if the {@code dropItem} exists within the internal collection.
	 * @param dropItem The {@link DropItem} to search for.
	 * @return {@code true} if the item is found, otherwise {@code false}.
	 */
	public boolean containDropItem(DropItem dropItem)
	{
		return itemsToBeDistributed.contains(dropItem);
	}
	
	/**
	 * Removes a specific item from the distribution list.<br>
	 * This method updates the internal {@code itemsToBeDistributed} collection.
	 * @param dropItem The {@code DropItem} object to be removed.
	 */
	public void removeItemToBeDistributed(DropItem dropItem)
	{
		itemsToBeDistributed.remove(dropItem);
	}
	
	/**
	 * Retrieves the list of items that need to be distributed.<br>
	 * This method returns the internal {@code List} containing all pending {@link DropItem} objects.
	 * @return A {@code List} of {@link DropItem} objects to be distributed.
	 */
	public List<DropItem> getItemsToBeDistributed()
	{
		return itemsToBeDistributed;
	}
}
