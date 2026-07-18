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
package com.aionemu.gameserver.services.siegeservice;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.aionemu.commons.utils.GenericValidator;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.siege.SiegeNpc;
import com.aionemu.gameserver.model.siege.SiegeRace;
import com.aionemu.gameserver.model.team.legion.Legion;

/**
 * This class manages the various counters associated with a siege.<br>
 * It stores data for each {@link SiegeRace} to track progress and statistics.<br>
 * One instance of this class should be used per race.
 * @author SoulKeeper
 */
public class SiegeRaceCounter implements Comparable<SiegeRaceCounter>
{
	private final AtomicLong totalDamage = new AtomicLong();
	private final Map<Integer, AtomicLong> playerDamageCounter = new ConcurrentHashMap<>();
	private final Map<Integer, AtomicLong> playerAPCounter = new ConcurrentHashMap<>();
	private final Map<Integer, AtomicLong> playerGPCounter = new ConcurrentHashMap<>();
	private final Map<Integer, AtomicLong> legionDamageCounter = new ConcurrentHashMap<>();
	private final SiegeRace siegeRace;
	
	/**
	 * Creates a new instance of {@code SiegeRaceCounter}.<br>
	 * This object tracks statistics for a specific {@link SiegeRace}.
	 * @param siegeRace The race to associate with this counter.
	 */
	public SiegeRaceCounter(SiegeRace siegeRace)
	{
		this.siegeRace = siegeRace;
	}
	
	/**
	 * Updates the siege counters with new damage values.<br>
	 * This method calls {@code addTotalDamage} for every call.<br>
	 * It also updates individual player stats if the {@code creature} is a {@link Player}.
	 * @param creature The {@code Creature} that dealt the damage.
	 * @param damage The amount of damage to add to the counters.
	 */
	public void addPoints(Creature creature, int damage)
	{
		addTotalDamage(damage);
		
		if (creature instanceof Player)
		{
			addPlayerDamage((Player) creature, damage);
		}
	}
	
	/**
	 * Updates the total damage counter for this siege race.<br>
	 * This method adds the specified amount to the {@code totalDamage} field.
	 * @param damage The amount of damage to add.
	 */
	public void addTotalDamage(int damage)
	{
		totalDamage.addAndGet(damage);
	}
	
	/**
	 * Updates the damage statistics for a specific {@link Player}.<br>
	 * This method adds the specified amount to both the individual player and their legion.<br>
	 * It uses the player's unique object ID as the key in the internal counter map.
	 * @param player The {@code Player} who dealt the damage.
	 * @param damage The amount of damage to add to the counters.
	 */
	public void addPlayerDamage(Player player, int damage)
	{
		final Legion legion = player.getLegion();
		if (legion != null)
		{
			addLegionDamage(legion, damage);
		}
		
		addToCounter(player.getObjectId(), damage, playerDamageCounter);
	}
	
	/**
	 * Updates the total damage for a specific {@link Legion}.<br>
	 * This method adds the provided amount to the internal counter.
	 * @param legion The {@code Legion} object to update.
	 * @param damage The amount of damage to add.
	 */
	public void addLegionDamage(Legion legion, int damage)
	{
		addToCounter(legion.getLegionId(), damage, legionDamageCounter);
	}
	
	/**
	 * Adds a specific amount of abyss points to a {@link Player}.<br>
	 * This method updates the internal counter for the player.
	 * @param player The {@code Player} object receiving the points.
	 * @param abyssPoints The number of points to add.
	 */
	public void addAbyssPoints(Player player, int abyssPoints)
	{
		addToCounter(player.getObjectId(), abyssPoints, playerAPCounter);
	}
	
	/**
	 * Adds a specific amount of glory points to a {@link Player}.<br>
	 * This method updates the player's score during an artifact siege.
	 * @param player The {@code Player} object to receive the points.
	 * @param gloryPoints The number of points to add to the player.
	 */
	public void addGloryPoints(Player player, int gloryPoints)
	{
		addToCounter(player.getObjectId(), gloryPoints, playerGPCounter);
	}
	
	/**
	 * Updates a specific counter within a map.<br>
	 * This method adds the provided {@code value} to the entry associated with the {@code key}.<br>
	 * It creates a new {@link AtomicLong} if the key does not already exist in the {@code counterMap}.
	 * @param <K> The type of the key used in the map.
	 * @param key The unique identifier for the counter to update.
	 * @param value The amount to add to the current counter.
	 * @param counterMap The map containing all counters for this category.
	 */
	protected <K> void addToCounter(K key, int value, Map<K, AtomicLong> counterMap)
	{
		// Get the counter for specific key
		AtomicLong counter = counterMap.get(key);
		
		// Counter was not registered, need to create it
		if (counter == null)
		{
			// Synchronize here because multiple threads may attempt to increment the same counter simultaneously.
			synchronized (this)
			{
				if (counterMap.containsKey(key))
				{
					counter = counterMap.get(key);
				}
				else
				{
					counter = new AtomicLong();
					counterMap.put(key, counter);
				}
			}
		}
		
		counter.addAndGet(value);
	}
	
	/**
	 * Retrieves the cumulative damage for this siege race.<br>
	 * This value is updated by the {@code addTotalDamage} method.
	 * @return The total damage as a {@code long}.
	 */
	public long getTotalDamage()
	{
		return totalDamage.get();
	}
	
	/**
	 * Calculates the damage dealt by players who are not part of a legion.<br>
	 * This value is derived by subtracting {@code getTotalLegionDamage} from the total damage.
	 * @return The amount of non-legion damage as a {@code long}.
	 */
	public long getNonLegionDamage()
	{
		return totalDamage.get() - getTotalLegionDamage();
	}
	
