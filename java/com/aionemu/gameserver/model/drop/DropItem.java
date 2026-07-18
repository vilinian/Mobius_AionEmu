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
package com.aionemu.gameserver.model.drop;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.model.Petition;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;

/**
 * Represents an item that can be dropped by a monster or NPC.<br>
 * It contains the data needed to determine which {@link ItemTemplate} is awarded during a drop event.
 * @author ATracer
 */
public class DropItem
{
	private int index = 0;
	private long count = 0;
	private final Drop dropTemplate;
	private int playerObjId = 0;
	private boolean isFreeForAll = false;
	private long highestValue = 0;
	private Player winningPlayer = null;
	private boolean isItemWonNotCollected = false;
	private boolean isDistributeItem = false;
	private int npcObj;
	private int optionalSocket = 0;
	
	/**
	 * Creates a new {@code DropItem} instance based on a provided template.<br>
	 * This constructor initializes the item data and checks for optional socket bonuses.
	 * @param dropTemplate The {@link Drop} object used to define the item properties.
	 */
	public DropItem(Drop dropTemplate)
	{
		this.dropTemplate = dropTemplate;
		final ItemTemplate template = dropTemplate.getItemTemplate();
		final int optionalBonus = template.getOptionSlotBonus();
		if (optionalBonus != 0)
		{
			optionalSocket = -1;
		}
	}
	
	/**
	 * Calculates the number of items to be dropped.<br>
	 * This method uses {@code getMinAmount} and {@code getMaxAmount} to pick a random value.<br>
	 * The result is stored in the {@code count} field.
	 */
	public void calculateCount()
	{
		count = Rnd.get(dropTemplate.getMinAmount(), dropTemplate.getMaxAmount());
	}
	
	/**
	 * Retrieves the current index of this {@code Triangle}.<br>
	 * This value is used to identify the triangle in a collection.
	 * @return The integer value of the {@code index}.
	 */
	public int getIndex()
	{
		return index;
	}
	
	/**
	 * Sets the unique identifier for this {@code Triangle}.<br>
	 * This value is used to identify the triangle in a collection.
	 * @param index The new integer value to assign to the triangle.
	 */
	public void setIndex(int index)
	{
		this.index = index;
	}
	
	/**
	 * Retrieves the current quantity of the item.<br>
	 * This value is updated by the {@code calculateCount} method.
	 * @return The total number of items as a {@code long}.
	 */
	public long getCount()
	{
		return count;
	}
	
	/**
	 * Sets the quantity of the item.<br>
	 * This updates the {@code count} field of this {@link DropItem}.
	 * @param count The new number of items to set.
	 */
	public void setCount(long count)
	{
		this.count = count;
	}
	
	/**
	 * Retrieves the base template for this drop.<br>
	 * This method returns the {@code Drop} object associated with this instance.
	 * @return The {@code Drop} template.
	 */
	public Drop getDropTemplate()
	{
		return dropTemplate;
	}
	
	/**
	 * Retrieves the unique object identifier for the player.<br>
	 * This value is used to identify which player created the {@link Petition}.
	 * @return The {@code int} value of the player's object ID.
	 */
	public int getPlayerObjId()
	{
		return playerObjId;
	}
	
	/**
	 * Sets the unique identifier for the player associated with this drop.<br>
	 * This value is stored in the {@code playerObjId} field.
	 * @param playerObjId The new ID to assign to the player.
	 */
	public void setPlayerObjId(int playerObjId)
	{
		this.playerObjId = playerObjId;
	}
	
	/**
	 * Updates the {@code isFreeForAll} status of this drop item.<br>
	 * This determines if the item can be claimed by any player.
	 * @param isFreeForAll The new value to set for the free-for-all status.
	 */
	public void isFreeForAll(boolean isFreeForAll)
	{
		this.isFreeForAll = isFreeForAll;
	}
	
	/**
	 * Checks if the item drop is available for everyone.<br>
	 * Returns {@code true} if it is free for all players.<br>
	 * Returns {@code false} otherwise.
	 * @return The current status of the free-for-all flag.
	 */
	public boolean isFreeForAll()
	{
		return isFreeForAll;
	}
	
	/**
	 * Retrieves the highest value recorded for this drop.<br>
	 * This value is used to determine the winning bid or amount.
	 * @return The {@code long} value of the highest record.
	 */
	public long getHighestValue()
	{
		return highestValue;
	}
	
	/**
	 * Sets the maximum value for this drop item.<br>
	 * This updates the {@code highestValue} field.
	 * @param highestValue The new value to assign to the drop.
	 */
	public void setHighestValue(long highestValue)
	{
		this.highestValue = highestValue;
	}
	
	/**
	 * Sets the player who won the item.<br>
	 * This updates the {@code winningPlayer} field of this {@link DropItem}.
	 * @param winningPlayer The {@code Player} object that won the drop.
	 */
	public void setWinningPlayer(Player winningPlayer)
	{
		this.winningPlayer = winningPlayer;
		
	}
	
	/**
	 * Retrieves the {@link Player} who won the item.<br>
	 * Returns {@code null} if no winner has been determined.
	 * @return The {@code Player} object of the winner.
	 */
	public Player getWinningPlayer()
	{
		return winningPlayer;
	}
	
	/**
	 * Updates the status of an item that has been won but not yet collected.<br>
	 * This method sets the {@code isItemWonNotCollected} flag to a new value.
	 * @param isItemWonNotCollected The new boolean status for the item collection state.
	 */
	public void isItemWonNotCollected(boolean isItemWonNotCollected)
	{
		this.isItemWonNotCollected = isItemWonNotCollected;
	}
	
	/**
	 * Checks if an item has been won but not yet collected by a player.<br>
	 * This helps determine if the reward is still available in the world.
	 * @return {@code true} if the item is won and uncollected, {@code false} otherwise.
	 */
	public boolean isItemWonNotCollected()
	{
		return isItemWonNotCollected;
	}
	
	/**
	 * Updates the distribution status of an item.<br>
	 * This method sets whether the item should be distributed to players.
	 * @param isDistributeItem The new value for {@code isDistributeItem}.
	 */
	public void isDistributeItem(boolean isDistributeItem)
	{
		this.isDistributeItem = isDistributeItem;
	}
	
	/**
	 * Checks if the item should be distributed.<br>
	 * This method returns the current state of the {@code isDistributeItem} flag.
	 * @return {@code true} if the item is for distribution, {@code false} otherwise.
	 */
	public boolean isDistributeItem()
	{
		return isDistributeItem;
	}
	
	/**
	 * Retrieves the unique identifier for the NPC object.<br>
	 * This value is used to identify which NPC owns this drop.
	 * @return The {@code int} ID of the NPC object.
	 */
	public int getNpcObj()
	{
		return npcObj;
	}
	
	/**
	 * Sets the {@code npcObj} identifier for this drop item.<br>
	 * This value is used to link the item to a specific NPC object.
	 * @param npcObj The unique ID of the NPC object.
	 */
	public void setNpcObj(int npcObj)
	{
		this.npcObj = npcObj;
	}
	
	/**
	 * Retrieves the value of the optional socket.<br>
	 * This method returns the {@code int} value stored in the {@code optionalSocket} field.
	 * @return The current value of the optional socket.
	 */
	public int getOptionalSocket()
	{
		return optionalSocket;
	}
}
