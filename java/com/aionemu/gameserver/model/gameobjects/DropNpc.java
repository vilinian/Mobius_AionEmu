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
package com.aionemu.gameserver.model.gameobjects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * Represents an NPC that is responsible for dropping items.<br>
 * This class manages the loot logic associated with a {@link Player}.
 * @author Simple
 */
public class DropNpc
{
	private Collection<Integer> allowedList = new ArrayList<>();
	private Collection<Player> inRangePlayers = new ArrayList<>();
	private final Collection<Player> playerStatus = new ArrayList<>();
	private Player lootingPlayer = null;
	private int distributionId = 0;
	private boolean distributionType;
	private int currentIndex = 0;
	private int groupSize = 0;
	private boolean isFreeForAll = false;
	private final int npcUniqueId;
	private long reamingDecayTime;
	
	/**
	 * Creates a new instance of {@link DropNpc}.<br>
	 * This constructor initializes the object with a specific NPC identifier.
	 * @param npcUniqueId The unique identification number for the NPC.
	 */
	public DropNpc(int npcUniqueId)
	{
		this.npcUniqueId = npcUniqueId;
	}
	
	/**
	 * Updates the list of allowed player object IDs.<br>
	 * This method sets the {@code allowedList} field.
	 * @param allowedList The {@code List<Integer>} containing valid IDs.
	 */
	public void setPlayersObjectId(List<Integer> allowedList)
	{
		this.allowedList = allowedList;
	}
	
	/**
	 * Updates the list of allowed player IDs.<br>
	 * This method adds the provided {@code Integer} to the internal collection if it is not already present.
	 * @param object The unique ID of the player to add.
	 */
	public void setPlayerObjectId(Integer object)
	{
		if (!allowedList.contains(object))
		{
			allowedList.add(object);
		}
	}
	
	/**
	 * Retrieves the list of allowed player object IDs.<br>
	 * This method returns the {@code allowedList} collection.
	 * @return a {@code Collection<Integer>} containing the allowed player IDs.
	 */
	public Collection<Integer> getPlayersObjectId()
	{
		return allowedList;
	}
	
	/**
	 * Checks if a specific player ID exists in the allowed list.<br>
	 * This method verifies if the {@code playerObjId} is permitted to interact with this NPC.
	 * @param playerObjId The unique identifier of the player to check.
	 * @return {@code true} if the ID is in the list, otherwise {@code false}.
	 */
	public boolean containsKey(int playerObjId)
	{
		return allowedList.contains(playerObjId);
	}
	
	/**
	 * Sets the {@link Player} who is currently looting this NPC.<br>
	 * This updates the internal {@code lootingPlayer} field.
	 * @param player The {@code Player} object to set as the looter.
	 */
	public void setBeingLooted(Player player)
	{
		lootingPlayer = player;
	}
	
	/**
	 * Retrieves the {@link Player} who is currently looting this NPC.<br>
	 * Returns {@code null} if no player is currently looting.
	 * @return The {@code Player} object being looted, or {@code null}.
	 */
	public Player getBeingLooted()
	{
		return lootingPlayer;
	}
	
	/**
	 * Checks if a player is currently looting this NPC.<br>
	 * It returns {@code true} if the {@code lootingPlayer} field is not {@code null}.
	 * @return {@code true} if someone is looting, otherwise {@code false}.
	 */
	public boolean isBeingLooted()
	{
		return lootingPlayer != null;
	}
	
	/**
	 * Sets the unique identifier for the loot distribution.<br>
	 * This value determines which specific distribution logic to use.
	 * @param distributionId The {@code int} ID of the distribution.
	 */
	public void setDistributionId(int distributionId)
	{
		this.distributionId = distributionId;
	}
	
	/**
	 * Retrieves the unique identifier for the current distribution.<br>
	 * This ID helps identify which loot group is being processed.
	 * @return The {@code int} value of the distribution ID.
	 */
	public int getDistributionId()
	{
		return distributionId;
	}
	
	/**
	 * Sets the type of loot distribution for this NPC.<br>
	 * This determines how items are shared among players.
	 * @param distributionType The boolean value to set for {@code distributionType}.
	 */
	public void setDistributionType(boolean distributionType)
	{
		this.distributionType = distributionType;
	}
	
