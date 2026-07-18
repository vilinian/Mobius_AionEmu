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
package com.aionemu.gameserver.services.item;

import java.util.ArrayList;
import java.util.List;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.dao.RealItemRndBonusDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.RealRandomBonus;
import com.aionemu.gameserver.model.items.RealRandomBonusStat;
import com.aionemu.gameserver.model.stats.calc.functions.StatFunction;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.model.stats.listeners.ItemEquipmentListener;
import com.aionemu.gameserver.model.templates.item.bonuses.BonusStat;
import com.aionemu.gameserver.model.templates.item.bonuses.RealItemRandomBonus;

/**
 * This service handles the logic for processing {@link RealRandomBonus} objects.<br>
 * It manages how random item bonuses are applied and calculated for players.<br>
 * Use this class to interact with real-time random statistics on equipment.
 */
public class RealRandomBonusService
{
	/**
	 * This method assigns random bonuses to a specific {@code Item}.<br>
	 * It retrieves the bonus template from {@link DataManager}.<br>
	 * The method generates unique stats and updates the database.
	 * @param item The {@code Item} object to receive new bonuses.
	 */
	public static void setBonus(Item item)
	{
		final RealItemRandomBonus rndBonus = DataManager.ITEM_REAL_RANDOM_BONUSES.getRealBonusById(item.getItemTemplate().getRealRndBonus());
		if (rndBonus != null)
		{
			final List<RealRandomBonusStat> statsList = new ArrayList<>();
			final List<BonusStat> stats = new ArrayList<>();
			stats.addAll(rndBonus.getRndStat());
			for (int i = 0; i < rndBonus.getRandomNumber(); ++i)
			{
				final BonusStat stat = stats.get(Rnd.get(0, stats.size() - 1));
				statsList.add(new RealRandomBonusStat(stat.getName(), Rnd.get(stat.getMin(), stat.getMax()), false));
				stats.remove(stat);
			}
			
			final RealRandomBonus bonus = new RealRandomBonus(item.getObjectId(), statsList);
			item.setRealRndBonus(bonus);
			DAOManager.getDAO(RealItemRndBonusDAO.class).updateRandomBonuses(bonus);
		}
	}
	
	/**
	 * This method rerolls all random bonuses for a specific item.<br>
	 * It removes existing bonuses and generates new ones for the {@link Player}.<br>
	 * The method also preserves any fusion stats during the process.
	 * @param player The {@code Player} who owns the item.
	 * @param item The {@code Item} to be updated.
	 */
	public static void rerollAllBonuses(Player player, Item item)
	{
		DAOManager.getDAO(RealItemRndBonusDAO.class).deleteMainRandomBonuses(item);
		final List<StatFunction> fusionStat = item.getRealRndBonus().getFusionFunctions();
		item.setRealRndBonus(null);
		setBonus(item);
		item.getRealRndBonus().getFusionFunctions().addAll(fusionStat);
		refreshStats(player, item);
	}
	
	/**
	 * Rerolls a specific random bonus for an item held by a player.<br>
	 * This method updates the value of a single stat based on its range.<br>
	 * It also refreshes the player's stats and updates the database.
	 * @param player The {@link Player} who owns the item.
	 * @param item The {@link Item} to be modified.
	 * @param statId The unique identifier for the stat to reroll.
	 */
	public static void rerollSingleBonus(Player player, Item item, int statId)
	{
		final RealItemRandomBonus rndBonus = DataManager.ITEM_REAL_RANDOM_BONUSES.getRealBonusById(item.getItemTemplate().getRealRndBonus());
		final StatEnum statName = StatEnum.findByItemStoneMask(statId);
		final BonusStat stat = rndBonus.getRndStat().get(statId);
		final int value = Rnd.get(stat.getMin(), stat.getMax());
		final RealRandomBonus bonus = item.getRealRndBonus();
		RealRandomBonusStat oldStat = null;
		final RealRandomBonusStat newStat = new RealRandomBonusStat(statName, value, false);
		for (RealRandomBonusStat rs : bonus.getStats())
		{
			if (!rs.isFusion() && rs.getStat().equals(statName))
			{
				oldStat = rs;
			}
		}
		
		bonus.getStats().remove(oldStat);
		bonus.getStats().add(newStat);
		bonus.recalcStats();
		refreshStats(player, item);
		DAOManager.getDAO(RealItemRndBonusDAO.class).deleteMainRandomBonuses(item);
		DAOManager.getDAO(RealItemRndBonusDAO.class).updateRandomBonuses(bonus);
	}
	
	/**
	 * Adds random bonuses from one item to another during a fusion process.<br>
	 * This method updates the database and merges the {@code StatFunction} list.<br>
	 * It also refreshes the stats for the resulting item.
	 * @param player The {@link Player} who is performing the action.
	 * @param firstItem The primary {@link Item} that will receive the new bonuses.
	 * @param secondItem The secondary {@link Item} whose bonuses will be transferred.
	 */
	public static void addFusionRandomBonuses(Player player, Item firstItem, Item secondItem)
	{
		if (secondItem.getRealRndBonus() != null)
		{
			DAOManager.getDAO(RealItemRndBonusDAO.class).updateFusionRandomBonuses(firstItem, secondItem);
			DAOManager.getDAO(RealItemRndBonusDAO.class).deleteAllRandomBonuses(secondItem);
			if (firstItem.getRealRndBonus() != null)
			{
				final RealRandomBonus bonus = firstItem.getRealRndBonus();
				bonus.getFusionFunctions().addAll(secondItem.getRealRndBonus().getFunctions());
				refreshStats(player, firstItem);
			}
			
			secondItem.setRealRndBonus(null);
		}
	}
	
	/**
	 * Removes fusion random bonuses from a specific {@link Item}.<br>
	 * This method clears the bonus functions and updates the database.<br>
	 * It also triggers a stat refresh for the {@link Player}.
	 * @param player The {@link Player} who owns the item.
	 * @param item The {@link Item} to have its fusion bonuses removed from.
	 */
	public static void deleteFusionRandomBonuses(Player player, Item item)
	{
		if (item.getRealRndBonus() != null)
		{
			final RealRandomBonus bonus = item.getRealRndBonus();
			bonus.getFusionFunctions().clear();
			DAOManager.getDAO(RealItemRndBonusDAO.class).deleteFusionRandomBonuses(item);
			refreshStats(player, item);
		}
	}
	
	/**
	 * Updates the character statistics for a specific item.<br>
	 * This method triggers equipment events to recalculate values.<br>
	 * It ensures that the {@link Player} reflects changes made to the {@code Item}.
	 * @param player The {@link Player} whose stats need updating.
	 * @param item The {@link Item} being equipped or modified.
	 */
	private static void refreshStats(Player player, Item item)
	{
		if (item.isEquipped())
		{
			ItemEquipmentListener.onItemUnequipment(item, player);
			ItemEquipmentListener.onItemEquipment(item, player);
		}
	}
}
