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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.siege.SiegeNpc;
import com.aionemu.gameserver.model.siege.SiegeRace;
import com.aionemu.gameserver.model.team.legion.Legion;

/**
 * This class manages and tracks the count of participants in a siege.<br>
 * It provides utility methods to handle {@link Legion} and {@link Player} data during siege events.
 */
public class SiegeCounter
{
	private static final Logger log = LoggerFactory.getLogger(SiegeCounter.class);
	private final Map<SiegeRace, SiegeRaceCounter> siegeRaceCounters = new HashMap<>();
	
	/**
	 * Initializes a new instance of the {@code SiegeCounter}.<br>
	 * This constructor sets up the counters for all available {@link SiegeRace} types.
	 */
	public SiegeCounter()
	{
		siegeRaceCounters.put(SiegeRace.ELYOS, new SiegeRaceCounter(SiegeRace.ELYOS));
		siegeRaceCounters.put(SiegeRace.ASMODIANS, new SiegeRaceCounter(SiegeRace.ASMODIANS));
		siegeRaceCounters.put(SiegeRace.BALAUR, new SiegeRaceCounter(SiegeRace.BALAUR));
	}
	
	/**
	 * Adds a specific amount of damage to the correct siege race counter.<br>
	 * This method identifies the {@link SiegeRace} based on the {@code creature}.<br>
	 * It updates the points for that race using the provided {@code damage} value.
	 * @param creature The {@link Creature} who dealt the damage.
	 * @param damage The amount of damage to record.
	 */
	public void addDamage(Creature creature, int damage)
	{
		SiegeRace siegeRace;
		if (creature instanceof Player)
		{
			siegeRace = SiegeRace.getByRace(creature.getRace());
		}
		else if (creature instanceof SiegeNpc)
		{
			siegeRace = ((SiegeNpc) creature).getSiegeRace();
		}
		else
		{
			log.warn("Please debug me!", new RuntimeException("Damage to Siege boss done by non-SiegeRace creature" + creature));
			return;
		}
		
		siegeRaceCounters.get(siegeRace).addPoints(creature, damage);
	}
	
	/**
	 * Adds abyss points to a specific player.<br>
	 * This method updates the counter for the player's race.
	 * @param player The {@code Player} receiving the points.
	 * @param ap The amount of abyss points to add.
	 */
	public void addAbyssPoints(Player player, int ap)
	{
		final SiegeRace sr = SiegeRace.getByRace(player.getRace());
		siegeRaceCounters.get(sr).addAbyssPoints(player, ap);
	}
	
	/**
	 * Adds glory points to a specific player.<br>
	 * This method updates the counter for the player's race.
	 * @param player The {@link Player} receiving the points.
	 * @param gp The amount of glory points to add.
	 */
	public void addGloryPoints(Player player, int gp)
	{
		final SiegeRace sr = SiegeRace.getByRace(player.getRace());
		siegeRaceCounters.get(sr).addGloryPoints(player, gp);
	}
	
	/**
	 * Retrieves the counter for a specific {@link SiegeRace}.<br>
	 * This method looks up the data associated with the provided race.
	 * @param race The {@code SiegeRace} to look up.
	 * @return The {@code SiegeRaceCounter} object for the given race, or {@code null} if not found.
	 */
	public SiegeRaceCounter getRaceCounter(SiegeRace race)
	{
		return siegeRaceCounters.get(race);
	}
	
	/**
	 * This method records damage dealt by a specific {@link Legion}.<br>
	 * It updates the statistics for a given {@link SiegeRace}.
	 * @param race The type of siege race being played.
	 * @param legion The legion that dealt the damage.
	 * @param damage The amount of damage to add.
	 */
	public void addLegionDamage(SiegeRace race, Legion legion, int damage)
	{
		getRaceCounter(race).addLegionDamage(legion, damage);
	}
	
	/**
	 * Adds a specific amount of damage to a {@link SiegeRace}.<br>
	 * This method updates the total damage for the chosen race.
	 * @param race The {@code SiegeRace} to update.
	 * @param damage The amount of damage to add.
	 */
	public void addRaceDamage(SiegeRace race, int damage)
	{
		getRaceCounter(race).addTotalDamage(damage);
	}
	
	/**
	 * Retrieves the {@link SiegeRaceCounter} for the winning race.<br>
	 * This method identifies the winner by sorting all active counters.<br>
	 * It returns the counter with the highest value from the internal map.
	 * @return The {@code SiegeRaceCounter} object representing the winning team.
	 */
	public SiegeRaceCounter getWinnerRaceCounter()
	{
		final List<SiegeRaceCounter> list = new ArrayList<>(siegeRaceCounters.values());
		Collections.sort(list);
		return list.get(0);
	}
}
