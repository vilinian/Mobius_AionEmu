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
package com.aionemu.gameserver.model.autogroup;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.aionemu.commons.taskmanager.AbstractLockManager;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.instance.instancereward.InstanceReward;
import com.aionemu.gameserver.world.WorldMapInstance;

/**
 * Represents a base model for automated game instances.<br>
 * This class handles the logic and state management for {@link WorldMapInstance} types that are managed automatically by the server.
 * @author xTz
 */
public abstract class AutoInstance extends AbstractLockManager implements AutoInstanceHandler
{
	protected int instanceMaskId;
	public long startInstanceTime;
	public WorldMapInstance instance;
	public AutoGroupType agt;
	public Map<Integer, AGPlayer> players = new HashMap<>();
	
	/**
	 * Reduces the quantity of a specific item from a player's inventory.<br>
	 * It checks if the player has enough items before removing them.<br>
	 * Items are removed starting from those with the earliest expiration time.
	 * @param player The {@link Player} who will lose the items.
	 * @param itemId The unique ID of the item to remove.
	 * @param count The total amount of the item to decrease.
	 * @return {@code true} if the items were successfully removed, or {@code false} if the player had insufficient stock.
	 */
	protected boolean decrease(Player player, int itemId, long count)
	{
		long i = 0;
		List<Item> items = player.getInventory().getItemsByItemId(itemId);
		for (Item findedItem : items)
		{
			i += findedItem.getItemCount();
		}
		
		if (i < count)
		{
			return false;
		}
		
		items = items.stream().sorted(Comparator.comparingInt(Item::getExpireTime)).collect(Collectors.toList());
		for (Item item : items)
		{
			final long l = player.getInventory().decreaseItemCount(item, count);
			if (l == 0)
			{
				break;
			}
			
			count = l;
		}
		
		return true;
	}
	
	/**
	 * Sets up the initial state for this auto instance.<br>
	 * It assigns the provided {@code instanceMaskId} to the internal field.<br>
	 * It also determines the {@link AutoGroupType} based on that ID.
	 * @param instanceMaskId The unique identifier used to mask the instance type.
	 */
	@Override
	public void initialize(int instanceMaskId)
	{
		this.instanceMaskId = instanceMaskId;
		agt = AutoGroupType.getAGTByMaskId(instanceMaskId);
	}
	
	/**
	 * This method is called when a new {@link WorldMapInstance} is created.<br>
	 * It initializes the local doors map from the provided instance.<br>
	 * It calls the superclass implementation of {@code onInstanceCreate}.
	 * @param instance The {@code WorldMapInstance} being created.
	 */
	@Override
	public void onInstanceCreate(WorldMapInstance instance)
	{
		this.instance = instance;
		startInstanceTime = System.currentTimeMillis();
	}
	
	/**
	 * Adds a {@link Player} to the current instance.<br>
	 * This method checks if the player meets all requirements for entry.<br>
	 * It handles both individual and group entry logic.
	 * @param player The {@link Player} attempting to join.
	 * @param searchInstance The {@link SearchInstance} containing the request details.
	 * @return An {@link AGQuestion} representing the result of the addition.
	 */
	@Override
	public AGQuestion addPlayer(Player player, SearchInstance searchInstance)
	{
		return AGQuestion.FAILED;
	}
	
	/**
	 * Updates the status of a {@link Player} when they enter the instance.<br>
	 * It sets the {@code inInstance} flag to {@code true}.<br>
	 * It also sets the {@code online} flag to {@code true} for the player.
	 * @param player The {@link Player} object who entered the instance.
	 */
	@Override
	public void onEnterInstance(Player player)
	{
		players.get(player.getObjectId()).setInInstance(true);
		players.get(player.getObjectId()).setOnline(true);
	}
	
	/**
	 * Handles the logic when a {@link Player} leaves this instance.<br>
	 * This method is called to clean up any specific data for the player.
	 * @param player The {@code Player} object who is leaving the instance.
	 */
	@Override
	public void onLeaveInstance(Player player)
	{
	}
	
	/**
	 * Updates the status of a {@link Player} who has pressed enter.<br>
	 * It sets the press enter flag to {@code true} for the specific player.
	 * @param player The {@code Player} who is pressing enter.
	 */
	@Override
	public void onPressEnter(Player player)
	{
		players.get(player.getObjectId()).setPressEnter(true);
	}
	
	/**
	 * Removes a {@link Player} from the internal group tracking.<br>
	 * This method checks if the {@code Player} exists in the {@code players} map.<br>
	 * If found, it removes the player using their unique object ID.
	 * @param player The {@code Player} to remove from the system.
	 */
	@Override
	public void unregister(Player player)
	{
		final Integer obj = player.getObjectId();
		if (players.containsKey(obj))
		{
			players.remove(obj);
		}
	}
	
	/**
	 * Removes all players from the {@code players} map.<br>
	 * This clears the current group of participants.<br>
	 * The size of the collection will become 0.
	 */
	@Override
	public void clear()
	{
		players.clear();
	}
	
	/**
	 * Checks if the instance can be entered based on time constraints.<br>
	 * It validates the current progress and group entry types.<br>
	 * This method ensures that the required duration has not been exceeded.
	 * @param searchInstance The {@link SearchInstance} being checked for entry requirements.
	 * @return {@code true} if the instance satisfies all time conditions, {@code false} otherwise.
	 */
	protected boolean satisfyTime(SearchInstance searchInstance)
	{
		if (instance != null)
		{
			final InstanceReward<?> instanceReward = instance.getInstanceHandler().getInstanceReward();
			if (((instanceReward != null) && instanceReward.getInstanceScoreType().isEndProgress()))
			{
				return false;
			}
		}
		
		if (!searchInstance.getEntryRequestType().isQuickGroupEntry())
		{
			return startInstanceTime == 0;
		}
		
		final int time = agt.getTime();
		if ((time == 0) || (startInstanceTime == 0))
		{
			return true;
		}
		
		return (System.currentTimeMillis() - startInstanceTime) < time;
	}
}