	/**
	 * Calculates the total damage dealt by all legions.<br>
	 * This method sums up every value stored in the {@code legionDamageCounter}.
	 * @return The sum of all legion damage as a {@code long}.
	 */
	public long getTotalLegionDamage()
	{
		long result = 0;
		for (AtomicLong damage : legionDamageCounter.values())
		{
			result += damage.get();
		}
		
		return result;
	}
	
	/**
	 * Retrieves the damage counters for each legion.<br>
	 * This method returns a sorted map of legion IDs and their total damage.
	 * @return A {@code Map<Integer, Long>} containing the legion ID as the key and the total damage as the value.
	 */
	public Map<Integer, Long> getLegionDamageCounter()
	{
		return getOrderedCounterMap(legionDamageCounter);
	}
	
	/**
	 * Retrieves the damage counts for all players.<br>
	 * The data is sorted by player ID.
	 * @return A {@code Map<Integer, Long>} where the key is the player ID and the value is their total damage.
	 */
	public Map<Integer, Long> getPlayerDamageCounter()
	{
		return getOrderedCounterMap(playerDamageCounter);
	}
	
	/**
	 * Retrieves the abyss points for all players.<br>
	 * This method returns a sorted map of player IDs and their corresponding scores.
	 * @return A {@code Map<Integer, Long>} containing player IDs as keys and abyss points as values.
	 */
	public Map<Integer, Long> getPlayerAbyssPoints()
	{
		return getOrderedCounterMap(playerAPCounter);
	}
	
	/**
	 * Retrieves the glory points for all players.<br>
	 * This method returns a sorted map of player IDs and their corresponding scores.
	 * @return A {@code Map<Integer, Long>} containing player IDs as keys and glory points as values.
	 */
	public Map<Integer, Long> getPlayerGloryPoints()
	{
		return getOrderedCounterMap(playerGPCounter);
	}
	
	/**
	 * Converts an unordered map of counters into a sorted map.<br>
	 * The results are ordered by value in descending order.<br>
	 * Entries with a value of 0 or less are excluded from the result.
	 * @param <K> The type of keys maintained by the map.
	 * @param unorderedMap The source map containing {@code AtomicLong} values to be sorted.
	 * @return A {@link Map} where entries are sorted from highest to lowest value.
	 */
	protected <K> Map<K, Long> getOrderedCounterMap(Map<K, AtomicLong> unorderedMap)
	{
		if (GenericValidator.isBlankOrNull(unorderedMap))
		{
			return Collections.emptyMap();
		}
		
		final LinkedList<Map.Entry<K, AtomicLong>> tempList = new LinkedList<>(unorderedMap.entrySet());
		Collections.sort(tempList, (o1, o2) -> Long.valueOf(o2.getValue().get()).compareTo(o1.getValue().get()));
		
		final Map<K, Long> result = new LinkedHashMap<>();
		for (Map.Entry<K, AtomicLong> entry : tempList)
		{
			if (entry.getValue().get() > 0)
			{
				result.put(entry.getKey(), entry.getValue().get());
			}
		}
		
		return result;
	}
	
	/**
	 * Compares this {@link SiegeRaceCounter} with another one.<br>
	 * It compares the total damage values of both counters.
	 * @param o The other {@code SiegeRaceCounter} to compare against.
	 * @return A negative integer if this counter has more damage than {@code o}, zero if equal, or a positive integer if it has less.
	 */
	@Override
	public int compareTo(SiegeRaceCounter o)
	{
		return Long.valueOf(o.getTotalDamage()).compareTo(getTotalDamage());
	}
	
	/**
	 * Retrieves the race associated with this {@link SiegeNpc}.<br>
	 * This identifies which faction the NPC belongs to during a siege.
	 * @return the {@code SiegeRace} of the current NPC.
	 */
	public SiegeRace getSiegeRace()
	{
		return siegeRace;
	}
	
	/**
	 * Retrieves the ID of the winning {@code Legion}.<br>
	 * It checks if the highest damage from a legion exceeds the non-legion damage.<br>
	 * Returns {@code null} if no data exists or if the condition is not met.
	 * @return The {@code Integer} ID of the winning legion, or {@code null}.
	 */
	public Integer getWinnerLegionId()
	{
		final Map<Integer, Long> legionDamageMap = getLegionDamageCounter();
		if (legionDamageMap.isEmpty())
		{
			return null;
		}
		
		final Integer topLegion = legionDamageMap.keySet().iterator().next();
		final long topLegionDamage = legionDamageMap.get(topLegion);
		
		// legion captures fortress if damage done is > then non-legion damage
		final boolean captureByLegion = topLegionDamage > getNonLegionDamage();
		return captureByLegion ? topLegion : null;
		/*
		 * Map<Player, AtomicLong> teamDamageMap = new HashMap<Player, AtomicLong>(); for (Integer id : playerDamageCounter.keySet()) { Player player = World.getInstance().findPlayer(id); if (player != null) { if (player.getCurrentTeam() != null) { Player teamLeader = player.getCurrentTeam().getLeaderObject(); long damage = playerDamageCounter.get(id).get(); if (teamLeader != null) { if (!teamDamageMap.containsKey(teamLeader)) { teamDamageMap.put(teamLeader, new AtomicLong()); } teamDamageMap.get(teamLeader).addAndGet(damage); } } } } if (teamDamageMap.isEmpty()) { return null; } Player topTeamLeader = getOrderedCounterMap(teamDamageMap).keySet().iterator().next(); Legion legion = topTeamLeader.getLegion(); return legion != null ? legion.getLegionId() : null;
		 */
	}
}