	/**
	 * Checks the current distribution type of the NPC.<br>
	 * This value determines how loot is distributed among players.
	 * @return {@code true} if a specific distribution type is active, {@code false} otherwise.
	 */
	public boolean getDistributionType()
	{
		return distributionType;
	}
	
	/**
	 * Updates the current index of the {@code DropNpc}.<br>
	 * This value is used to track the position during distribution.
	 * @param currentIndex The new integer value for the current index.
	 */
	public void setCurrentIndex(int currentIndex)
	{
		this.currentIndex = currentIndex;
	}
	
	/**
	 * Retrieves the current position in the distribution sequence.<br>
	 * This value tracks which item is currently being processed.
	 * @return The current {@code int} index.
	 */
	public int getCurrentIndex()
	{
		return currentIndex;
	}
	
	/**
	 * Sets the number of players in a group.<br>
	 * This value determines how many players are included in the distribution.
	 * @param groupSize The size of the player group.
	 */
	public void setGroupSize(int groupSize)
	{
		this.groupSize = groupSize;
	}
	
	/**
	 * Retrieves the current size of the group.<br>
	 * This value represents how many players are in the group.
	 * @return The total number of players in the group as an {@code int}.
	 */
	public int getGroupSize()
	{
		return groupSize;
	}
	
	/**
	 * Updates the list of players currently within range.<br>
	 * This method sets the {@code inRangePlayers} collection.
	 * @param inRangePlayers The collection of {@link Player} objects to store.
	 */
	public void setInRangePlayers(Collection<Player> inRangePlayers)
	{
		this.inRangePlayers = inRangePlayers;
	}
	
	/**
	 * Retrieves all players currently within range.<br>
	 * This method returns the internal collection of {@link Player} objects.
	 * @return a {@code Collection} of {@link Player} objects.
	 */
	public Collection<Player> getInRangePlayers()
	{
		return inRangePlayers;
	}
	
	/**
	 * Adds a {@link Player} to the internal status list.<br>
	 * This method tracks which players are currently interacting with this NPC.
	 * @param player The {@code Player} object to add.
	 */
	public void addPlayerStatus(Player player)
	{
		playerStatus.add(player);
	}
	
	/**
	 * Removes a specific {@link Player} from the current status list.<br>
	 * This method updates the internal collection of players.
	 * @param player The {@code Player} object to remove.
	 */
	public void delPlayerStatus(Player player)
	{
		playerStatus.remove(player);
	}
	
	/**
	 * Retrieves the list of players with a specific status.<br>
	 * This method returns the internal {@code playerStatus} collection.
	 * @return A {@code Collection} of {@link Player} objects.
	 */
	public Collection<Player> getPlayerStatus()
	{
		return playerStatus;
	}
	
	/**
	 * Checks if a specific {@link Player} is in the status list.<br>
	 * This method verifies if the provided {@code player} object exists within the internal collection.
	 * @param player The {@code Player} to check for.
	 * @return {@code true} if the player is found, otherwise {@code false}.
	 */
	public boolean containsPlayerStatus(Player player)
	{
		return playerStatus.contains(player);
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
	 * Enables the free for all loot distribution mode.<br>
	 * This method sets {@code isFreeForAll} to {@code true}.<br>
	 * It resets the {@code distributionId} to {@code 0}.<br>
	 * It clears all entries from the {@code allowedList}.
	 */
	public void startFreeForAll()
	{
		isFreeForAll = true;
		distributionId = 0;
		allowedList.clear();
	}
	
	/**
	 * Retrieves the unique identifier for this {@link DropNpc}.<br>
	 * This ID distinguishes this specific NPC from others.
	 * @return The unique integer ID of the NPC.
	 */
	public int getNpcUniqueId()
	{
		return npcUniqueId;
	}
	
	/**
	 * Retrieves the remaining decay time for this NPC.<br>
	 * This value represents how long until the object disappears.
	 * @return The current decay time as a {@code long}.
	 */
	public long getReamingDecayTime()
	{
		return reamingDecayTime;
	}
	
	/**
	 * Sets the decay time for remaining items.<br>
	 * This value determines how long an item stays before it disappears.
	 * @param reamingDecayTime The duration in milliseconds to keep the item.
	 */
	public void setReamingDecayTime(long reamingDecayTime)
	{
		this.reamingDecayTime = reamingDecayTime;
	}
}
